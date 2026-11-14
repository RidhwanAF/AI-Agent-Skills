# 🔍 Local Code Search MCP (`raf-local-code-search`)

A **zero-dependency Python 3.8+ MCP stdio server & CLI** designed to save AI agent tokens when fixing bugs or building features in large codebases.

Instead of reading entire 500-line files or running multiple broad searches in chat, the AI agent passes candidate keywords (`["CartScreen", "clickable", "padding"]`), and the Python engine runs a **BM25 + Symbol-Definition Weighted** search locally, returning only the top-ranked compact line windows within a strict token budget.

---

## 🛠️ Exposed MCP Tools

| Tool | Purpose | Token Savings |
| :--- | :--- | :--- |
| `search_code_smart` | BM25 + camelCase/snake_case splitting + `3.0x` symbol-definition boost + path boost, returning merged line windows capped by `max_tokens_budget`. | Returns ~200–1,800 tokens instead of reading 10+ full files. |
| `outline_symbols` | Extracts only declarations (`class`, `interface`, `fun`, `@Composable`, `export`, `def`) with line numbers. | ~95% smaller than reading a full file. |
| `read_line_range` | Reads only the exact `start_line..end_line` range needed. | Reads 10–30 lines instead of whole files. |

---

## ⚙️ MCP Configuration (`mcp_config.json`)

Add to `~/.gemini/config/mcp_config.json` (Antigravity) or your IDE's MCP config (or run `python3 installer/install.py --install-mcp`):

```json
{
  "mcpServers": {
    "raf-local-code-search": {
      "command": "python3",
      "args": ["/path/to/AI-Engineer-Skills/mcp-code-search/server.py"]
    }
  }
}
```

---

## 🧪 Running Locally (CLI & Tests)

```bash
# Run unit & MCP protocol tests
python3 -m unittest discover -s mcp-code-search -p "test_*.py"

# Run one-shot CLI search
python3 mcp-code-search/server.py --cli --root . --keywords CartViewModel clickable padding
```
