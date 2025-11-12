package com.laporeon.urlshortener.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Document(collection = "urls")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Url {

    @MongoId
    private String id;

    @Field(name = "short_code")
    private String shortCode;

    @Field(name = "original_url")
    private String originalUrl;

    @Field(name = "expires_at")
    @Indexed(name = "ttl", expireAfter = "0")
    private Instant expiresAt;

    @CreatedDate
    @Field(name = "created_at")
    private Instant createdAt;

}
