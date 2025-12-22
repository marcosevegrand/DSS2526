#!/bin/bash
# Run script for Restaurant Management System

# Check if build directory exists
if [ ! -d "build" ]; then
    echo "Build directory not found. Compiling first..."
    ./build.sh
    if [ $? -ne 0 ]; then
        exit 1
    fi
fi

echo "Starting Restaurant Management System..."
echo ""
java -cp build restaurante.Main
