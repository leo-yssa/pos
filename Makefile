.PHONY: help backend-up backend-down backend-logs backend-run backend-run-docker backend-run-docker-bg backend-logs-api backend-stop backend-test \
        frontend-compile frontend-run frontend-clean clean

SHELL := /bin/sh

JAVA ?= java
JAVAC ?= javac
DOCKER ?= docker

POS_API_BASE_URL ?= http://localhost:8080

DB_HOST ?= localhost
DB_PORT ?= 3306
DB_NAME ?= judang
DB_USER ?= pos
DB_PASSWORD ?= pos

BACKEND_PORT ?= 8080
BACKEND_NETWORK ?= pos-api_default
M2_CACHE ?= $(HOME)/.m2

FRONTEND_OUT := build/frontend/classes
FRONTEND_ENCODING ?= UTF-8

FRONTEND_CP := $(FRONTEND_OUT):frontend/lib/DateChooser.jar:frontend/lib/jfreechart-1.0.19.jar:frontend/lib/jcommon-1.0.23.jar

FRONTEND_SOURCES := \
  $(wildcard frontend/src/GW_POS/Api/*.java) \
  $(wildcard frontend/src/GW_POS/Database/*.java) \
  $(wildcard frontend/src/GW_POS/Frame/*.java) \
  $(wildcard frontend/src/GW_POS/Product/*.java) \
  $(wildcard frontend/src/GW_POS/Panel/*.java) \
  $(wildcard frontend/src/GW_POS/Interfaces/*.java) \
  $(wildcard frontend/src/GW_POS/Main/*.java)

help:
	@echo ""
	@echo "Targets:"
	@echo "  make backend-up        # start MySQL (docker)"
	@echo "  make backend-run       # run Spring Boot API"
	@echo "  make backend-run-docker # run API using docker maven (no local mvn)"
	@echo "  make backend-run-docker-bg # run API in background (docker)"
	@echo "  make backend-logs-api  # tail API logs (docker)"
	@echo "  make frontend-run      # compile & run Swing POS"
	@echo "  make clean             # remove build outputs"
	@echo ""
	@echo "Config (override with VAR=...):"
	@echo "  POS_API_BASE_URL=$(POS_API_BASE_URL)"
	@echo "  DB_HOST=$(DB_HOST) DB_PORT=$(DB_PORT) DB_NAME=$(DB_NAME) DB_USER=$(DB_USER)"
	@echo "  FRONTEND_ENCODING=$(FRONTEND_ENCODING)"
	@echo ""

backend-up:
	cd backend/pos-api && docker compose up -d

backend-down:
	cd backend/pos-api && docker compose down

backend-logs:
	cd backend/pos-api && docker compose logs -f --tail=200

backend-run:
	@command -v mvn >/dev/null 2>&1 && ( \
		cd backend/pos-api && \
		DB_HOST=$(DB_HOST) DB_PORT=$(DB_PORT) DB_NAME=$(DB_NAME) DB_USER=$(DB_USER) DB_PASSWORD=$(DB_PASSWORD) \
		PORT=$(BACKEND_PORT) mvn spring-boot:run \
	) || ( \
		$(MAKE) backend-run-docker \
	)

backend-run-docker:
	@echo "mvn not found. Running API using docker + maven image..."
	@echo "Note: backend-up must have created network: $(BACKEND_NETWORK)"
	@$(DOCKER) rm -f pos-api >/dev/null 2>&1 || true
	$(DOCKER) run --rm --name pos-api \
	  -p "$(BACKEND_PORT):8080" \
	  --network "$(BACKEND_NETWORK)" \
	  -e PORT=8080 \
	  -e DB_HOST=mysql -e DB_PORT=$(DB_PORT) -e DB_NAME=$(DB_NAME) -e DB_USER=$(DB_USER) -e DB_PASSWORD=$(DB_PASSWORD) \
	  -v "$(PWD)/backend/pos-api:/workspace" \
	  -v "$(M2_CACHE):/root/.m2" \
	  -w /workspace \
	  maven:3.9-eclipse-temurin-17 \
	  mvn spring-boot:run

backend-run-docker-bg:
	@echo "Running API in background using docker..."
	@echo "Note: backend-up must have created network: $(BACKEND_NETWORK)"
	@$(DOCKER) rm -f pos-api >/dev/null 2>&1 || true
	$(DOCKER) run -d --name pos-api \
	  -p "$(BACKEND_PORT):8080" \
	  --network "$(BACKEND_NETWORK)" \
	  -e PORT=8080 \
	  -e DB_HOST=mysql -e DB_PORT=$(DB_PORT) -e DB_NAME=$(DB_NAME) -e DB_USER=$(DB_USER) -e DB_PASSWORD=$(DB_PASSWORD) \
	  -v "$(PWD)/backend/pos-api:/workspace" \
	  -v "$(M2_CACHE):/root/.m2" \
	  -w /workspace \
	  maven:3.9-eclipse-temurin-17 \
	  mvn spring-boot:run

backend-logs-api:
	$(DOCKER) logs -f --tail=200 pos-api

backend-stop:
	@$(DOCKER) rm -f pos-api >/dev/null 2>&1 || true

backend-test:
	cd backend/pos-api && mvn test

frontend-compile:
	mkdir -p "$(FRONTEND_OUT)"
	$(JAVAC) -encoding "$(FRONTEND_ENCODING)" -cp "$(FRONTEND_CP)" $(FRONTEND_SOURCES) -d "$(FRONTEND_OUT)"
	@mkdir -p "$(FRONTEND_OUT)/GW_POS"
	@cp -R "frontend/src/GW_POS/Images" "$(FRONTEND_OUT)/GW_POS/" 2>/dev/null || true

frontend-run: frontend-compile
	$(JAVA) -DPOS_API_BASE_URL="$(POS_API_BASE_URL)" -cp "$(FRONTEND_CP)" GW_POS.Main.Main

frontend-clean:
	rm -rf build/frontend

clean: frontend-clean
	rm -rf build

