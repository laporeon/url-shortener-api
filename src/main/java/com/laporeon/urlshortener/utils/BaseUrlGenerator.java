package com.laporeon.urlshortener.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaseUrlGenerator {

    public String generateBaseUrl(HttpServletRequest request) {
        String host = request.getHeader("host");
        String scheme = request.getScheme();

        String baseUrl = scheme + "://" + host;

        log.info("Base URL: {}", baseUrl);
        return baseUrl;
    }

}
