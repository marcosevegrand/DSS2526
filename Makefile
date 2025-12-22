# Makefile for Restaurant Management System
# Java compilation and execution

# Compiler and flags
JAVAC = javac
JAVA = java
SRC_DIR = src/main/java
BUILD_DIR = build
MAIN_CLASS = restaurante.Main

# Find all Java source files
SOURCES = $(shell find $(SRC_DIR) -name "*.java")

# Compilation target
.PHONY: all
all: compile

# Compile all Java files
compile:
	@echo "Compiling Java sources..."
	@mkdir -p $(BUILD_DIR)
	$(JAVAC) -d $(BUILD_DIR) -sourcepath $(SRC_DIR) $(SOURCES)
	@echo "Compilation complete!"

# Run the application
run: compile
	@echo "Running application..."
	$(JAVA) -cp $(BUILD_DIR) $(MAIN_CLASS)

# Clean build directory
clean:
	@echo "Cleaning build directory..."
	@rm -rf $(BUILD_DIR)
	@echo "Clean complete!"

# Rebuild (clean + compile)
rebuild: clean compile

# Help
help:
	@echo "Available targets:"
	@echo "  make compile  - Compile all Java sources"
	@echo "  make run      - Compile and run the application"
	@echo "  make clean    - Remove compiled files"
	@echo "  make rebuild  - Clean and recompile"
	@echo "  make help     - Show this help message"
