package com.laporeon.urlshortener.entities;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Document(collection = "urls")
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class Url {

    @MongoId
    private String id;

    @Field(name = "short_code")
    @Indexed(unique = true, name = "idx_url_short_code")
    private String shortCode;

    @Field(name = "original_url")
    private String originalUrl;

    @Field(name = "expires_at")
    @Indexed(name = "ttl", expireAfter = "0")
    private Instant expiresAt;

    @CreatedDate
    @Field(name = "created_at")
    private Instant createdAt;

    public static Url create(String originalUrl, String shortCode, Instant expiresAt) {
        return Url.builder()
                      .shortCode(shortCode)
                      .originalUrl(originalUrl)
                      .expiresAt(expiresAt)
                      .createdAt(Instant.now())
                      .build();
    }

}
