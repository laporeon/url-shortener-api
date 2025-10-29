package com.laporeon.urlshortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfiguration {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        String serverUrl = "http://localhost:" + serverPort;

        return new OpenAPI()
                .servers(Collections.singletonList(new Server().url(serverUrl)))
                .info(
                        new Info()
                                .title("URL Shortener API")
                                .description("API for creating and managing shortened URLs.")
                                .version("1.0.0"));
    }

}
