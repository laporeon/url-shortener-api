<h1 align="center"> URL Shortener API

![java](https://img.shields.io/static/v1?label=java&message=21.0.8&color=2d3748&logo=openjdk&style=flat-square)
![spring boot](https://img.shields.io/static/v1?label=spring%20boot&message=4.1.2&color=2d3748&logo=springboot&style=flat-square)
![mongodb](https://img.shields.io/badge/mongodb-8.0.17-4b32c3?style=flat-square&logo=mongodb&color=2d3748)
![docker](https://img.shields.io/static/v1?label=docker&message=29.4.3&color=2d3748&logo=docker&style=flat-square)
![swagger](https://img.shields.io/static/v1?label=swagger&message=3.0.3&color=2d3748&logo=swagger&style=flat-square)

</h1>

## Table of Contents

- [About](#about)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
  - [Quick Start](#quick-start) 
  - [Environment Variables Reference](#environment-variables-reference)
- [Usage](#usage)
    - [Routes](#routes)
        - [Requests](#requests)

## About

This project provides a simple REST API for creating and resolving shortened URLs.

The application uses MongoDB to persist shortened URLs and supports optional expiration dates. Expired URLs are rejected by the application, while MongoDB's TTL index asynchronously removes expired documents.

**Key features:**

- URL format and optional expiration date validation.
- Dynamically generated base URL based on the incoming HTTP request.
- MongoDB TTL index for asynchronous cleanup of expired URLs.
- Unique short code constraint at database level.
- Redirect endpoint with proper not-found/expired handling.
- Environment profiles for development and production.
- Swagger/OpenAPI documentation in non-production profiles.
- Configurable CORS through environment variables.
- Health check endpoint through Spring Boot Actuator.

## Requirements:

**For Docker (Recommended):**

- Docker & Docker Compose
- Make _(optional)_

**For Local Development:**

- Java 21+
- MongoDB 8+
- Make _(optional)_

> [!NOTE]
> - The project ships with the Maven Wrapper (`./mvnw`), so a global Maven installation is not required.

## Getting Started

The development workflow is powered by the Makefile. Run `make help` to list all available commands.

> [!NOTE]
> `make` is available out of the box on Linux and macOS. On Windows, use [WSL](https://learn.microsoft.com/windows/wsl/),
> or run the equivalent commands from the table below (use `mvnw.cmd` instead of `./mvnw`).

| Command | Description | Equivalent without Make |
|---|---|---|
| `make env` | Create `.env` from `.env.example` if it does not exist | `cp .env.example .env` |
| `make run` | Run the application locally without Docker | `./mvnw spring-boot:run` |
| `make test` | Run the test suite | `./mvnw test` |
| `make test TEST=ShortUrlServiceTest` | Run a specific test class | `./mvnw test -Dtest=ShortUrlServiceTest` |
| `make build` | Package the application without running tests | `./mvnw clean package -DskipTests` |
| `make dev` | Create `.env` if missing, then build and start the containers in the background | `docker compose up -d --build` |
| `make down` | Stop and remove the containers | `docker compose down` |
| `make logs` | Follow the container logs | `docker compose logs -f` |

### Quick Start

**Docker**

```bash
make env # Create the local environment file if it does not exist.
make dev # Build and start the application and MongoDB containers
```

**Local Development**

```bash
make env # Create the local environment file if it does not exist.
make run # Start the application.
```

The application will be available at `http://localhost:8080` (or the port you configured).

### Environment Variables Reference

The application loads MongoDB credentials based on the active Spring profile:
- `dev` profile uses individual MongoDB credentials
- `prod` profile expects a full MongoDB connection URI

| Variable               | Docker                               | Local                                | Production               | Description            |
|------------------------|--------------------------------------|--------------------------------------|--------------------------|------------------------|
| PORT                   | Optional (Default: "8080")           | Optional (Default: "8080")           | Auto-set by platform     | Server port            |
| SPRING_PROFILES_ACTIVE | Default: "dev"                       | Optional (Default: "dev")            | **Required**, set to "prod" | Active Spring profile  |
| MONGO_USER             | Optional (Default: "admin")          | **Required**                         | —                        | MongoDB username       |
| MONGO_PASSWORD         | Optional (Default: "dbpassword")     | **Required**                         | —                        | MongoDB password       |
| MONGO_DATABASE         | Optional (Default: "url_shortener_db") | Optional (Default: "url_shortener_db") | —                        | MongoDB database name  |
| MONGO_URI              | —                                    | —                                    | **Required**             | MongoDB connection URI |
| ALLOWED_ORIGINS        | Optional (Default: "*")              | Optional (Fallback: "*")             | **Required**             |  Comma-separated allowed frontend origins |

> [!WARNING]
> Do not use `ALLOWED_ORIGINS=*` in production. Configure explicit frontend origins instead.

## Usage

Once the application is running, you can interact with it through Swagger UI at `/docs` in non-production profiles or directly through HTTP requests.

### Routes

| Route      | HTTP Method | Params                                                          | Description                            | Auth Method |
|------------|-------------|-----------------------------------------------------------------|----------------------------------------|-------------|
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
