# ==============================================================================
# AI-Engineer-Skills One-Click Windows PowerShell Launcher
# Checks for Python 3, installs via winget if missing,
# and routes to the installer in installer\install.py
# ==============================================================================

[CmdletBinding()]
param(
    [switch]$All,
    [string]$Agents,
    [switch]$AutoInstallAndroidCli,
    [switch]$SkipAndroidCli,
    [switch]$DryRun,
    [switch]$NonInteractive
)

$ErrorActionPreference = "Stop"

Set-Location -Path $PSScriptRoot

Write-Host "========================================================================" -ForegroundColor Cyan
Write-Host "   AI-Engineer-Skills One-Click PowerShell Installer" -ForegroundColor Cyan
Write-Host "========================================================================" -ForegroundColor Cyan

# Check for Python
$pythonCmd = $null
if (Get-Command python -ErrorAction SilentlyContinue) {
    $pythonCmd = "python"
} elseif (Get-Command py -ErrorAction SilentlyContinue) {
    $pythonCmd = "py -3"
}

if (-not $pythonCmd) {
    Write-Host "[!] Python 3 was not detected on your system." -ForegroundColor Yellow
    if (Get-Command winget -ErrorAction SilentlyContinue) {
        Write-Host "[*] Attempting to install Python 3 using winget..." -ForegroundColor Cyan
        try {
            winget install -e --id Python.Python.3.12 --accept-package-agreements --accept-source-agreements
            $pythonCmd = "python"
        } catch {
            Write-Host "[x] Automated installation via winget failed." -ForegroundColor Red
            Write-Host "Please download and install Python from https://www.python.org/downloads/"
            Read-Host "Press [Enter] to exit..."
            exit 1
        }
    } else {
        Write-Host "[x] Please download and install Python 3 from https://www.python.org/downloads/" -ForegroundColor Red
        Read-Host "Press [Enter] to exit..."
        exit 1
    }
}

# Forward parameters to installer/install.py
$pyArgs = @()
if ($All) { $pyArgs += "--all" }
if ($Agents) { $pyArgs += "--agents"; $pyArgs += $Agents }
if ($AutoInstallAndroidCli) { $pyArgs += "--auto-install-android-cli" }
if ($SkipAndroidCli) { $pyArgs += "--skip-android-cli" }
if ($DryRun) { $pyArgs += "--dry-run" }
if ($NonInteractive) { $pyArgs += "--non-interactive" }

Write-Host "[*] Executing installer\install.py..." -ForegroundColor Cyan
& python installer\install.py @pyArgs

Write-Host "`nInstallation completed." -ForegroundColor Green
