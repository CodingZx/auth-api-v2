package fml.plus.auth.common;

import com.google.common.collect.Lists;
import fml.plus.auth.common.interceptors.TokenInterceptor;
import fml.plus.auth.common.util.GsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Configuration
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SpringMvcConfiguration implements WebMvcConfigurer {
    private TokenInterceptor tokenInterceptor;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.debug("Configuration SpringMVC..");
        converters.removeIf(converter -> converter instanceof GsonHttpMessageConverter);

        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter(GsonUtils.gson());
        gsonHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        gsonHttpMessageConverter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));

        converters.add(0, gsonHttpMessageConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(3600)
                ;
    }
}
