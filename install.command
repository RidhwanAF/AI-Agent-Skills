#!/usr/bin/env bash
# ==============================================================================
# AI-Engineer-Skills Double-Clickable Launcher (macOS Finder)
# Automatically checks for Python 3, installs it if missing, and runs install.py
# ==============================================================================

set -e

# Change directory to this script's directory
cd "$(dirname "$0")"

echo "========================================================================"
echo "   AI-Engineer-Skills Installer Launcher"
echo "========================================================================"

# Check if python3 is installed
if ! command -v python3 >/dev/null 2>&1; then
    echo "⚠️  Python 3 was not detected on your system."
    
    if [ "$(uname -s)" = "Darwin" ]; then
        if command -v brew >/dev/null 2>&1; then
            echo "📦 Homebrew detected. Installing Python 3..."
            brew install python3
        else
            echo "Attempting to install Xcode Command Line Tools for Python..."
            xcode-select --install || true
            echo "Please complete the developer tools installation and rerun this script."
            read -p "Press [Enter] to exit..."
            exit 1
        fi
    else
        echo "Please install Python 3 using your system package manager."
        read -p "Press [Enter] to exit..."
        exit 1
    fi
fi

# Run the Python installer
python3 install.py "$@"

echo ""
read -p "Installation finished. Press [Enter] to close this window..."
