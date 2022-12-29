package fml.plus.auth.common.version;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Configuration
public class WebMvcConfiguration implements WebMvcRegistrations {

    @Autowired
    private ApiVersionProperties properties;

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "api.version")
    public static class ApiVersionProperties {
        private String header = "app-version";
        private String separator = "\\.";
        private String defaultVersion = "0.0.1";
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping() {

            @Override
            protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
                return createCondition(AnnotationUtils.findAnnotation(handlerType, ApiControl.class));
            }

            @Override
            protected RequestCondition<?> getCustomMethodCondition(Method method) {
                return createCondition(AnnotationUtils.findAnnotation(method, ApiControl.class));
            }

            private RequestCondition<ApiVersionCondition> createCondition(ApiControl apiVersion) {
                return new ApiVersionCondition(apiVersion, properties);
            }
        };
    }
}
