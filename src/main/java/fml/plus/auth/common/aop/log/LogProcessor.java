package fml.plus.auth.common.aop.log;

import com.fasterxml.uuid.Generators;
import fml.plus.auth.common.context.RequestHolder;
import fml.plus.auth.common.context.UserThreadInfo;
import fml.plus.auth.common.util.GsonUtils;
import fml.plus.auth.common.util.IPUtils;
import fml.plus.auth.entity.LogEntity;
import fml.plus.auth.mapper.LogMapper;
import fml.plus.auth.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Configuration
public class LogProcessor {
    @Autowired
    private AccountService accountService;
    @Autowired
    private LogMapper logMapper;

    @Pointcut("@annotation(fml.plus.auth.common.aop.log.Log)")
    public void logJoinPoint() {
    }

    @Around(value = "logJoinPoint()&&@annotation(logAnnotation)")
    public Object doConcurrentOperation(ProceedingJoinPoint pjp, Log logAnnotation) throws Throwable {
        var request = RequestHolder.get().getRequest();
        var logModel = new LogEntity();
        logModel.setId(Generators.timeBasedGenerator().generate());
        logModel.setBusinessType(logAnnotation.businessType());
        logModel.setTitle(logAnnotation.title());
        logModel.setOperIp(IPUtils.getRealIp(request));
        logModel.setRequestMethod(request.getMethod());
        logModel.setOperUri(request.getRequestURI());
        logModel.setOperName(accountService.getRealName(UserThreadInfo.get().getUserId()));
        logModel.setCreateTime(LocalDateTime.now());
        logModel.setErrorMsg("");
        if (logAnnotation.saveRequestData()) {
            logModel.setOperParam(requestValue(pjp, request));
        } else {
            logModel.setOperParam("");
        }
        String className = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();
        logModel.setMethod(className + "." + methodName + "()");
        try {
            return pjp.proceed();
        } catch (Exception e) {
            logModel.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            try {
                logMapper.insert(logModel);
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
            }
        }
    }

    // 获取请求参数
    private String requestValue(JoinPoint joinPoint, HttpServletRequest request) {
        String requestMethod = request.getMethod();
        if (HttpMethod.PUT.name().equalsIgnoreCase(requestMethod) || HttpMethod.POST.name().equalsIgnoreCase(requestMethod)) {
            return GsonUtils.gson().toJson(filterArgs(joinPoint.getArgs()));
        } else {
            return GsonUtils.gson().toJson(request.getParameterMap());
        }
    }

    private List<Object> filterArgs(Object[] args) {
        return Arrays.stream(args)
                .filter(f -> !isFilterObject(f))
                .collect(Collectors.toList());
    }

    // 过滤参数类型
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            var collection = (Collection<?>) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            var map = (Map<?,?>) o;
            for (Map.Entry<?, ?> value : map.entrySet()) {
                return value.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse || o instanceof BindingResult;
    }

}