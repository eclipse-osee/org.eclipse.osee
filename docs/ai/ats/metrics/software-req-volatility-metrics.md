# Software Requirements Volatility Metrics

## Overview

The SoftwareReqVolatilityMetrics report generates an Excel XML spreadsheet tracking requirement changes across team workflows targeted to a specific ATS version. It is exposed via the REST endpoint at `ats/metrics/SoftwareReqVolatility/{targetVersion}`.

## Key Files

- `plugins/org.eclipse.osee.ats.rest/src/org/eclipse/osee/ats/rest/metrics/SoftwareReqVolatilityMetrics.java` — Core report logic (StreamingOutput)
- `plugins/org.eclipse.osee.ats.rest/src/org/eclipse/osee/ats/rest/metrics/MetricsEndpointImpl.java` — REST endpoint
- `plugins/org.eclipse.osee.ats.rest/src/org/eclipse/osee/ats/rest/metrics/MetricsReportOperations.java` — Orchestration/response building
- `plugins/org.eclipse.osee.ats.rest/src/org/eclipse/osee/ats/rest/metrics/SoftwareReqVolatilityId.java` — Column enum definitions
- `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/metrics/MetricsEndpointApi.java` — API interface
- `plugins/org.eclipse.osee.ats.ide/src/org/eclipse/osee/ats/ide/metrics/SoftwareReqVolatilityMetricsBlam.java` — Client-side BLAM UI
- `plugins/org.eclipse.osee.ats.ide.integration.tests/src/org/eclipse/osee/ats/ide/integration/tests/ats/report/SoftwareReqVolatilityMetricsTest.java` — Integration test

## Architecture

### Data Flow

1. Endpoint receives a `targetVersion` (artifact ID string), optional date range, and flags (`allTime`, `countImpacts`).
2. `SoftwareReqVolatilityMetrics` queries for the Version artifact via OrcsApi with `followFork` to eagerly load related workflows and team definition in a single query.
3. Related team workflows are fetched from the already-loaded version artifact using `ArtifactReadable.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow)`.
4. Each `ArtifactReadable` is converted to `IAtsTeamWorkflow` via `atsApi.getWorkItemService().getTeamWf(artifact)`.
5. Workflows are filtered to Requirements type only (via `WorkType.Requirements` or work definition name).
6. Workflows are filtered by completion state date (Verify > Demonstrate > Completed > Closed priority).
7. For each qualifying workflow, branch diff change items are retrieved (from cached `BranchDiffData` attribute or computed via `orcsApi.getBranchOps().compareBranch()`).
8. Artifact types are resolved via **batch queries** (single `andIds()` call per workflow, not per change item).
9. Results are written to Excel XML via `ExcelXmlWriter`.

### Performance Design

The inner loop (per change item) must **never** make individual database queries. All artifact type resolution is done in batch:

1. Collect all artifact IDs from change items.
2. Batch-load from parent branch: `orcsApi.getQueryFactory().fromBranch(parentBranch).includeDeletedAttributes(true).includeDeletedArtifacts(true).andIds(allIds).asArtifactTokens()`
3. Fallback batch-load unresolved IDs from working branch.
4. Safety/security impact counts use batch `getCount()` queries with `andIds(swReqArtIds)`.

Branch resolution (`getBranch`, parent branch lookup) is done **once per workflow**, outside the change item loop.

### Important Patterns

- **Do NOT use `ArtifactReadable.getRelated(relationType, Class<T>)`** — that overload does not exist. Use `getRelated(RelationTypeSide)` which returns `ResultSet<ArtifactReadable>`, then convert with `atsApi.getWorkItemService().getTeamWf(art)`.
- **Do NOT load versions from ATS cache** when you need their relations. Use `orcsApi.getQueryFactory()` with `followFork` to get the `ArtifactReadable` directly with relations pre-loaded.
- **Do NOT make per-item database queries inside loops.** Always batch-resolve using `andIds(Collection)`.
- The `atsApi.getRelationResolver().getRelated(IAtsObject, RelationTypeSide, Class)` pattern works for IAtsObject instances but not for raw ArtifactReadable.
- Read cached attributes (like `BranchDiffData`) directly from the `ArtifactReadable` store object rather than going through `atsApi.getAttributeResolver()`.

### Client BLAM (SoftwareReqVolatilityMetricsBlam)

- The BLAM `runOperation` method runs on a background Eclipse Job thread.
- **Use `Displays.pendInDisplayThread()`** (not `ensureInDisplayThread`) when reading SWT widget values from a background thread. `ensureInDisplayThread` uses `asyncExec` which is fire-and-forget — the background thread continues before the widget value is read.
- The REST call and file I/O must **never** run on the display thread. Only widget access needs the display thread.

### Report Columns

| Column | Description |
|--------|-------------|
| Action Id | Parent action ATS ID |
| Workflow Id | Team workflow ATS ID |
| Action Name | Workflow name |
| Program | Team definition name (from version's TeamDefinitionToVersion relation) |
| Build | The target version ID |
| Creation Date | Workflow creation date |
| Verify/Complete Date | Date the workflow entered its terminal state |
| Added/Modified/Deleted (Software Reqs) | Change counts for AbstractSoftwareRequirement |
| Added/Modified/Deleted (Subsystem Details) | Change counts for AbstractSubsystemRequirement |
| Added/Modified/Deleted (Heading Details) | Change counts for AbstractHeading |
| Added/Modified/Deleted (Impl Details) | Change counts for AbstractImplementationDetails |
| Safety Related Requirements | Count of changes with IDAL A/B/C (only when countImpacts=true) |
| Security Related Requirements | Count of changes with PotentialSecurityImpact (only when countImpacts=true) |

### Change Item Classification

Change items are only counted if:
- `ChangeType` is `Attribute`
- `IgnoreType` is `NONE`
- Attribute type is `WordTemplateContent` or `PlainTextContent`

The modification type determines added/modified/deleted:
- `NEW` → added
- `MODIFIED` or `MERGED` → modified
- `DELETED` or `ARTIFACT_DELETED` → deleted

## Testing

The integration test (`SoftwareReqVolatilityMetricsTest`) uses demo data:
- `DemoArtifactToken.SAW_Bld_2` as the target version
- Tests are registered in `AtsTest_Report_Suite`
- Note: The integration test runs in-process (single JVM) where the ATS cache is fully loaded. The cache-vs-direct-query issue only manifests in deployed client-server topology.

Run via the ATS IDE integration test suite. The test validates response status, Excel XML structure, column headers, and version data presence.
