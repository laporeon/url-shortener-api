.DEFAULT_GOAL := help

MVN     := ./mvnw
SHELL   := /bin/bash
COMPOSE ?= docker compose

.PHONY: help env run test build dev down logs

help: ## Show available commands
	@awk 'BEGIN {FS = ":.*## "} /^[a-zA-Z_-]+:.*## / {printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

env: ## Create .env from .env.example if missing
	@test -f .env || cp .env.example .env

run: ## Run the app locally (without Docker)
	$(MVN) spring-boot:run

test: ## Run tests (optional: TEST=ShortUrlServiceTest)
	$(MVN) test $(if $(TEST),-Dtest=$(TEST),)

build: ## Package the application (skips tests)
	$(MVN) clean package -DskipTests

dev: env ## Build and start containers in the background
	$(COMPOSE) up -d --build

down: ## Stop and remove containers
	$(COMPOSE) down

logs: ## Follow container logs
	$(COMPOSE) logs -f