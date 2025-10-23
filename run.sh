#!/bin/bash
# =====================================================
# Run underLeaf (JavaFX Steganography App)
# =====================================================

# Set JavaFX SDK path
JAVAFX_LIB="/home/cslinux/javafx-sdk-21.0.9/lib"

# Set bin folder path (compiled classes)
BIN_DIR="bin"

# Run the application
java --module-path "$JAVAFX_LIB" --add-modules javafx.controls,javafx.fxml -cp "$BIN_DIR" Steganography.Main

read -p "Press Enter to continue..."
