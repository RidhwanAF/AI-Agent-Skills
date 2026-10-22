#!/usr/bin/env bash
# ==============================================================================
# AI-Engineer-Skills One-Click macOS Launcher (Finder Double-Clickable)
# Adaptively locates a verified Python 3 (3.8+) executable across standard paths
# and version managers, bypassing broken environment shims, and launches installer.
# ==============================================================================

set -e

cd "$(dirname "$0")"

echo "========================================================================"
echo "   AI-Engineer-Skills One-Click Installer"
echo "========================================================================"

# Dynamically locate a verified, working Python 3.8+ executable for ANY user/system
find_working_python() {
    local candidates=()

    # 1. Inspect standalone pyenv versions (bypassing broken shims)
    for p in "$HOME"/.pyenv/versions/*/bin/python3; do
        [ -x "$p" ] && candidates+=("$p")
    done

    # 2. Inspect asdf / conda / miniforge environments
    for p in "$HOME"/.asdf/installs/python/*/bin/python3; do
        [ -x "$p" ] && candidates+=("$p")
    done
    for p in "$HOME"/{miniforge3,miniconda3,anaconda3}/bin/python3; do
        [ -x "$p" ] && candidates+=("$p")
    done

    # 3. Standard OS and Homebrew locations
    candidates+=(
        "/opt/homebrew/bin/python3"
        "/usr/local/bin/python3"
        "/usr/bin/python3"
    )

    # 4. PATH lookup (which -a inspects all candidate directories in PATH)
    for p in $(which -a python3 2>/dev/null || true); do
        candidates+=("$p")
    done
    for p in $(which -a python 2>/dev/null || true); do
        candidates+=("$p")
    done

    # Test each candidate by actually executing code (guarantees not a broken shim)
    for cand in "${candidates[@]}"; do
        if [ -x "$cand" ] 2>/dev/null; then
            if "$cand" -c "import sys; assert sys.version_info >= (3, 8)" >/dev/null 2>&1; then
                echo "$cand"
                return 0
            fi
        fi
    done

    return 1
}

PY_CMD="$(find_working_python || true)"

if [ -z "$PY_CMD" ]; then
    echo "⚠️  No working Python 3 (3.8+) installation was detected on your system."
    
    if command -v brew >/dev/null 2>&1; then
        echo "📦 Homebrew detected. Installing Python 3 via brew..."
        brew install python3
        PY_CMD="$(find_working_python || true)"
    fi

    if [ -z "$PY_CMD" ]; then
        echo "Attempting to install Xcode Command Line Tools for Python..."
        xcode-select --install || true
        echo "Please complete the installation and rerun this script."
        read -p "Press [Enter] to exit..." || true
        exit 1
    fi
fi

# Route directly to the installer subfolder using verified Python
"$PY_CMD" installer/install.py "$@"

echo ""
read -p "Installation finished. Press [Enter] to close this window..." || true
