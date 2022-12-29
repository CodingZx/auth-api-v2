package fml.plus.auth.common.interceptors;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.common.base.Strings;
import fml.plus.auth.common.SignConfiguration;
import fml.plus.auth.common.annotation.Authority;
import fml.plus.auth.common.annotation.VisitorAccess;
import fml.plus.auth.common.constants.GlobalConstants;
import fml.plus.auth.common.constants.RedisConstants;
import fml.plus.auth.common.context.RequestHolder;
import fml.plus.auth.common.context.UserThreadInfo;
import fml.plus.auth.common.exception.BusinessException;
import fml.plus.auth.common.model.R;
import fml.plus.auth.common.model.Token;
import fml.plus.auth.common.request.RequestBodyWrapper;
import fml.plus.auth.common.util.GsonUtils;
import fml.plus.auth.common.util.HMacUtils;
import fml.plus.auth.service.LoginService;
import fml.plus.auth.service.MonitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private SignConfiguration.SignProperty signProperty;
    @Autowired
    private LoginService loginService;
    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private MonitorService monitorService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestHolder.get().setRequest(request);
        log.debug("Request URI -> {}, Method: {}", request.getRequestURI(), request.getMethod());

        if (handler instanceof HandlerMethod method) {
            if(method.getBean().getClass() == BasicErrorController.class) {
                // 默认error页面 不拦截
                return true;
            }
            // 统计当前请求次数
            monitorService.incrementRequestCount();

            // 校验注解
            var access = method.getMethodAnnotation(VisitorAccess.class);
            if(access != null && access.special()) {
                return true;
            }

            // 校验签名
            if(request instanceof RequestBodyWrapper wrapper && signProperty.isEnable() && (access == null || !access.skipSign())) {
                // Header sign
                String sign = request.getHeader(GlobalConstants.Sign.SIGN_HEADER);
                String signStr = requestSign(wrapper);
                if(!signStr.equals(sign)) {
                    sendError(response, R.other("签名错误, 非法请求"));
                    return false;
                }
            }

            // 获得Token
            var tokenStr = request.getHeader(GlobalConstants.Token.HEADER);
            var tokenModel = Token.from(tokenStr);
            if(tokenModel == null) {
                if (access == null || !access.skipLogin()) {
                    // 未登录无法访问
                    sendError(response, R.login());
                    return false;
                }
               return true;
            }

            var ops = redis.boundValueOps(RedisConstants.getTokenKey(tokenModel.getId()));
            var val = ops.get();
            if(!tokenStr.equals(val)) {
                // token与缓存中不一致, 被其他人登录或者失效
                sendError(response, R.login());
                return false;
            }
            ops.expire(GlobalConstants.Token.EXPIRE_DAYS, TimeUnit.DAYS);

            // 权限认证
            var authority = method.getMethodAnnotation(Authority.class);
            if(authority != null && authority.value().length > 0) {
                // 校验权限
                if(!loginService.checkPermissions(authority.value(), tokenModel.getRoleId())) {
                    sendError(response, R.auth());
                    return false;
                }
            }

            UserThreadInfo.VO vo = UserThreadInfo.get();
            vo.setUserId(tokenModel.getId());
            vo.setRoleId(tokenModel.getRoleId());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        UserThreadInfo.clear();
        RequestHolder.clear();
    }


    private void sendError(HttpServletResponse response, R r) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter pw = response.getWriter()) {
            pw.write(GsonUtils.gson().toJson(r));
            pw.flush();
        }
    }

    private String requestSign(RequestBodyWrapper request) {
        String body = request.getBodyString();
        String version = request.getHeader(GlobalConstants.Sign.VERSION_HEADER);
        String timestamp = request.getHeader(GlobalConstants.Sign.TIMESTAMP_HEADER);
        String source = request.getHeader(GlobalConstants.Sign.SOURCE_HEADER);
        String nonce = request.getHeader(GlobalConstants.Sign.NONCE_HEADER);
        String token = request.getHeader(GlobalConstants.Token.HEADER);
        String uri = request.getRequestURI();
        String method = request.getMethod().toUpperCase();

        if(Strings.isNullOrEmpty(version) || Strings.isNullOrEmpty(timestamp) || Strings.isNullOrEmpty(source) || Strings.isNullOrEmpty(nonce)) {
            throw new BusinessException("参数错误, 非法请求");
        }

        // 转为北京时间 校验时间戳是否有效
        var now = LocalDateTime.now();
        var reqTime = LocalDateTimeUtil.of(NumberUtils.toLong(timestamp, 0), ZoneId.of("+8"));
        if(reqTime.isAfter(now.plusMinutes(1)) || reqTime.isBefore(now.minusMinutes(GlobalConstants.Sign.EXPIRE_MINUTES))) {
            // 不在有效期
            throw new BusinessException("不要闹, 把系统时间调回去吧");
        }

        var params = request.getParameterMap()
                .entrySet()
                .stream()
                .map(f -> {
                    var val = f.getValue();
                    if(val == null || val.length == 0) return Tuples.of(f.getKey(), "");
                    else return Tuples.of(f.getKey(), val[0]);
                })
                .sorted(Comparator.comparing(Tuple2::getT1))
                .map(f -> f.getT1()+"="+f.getT2())
                .collect(Collectors.joining("&"));

        String signStr = uri + "\n" + method + "\n" + version + "\n" + (timestamp+"," + Strings.nullToEmpty(token) + "," + source+","+nonce) + "\n" + body + "\n" + params;
        return HMacUtils.sign(signStr, signProperty.getKey());
    }

}
