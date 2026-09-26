package com.example.myapp.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Override
    public void addCorsMappings(CorsRegistry registry){
         registry.addMapping("/**") 
            // .setAllowedOriginPatterns(List.of("*"));
            .allowedOrigins("http://localhost:4200") 
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*") 
            // .exposedHeaders("Authorization")
            .allowCredentials(true)
            .maxAge(3600); 
    }
}

