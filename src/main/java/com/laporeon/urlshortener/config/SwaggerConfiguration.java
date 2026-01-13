package com.laporeon.urlshortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("URL Shortener API")
                                .description("""
                                            A simple and efficient REST API for shortening long URLs.
                                            
                                            ## Features
                                            - **Shorten URLs**: Convert long URLs into short, shareable links
                                            - **Custom Expiration**: Set expiration dates for shortened URLs
                                            - **Automatic Redirect**: Seamlessly redirect users to original URLs
                                            - **Expired Link Detection**: Automatically handle and report expired links
                                            
                                            ## How It Works
                                            1. Submit a long URL with an optional expiration date (format: yyyy-MM-dd)
                                            2. Receive a shortened URL with a unique code
                                            3. Share the short URL. If not expired, it will automatically redirect to the original
                                            4. Links expire automatically after the expiration date and can no longer be accessed
                                            
                                            ## Quick Start
                                            Use `POST /shorten` to create a shortened URL, then access it via `GET /{shortCode}`.
                                            """
                                )
                                .version("1.0.0")
                                .license(
                                        new License()
                                                .name("MIT License")
                                                .url("https://opensource.org/licenses/MIT")
                                )
                );

    }

}
