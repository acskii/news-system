ifneq (,$(wildcard ./.env))
    include .env
    export
endif

# default: help

# .PHONY: help
# help:
# 	# Usage:
# 	@sed -n '/^\([a-z][^:]*\).*/s//    make \1/p' $(MAKEFILE_LIST)

# Setup commands
.PHONY: install/analyser
install/analyser:
		cd applications/analyser; \
		source .venv/bin/activate; \
		pip install -r requirements.txt;

.PHONY: install/dashboard
install/dashboard:
		cd applications/dashboard; \
		source .venv/bin/activate; \
		pip install -r requirements.txt;

.PHONY: build/collector
build/collector:
	./gradlew :applications:collector:build

# Run commands

.PHONY: run/collector
run/collector:
	./gradlew :applications:collector:bootRun

.PHONY: run/analyser
run/analyser: 
		cd applications/analyser; \
		source .venv/bin/activate; \
		PYTHONPATH=../.. python app.py

.PHONY: run/dashboard
run/dashboard: 
		cd applications/dashboard; \
		source .venv/bin/activate; \
		PYTHONPATH=../.. python app.py