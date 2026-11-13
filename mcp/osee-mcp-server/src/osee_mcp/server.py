"""MCP server exposing OSEE type-system lookups (artifact, attribute and enum types)."""

from typing import Any

from mcp.server.fastmcp import FastMCP

from .client import OseeClient
from .config import Settings


def create_server(settings: Settings, client: OseeClient | None = None) -> FastMCP:
    osee = client or OseeClient(settings)
    mcp = FastMCP("osee", host=settings.host, port=settings.port)

    @mcp.tool()
    async def list_artifact_types(name_filter: str = "", exclude_abstract: bool = False) -> list[dict[str, Any]]:
        """List OSEE artifact types as {id, name}, sorted by name.

        name_filter is a case-insensitive substring match; empty returns all types.
        Set exclude_abstract to hide types that cannot be instantiated.
        """
        return await osee.get_json(
            "/types/artifact",
            {"filter": name_filter, "excludeAbstract": str(exclude_abstract).lower()},
        )

    @mcp.tool()
    async def get_artifact_type_attributes(artifact_type_id: int) -> list[dict[str, Any]]:
        """List the attributes valid for an artifact type, with default values and multiplicity.

        Use an id from list_artifact_types.
        """
        return await osee.get_json(f"/types/artifact/{artifact_type_id}/attributes")

    @mcp.tool()
    async def list_attribute_types(artifact_type_ids: list[int] | None = None) -> list[dict[str, Any]]:
        """List attribute types as {id, name}.

        With artifact_type_ids, returns the union of attributes valid for those artifact types;
        without, returns every attribute type.
        """
        params = {"artifactType": artifact_type_ids} if artifact_type_ids else None
        return await osee.get_json("/types/attribute", params)

    @mcp.tool()
    async def get_attribute_enums(attribute_type_id: int) -> list[str]:
        """List the allowed values of an enumerated attribute type (empty if it is not an enum)."""
        values = await osee.get_json(f"/types/attribute/{attribute_type_id}/enums")
        return sorted(values)

    @mcp.tool()
    async def list_relation_types(artifact_type_id: int | None = None) -> list[dict[str, Any]]:
        """List relation types with side names, side artifact types, multiplicity and sort order.

        With artifact_type_id, returns only relation types that can relate that artifact type.
        """
        params = {"artifactType": artifact_type_id} if artifact_type_id is not None else None
        return await osee.get_json("/types/relation", params)

    @mcp.tool()
    async def list_enumerated_attribute_types() -> list[dict[str, Any]]:
        """List every enumerated attribute type with its namespace and allowed values."""
        return await osee.get_json("/types/serverEnumTypesAndValues")

    return mcp


def main() -> None:
    create_server(Settings.from_env()).run(transport="streamable-http")


if __name__ == "__main__":
    main()
