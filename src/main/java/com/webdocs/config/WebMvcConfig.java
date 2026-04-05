package com.webdocs.config;

import com.webdocs.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/", "/files/**", "/editor/**")
                .excludePathPatterns("/login", "/logout", "/css/**", "/js/**",
                        "/images/**", "/webjars/**", "/swagger-ui/**",
                        "/v3/api-docs/**", "/api/**");
    }
}
