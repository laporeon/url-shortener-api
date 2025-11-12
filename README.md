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

This is a REST API to shorten URLs. It accepts long URLs and optionally an expiration date in `yyyy-MM-dd` format to return a shortened URL. It
supports redirection to the original URL if the short URL is valid and not expired. The API includes Swagger
documentation and can be deployed easily using Docker Compose.

**Key features:**

- Input validation for shortening URL requests.
- Generates the base URL dynamically from the incoming HTTP request.
- Calculates expiration date for shortened URLs, defaulting to 24 hours if none provided.
- Redirects from short URL to the original URL.
- Handles expired and non-existent short URLs properly.
- Swagger UI documentation for all endpoints.

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

Using Docker, `.env` file is optional since default values exist in [Docker Compose file](./docker-compose.yml). For
local development **without Docker**, you must
set `MONGO_USER` and `MONGO_PASSWORD` environment variables for MongoDB authentication. Other parameters have sensible
defaults.

For production or deployments on platforms like Render, AWS, or any service handling the deployment, you should set the
`MONGO_URI` environment variable in the deployment environment to ensure a proper MongoDB connection.

Rename  `.env.example` to `.env` and modify variables according to your needs.

| Variable       | For Docker | For Local Development | Default                         | Description                                                                |
| -------------- | ---------- | --------------------- | ------------------------------- | -------------------------------------------------------------------------- |
| SERVER_PORT    | Optional   | Optional              | 8080                            | Server port                                                                |
| MONGO_USER     | Optional   | **Required**              | -                               | MongoDB username                                                           |
| MONGO_PASSWORD | Optional   | **Required**              | -                               | MongoDB password                                                           |
| MONGO_URI      | Optional   | Optional              | Auto-built from other variables | Full MongoDB connection URI (set explicitly only in production/deployment) |

## Usage

### **Starting**

For the fastest setup, it is recommended to use Docker Compose to start the app and its services:

```bash
# Run docker compose command to start all services
$ docker compose up -d --build
```

Access the application at `http://localhost:8080/docs` (or the port you configured).

### **Routes**

| Route               | HTTP Method | Params                                                 | Description                              | Auth Method |
|---------------------|-------------|--------------------------------------------------------|------------------------------------------|-------------|
| `/docs`             | GET         | -                                                      | Swagger documentation                    | None        |
| `/shorten-url`      | POST        | Body with `originalUrl` and `expirationDate`(optional) | Create a short URL from a long URL                        | None        |
| `/{shortCode}`      | GET         | Path: `:shortCode`                                     | Redirects to the original URL if valid   | None        |

#### Requests

- `POST /shorten-url`

Request body:

```json
{
  "originalUrl": "https://example.com/a/very/long/url",
  "expirationDate": "2025-11-12"// optional, format: yyyy-MM-dd
}
```

[⬆ Back to the top](#-url-shortener-api)