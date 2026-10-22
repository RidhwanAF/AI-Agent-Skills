#!/usr/bin/env bash
# ==============================================================================
# AI-Engineer-Skills One-Click macOS Launcher (Finder Double-Clickable)
# Automatically checks for Python 3, installs via brew if missing,
# and routes to the installer in installer/install.py
# ==============================================================================

set -e

cd "$(dirname "$0")"

echo "========================================================================"
echo "   AI-Engineer-Skills One-Click Installer"
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

# Route directly to the installer subfolder
python3 installer/install.py "$@"

echo ""
read -p "Installation finished. Press [Enter] to close this window..."
