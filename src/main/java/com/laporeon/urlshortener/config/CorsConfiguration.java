package com.laporeon.urlshortener.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                                 .map(String::trim)
                                 .toArray(String[]::new);

        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("Content-Type")
                .exposedHeaders("Location")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
