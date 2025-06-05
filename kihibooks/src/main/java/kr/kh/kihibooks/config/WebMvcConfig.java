package kr.kh.kihibooks.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "kr.kh.kihibooks")
public class WebMvcConfig implements WebMvcConfigurer{

    @Value("${spring.path.upload}")
    String uploadPath;
    
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/file/**").addResourceLocations("file:///"+uploadPath);
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/");
        // 파비콘 관련 설정
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/");
        // 장르별 페이지 공통 css 관련 설정
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/img/**")
        .addResourceLocations("classpath:/static/img/");
    }
}