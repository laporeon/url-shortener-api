package com.laporeon.urlshortener.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;


@Document(collection = "urls")
@Data
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
    private LocalDateTime expiresAt;

    @CreatedDate
    @Field(name = "created_at")
    private LocalDateTime createdAt;

}
