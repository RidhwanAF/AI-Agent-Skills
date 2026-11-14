#!/usr/bin/env python3
"""
Local Code Search MCP Server (Zero-Dependency Python 3.8+)
Implements a token-saving Model Context Protocol (MCP) stdio server and CLI tool.

Algorithms:
- CamelCase / snake_case identifier splitting
- BM25-inspired keyword scoring with Inverse Document Frequency (IDF)
- Symbol definition weighting (3.0x for class/fun/interface/@Composable/export/def)
- Filename/path relevance boost (2.0x)
- Compact merged line-window extraction with strict token-budget capping
"""

import argparse
import json
import math
import os
import re
import sys
from collections import Counter, defaultdict
from pathlib import Path
from typing import Any, Dict, List, Optional, Tuple

IGNORED_DIRS = {
    ".git", ".gradle", ".idea", ".vscode", ".venv", "venv", "__pycache__",
    "node_modules", "build", "dist", "out", "target", ".next", ".nuxt",
    "Pods", ".dart_tool", ".kotlin", "coverage", ".pytest_cache",
}

BINARY_EXTENSIONS = {
    ".png", ".jpg", ".jpeg", ".gif", ".webp", ".ico", ".svg", ".pdf",
    ".zip", ".jar", ".aar", ".apk", ".aab", ".so", ".dylib", ".dll",
    ".exe", ".class", ".dex", ".ttf", ".otf", ".woff", ".woff2", ".mp3",
    ".mp4", ".mov", ".sqlite", ".db", ".lock",
}

SYMBOL_DEF_RE = re.compile(
    r"^\s*(?:@Composable\s+|public\s+|private\s+|internal\s+|protected\s+|"
    r"open\s+|abstract\s+|sealed\s+|data\s+|suspend\s+|inline\s+|export\s+|"
    r"async\s+|default\s+)*"
    r"(?:fun|class|interface|object|enum\s+class|typealias|val|var|"
    r"function|const|type|def|struct|impl)\b"
)

OUTLINE_RE = re.compile(
    r"^\s*(?:@Composable|@HiltViewModel|@Immutable|@Stable|"
    r"(?:(?:public|private|internal|protected|open|abstract|sealed|data|"
    r"suspend|inline|override|export|async|default)\s+)*"
    r"(?:fun|class|interface|object|enum\s+class|typealias|function|"
    r"const\s+\w+\s*=|type\s+\w+|interface\s+\w+|def|async\s+def)\b)"
)

TOKEN_SPLIT_RE = re.compile(r"[A-Za-z0-9_]+")
CAMEL_SPLIT_RE = re.compile(r"(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|_")


def tokenize(text: str) -> List[str]:
    """Splits text into lowercase tokens, decomposing camelCase and snake_case."""
    tokens: List[str] = []
    for raw in TOKEN_SPLIT_RE.findall(text):
        lower_raw = raw.lower()
        tokens.append(lower_raw)
        parts = [p.lower() for p in CAMEL_SPLIT_RE.split(raw) if p and len(p) > 1]
        if len(parts) > 1:
            tokens.extend(parts)
    return tokens


def iter_source_files(
    root: Path,
    extensions: Optional[List[str]] = None,
    max_file_bytes: int = 512 * 1024,
) -> List[Path]:
    """Walks workspace root while skipping ignored/binary directories and huge files."""
    norm_exts = None
    if extensions:
        norm_exts = {e if e.startswith(".") else f".{e}" for e in extensions}

    files: List[Path] = []
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in IGNORED_DIRS and not d.startswith(".")]
        for fname in filenames:
            fpath = Path(dirpath) / fname
            suffix = fpath.suffix.lower()
            if suffix in BINARY_EXTENSIONS:
                continue
            if norm_exts is not None and suffix not in norm_exts:
                continue
            try:
                if fpath.stat().st_size <= max_file_bytes:
                    files.append(fpath)
            except OSError:
                continue
    return files


def merge_line_windows(
    hit_lines: List[int],
    total_lines: int,
    context_lines: int,
    max_windows: int = 4,
) -> List[Tuple[int, int]]:
    """Merges overlapping 1-based line windows around top matching lines."""
    if not hit_lines:
        return []
    windows: List[Tuple[int, int]] = []
    for line_no in sorted(set(hit_lines)):
        start = max(1, line_no - context_lines)
        end = min(total_lines, line_no + context_lines)
        if windows and start <= windows[-1][1] + 1:
            prev_start, prev_end = windows[-1]
            windows[-1] = (prev_start, max(prev_end, end))
        else:
            windows.append((start, end))
    return windows[:max_windows]


def search_code_smart(
    keywords: List[str],
    workspace_root: str = ".",
    extensions: Optional[List[str]] = None,
    max_results: int = 8,
    context_lines: int = 2,
    max_tokens_budget: int = 1800,
) -> Dict[str, Any]:
    """
    Ranks files and extracts compact line-window snippets using BM25 + symbol boosting.
    Enforces a strict character/token budget to minimize agent context usage.
    """
    root = Path(workspace_root).expanduser().resolve()
    if not root.exists():
        return {"error": f"Workspace root does not exist: {workspace_root}", "matches": []}

    raw_terms = [k.strip() for k in keywords if k and k.strip()]
    if not raw_terms:
        return {"error": "No search keywords provided.", "matches": []}

    query_tokens = []
    for kw in raw_terms:
        query_tokens.extend(tokenize(kw))
    unique_query_tokens = list(dict.fromkeys(query_tokens))
    raw_lower_terms = [k.lower() for k in raw_terms]

    files = iter_source_files(root, extensions=extensions)
    if not files:
        return {"workspace_root": str(root), "total_files_scanned": 0, "matches": []}

    doc_freq: Counter = Counter()
    file_records: List[Dict[str, Any]] = []

    for fpath in files:
        try:
            content = fpath.read_text(encoding="utf-8", errors="ignore")
        except OSError:
            continue

        rel_path = str(fpath.relative_to(root))
        lines = content.splitlines()
        doc_tokens = tokenize(rel_path) + tokenize(content)
        tf = Counter(doc_tokens)

        matched_tokens = [t for t in unique_query_tokens if t in tf]
        if not matched_tokens:
            continue

        for t in matched_tokens:
            doc_freq[t] += 1

        file_records.append({
            "path": str(fpath),
            "rel_path": rel_path,
            "lines": lines,
            "tf": tf,
            "doc_len": max(1, len(doc_tokens)),
        })

    if not file_records:
        return {
            "workspace_root": str(root),
            "total_files_scanned": len(files),
            "keywords": raw_terms,
            "matches": [],
        }

    num_docs = len(files)
    avg_dl = sum(r["doc_len"] for r in file_records) / len(file_records)
    k1 = 1.5
    b = 0.75

    scored_files: List[Dict[str, Any]] = []
    for rec in file_records:
        bm25_score = 0.0
        for tok in unique_query_tokens:
            freq = rec["tf"].get(tok, 0)
            if freq == 0:
                continue
            df = doc_freq[tok]
            idf = math.log((num_docs - df + 0.5) / (df + 0.5) + 1.0)
            denom = freq + k1 * (1.0 - b + b * (rec["doc_len"] / avg_dl))
            bm25_score += idf * ((freq * (k1 + 1.0)) / denom)

        rel_lower = rec["rel_path"].lower()
        path_boost = 1.0 + sum(1.0 for term in raw_lower_terms if term in rel_lower)

        line_scores: List[Tuple[int, float]] = []
        symbol_hits = 0
        for idx, line in enumerate(rec["lines"], start=1):
            line_lower = line.lower()
            exact_hits = sum(1 for term in raw_lower_terms if term in line_lower)
            if exact_hits == 0:
                line_toks = set(tokenize(line))
                tok_hits = sum(1 for tok in unique_query_tokens if tok in line_toks)
                if tok_hits == 0:
                    continue
                base = tok_hits * 0.7
            else:
                base = exact_hits * 2.0

            is_symbol = bool(SYMBOL_DEF_RE.match(line))
            if is_symbol:
                base *= 3.0
                symbol_hits += 1
            line_scores.append((idx, base))

        if not line_scores and path_boost <= 1.0:
            continue

        line_scores.sort(key=lambda item: (-item[1], item[0]))
        top_hit_lines = [ln for ln, _ in line_scores[:6]]
        total_score = (bm25_score * path_boost) + (symbol_hits * 1.5) + (len(line_scores) * 0.1)

        scored_files.append({
            "rel_path": rec["rel_path"],
            "abs_path": rec["path"],
            "score": round(total_score, 3),
            "lines": rec["lines"],
            "hit_lines": top_hit_lines,
        })

    scored_files.sort(key=lambda item: -item["score"])

    # Enforce approximate token budget (1 token ~= 4 chars)
    max_chars = max(400, max_tokens_budget * 4)
    used_chars = 0
    results: List[Dict[str, Any]] = []

    for item in scored_files[:max_results]:
        windows = merge_line_windows(
            item["hit_lines"],
            total_lines=len(item["lines"]),
            context_lines=context_lines,
        )
        snippets: List[Dict[str, Any]] = []
        for start, end in windows:
            numbered = [
                f"{ln}: {item['lines'][ln - 1]}"
                for ln in range(start, end + 1)
            ]
            block = "\n".join(numbered)
            if used_chars + len(block) > max_chars and results:
                break
            used_chars += len(block)
            snippets.append({
                "start_line": start,
                "end_line": end,
                "code": block,
            })
        if snippets:
            results.append({
                "file": item["rel_path"],
                "path": item["abs_path"],
                "score": item["score"],
                "snippets": snippets,
            })
        if used_chars >= max_chars:
            break

    return {
        "workspace_root": str(root),
        "total_files_scanned": len(files),
        "keywords": raw_terms,
        "estimated_tokens": used_chars // 4,
        "matches": results,
    }


def outline_symbols(file_path: str) -> Dict[str, Any]:
    """Extracts declaration/symbol signatures with line numbers for minimal-token inspection."""
    fpath = Path(file_path).expanduser().resolve()
    if not fpath.is_file():
        return {"error": f"File not found: {file_path}", "symbols": []}

    lines = fpath.read_text(encoding="utf-8", errors="ignore").splitlines()
    symbols: List[str] = []
    for idx, line in enumerate(lines, start=1):
        if OUTLINE_RE.match(line):
            symbols.append(f"{idx}: {line.rstrip()}")

    return {
        "file": str(fpath),
        "total_lines": len(lines),
        "symbol_count": len(symbols),
        "symbols": symbols,
    }


def read_line_range(file_path: str, start_line: int = 1, end_line: int = 80) -> Dict[str, Any]:
    """Reads only the requested 1-based line range from a file."""
    fpath = Path(file_path).expanduser().resolve()
    if not fpath.is_file():
        return {"error": f"File not found: {file_path}", "content": ""}

    lines = fpath.read_text(encoding="utf-8", errors="ignore").splitlines()
    total = len(lines)
    start = max(1, start_line)
    end = min(total, max(start, end_line))
    content = "\n".join(f"{ln}: {lines[ln - 1]}" for ln in range(start, end + 1))

    return {
        "file": str(fpath),
        "start_line": start,
        "end_line": end,
        "total_lines": total,
        "content": content,
    }


MCP_TOOLS = [
    {
        "name": "search_code_smart",
        "description": (
            "Token-saving local code search using BM25 + symbol-definition weighting. "
            "Pass candidate keywords (function names, classes, annotations, error strings) "
            "to receive only top-ranked compact line-window snippets within a token budget."
        ),
        "inputSchema": {
            "type": "object",
            "properties": {
                "keywords": {
                    "type": "array",
                    "items": {"type": "string"},
                    "description": "Candidate search keywords (e.g. ['CartViewModel', 'clickable', 'padding']).",
                },
                "workspace_root": {
                    "type": "string",
                    "description": "Absolute or relative path to the repository root.",
                    "default": ".",
                },
                "extensions": {
                    "type": "array",
                    "items": {"type": "string"},
                    "description": "Optional file extensions filter (e.g. ['.kt', '.kts', '.ts', '.tsx']).",
                },
                "max_results": {
                    "type": "integer",
                    "description": "Maximum number of matching files to return.",
                    "default": 8,
                },
                "context_lines": {
                    "type": "integer",
                    "description": "Lines of context above/below each matching line.",
                    "default": 2,
                },
                "max_tokens_budget": {
                    "type": "integer",
                    "description": "Approximate maximum response token budget.",
                    "default": 1800,
                },
            },
            "required": ["keywords"],
        },
    },
    {
        "name": "outline_symbols",
        "description": (
            "Extracts only class, interface, fun, @Composable, and export signatures with line numbers "
            "from a source file to inspect structure without reading the entire file."
        ),
        "inputSchema": {
            "type": "object",
            "properties": {
                "file_path": {
                    "type": "string",
                    "description": "Path to the source file.",
                },
            },
            "required": ["file_path"],
        },
    },
    {
        "name": "read_line_range",
        "description": "Reads only an exact 1-based line range from a file to save context tokens.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "file_path": {"type": "string"},
                "start_line": {"type": "integer", "default": 1},
                "end_line": {"type": "integer", "default": 80},
            },
            "required": ["file_path"],
        },
    },
]


def handle_jsonrpc_request(request: Dict[str, Any]) -> Optional[Dict[str, Any]]:
    """Handles a single MCP JSON-RPC 2.0 message and returns a response (or None for notifications)."""
    method = request.get("method")
    req_id = request.get("id")
    params = request.get("params") or {}

    # Notifications have no id and expect no response
    if req_id is None:
        return None

    if method == "initialize":
        return {
            "jsonrpc": "2.0",
            "id": req_id,
            "result": {
                "protocolVersion": params.get("protocolVersion", "2024-11-05"),
                "capabilities": {"tools": {}},
                "serverInfo": {"name": "raf-local-code-search", "version": "1.0.0"},
            },
        }

    if method == "ping":
        return {"jsonrpc": "2.0", "id": req_id, "result": {}}

    if method == "tools/list":
        return {"jsonrpc": "2.0", "id": req_id, "result": {"tools": MCP_TOOLS}}

    if method == "tools/call":
        tool_name = params.get("name")
        args = params.get("arguments") or {}

        if tool_name == "search_code_smart":
            output = search_code_smart(
                keywords=args.get("keywords", []),
                workspace_root=args.get("workspace_root", "."),
                extensions=args.get("extensions"),
                max_results=args.get("max_results", 8),
                context_lines=args.get("context_lines", 2),
                max_tokens_budget=args.get("max_tokens_budget", 1800),
            )
        elif tool_name == "outline_symbols":
            output = outline_symbols(file_path=args.get("file_path", ""))
        elif tool_name == "read_line_range":
            output = read_line_range(
                file_path=args.get("file_path", ""),
                start_line=args.get("start_line", 1),
                end_line=args.get("end_line", 80),
            )
        else:
            return {
                "jsonrpc": "2.0",
                "id": req_id,
                "error": {"code": -32601, "message": f"Unknown tool: {tool_name}"},
            }

        return {
            "jsonrpc": "2.0",
            "id": req_id,
            "result": {
                "content": [{"type": "text", "text": json.dumps(output, indent=2)}],
                "isError": "error" in output,
            },
        }

    return {
        "jsonrpc": "2.0",
        "id": req_id,
        "error": {"code": -32601, "message": f"Method not found: {method}"},
    }


def run_stdio_server() -> None:
    """Runs the MCP JSON-RPC 2.0 loop over stdin/stdout."""
    for raw_line in sys.stdin:
        line = raw_line.strip()
        if not line:
            continue
        try:
            req = json.loads(line)
        except json.JSONDecodeError as exc:
            err_resp = {
                "jsonrpc": "2.0",
                "id": None,
                "error": {"code": -32700, "message": f"Parse error: {exc}"},
            }
            sys.stdout.write(json.dumps(err_resp) + "\n")
            sys.stdout.flush()
            continue

        resp = handle_jsonrpc_request(req)
        if resp is not None:
            sys.stdout.write(json.dumps(resp) + "\n")
            sys.stdout.flush()


def main() -> None:
    parser = argparse.ArgumentParser(description="Local Code Search MCP Server & CLI")
    parser.add_argument("--cli", action="store_true", help="Run a one-shot CLI search instead of MCP stdio")
    parser.add_argument("--keywords", "-k", nargs="+", help="Keywords for CLI search")
    parser.add_argument("--root", "-r", default=".", help="Workspace root directory")
    parser.add_argument("--ext", nargs="+", help="File extensions filter (e.g. .kt .ts)")
    parser.add_argument("--max-results", type=int, default=8, help="Max files to return")
    parser.add_argument("--budget", type=int, default=1800, help="Max token budget")
    args = parser.parse_args()

    if args.cli:
        if not args.keywords:
            parser.error("--keywords is required in --cli mode")
        res = search_code_smart(
            keywords=args.keywords,
            workspace_root=args.root,
            extensions=args.ext,
            max_results=args.max_results,
            max_tokens_budget=args.budget,
        )
        print(json.dumps(res, indent=2))
    else:
        run_stdio_server()


if __name__ == "__main__":
    main()
