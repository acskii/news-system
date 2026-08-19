ifneq (,$(wildcard ./.env))
    include .env
    export
endif

# OS-specific configurations for Windows and Linux
ifeq ($(OS),Windows_NT)
    PYTHON := python
    VENV_BIN := Scripts
    GRADLEW := gradlew.bat
    CHECK_VENV = if not exist .venv $(PYTHON) -m venv .venv
else
    PYTHON := python3
    VENV_BIN := bin
    GRADLEW := ./gradlew
    CHECK_VENV = [ -d .venv ] || $(PYTHON) -m venv .venv
endif

.PHONY: help
help: ## Display this help screen
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n\nTargets:\n"} /^[a-zA-Z0-9_\/-]+:.*?##/ { printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2 }' $(MAKEFILE_LIST)

# Setup commands
.PHONY: run/database
run/database: ## Run Docker Compose
	docker compose up -d

.PHONY: install/analyser
install/analyser: ## Install dependencies for data analyser application
	cd applications/analyser && $(CHECK_VENV) && .venv/$(VENV_BIN)/pip install -r requirements.txt

.PHONY: install/dashboard
install/dashboard: ## Install dependencies for dashboard web application
	cd applications/dashboard && $(CHECK_VENV) && .venv/$(VENV_BIN)/pip install -r requirements.txt
	
.PHONY: build/collector
build/collector: ## Build JAR file for data collector application
	$(GRADLEW) :applications:collector:build

# Run commands

.PHONY: run/collector
run/collector: ## Run data collector
	$(GRADLEW) :applications:collector:bootRun

.PHONY: run/analyser
run/analyser: export PYTHONPATH := ../..
run/analyser: ## Run data analyser
	cd applications/analyser && .venv/$(VENV_BIN)/python app.py

.PHONY: run/dashboard
run/dashboard: export PYTHONPATH := ../..
run/dashboard: ## Run dashboard
	cd applications/dashboard && .venv/$(VENV_BIN)/python app.py