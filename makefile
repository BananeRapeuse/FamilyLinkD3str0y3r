# Makefile pour le projet FamilyLinkDestroyer

# Variables
APP_NAME = FamilyLinkDestroyer
BUILD_DIR = app/build
GRADLE_CMD = ./gradlew

# Cibles par défaut
all: build

# Cible pour construire l'application
build:
	@echo "Building $(APP_NAME)..."
	$(GRADLE_CMD) build

# Cible pour nettoyer le projet
clean:
	@echo "Cleaning $(APP_NAME)..."
	$(GRADLE_CMD) clean

# Cible pour exécuter l'application
run:
	@echo "Running $(APP_NAME)..."
	$(GRADLE_CMD) installDebug

# Cible pour installer l'application sur un appareil connecté
install:
	@echo "Installing $(APP_NAME) on the connected device..."
	$(GRADLE_CMD) installDebug

# Cible pour les tests (optionnelle)
test:
	@echo "Running tests for $(APP_NAME)..."
	$(GRADLE_CMD) test

.PHONY: all build clean run install test
