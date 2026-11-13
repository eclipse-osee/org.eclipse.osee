"""Thin async client for the OSEE ORCS REST API."""

from typing import Any

import httpx

from .config import Settings


class OseeError(RuntimeError):
    """Raised with a message that is safe to show to an LLM."""


class OseeClient:
    def __init__(self, settings: Settings, transport: httpx.AsyncBaseTransport | None = None):
        headers = {"Accept": "application/json"}
        if settings.api_key:
            # OSEE's Basic scheme carries the raw API key, not base64 user:password.
            headers["Authorization"] = f"Basic {settings.api_key}"
        self._http = httpx.AsyncClient(
            base_url=f"{settings.base_url}/orcs",
            headers=headers,
            timeout=settings.timeout_seconds,
            transport=transport,
        )

    async def get_json(self, path: str, params: dict[str, Any] | None = None) -> Any:
        try:
            response = await self._http.get(path, params=params)
            response.raise_for_status()
            return response.json()
        except httpx.HTTPStatusError as ex:
            status = ex.response.status_code
            if status == 401:
                raise OseeError("OSEE rejected the credentials (401). Check OSEE_API_KEY.") from ex
            raise OseeError(f"OSEE returned HTTP {status} for GET {path}") from ex
        except httpx.RequestError as ex:
            raise OseeError(f"Could not reach OSEE server: {type(ex).__name__}") from ex
        except ValueError as ex:
            raise OseeError(f"OSEE returned a non-JSON response for GET {path}") from ex

    async def aclose(self) -> None:
        await self._http.aclose()
