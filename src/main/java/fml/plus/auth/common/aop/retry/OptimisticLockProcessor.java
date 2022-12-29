package fml.plus.auth.common.aop.retry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Aspect
@Configuration
public class OptimisticLockProcessor implements Ordered {

    public int getOrder() {
        return 1;
    }

    @Pointcut("@annotation(fml.plus.auth.common.aop.retry.Retry)")
    public void retryOnOptFailure() {}

    @Around(value = "retryOnOptFailure()&&@annotation(retry)")
    public Object doConcurrentOperation(ProceedingJoinPoint pjp, Retry retry) throws Throwable {
        int maxRetries = retry.max();
        int numAttempts = 1;
        do {
            try {
                return pjp.proceed();
            } catch (Exception ex) {
                if(!checkException(ex, retry.forException())){
                    throw ex;
                }
                if (numAttempts > maxRetries) { // 超过次数
                    throw ex;
                }
            }
            numAttempts++;
        } while (true);
    }

    private boolean checkException(Exception e, Class<? extends Throwable> retryFor) {
        try {
            retryFor.cast(e);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
}