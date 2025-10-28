package com.laporeon.urlshortener.repositories;

import com.laporeon.urlshortener.entities.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<Url, String> {
    Boolean existsByShortCode(String shortCode);

    Optional<Url> findByShortCode(String shortCode);
}
