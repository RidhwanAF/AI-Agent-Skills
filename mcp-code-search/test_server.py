import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

from server import handle_jsonrpc_request, outline_symbols, read_line_range, search_code_smart


class TestLocalCodeSearchMCP(unittest.TestCase):
    def setUp(self) -> None:
        self.tmp = tempfile.TemporaryDirectory()
        self.root = Path(self.tmp.name)

        (self.root / "CartScreen.kt").write_text(
            "package com.example.cart\n\n"
            "import androidx.compose.foundation.clickable\n"
            "import androidx.compose.foundation.layout.padding\n"
            "import androidx.compose.runtime.Composable\n"
            "import androidx.compose.ui.Modifier\n"
            "import androidx.compose.ui.unit.dp\n\n"
            "@Composable\n"
            "fun CartScreen(\n"
            "    modifier: Modifier = Modifier,\n"
            ") {\n"
            "    Column(\n"
            "        modifier = modifier\n"
            "            .clickable { }\n"
            "            .padding(16.dp),\n"
            "    ) {}\n"
            "}\n",
            encoding="utf-8",
        )

        (self.root / "OtherFile.kt").write_text(
            "package com.example.other\n\n"
            "// Mentions padding once in a comment\n"
            "class Helper {\n"
            "    fun doWork() = Unit\n"
            "}\n",
            encoding="utf-8",
        )

    def tearDown(self) -> None:
        self.tmp.cleanup()

    def test_search_code_smart_ranks_symbol_and_keywords_first(self) -> None:
        res = search_code_smart(
            keywords=["CartScreen", "clickable", "padding"],
            workspace_root=str(self.root),
            extensions=[".kt"],
        )
        self.assertEqual(res["total_files_scanned"], 2)
        self.assertGreaterEqual(len(res["matches"]), 1)
        self.assertEqual(res["matches"][0]["file"], "CartScreen.kt")
        joined_snippets = "\n".join(s["code"] for s in res["matches"][0]["snippets"])
        self.assertIn("fun CartScreen", joined_snippets)

    def test_outline_symbols_and_read_line_range(self) -> None:
        target = str(self.root / "CartScreen.kt")
        outline = outline_symbols(target)
        self.assertGreaterEqual(outline["symbol_count"], 2)
        self.assertTrue(any("fun CartScreen" in s for s in outline["symbols"]))

        ranged = read_line_range(target, start_line=9, end_line=12)
        self.assertIn("9: @Composable", ranged["content"])
        self.assertNotIn("package com.example.cart", ranged["content"])

    def test_mcp_stdio_protocol_roundtrip(self) -> None:
        init_resp = handle_jsonrpc_request({"jsonrpc": "2.0", "id": 1, "method": "initialize", "params": {}})
        self.assertEqual(init_resp["result"]["serverInfo"]["name"], "raf-local-code-search")

        list_resp = handle_jsonrpc_request({"jsonrpc": "2.0", "id": 2, "method": "tools/list"})
        tool_names = [t["name"] for t in list_resp["result"]["tools"]]
        self.assertEqual(tool_names, ["search_code_smart", "outline_symbols", "read_line_range"])

        call_resp = handle_jsonrpc_request({
            "jsonrpc": "2.0",
            "id": 3,
            "method": "tools/call",
            "params": {
                "name": "search_code_smart",
                "arguments": {"keywords": ["CartScreen"], "workspace_root": str(self.root)},
            },
        })
        payload = json.loads(call_resp["result"]["content"][0]["text"])
        self.assertEqual(payload["matches"][0]["file"], "CartScreen.kt")

    def test_cli_execution(self) -> None:
        server_path = Path(__file__).resolve().parent / "server.py"
        proc = subprocess.run(
            [sys.executable, str(server_path), "--cli", "--root", str(self.root), "--keywords", "CartScreen"],
            capture_output=True,
            text=True,
            check=True,
        )
        data = json.loads(proc.stdout)
        self.assertEqual(data["matches"][0]["file"], "CartScreen.kt")


if __name__ == "__main__":
    unittest.main()
