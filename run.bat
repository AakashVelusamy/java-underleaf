@echo off
REM =====================================================
REM Run underLeaf (JavaFX Steganography App)
REM =====================================================

REM Set JavaFX SDK path
set JAVAFX_LIB="C:\Users\Aakash V\javafx-sdk-21.0.8\lib"

REM Set bin folder path (compiled classes)
set BIN_DIR=bin

REM Run the application
java --module-path %JAVAFX_LIB% --add-modules javafx.controls,javafx.fxml -cp %BIN_DIR% Steganography.Main

pause
