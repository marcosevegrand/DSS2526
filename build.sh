#!/bin/bash
# Build script for Restaurant Management System

echo "================================"
echo "Restaurant System - Build Script"
echo "================================"
echo ""

# Check if Java is installed
if ! command -v javac &> /dev/null; then
    echo "ERROR: Java compiler (javac) not found!"
    echo "Please install Java JDK 11 or higher."
    exit 1
fi

# Display Java version
echo "Using Java version:"
java -version
echo ""

# Create build directory
echo "Creating build directory..."
mkdir -p build

# Compile all Java files
echo "Compiling Java sources..."
find src/main/java -name "*.java" > sources.txt
javac -d build @sources.txt

# Check compilation status
if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Compilation successful!"
    echo ""
    echo "To run the application:"
    echo "  java -cp build restaurante.Main"
    echo ""
    echo "Or use: ./run.sh"
else
    echo ""
    echo "✗ Compilation failed!"
    exit 1
fi

# Clean up
rm sources.txt
