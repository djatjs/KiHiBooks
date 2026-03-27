package kr.kh.kihibooks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.kh.kihibooks.interceptor.PublisherInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private PublisherInterceptor publisherInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(publisherInterceptor)
                .addPathPatterns("/publisher/**", "/editor/**")
                .excludePathPatterns("/publisher/css/**", "/publisher/js/**", "/publisher/img/**");
    }
}
