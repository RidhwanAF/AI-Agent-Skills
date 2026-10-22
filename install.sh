#!/usr/bin/env bash
# ==============================================================================
# AI-Engineer-Skills One-Click Terminal Launcher (Linux / macOS / WSL)
# Automatically locates a working Python 3 (3.8+) executable, installs if missing,
# and routes to the installer in installer/install.py
# ==============================================================================

set -e

cd "$(dirname "$0")"

# Locate a verified, working Python 3.8+ executable (bypasses broken shims like pyenv)
find_working_python() {
    local candidates=()

    # 1. Any installed pyenv standalone versions (bypassing broken pyenv shims)
    for p in "$HOME"/.pyenv/versions/*/bin/python3; do
        [ -x "$p" ] && candidates+=("$p")
    done

    # 2. Standard system & package manager paths
    candidates+=(
        "/opt/homebrew/bin/python3"
        "/usr/local/bin/python3"
        "/usr/bin/python3"
    )

    # 3. Path lookup as fallback (tested for validity)
    local which_py3
    which_py3="$(which python3 2>/dev/null || true)"
    [ -n "$which_py3" ] && candidates+=("$which_py3")

    local which_py
    which_py="$(which python 2>/dev/null || true)"
    [ -n "$which_py" ] && candidates+=("$which_py")

    # Test each candidate by actually executing code
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
    echo "⚠️  No working Python 3 (3.8+) was detected on your system."
    
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

    PY_CMD="$(find_working_python || true)"
fi

if [ -z "$PY_CMD" ]; then
    echo "❌ Error: Could not locate a working Python 3 executable."
    exit 1
fi

# Route directly to the installer subfolder
exec "$PY_CMD" installer/install.py "$@"
