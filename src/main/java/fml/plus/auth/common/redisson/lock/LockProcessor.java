package fml.plus.auth.common.redisson.lock;

import com.google.common.base.Strings;
import fml.plus.auth.common.exception.LockAcquiredFailedException;
import fml.plus.auth.common.redisson.lock.annotation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
public class LockProcessor {
    private final ILockCurrentUser lockCurrentUser;
    private final RedissonClient redissonClient;

    private static final String EL_SYMBOL = "#";

    private static final ExpressionParser parser = new SpelExpressionParser();
    private static final StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();

    public LockProcessor(ILockCurrentUser support, RedissonClient redissonClient) {
        this.lockCurrentUser = support;
        this.redissonClient = redissonClient;
    }

    @Pointcut("@annotation(fml.plus.auth.common.redisson.lock.annotation.Lock)")
    public void lockPointCut() {
        //定义切面
    }

    @Around("lockPointCut() && @annotation(lock)")
    public Object around(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        long beginTimeStamp = System.currentTimeMillis();
        var lockKey = getLockKey(pjp, lock);
        String keyStr = genKey(lockKey, pjp, lock.lockUser());
        RLock rLock = redissonClient.getLock(keyStr);
        log.debug("尝试获得锁，key:{},waitTime:{},keepTime:{},curThreadId:{}", keyStr, lock.waitTime(), lock.keepTime(), Thread.currentThread().getId());

        if (rLock.tryLock(lock.waitTime(), lock.keepTime(), TimeUnit.SECONDS)) {
            log.debug("获得锁[{}]成功", keyStr);
            try {
                return pjp.proceed();
            } finally {
                release(lock.keepTime(), rLock, System.currentTimeMillis() - beginTimeStamp);
            }
        } else {
            log.debug("未能获得锁，key:{},waitTime:{},keepTime:{},curThreadId:{}", keyStr, lock.waitTime(), lock.keepTime(), Thread.currentThread().getId());
            throw new LockAcquiredFailedException("未能获得锁:" + keyStr);
        }
    }

    private String getLockKey(ProceedingJoinPoint pjp, Lock lock) {
        for(var condition : lock.condition()) {
            if(condition == null) continue;

            if(parseSPELBool(pjp, condition.conditionCase())){
                return condition.conditionKey();
            }
        }
        return lock.value();
    }

    /**
     * 生成LockKey
     */
    private String genKey(String key, ProceedingJoinPoint pjp, boolean lockUser) {
        Signature signature = pjp.getSignature();
        String keyStr;
        if (StringUtils.hasLength(key)) {
            keyStr = parseSPELStr(pjp, key);
        } else {
            keyStr = "lock_" + signature.getDeclaringTypeName() + "." + signature.getName();
        }
        if (lockUser) {
            String currentUserId = lockCurrentUser.getCurrentUserId();
            if (Strings.isNullOrEmpty(currentUserId)) {
                currentUserId = UUID.randomUUID().toString().replace("-","");
            }
            keyStr = keyStr + ":user:" + currentUserId;
        }
        return keyStr;
    }


    /**
     * EL表达式解析转换 String类型
     */
    private static String parseSPELStr(ProceedingJoinPoint pjp, String express) {
        if (express.contains(EL_SYMBOL)) {
            Object[] args = pjp.getArgs();
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            String[] params = discoverer.getParameterNames(method);
            EvaluationContext context = new StandardEvaluationContext();
            if (params != null) {
                for (int len = 0; len < params.length; len++) {
                    context.setVariable(params[len], args[len]);
                }
            }
            Expression expression = parser.parseExpression(express);
            return Optional.ofNullable(expression.getValue(context, String.class)).orElse(express);
        } else {
            return express;
        }
    }

    /**
     * EL表达式解析转换 Boolean类型
     */
    protected static boolean parseSPELBool(ProceedingJoinPoint pjp, String express) {
        Object[] args = pjp.getArgs();
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        if (params != null) {
            for (int len = 0; len < params.length; len++) {
                context.setVariable(params[len], args[len]);
            }
        }
        Expression expression = parser.parseExpression(express);
        return Optional.ofNullable(expression.getValue(context, Boolean.class)).orElse(false);
    }

    /**
     * 生成锁
     */
    private void release(long keepTime, RLock rLock, long duration) {
        try {
            rLock.unlock();
            log.debug("释放锁[{}]成功", rLock.getName());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not locked by current thread") && keepTime > 0 && keepTime * 1000 < duration) {
                log.debug("锁{}已被提前释放，无需解锁", rLock.getName());
            } else {
                throw e;
            }
        }
    }
}
