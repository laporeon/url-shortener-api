package com.laporeon.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.mongodb.autoconfigure.health.MongoHealthContributorAutoConfiguration;

/**
 * Excludes MongoHealthContributorAutoConfiguration because Spring Boot's MongoHealthIndicator
 * executes the 'hello' command on the 'local' database, which triggers an authentication error
 * on each health check when the configured MongoDB user lacks access to 'local'.
 *
 * If/when the health check implementation improves or MongoDB user permissions change,
 * this exclusion can be reconsidered.
 */
@SpringBootApplication(exclude = {MongoHealthContributorAutoConfiguration.class})
public class UrlShortenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShortenerApplication.class, args);
	}

}
