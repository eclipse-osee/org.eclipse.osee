"""Runtime settings, read from environment variables."""

import os
from dataclasses import dataclass


@dataclass(frozen=True)
class Settings:
    base_url: str
    api_key: str | None
    timeout_seconds: float
    host: str
    port: int

    @classmethod
    def from_env(cls) -> "Settings":
        return cls(
            base_url=os.environ.get("OSEE_BASE_URL", "http://localhost:8089").rstrip("/"),
            api_key=os.environ.get("OSEE_API_KEY") or None,
            timeout_seconds=float(os.environ.get("OSEE_TIMEOUT_SECONDS", "30")),
            host=os.environ.get("MCP_HOST", "127.0.0.1"),
            port=int(os.environ.get("MCP_PORT", "8000")),
        )
