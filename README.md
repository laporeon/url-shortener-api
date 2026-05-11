<h1 align="center"> URL Shortener API

![java](https://img.shields.io/static/v1?label=java&message=21.0.8&color=2d3748&logo=openjdk&style=flat-square)
![spring boot](https://img.shields.io/static/v1?label=spring%20boot&message=4.0.6&color=2d3748&logo=springboot&style=flat-square)
![mongodb](https://img.shields.io/badge/mongodb-8.0.17-4b32c3?style=flat-square&logo=mongodb&color=2d3748)
![docker](https://img.shields.io/static/v1?label=docker&message=29.4.3&color=2d3748&logo=docker&style=flat-square)
![swagger](https://img.shields.io/static/v1?label=swagger&message=3.0.3&color=2d3748&logo=swagger&style=flat-square)

</h1>

## Table of Contents

- [About](#about)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
    - [Running with Docker (Recommended)](#running-with-docker-recommended)
    - [Running Locally (Without Docker)](#running-locally-without-docker)
    - [Environment Variables Reference](#environment-variables-reference)
- [Usage](#usage)
    - [Routes](#routes)
        - [Requests](#requests)

## About

A production-ready URL shortening REST API built with Spring Boot and MongoDB. Convert long URLs into short, memorable
links with optional expiration dates. Features include automatic redirection, comprehensive validation, and Swagger
documentation.

**Key features:**

- Input validation for URL and expiration date.
- Generates the base URL dynamically from the incoming HTTP request.
- Automatic URL expiration via MongoDB TTL index.
- Unique short code constraint at database level.
- Redirect endpoint with proper not-found/expired handling.
- Environment profiles support (`dev` and `prod`).
- Swagger/OpenAPI available in non-production profile.
- Production-safe CORS configuration via environment variable.
- Health check endpoint via Spring Boot Actuator (`/actuator/health`).

## Requirements:

**For Docker (Recommended):**

- Docker & Docker Compose

**For Local Development:**

- Java 21+
- Maven 3.9+
- MongoDB 8+

## Getting Started

### Running with Docker (Recommended)

This is the simplest setup. Docker Compose will automatically build and start all required services, using default values in the [Compose file](./docker-compose.yml).

```bash
docker compose up -d --build
```

Then access the application at `http://localhost:8080/` (or the port you configured).

**Optionally**, you can override any environment variables with your own settings.
```bash
cp .env.example .env
# Edit .env with your local values
```

### Running Locally (Without Docker)

1. Set the required environment variables using **either** method below:
    - **Option 1: Using `.env` file**. Copy the example file and edit with your values:
      ```bash
      cp .env.example .env
      # Edit .env with your local settings
      ```

    - **Option 2: Exporting via shell**
      ```bash
      export MONGO_USER=<your-mongodb-user-here>
      export MONGO_PASSWORD=<your-mongodb-password-here>
      export MONGO_DATABASE=<database-name>
      export ALLOWED_ORIGINS=http://localhost:5173,https://your-frontend-url.com # optional
      export PORT=8081 # optional
      ```

2. Start the application:
```bash
mvn spring-boot:run
```
3. Access at `http://localhost:8080/` (or the port you configured).

### Environment Variables Reference

> [!NOTE]
> The application loads MongoDB credentials based on the active Spring profile:
> - `dev` profile uses individual MongoDB credentials
> - `prod` profile expects a full MongoDB connection URI

| Variable               | Docker                               | Local                    | Production               | Description            |
|------------------------|--------------------------------------|-------------------------------------|--------------------------|------------------------|
| PORT                   | Optional (Default: "8080")           | Optional (Default: "8080")          | Auto-set by platform     | Server port            |
| SPRING_PROFILES_ACTIVE | Default: "dev"                       | Optional (Default: "dev")           | **Required** (set to "prod") | Active Spring profile  |
| MONGO_USER             | Optional (Default: "admin")          | **Required**                        | —                        | MongoDB username       |
| MONGO_PASSWORD         | Optional (Default: "dbpassword")     | **Required**                        | —                        | MongoDB password       |
| MONGO_DATABASE         | Optional (Default: "urlshortenerdb") | Optional (Default: "urlshortenerdb") | —                        | MongoDB database name  |
| MONGO_URI              | —                                    | —                                   | **Required**             | MongoDB connection URI |
| ALLOWED_ORIGINS        | Optional (Fallback: "*")              | Optional (Default: "*")                        | **Required**             |  Comma-separated allowed frontend origins |

## Usage

Once the application is running, you can interact via Swagger UI at `/docs` in non-production profile or directly through HTTP requests.

### **Routes**

| Route      | HTTP Method | Params                                                          | Description                            | Auth Method |
|------------|-------------|-----------------------------------------------------------------|----------------------------------------|-------------|
| `/`        | GET         | -                                                               | API metadata                           | None        |
| `/docs`    | GET         | -                                                               | Swagger documentation                  | None        |
| `/shorten` | POST        | Body with `originalUrl` and _optional_ `expirationDate` | Create a short URL from a long URL     | None        |
| `/{shortCode}` | GET         | **Path Parameters:** `shortCode`                                              | Redirects to the original URL if valid | None        |

#### Requests

- `POST /shorten`

Request body:

```json
{
  "originalUrl": "https://example.com/a/very/long/url",
  "expirationDate": "2026-11-12" // optional, format: yyyy-MM-dd
}
```

Example:

```bash
curl -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl": "https://example.com/a/very/long/url", "expirationDate": "2026-11-12"}'
```

- `GET /{shortCode}`

Example: 

```bash
curl -L http://localhost:8080/a1b2c3d 
```

If the short code is valid, the API returns `HTTP 302` redirecting to the original URL.
If expired or not found, the API returns `HTTP 404`.

[⬆ Back to the top](#-url-shortener-api)
