package fml.plus.auth.common.request;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RequestBodyFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        log.debug("RequestBodyFilter init...");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 防止流读取一次后就没有了, 所以需要将流继续写出去
        if (servletRequest instanceof HttpServletRequest request) {
            // form 上传文件需要忽略.. 要不然会上传文件失败
            if(request.getContentType() == null || request.getContentType().contains("application/json")) {
                HttpServletRequest requestWrapper = new RequestBodyWrapper(request);
                filterChain.doFilter(requestWrapper, servletResponse);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        log.debug("RequestBodyFilter destroy...");
    }
}
