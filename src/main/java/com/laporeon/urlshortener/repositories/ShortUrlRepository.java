package com.laporeon.urlshortener.repositories;

import com.laporeon.urlshortener.entities.ShortUrl;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<ShortUrl, String> {
    Boolean existsByShortCode(String shortCode);

    Optional<ShortUrl> findByShortCode(String shortCode);
}
