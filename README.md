<h1 align="center"> URL Shortener API

![java](https://img.shields.io/static/v1?label=java&message=21.0.8&color=2d3748&logo=openjdk&style=flat-square)
![spring boot](https://img.shields.io/static/v1?label=spring%20boot&message=3.5.4&color=2d3748&logo=springboot&style=flat-square)
![mongodb](https://img.shields.io/badge/mongodb-latest-4b32c3?style=flat-square&logo=mongodb)
![docker](https://img.shields.io/static/v1?label=docker&message=28.5.0&color=2d3748&logo=docker&style=flat-square)

</h1>

## Table of Contents

- [About](#about)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
    - [Configuring](#configuring)
        - [.env](#env)
- [Usage](#usage)
    - [Starting](#starting)
    - [Routes](#routes)
        - [Requests](#requests)

## About

A production-ready URL shortening REST API built with Spring Boot and MongoDB. Convert long URLs into short, memorable
links with optional expiration dates. Features include automatic redirection, comprehensive validation, and Swagger
documentation.

**Key features:**

- Input validation for shortening URL requests.
- Generates the base URL dynamically from the incoming HTTP request.
- Calculates expiration date for shortened URLs, defaulting to 24 hours if none provided.
- Automatically redirects short URLs to their original destinations, handling expired and non-existent links properly.
- Interactive API Docs via Swagger UI
- Environment profiles with separate configurations

## Requirements:

**For Docker (Recommended):**

- Docker & Docker Compose

**For Local Development:**

- Java 21+
- Maven 3.9+
- MongoDB

## Getting Started

### Configuring

#### **.env**

> [!NOTE]
> The application loads MongoDB credentials based on the active Spring profile:
> - `dev`: Uses individual MongoDB credentials
> - `prod`: Uses a full MongoDB connection URI

Using Docker, `.env` file is optional since default values are defined in [Docker Compose file](./docker-compose.yml).

For local development without Docker, MongoDB credentials must be provided. Other parameters
have sensible
defaults.

In **production**, the application uses a single MongoDB connection URI (`MONGO_URI`), following cloud provider best
practices (e.g., MongoDB Atlas).

Rename  `.env.example` to `.env` and modify variables according to your needs.

| Variable               | For Docker                           | For Local Development                | For Production               | Description            |
|------------------------|--------------------------------------|--------------------------------------|------------------------------|------------------------|
| PORT                   | Optional (Default: "8080")           | Optional (Default: "8080")           | Auto-set by platform         | Server port            |
| SPRING_PROFILES_ACTIVE | Default: "dev"            | Optional (Default: "dev")            | **Required** (set to "prod") | Active Spring profile  |
| MONGO_USER             | Optional (Default: "admin")          | **Required**                         | —                            | MongoDB username       |
| MONGO_PASSWORD         | Optional (Default: "dbpassword")     | **Required**                         | —                            | MongoDB password       |
| MONGO_DATABASE         | Optional (Default: "urlshortenerdb") | Optional (Default: "urlshortenerdb") | —                            | MongoDB database name  |
| MONGO_URI              | —                                    | —                                    | **Required**                 | MongoDB connection URI |


## Usage

### **Starting**

For the fastest setup, it is recommended to use Docker Compose to start the app and its services:

```bash
# Run docker compose command to start all services
$ docker compose up -d --build
```

Access the application at `http://localhost:8080/docs` (or the port you configured).

### **Routes**

| Route          | HTTP Method | Params                                                          | Description                            | Auth Method |
|----------------|-------------|-----------------------------------------------------------------|----------------------------------------|-------------|
| `/docs`        | GET         | -                                                               | Swagger documentation                  | None        |
| `/shorten`     | POST        | Body with `originalUrl` and _optional_ `expirationDate` | Create a short URL from a long URL     | None        |
| `/{shortCode}` | GET         | **Path Parameters:** `shortCode`                                              | Redirects to the original URL if valid | None        |

#### Requests

- `POST /shorten`

Request body:

```json
{
  "originalUrl": "https://example.com/a/very/long/url",
  "expirationDate": "2025-11-12" // optional, format: yyyy-MM-dd
}
```

[⬆ Back to the top](#-url-shortener-api)
