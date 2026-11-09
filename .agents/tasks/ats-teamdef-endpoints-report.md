# ATS Team Definition Endpoints - Investigation Report

Read-only investigation. No code changed.

## Summary

- **Yes, one existing endpoint already returns all Team Definitions** - `GET /ats/config` (`AtsConfigEndpointApi.get()`). It returns the whole cached `AtsConfigurations`, whose `idToTeamDef` map holds every `TeamDefinition` (id, name, active, parentId, children, ais, versions, ...). This is what the IDE search widget effectively uses.
- It is heavy (all AIs, versions, users, programs too). There is **no dedicated, lightweight "list team defs" endpoint** (no active filter, no tree, no id/name-only list).
- The search side is ready: `POST /ats/action/query/workitems` takes `AtsSearchData` which already has `teamDefIds`.
- Only per-id endpoints exist besides the config dump: `GET /ats/config/teamdef/{id}`, `/leads`, `GET /ats/ai/{aiId}/teamdef`.
- The web app has no team-def list call today. It only uses `ai/{id}/teamdef` and `config/teamdef/{id}/leads` in create-action.
- Recommendation: add a small `GET /ats/config/teamdef?active=...` returning a flat id/name/active/parentId list (details below).

## 1. Existing Endpoints Returning Team Defs

Base path in web code is `apiURL + '/ats/...'`. Ats REST classes carry no `@RolesAllowed`/`@PermitAll` (grep of `ats.rest` found none), so access is whatever the OSEE REST/ATS-level session security applies globally. I did not trace the global filter; treat auth as unverified.

| # | Interface (path) | Method + @Path | Params | Return | Notes |
|---|---|---|---|---|---|
| 1 | `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/config/AtsConfigEndpointApi.java` (class `@Path("config")`) `get()` ~L40; impl `ats.rest/.../internal/config/AtsConfigEndpointImpl.java:114` | `GET /ats/config` | none | `AtsConfigurations` JSON: `idToTeamDef`, `idToAi`, `idToVersion`, `idToUser`, `idToProgram`, `teamDefToProgram`, `atsConfig`, `topTeamDefinition` (`api/config/AtsConfigurations.java:41`) | Cached server copy, reloaded every 5 min. **Only way today to get ALL team defs.** Large payload. |
| 2 | same, `getWithPend()` | `GET /ats/config/fromdb` | none | `AtsConfigurations` | Uncached, ~30s, updates server cache. Not for UI use. |
| 3 | same, `getTeamDefinition` (api ~L172; impl `AtsConfigEndpointImpl.java:288`) | `GET /ats/config/teamdef/{teamDefId}` | path `teamDefId` (ArtifactId) | `TeamDefinition` | One item from `atsApi.getTeamDefinitionService().getTeamDefinitionById`. |
| 4 | same, `getTeamLeads` (api ~L179; impl `:293`) | `GET /ats/config/teamdef/{teamDefId}/leads` | path | `List<ArtifactToken>` (users) | Used by web. |
| 5 | `ats.rest/.../internal/config/ActionableItemResource.java:81` | `GET /ats/ai/{id}/teamdef` | path AI id | `IAtsTeamDefinition` (shape of `TeamDefinition`) | Team def for one AI. Used by web. |
| 6 | `api/workdef/AtsWorkDefEndpointApi.java:48` (`@Path("workdef")`) | `GET /ats/workdef/teamdef/{id}` | path | `WorkDefinition` | Not a team def; work def for a team. |
| 7 | `api/workflow/AtsTeamWfEndpointApi.java:52` (`@Path("teamwf")`) | `GET /ats/teamwf/{aiId}/version` | `sort` | `Collection<IAtsVersion>` | Versions by team def/AI; not team defs. |
| 8 | `api/agile/AgileEndpointApi.java` (`@Path("team")` etc.) | `GET /ats/agile/team...` | | Agile teams | Agile teams, not Team Definitions. |

JSON shape of a `TeamDefinition` (`api/config/TeamDefinition.java:34-48`, `JaxAtsObject`, `JaxAtsConfigObject`):
`id`, `name`, `description`, `active`, `parentId` (string via ToStringSerializer), `ais` (long set), `versions` (long set), `children` (long set), `workTypes`, `tags`, `artifactType`, `programId`, `cscis`.

Server-side service methods exist but are not exposed over REST (`api/team/IAtsTeamDefinitionService.java`):
- `getTeamDefinitions(Active)` L111, flat list from config cache (`core/config/TeamDefinitionServiceImpl.java:384`)
- `getTopLevelTeamDefinitions(Active)` L95 (impl `:340`), `getTeamTopLevelDefinitions(Active)` L113, `getTeamTopLevelJaxDefinitions(Active)` L127
- `getChildrenTeamDefinitions`, `getChildren(teamDef, recurse)`, `getTeamReleaseableDefinitions(Active)` L105

## 2. How the IDE Widget Loads Team Defs

- Search widget: `plugins/org.eclipse.osee.ats.ide/.../ide/search/widget/TeamDefinitionSearchWidget.java` ("Team Definition(s)", wraps `XXTeamDefinitionsWidget`).
- Widget: `.../ide/util/widgets/xx/XXTeamDefinitionsWidget.java:76` calls `AtsApiService.get().getTeamDefinitionService().getTopLevelTeamDefinitions(Active.Both)` and builds a tree with `TeamDefinitionTreeContentProvider(Active.Both or Active.Active)` (L108; Active only unless "enable all" option set).
- On the IDE client this reads the client-side `AtsConfigurations` cache, which is loaded from the server via `GET /ats/config`. Restore of saved searches reads `getConfigService().getConfigurations().getIdToTeamDef().get(id)` (`TeamDefinitionSearchWidget.set(AtsSearchData)`).
- So: IDE = one bulk config load + local tree filtering by `active` and `parentId/children`. There is no per-widget REST call.

## 3. Passing Selected Team Defs to the Query

- `AtsSearchData.teamDefIds` is `List<Long>` (`api/query/AtsSearchData.java:49`, getter/setter L186-190). `AtsSearchData` is also copied via `setTeamDefIds` at L100.
- IDE builds it, e.g. `OpenWorkflowsByTeamDefSearchItem.java:65` `data.setTeamDefIds(AtsObjects.toIds(teamDefProvider.getTeamDefs()))`.
- Server applies it: `ats.core/.../query/AtsSearchDataSearch.java:274` -> `query.andTeam(data.getTeamDefIds())` -> `AbstractAtsQueryImpl.addTeamDefCriteria` (L865, matches `AtsAttributeTypes.TeamDefinitionReference`, exact match). Version search matches via `AtsSearchDataVersionSearch.matchTeamDefs` (L219).
- REST search endpoints accepting `AtsSearchData` (`api/workflow/AtsActionEndpointApi.java`, `@Path("action")`):
  - `POST /ats/action/query/workitems` (L126) -> `List<IAtsWorkItem>`; body `AtsSearchData` JSON
  - `POST /ats/action/query/workitems/ids` (L120) -> `XResultData`
- The `teamwf/search` GET endpoints (`AtsTeamWfEndpointApi.java:93-115`) take text/originator/assignee only, no team def.
- **Web can filter workflows by team def today** by POSTing `{"teamDefIds":[...], "workItemTypes":["TeamWorkflow"], ...}`. Note the query only filters `isOfType(TeamWorkflow)` when team defs are specified, and a "Team Workflow" team def match is by exact id (children are not auto-included; the IDE widget selection decides that).
- I did not verify the exact `AtsSearchData` JSON field names beyond the getters above, or which other fields (`stateTypes`, `userId`, etc.) are required.

## 4. Existing Web Code (web/apps/osee)

Only in `src/app/configuration-management/services/action.service.ts`:
- `getTeamDef(actionableItemId)` -> `GET /ats/ai/{id}/teamdef` (L249-253), used by `create-action-form.component.ts:183`
- `getTeamLeads(teamDef)` -> `GET /ats/config/teamdef/{id}/leads` (L314-318)
- `getSprints`, `getFeatureGroups` take a teamDefId but target agile endpoints
- No call to `/ats/config` or any list of team defs, and no call to `/ats/action/query/workitems` in files matching "teamDef". (Grep for team def only; a general search of `query/workitems` usage in web was not done.)

## 5. Gaps and Recommendation

Gaps:
- No lightweight list endpoint. `GET /ats/config` works but ships the entire config (AIs, versions, users, programs) for a dropdown.
- No active filter, no top-level/children lookup, no tree, over REST.
- `@SkipAtsConfigJsonWriter` on the per-id endpoints indicates a custom JSON writer is used for `AtsConfigurations`; response shape of #1 should be confirmed against a live server before the web depends on it.

Recommended minimal endpoint (add, do not change existing ones):
- `GET /ats/config/teamdef?active=Active|Inactive|Both` (default `Active`) on `AtsConfigEndpointApi`; impl in `AtsConfigEndpointImpl` using `atsApi.getTeamDefinitionService().getTeamDefinitions(active)` (core `TeamDefinitionServiceImpl.java:384`, reads config cache, no DB hit).
- Return a flat list of lightweight DTOs: `{ id, name, active, parentId }` (new small class in `ats.api/.../config`, e.g. `TeamDefinitionToken`). The web builds the tree from `parentId`; keeps it a pure `ats.api`/`ats.core` change with unit tests in `ats.core.test`, per `docs/ai/ats/ats-coding-standards.md`.
- Optional second form `?top=true` / `GET .../teamdef/{id}/children` using `getTopLevelTeamDefinitions` / `getChildren` if lazy tree loading is wanted; not needed at typical team-def counts.
- Check `Active` is a JAX-RS-bindable enum (`org.eclipse.osee.framework.core.enums.Active`); otherwise take a String/boolean `active` query param.
- Web: add `getTeamDefinitions()` to a service, feed a select/autocomplete, then POST `AtsSearchData` with `teamDefIds` to `/ats/action/query/workitems`.

Interim option with no server change: web calls `GET /ats/config` once, caches `idToTeamDef` values, filters `active` client-side.
