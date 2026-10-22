#!/usr/bin/env bash
# ==============================================================================
# AI-Engineer-Skills One-Click Terminal Launcher (Linux / macOS / WSL)
# Automatically checks for Python 3, installs if missing,
# and routes to the installer in installer/install.py
# ==============================================================================

set -e

cd "$(dirname "$0")"

# Check if python3 is installed
if ! command -v python3 >/dev/null 2>&1; then
    echo "⚠️  Python 3 was not detected on your system."
    
    if [ "$(uname -s)" = "Darwin" ]; then
        if command -v brew >/dev/null 2>&1; then
            echo "📦 Homebrew detected. Installing Python 3 via brew..."
            brew install python3
        else
            echo "❌ Homebrew is not installed. Please install Python 3 via Xcode Command Line Tools or https://python.org"
            exit 1
        fi
    elif [ -f /etc/debian_version ]; then
        echo "📦 Debian/Ubuntu detected. Installing python3 via apt..."
        sudo apt update && sudo apt install -y python3
    elif [ -f /etc/redhat-release ]; then
        echo "📦 RHEL/Fedora detected. Installing python3 via dnf..."
        sudo dnf install -y python3
    elif [ -f /etc/arch-release ]; then
        echo "📦 Arch Linux detected. Installing python via pacman..."
        sudo pacman -S --noconfirm python
    else
        echo "❌ Unsupported OS for automatic Python installation. Please install Python 3 manually."
        exit 1
    fi
fi

# Route directly to the installer subfolder
exec python3 installer/install.py "$@"
