@echo off
REM ==============================================================================
REM AI-Engineer-Skills Windows Launcher (Command Prompt / File Explorer)
REM Checks for Python 3, attempts automated installation via winget if missing,
REM and executes the universal installer.
REM ==============================================================================

setlocal enabledelayedexpansion

cd /d "%~dp0"

echo ========================================================================
echo    AI-Engineer-Skills Windows Installer Launcher
echo ========================================================================

REM Check if python is available
set "PY_CMD="

python --version >nul 2>&1
if %ERRORLEVEL% equ 0 (
    set "PY_CMD=python"
) else (
    py -3 --version >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        set "PY_CMD=py -3"
    )
)

REM If Python is not installed, attempt winget installation
if "%PY_CMD%"=="" (
    echo [!] Python 3 was not detected on your Windows system.
    echo [*] Checking Windows Package Manager (winget)...
    
    where winget >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        echo [*] Installing Python 3 via winget...
        winget install -e --id Python.Python.3.12 --accept-package-agreements --accept-source-agreements
        if %ERRORLEVEL% equ 0 (
            echo [v] Python 3 installed successfully. Please restart this script if PATH has not refreshed.
            set "PY_CMD=python"
        ) else (
            echo [x] Automated winget installation failed.
            echo Please install Python 3 manually from https://www.python.org/downloads/
            pause
            exit /b 1
        )
    ) else (
        echo [x] winget not found. Please install Python 3 from https://www.python.org/downloads/
        pause
        exit /b 1
    )
)

echo [*] Launching Python installer with: %PY_CMD%
%PY_CMD% install.py %*

if %ERRORLEVEL% neq 0 (
    echo [x] Installer exited with an error code.
)

echo.
echo Press any key to close this window...
pause >nul
