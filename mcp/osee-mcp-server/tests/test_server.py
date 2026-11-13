import httpx
import pytest

from osee_mcp.client import OseeClient, OseeError
from osee_mcp.config import Settings
from osee_mcp.server import create_server

SETTINGS = Settings(base_url="http://osee", api_key="key123", timeout_seconds=5, host="127.0.0.1", port=8000)


def make_client(handler) -> OseeClient:
    return OseeClient(SETTINGS, transport=httpx.MockTransport(handler))


async def call(server, tool, args):
    _, structured = await server.call_tool(tool, args)
    return structured["result"]


async def test_list_artifact_types_sends_filter_and_auth():
    seen = {}

    def handler(request: httpx.Request) -> httpx.Response:
        seen["url"] = str(request.url)
        seen["auth"] = request.headers["Authorization"]
        return httpx.Response(200, json=[{"id": 1, "name": "Folder"}])

    server = create_server(SETTINGS, make_client(handler))
    data = await call(server, "list_artifact_types", {"name_filter": "Fold", "exclude_abstract": True})

    assert data == [{"id": 1, "name": "Folder"}]
    assert seen["url"] == "http://osee/orcs/types/artifact?filter=Fold&excludeAbstract=true"
    assert seen["auth"] == "Basic key123"


async def test_list_attribute_types_repeats_artifact_type_param():
    seen = {}

    def handler(request: httpx.Request) -> httpx.Response:
        seen["query"] = request.url.query.decode()
        return httpx.Response(200, json=[])

    server = create_server(SETTINGS, make_client(handler))
    await call(server, "list_attribute_types", {"artifact_type_ids": [10, 20]})

    assert seen["query"] == "artifactType=10&artifactType=20"


async def test_list_relation_types_filters_by_artifact_type():
    seen = []

    def handler(request: httpx.Request) -> httpx.Response:
        seen.append(str(request.url))
        return httpx.Response(200, json=[{"id": 5, "name": "Default Hierarchical"}])

    server = create_server(SETTINGS, make_client(handler))
    await call(server, "list_relation_types", {})
    await call(server, "list_relation_types", {"artifact_type_id": 7})

    assert seen == ["http://osee/orcs/types/relation", "http://osee/orcs/types/relation?artifactType=7"]


async def test_unauthorized_maps_to_readable_error():
    client = make_client(lambda request: httpx.Response(401))
    with pytest.raises(OseeError, match="401"):
        await client.get_json("/types/artifact")
