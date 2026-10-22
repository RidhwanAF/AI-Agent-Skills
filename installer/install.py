#!/usr/bin/env python3
"""
AI-Engineer-Skills Universal Installer
Interactive installation, tool verification, and synchronization script for AI coding agent skills.
Cross-platform support: Windows, macOS, and Linux.
Supports: Google Gemini / Antigravity, Claude Code, Cursor, Codex, Copilot, Windsurf, Roo Code / Cline.
"""

import sys
import os
import stat
import shutil
import platform
import subprocess
import json
import argparse
from pathlib import Path

# ANSI Color formatting
class Style:
    RESET = "\033[0m"
    BOLD = "\033[1m"
    DIM = "\033[2m"
    GREEN = "\033[32m"
    CYAN = "\033[36m"
    YELLOW = "\033[33m"
    RED = "\033[31m"
    MAGENTA = "\033[35m"

def print_banner():
    art = r"""========================================================================
  ____      _     _____            _                    _     ____   _     _ _ _      
 |  _ \    / \   |  ___|          / \   __ _  ___ _ __ | |_  / ___| | |   (_) | |___  
 | |_) |  / _ \  | |_    _____   / _ \ / _` |/ _ \ '_ \| __| \___ \ | |/ / | | / __| 
 |  _ <  / ___ \ |  _|  |_____| / ___ \ (_| |  __/ | | | |_   ___) ||   <  | | | \__ \ 
 |_| \_\/_/   \_\|_|           /_/   \_\__, |\___|_| |_|\__| |____/ |_|\_\_|_|_|___/ 
                                       |___/                                        
========================================================================"""
    print(f"{Style.CYAN}{Style.BOLD}{art}{Style.RESET}")
    print(f"{Style.BOLD}   RAF - Agent Skills • Connected Skills • Zero Code Slop{Style.RESET}\n")

# In installer/ subfolder, REPO_ROOT is the parent folder
REPO_ROOT = Path(__file__).resolve().parent.parent

# Mapping of all 15 modular managed skills to source directories
MANAGED_SKILLS = {
    # Core Orchestration
    "engineer-mode": REPO_ROOT / "engineer-mode",
    # Modular Android Suite
    "android-architecture": REPO_ROOT / "android-skills" / "android-architecture",
    "compose-ui": REPO_ROOT / "android-skills" / "compose-ui",
    "compose-maplibre": REPO_ROOT / "android-skills" / "compose-maplibre",
    "android-testing": REPO_ROOT / "android-skills" / "android-testing",
    "gradle-build-hygiene": REPO_ROOT / "android-skills" / "gradle-build-hygiene",
    # Modular React TS Suite
    "react-ts-architecture": REPO_ROOT / "react-ts-skills" / "react-ts-architecture",
    "react-ui-components": REPO_ROOT / "react-ts-skills" / "react-ui-components",
    "react-state-management": REPO_ROOT / "react-ts-skills" / "react-state-management",
    "react-testing": REPO_ROOT / "react-ts-skills" / "react-testing",
    # Diagnostics, ADR, Design & Changelogs
    "error-detective": REPO_ROOT / "diagnostic-skills" / "error-detective",
    "adr-analyze": REPO_ROOT / "diagnostic-skills" / "adr-analyze",
    "app-design": REPO_ROOT / "diagnostic-skills" / "app-design",
    "changelog-recorder": REPO_ROOT / "diagnostic-skills" / "changelog-recorder",
    # Version Control
    "git-assistant": REPO_ROOT / "git-skills" / "git-assistant",
}

# Legacy skills from previous standalone installations that must be cleaned up
LEGACY_SKILLS = [
    "android-expert",        # Replaced by modular Android skills
    "react-ts-expert",       # Replaced by modular React TS skills
    "android-cli",           # Custom skill removed in favor of routing to official Google android-cli
    "android-clean-architecture",
    "android-data-layer",
    "android-project-onboarding-architect",
    "compose-ui-preview-design",
    "agent-audit-workflow",
]

HOME = Path.home()

# Target AI Agents configuration (works across Windows, macOS, and Linux)
SUPPORTED_AGENTS = {
    "1": {
        "id": "gemini",
        "name": "Google Gemini / Antigravity",
        "dirs": [
            HOME / ".agents" / "skills",
            HOME / ".gemini" / "config" / "skills",
        ],
    },
    "2": {
        "id": "claude",
        "name": "Claude Code (Anthropic)",
        "dirs": [
            HOME / ".claude" / "skills",
        ],
    },
    "3": {
        "id": "cursor",
        "name": "Cursor",
        "dirs": [
            HOME / ".cursor" / "skills",
        ],
    },
    "4": {
        "id": "codex",
        "name": "OpenAI Codex CLI",
        "dirs": [
            HOME / ".codex" / "skills",
        ],
    },
    "5": {
        "id": "copilot",
        "name": "GitHub Copilot (CLI / Agent)",
        "dirs": [
            HOME / ".copilot" / "skills",
        ],
    },
    "6": {
        "id": "windsurf",
        "name": "Windsurf (Codeium)",
        "dirs": [
            HOME / ".codeium" / "windsurf" / "skills",
        ],
    },
    "7": {
        "id": "roo_cline",
        "name": "Roo Code / Cline",
        "dirs": [
            HOME / ".roo" / "skills",
            HOME / ".cline" / "skills",
        ],
    },
}

MANIFEST_PATH = HOME / ".ai-engineer-skills-manifest.json"

def check_python_environment():
    major, minor = sys.version_info.major, sys.version_info.minor
    if major < 3 or (major == 3 and minor < 8):
        print(f"{Style.RED}❌ Error: Python 3.8+ required. Current version is {major}.{minor}{Style.RESET}")
        sys.exit(1)
    print(f"{Style.GREEN}✓ Python Environment Verified:{Style.RESET} Python {platform.python_version()} on {platform.system()} ({platform.machine()})")

def get_official_android_cli_install_cmd():
    sys_name = platform.system().lower()
    machine = platform.machine().lower()

    if sys_name == "darwin":
        if "arm" in machine or "aarch64" in machine:
            return "curl -fsSL https://dl.google.com/android/cli/latest/darwin_arm64/install.sh | bash"
        else:
            return "curl -fsSL https://dl.google.com/android/cli/latest/darwin_x86_64/install.sh | bash"
    elif sys_name == "linux":
        if "arm" in machine or "aarch64" in machine:
            return "curl -fsSL https://dl.google.com/android/cli/latest/linux_arm64/install.sh | bash"
        else:
            return "curl -fsSL https://dl.google.com/android/cli/latest/linux_x86_64/install.sh | bash"
    elif sys_name == "windows":
        return 'curl -fsSL https://dl.google.com/android/cli/latest/windows_x86_64/install.cmd -o "%TEMP%\\i.cmd" && "%TEMP%\\i.cmd"'
    return None

def check_and_setup_android_cli(non_interactive=False, auto_install=False):
    print(f"\n{Style.BOLD}🔧 Checking Required Tools: Google Android CLI{Style.RESET}")
    android_path = shutil.which("android")

    if android_path:
        print(f"{Style.GREEN}✓ Found Google Android CLI:{Style.RESET} {android_path}")
        
        # Run android update
        try:
            print(f"  Checking for updates via 'android update'...")
            res = subprocess.run(["android", "update"], capture_output=True, text=True, timeout=15)
            if res.returncode == 0:
                print(f"  {Style.GREEN}✓ android update completed successfully.{Style.RESET}")
            else:
                print(f"  {Style.DIM}android update note: {res.stdout.strip() or res.stderr.strip()}{Style.RESET}")
        except Exception as e:
            print(f"  {Style.DIM}(android update note: {e}){Style.RESET}")

        # Run android init
        try:
            print(f"  Running 'android init'...")
            res = subprocess.run(["android", "init"], capture_output=True, text=True, timeout=15)
            if res.returncode == 0:
                print(f"  {Style.GREEN}✓ android init completed successfully.{Style.RESET}")
            else:
                print(f"  {Style.DIM}android init note: {res.stdout.strip() or res.stderr.strip()}{Style.RESET}")
        except Exception as e:
            print(f"  {Style.DIM}(android init note: {e}){Style.RESET}")
        return True

    print(f"{Style.YELLOW}⚠️  Google Android CLI ('android') was not found on your PATH.{Style.RESET}")
    cmd = get_official_android_cli_install_cmd()
    
    if not cmd:
        print(f"  Please refer to official Android documentation to install the CLI tool.")
        return False

    print(f"  Official Google install command for your platform ({platform.system()} {platform.machine()}):")
    print(f"  {Style.CYAN}{cmd}{Style.RESET}\n")

    should_install = False
    if auto_install or non_interactive:
        should_install = True
    else:
        try:
            choice = input(f"Would you like to install Google Android CLI now? [Y/n]: ").strip().lower()
            should_install = choice in ("", "y", "yes")
        except (KeyboardInterrupt, EOFError):
            print("\nSkipping Android CLI installation.")
            return False

    if should_install:
        print(f"\n{Style.CYAN}Executing official installation script...{Style.RESET}")
        try:
            res = subprocess.run(cmd, shell=True, check=True)
            print(f"{Style.GREEN}✓ Google Android CLI installation finished.{Style.RESET}")
            if shutil.which("android"):
                subprocess.run(["android", "init"], check=False)
            return True
        except Exception as e:
            print(f"{Style.RED}❌ Installation failed: {e}{Style.RESET}")
            print("You can install it manually later using the command above.")
            return False
    else:
        print("Continuing without installing Android CLI. (Note: Skills will route to it when available).")
        return False

def prompt_agent_selection(non_interactive=False, selected_keys=None):
    if non_interactive and selected_keys is not None:
        if "all" in selected_keys or "a" in selected_keys:
            return list(SUPPORTED_AGENTS.keys())
        return [k for k in selected_keys if k in SUPPORTED_AGENTS]

    print(f"\n{Style.BOLD}🤖 Select AI Agents to install skills into:{Style.RESET}\n")
    for key, data in SUPPORTED_AGENTS.items():
        primary_dir = data["dirs"][0]
        exists_mark = f"{Style.GREEN}(configured){Style.RESET}" if primary_dir.exists() else f"{Style.DIM}(not configured){Style.RESET}"
        print(f"  [{Style.BOLD}{key}{Style.RESET}] {data['name']:<32} {Style.DIM}-> {primary_dir}{Style.RESET} {exists_mark}")
    print(f"  [{Style.BOLD}A{Style.RESET}] {Style.BOLD}All AI Agents{Style.RESET}\n")

    while True:
        try:
            raw_input = input(f"{Style.CYAN}Enter numbers (e.g. 1,3 or 1-4 or A for All) [Default: A]: {Style.RESET}").strip()
        except (KeyboardInterrupt, EOFError):
            print("\nInstallation aborted.")
            sys.exit(0)

        if not raw_input or raw_input.lower() in ("a", "all"):
            return list(SUPPORTED_AGENTS.keys())

        # Parse comma or space separated tokens
        tokens = raw_input.replace(",", " ").split()
        selected = set()
        for token in tokens:
            if "-" in token:
                parts = token.split("-")
                if len(parts) == 2 and parts[0].isdigit() and parts[1].isdigit():
                    for num in range(int(parts[0]), int(parts[1]) + 1):
                        if str(num) in SUPPORTED_AGENTS:
                            selected.add(str(num))
            elif token in SUPPORTED_AGENTS:
                selected.add(token)

        if selected:
            return sorted(list(selected))
        print(f"{Style.YELLOW}Invalid selection. Please try again.{Style.RESET}")

def safe_remove_path(path: Path) -> bool:
    """Safely removes a file or directory tree with permission overrides."""
    def handle_remove_readonly(func, p, exc):
        try:
            os.chmod(p, stat.S_IWRITE | stat.S_IWUSR)
            func(p)
        except Exception:
            pass

    try:
        if not path.exists():
            return True
        if path.is_dir():
            shutil.rmtree(path, onerror=handle_remove_readonly)
        else:
            path.unlink(missing_ok=True)
        return True
    except Exception as e:
        print(f"  {Style.DIM}(Note: Skipped locked/permission-restricted path: {path} - {e}){Style.RESET}")
        return False

def copy_skill(source_path: Path, target_dir: Path, skill_name: str) -> bool:
    dest_path = target_dir / skill_name
    try:
        safe_remove_path(dest_path)
        shutil.copytree(source_path, dest_path, dirs_exist_ok=True)
        return True
    except Exception as e:
        print(f"  {Style.YELLOW}⚠️  Could not copy {skill_name} into {dest_path}: {e}{Style.RESET}")
        return False

def cleanup_legacy_skills(target_dir: Path):
    removed = []
    for legacy_name in LEGACY_SKILLS:
        legacy_path = target_dir / legacy_name
        if legacy_path.exists():
            if safe_remove_path(legacy_path):
                removed.append(legacy_name)
    return removed

def remove_managed_skills(target_dir: Path):
    removed = []
    all_to_remove = list(MANAGED_SKILLS.keys()) + LEGACY_SKILLS
    for name in all_to_remove:
        path = target_dir / name
        if path.exists():
            if safe_remove_path(path):
                removed.append(name)
    return removed

def install_skills(selected_agent_keys, dry_run=False):
    print(f"\n{Style.BOLD}🚀 Installing Unified RAF - Agent Skills Suite...{Style.RESET}\n")
    
    selected_agent_ids = [SUPPORTED_AGENTS[k]["id"] for k in selected_agent_keys]
    
    # 1. Process selected agents: Clean legacy and install fresh skills
    for key in selected_agent_keys:
        agent = SUPPORTED_AGENTS[key]
        print(f"{Style.BOLD}📦 Agent: {agent['name']}{Style.RESET}")
        for target_dir in agent["dirs"]:
            if dry_run:
                print(f"  {Style.DIM}[Dry-Run] Would prepare {target_dir}{Style.RESET}")
                continue

            try:
                target_dir.mkdir(parents=True, exist_ok=True)
            except Exception as e:
                print(f"  {Style.DIM}(Could not create directory {target_dir}: {e}){Style.RESET}")
                continue
            
            # Clean up old legacy skills
            removed_legacy = cleanup_legacy_skills(target_dir)
            if removed_legacy:
                print(f"  {Style.YELLOW}↳ Cleaned up {len(removed_legacy)} legacy skill(s): {', '.join(removed_legacy)}{Style.RESET}")

            # Copy current modular managed skills
            installed_count = 0
            for skill_name, src_path in MANAGED_SKILLS.items():
                if not src_path.exists():
                    print(f"  {Style.RED}❌ Source missing: {src_path}{Style.RESET}")
                    continue
                if copy_skill(src_path, target_dir, skill_name):
                    installed_count += 1

            if installed_count > 0:
                print(f"  {Style.GREEN}✓ Installed {installed_count} managed modular skills into {target_dir}{Style.RESET}")

    # 2. Process unselected agents: Delete skills from their directories if present
    unselected_keys = [k for k in SUPPORTED_AGENTS.keys() if k not in selected_agent_keys]
    if unselected_keys:
        print(f"\n{Style.BOLD}🧹 Cleaning unselected agents (as requested)...{Style.RESET}")
        for key in unselected_keys:
            agent = SUPPORTED_AGENTS[key]
            for target_dir in agent["dirs"]:
                if target_dir.exists():
                    if dry_run:
                        print(f"  {Style.DIM}[Dry-Run] Would clean skills from unselected agent at {target_dir}{Style.RESET}")
                        continue
                    removed = remove_managed_skills(target_dir)
                    if removed:
                        print(f"  {Style.YELLOW}✓ Removed {len(removed)} skill(s) from unselected agent at {target_dir}{Style.RESET}")

    # 3. Update manifest file
    if not dry_run:
        try:
            manifest_data = {
                "version": "2.1.0",
                "active_agents": selected_agent_ids,
                "managed_skills": list(MANAGED_SKILLS.keys()),
            }
            with open(MANIFEST_PATH, "w", encoding="utf-8") as f:
                json.dump(manifest_data, f, indent=2)
        except Exception:
            pass

def print_summary(selected_agent_keys):
    print(f"\n{Style.GREEN}{Style.BOLD}========================================================================{Style.RESET}")
    print(f"{Style.GREEN}{Style.BOLD} 🎉 Installation & Synchronization Successfully Completed!{Style.RESET}")
    print(f"{Style.GREEN}{Style.BOLD}========================================================================{Style.RESET}\n")

    print(f"{Style.BOLD}Active Skills Installed ({len(MANAGED_SKILLS)} Modular Skills):{Style.RESET}")
    print(f"  • {Style.CYAN}/engineer-mode{Style.RESET}           ➔ Adaptive master orchestrator & playbook router")
    print(f"  • {Style.CYAN}/android-architecture{Style.RESET}   ➔ Clean Architecture, pure KMP-ready domain, Room/Retrofit")
    print(f"  • {Style.CYAN}/compose-ui{Style.RESET}              ➔ Modular Compose UI (never 1 file), modifier rule, haptics")
    print(f"  • {Style.CYAN}/compose-maplibre{Style.RESET}        ➔ MapLibre in Compose, lifecycle safety, GeoJSON caching")
    print(f"  • {Style.CYAN}/android-testing{Style.RESET}         ➔ Turbine Flow tests, MockK fakes, ComposeTestRule")
    print(f"  • {Style.CYAN}/gradle-build-hygiene{Style.RESET}   ➔ libs.versions.toml, R8 rules, official docs grounding")
    print(f"  • {Style.CYAN}/react-ts-architecture{Style.RESET}  ➔ Frontend Clean Architecture, pure domain, Zod, strict TS")
    print(f"  • {Style.CYAN}/react-ui-components{Style.RESET}   ➔ Accessible modular UI (never 1 file), prop order, Tailwind")
    print(f"  • {Style.CYAN}/react-state-management{Style.RESET} ➔ TanStack Query (server state), Zustand (client state)")
    print(f"  • {Style.CYAN}/react-testing{Style.RESET}          ➔ Vitest, React Testing Library, MSW network mocking")
    print(f"  • {Style.CYAN}/error-detective{Style.RESET}        ➔ Interactive triage, root cause, /docs/error-fix/ report")
    print(f"  • {Style.CYAN}/adr-analyze{Style.RESET}            ➔ Architecture Decision Records grounded in official docs")
    print(f"  • {Style.CYAN}/app-design{Style.RESET}             ➔ Visual UML class/sequence/state models in English")
    print(f"  • {Style.CYAN}/changelog-recorder{Style.RESET}     ➔ Timestamped logs: /docs/changelogs/YYYY-MM-DD_HH-mm-ss_*.md")
    print(f"  • {Style.CYAN}/git-assistant{Style.RESET}           ➔ Safety-first Git operations, conventional commits, PRs\n")

    print(f"{Style.BOLD}Selected Agents:{Style.RESET}")
    for k in selected_agent_keys:
        print(f"  {Style.GREEN}✓{Style.RESET} {SUPPORTED_AGENTS[k]['name']}")
    print(f"\n{Style.BOLD}Try it in your AI agent chat:{Style.RESET}")
    print(f"  Type {Style.BOLD}{Style.CYAN}/engineer-mode{Style.RESET} to begin your adaptive engineering workflow!\n")

def main():
    parser = argparse.ArgumentParser(description="RAF - Agent Skills Universal Installer")
    parser.add_argument("--all", "-a", action="store_true", help="Install to all supported AI agents")
    parser.add_argument("--agents", type=str, help="Comma-separated agent keys to install (e.g. '1,3')")
    parser.add_argument("--auto-install-android-cli", action="store_true", help="Automatically install Google Android CLI if missing")
    parser.add_argument("--skip-android-cli", action="store_true", help="Skip checking or installing Google Android CLI")
    parser.add_argument("--dry-run", action="store_true", help="Simulate execution without modifying files")
    parser.add_argument("--non-interactive", action="store_true", help="Run without interactive prompts")

    args = parser.parse_args()

    print_banner()
    check_python_environment()

    # Android CLI check & setup
    if not args.skip_android_cli:
        check_and_setup_android_cli(
            non_interactive=args.non_interactive or args.all,
            auto_install=args.auto_install_android_cli,
        )

    # Agent selection
    if args.all:
        selected_keys = list(SUPPORTED_AGENTS.keys())
    elif args.agents:
        selected_keys = [k.strip() for k in args.agents.split(",") if k.strip() in SUPPORTED_AGENTS]
    else:
        selected_keys = prompt_agent_selection(non_interactive=args.non_interactive)

    # Perform installation
    install_skills(selected_keys, dry_run=args.dry_run)
    print_summary(selected_keys)

if __name__ == "__main__":
    main()
