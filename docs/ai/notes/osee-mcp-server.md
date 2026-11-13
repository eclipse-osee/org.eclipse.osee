---
summary: "Handoff notes for the OSEE MCP server (Open WebUI integration): status, design decisions, and next steps"
tags: [mcp, python, orcs, types, report, open-webui, handoff]
fileMatch: "mcp/osee-mcp-server/**"
---

# OSEE MCP Server - Handoff Notes

## Goal

- **Purpose** - expose OSEE to AI clients (Open WebUI) through MCP.
- **Phase 1** - wrap `orcs/types` REST endpoints as MCP tools (done, see Status).
- **Phase 2** - execute Generic Reports through MCP.
- **Phase 3** - template authoring for Generic Reports.

## Decisions

- **Sidecar Architecture** - a standalone Python MCP server that calls the OSEE REST API. Chosen over an in-server endpoint because the server is OSGi/javax and a Java MCP SDK will not drop in.
- **Python, Not Rust** - chosen for speed. The Rust workspace has no HTTP client or async runtime today.
- **Transport** - streamable HTTP at `http://<host>:8000/mcp`. Older Open WebUI versions may need the `mcpo` proxy; the installed version was not checked.
- **Auth** - `OSEE_API_KEY` is sent as `Authorization: Basic <key>`. OSEE's Basic scheme takes the raw API key (see `UserServiceImpl.setUserFromBasic`), not base64 user:password. Not tested against a live server.
- **`mcp` Pin** - `mcp==1.30.0`. 1.12.4 fails to import against the pydantic that Python 3.14 pulls in. A 2.x line exists; it was not evaluated.
- **Read-Only First** - writes such as `PUT orcs/report/.../hierarchyNumber` are deliberately excluded.

## Status

- **Python Server (done)** - `mcp/osee-mcp-server/`. 4 unit tests pass with a mocked transport. It starts and answers an MCP `initialize` over HTTP.
  - Tools: `list_artifact_types`, `get_artifact_type_attributes`, `list_attribute_types`, `get_attribute_enums`, `list_relation_types`, `list_enumerated_attribute_types`.
  - Settings (env): `OSEE_BASE_URL` (default `http://localhost:8089`), `OSEE_API_KEY`, `OSEE_TIMEOUT_SECONDS` (30), `MCP_HOST` (127.0.0.1), `MCP_PORT` (8000).
- **Java Endpoint (written, NOT compiled)** - `GET /orcs/types/relation?artifactType=<id>`.
  - Files: `TypesEndpoint.java` (orcs.rest.model) and `TypesEndpointImpl.java` (orcs.rest).
  - Returns a list of maps: `id`, `name`, `sideAName`, `sideAArtifactType {id,name}`, `sideBName`, `sideBArtifactType`, `multiplicity`, `order`.
  - `order` is the `RelationSorter` enum constant name because the enum has no display-name getter.
  - No Java tooling was available on the authoring machine, so a build is required.
- **Dockerfile** - written, never built. No compose entry exists yet.

## Verification Still Needed

- **Compile The Java** - build `orcs.rest.model` and `orcs.rest`. Check the `getValidRelationTypes(ArtifactTypeToken)` call and the `ArtifactTypeToken` query-param conversion.
- **Live Call** - hit `GET /orcs/types/relation` and the other type endpoints on a running server with a real API key.
- **Open WebUI** - confirm it can connect to the sidecar and see the tools.
- **Docker** - build the image and run it next to `osee-server` (`OSEE_BASE_URL=http://osee-server:8089`).

## Next Steps

1. Build and verify the Java endpoint, then a live end-to-end test.
2. Regenerate the checked-in Swagger JSON by running `SwaggerGenerator` from the IDE (`web.ui/src/swagger/definitions/org_eclipse_osee_orcs_rest.json`).
3. Add a `.gitignore` entry for `.venv`, `__pycache__`, `*.egg-info`, `.pytest_cache`.
4. Add a compose entry for the sidecar.
5. Phase 2 (see Generic Report Findings).

## Generic Report Findings (For Phase 2)

- **Endpoint** - `GET orcs/report/{branch}/view/{view}/template/{template}?format=xml|xlsx|html&email=`. Details in `docs/ai/orcs/report/generic-report.md`.
- **Formats** - only XML (SpreadsheetML), XLSX and HTML. All are poor for an LLM.
  - Plan: add `format=json` (and maybe `csv`) as a new `ReportFormat` value with its own writer and a row cap. Include the DebugInfo log.
- **Async Path** - with `email`, the report is written to disk and a link is emailed. This does not suit MCP; use the sync path with a row limit.
- **Template Discovery** - `ReportTemplate` artifacts live on COMMON. A tool needs to query them; the artifact query endpoints were not read yet.
- **Auth** - no explicit role check on report generation; only `applyHierarchyNumbers` requires `OseeAccessAdmin`. Global filter: `AuthenticationRequestFilter` (JWT, Basic/API key, numeric user id, SSO headers).
- **Not Read** - `TemplateParser`, `GenericReportBuilder` and the writers were not opened, so the JSON-writer effort is an estimate.

## Phase 3 (Template Authoring)

- **Write Path** - templates are Java source in the `JavaCode` attribute, parsed by Eclipse JDT and run by whitelisted reflection. Writing them is a write to COMMON.
- **Needs** - a dry-run/validate endpoint (does not exist), a reference served as an MCP resource (from `generic-report.md`), and its own permission gate.

## Other Observations

- **Swagger/OpenAPI** - an OpenAPI tool server for Open WebUI is a lower-effort alternative for types only, but exposes every endpoint including writes. Not chosen.
- **Types Endpoint Gaps** - `GET orcs/types` returns relation types only as a text log. `types/artifact` returns only id and name; a detail call (super types, valid attributes with storage type, abstract flag) would save LLM round trips.
- **Environment (authoring machine)** - Python 3.14 at `%LOCALAPPDATA%\Programs\Python\Python314` was not on the shell PATH. No git, bazel, mvn or javac on the shell PATH.
- **Session Portability** - Kiro chat sessions live under `~/.kiro/sessions` and `~/.kiro/session-index`. Not stored in the repo; this note is the carry-forward record.
