---
inclusion: manual
---

# Build Impact Table (BIT) Architecture

The Build Impact Table is an XViewer-based editor tab in the ATS Workflow Editor that shows which builds/programs are impacted by a Change Request (CR) team workflow, and tracks the state of each impact and its sibling team workflows.

## Package Structure

- **Model**: `org.eclipse.osee.ats.api.workflow.cr.bit.model`
- **UI/Table**: `org.eclipse.osee.ats.ide.editor.tab.bit`
- **Server endpoint**: `atsApi.getServerEndpoints().getActionEndpoint().getBidsById(teamWfArtId)`

## Data Model

### BuildImpactDatas
Collection wrapper returned from the server REST endpoint. Contains:
- `buildImpacts` — list of `BuildImpactData` entries
- `teamWf` — the parent CR team workflow artifact token
- `teamWfToBidMap` — maps child `JaxTeamWorkflow` tokens to their parent `BuildImpactData` (used for tree parent lookup)
- `results` — `XResultData` for error reporting
- `transaction` — the transaction ID for the data
- `bidArtType` — the artifact type token for BID artifacts

### BuildImpactData
A single build impact row. Contains:
- `bidArt` — artifact token for this BID artifact
- `program` — the impacted program (ArtifactToken)
- `build` — the impacted build/version (ArtifactToken)
- `state` — current state string (defaults to "Open")
- `teamWfs` — list of `JaxTeamWorkflow` child workflows created for this impact
- `teamWfIds` — list of artifact IDs for the team workflows
- `bids` — back-reference to parent `BuildImpactDatas`

### BuildImpactState
Enum-like class (extends `OseeEnum`) with valid states:
- **Open** (111L)
- **InWork** (222L)
- **Promoted** (333L)
- **Closed** (444L)
- **Deferred** (555L)
- **Cancelled** (666L)

### JaxTeamWorkflow
Lightweight DTO for child team workflows shown as children in the tree. Fields include:
- `atsId`, `name`, `currentState`, `stateType`, `teamName`, artifact token

## XViewer Table Components

### XBitXViewerFactory
- Namespace: `"BitXViewer"`
- Columns:
  - **Program** (`ats.bit.program`, 150px) — program name
  - **Build** (`ats.bit.build`, 150px) — build/version name
  - **Config** (`ats.bit.config`, 60px) — configuration
  - **State** (`ats.bibit.state`, 80px, **multi-editable**) — BIT state (Open, InWork, etc.)
  - **Id** (`ats.bibit.cr.id`, 65px) — artifact ID
  - **CR State** (`ats.bibit.cr.state`, 140px) — completion status of child workflows
  - **CR Type** (`ats.bibit.cr.type`, 130px) — child workflow team name
  - **CR Title** (`ats.bibit.cr.title`, 480px) — child workflow title

### XBitViewer (extends TaskXViewer)
- Holds `BuildImpactDatas bids`
- `loadTable()` — sets input to `bids` and triggers content provider
- `handleAltLeftClick()` — on State column, opens state change dialog
- `handleColumnMultiEdit()` — multi-select state change
- `getSelectedBuildImpactDatas()` — returns selected BID rows
- `getSelectedArtifacts()` — resolves JaxTeamWorkflow or BuildImpactData to actual Artifacts

### XBitContentProvider (ITreeContentProvider)
Two-level tree structure:
```
BuildImpactDatas (input)
  └── BuildImpactData[] (top-level rows — one per impacted build)
        └── JaxTeamWorkflow[] (child rows — sibling team workflows)
```

- `getChildren(BuildImpactDatas)` → `bids.getBuildImpacts().toArray()`
- `getChildren(BuildImpactData)` → `bid.getTeamWfs().toArray()`
- `getParent(ArtifactToken)` → uses `teamWfToBidMap` for child→parent lookup

### XBitLabelProvider (extends XViewerLabelProvider)
Column text dispatch by element type:

**For BuildImpactData rows:**
| Column | Value |
|--------|-------|
| Program | `bid.getProgram().getName()` |
| Build | `bid.getBuild().getName()` |
| State | `bid.getState()` |
| Id | `bid.getBidArt().getIdString()` |
| CR State | "N of M Completed" or "None Created" |

**For JaxTeamWorkflow rows (children):**
| Column | Value |
|--------|-------|
| Id | `teamWf.getAtsId()` |
| CR State | `teamWf.getCurrentState()` |
| CR Title | `teamWf.getName()` |
| CR Type | `teamWf.getTeamName()` |

**Images:**
- Program column: PROGRAM icon (BuildImpactData rows)
- Build column: VERSION icon (BuildImpactData rows)
- State column: STATE icon (BuildImpactData rows)
- CR Type column: WORKFLOW icon (JaxTeamWorkflow rows)

## Tab and Data Loading (WfeBitTab)

### WfeBitTab (extends WfeAbstractTab)
- Tab ID: `"ats.bit.tab"`, title: "Build Impact Table"
- Implements `IArtifactEventListener`, `IArtifactTopicEventListener` for live refresh

### Data Loading Flow
```
WfeBitTab.refresh()
  │
  ├── Background Job: atsApi.getServerEndpoints().getActionEndpoint().getBidsById(teamWfArtId)
  │     └── Returns BuildImpactDatas from server REST endpoint
  │
  └── UI Thread:
        ├── storeExpandState() — saves which BIDs are expanded
        ├── xViewer.setBids(bids)
        ├── xViewer.loadTable() → setInput(bids) → content provider resolves tree
        └── restoreExpandState() — re-expands previously expanded items
```

### Event Handling
- Listens for artifact events on `BuildImpactData` type artifacts
- Listens for team workflow artifacts that have `BuildImpactDataToTeamWf_Bid` relation
- Auto-refreshes the table when related artifacts change

## Actions

- **ChangeBitState** — changes BIT state via alt-click or multi-edit on State column
- **RemoveBitWorkflowAction** — admin action to remove a BID workflow
- **WfeBitToolbar** — toolbar with additional BIT-related actions
- **WfeBitDragAndDrop** — supports drag-and-drop onto the BIT table

## Key Relationships

```
CR Team Workflow (parent)
  │
  ├── BuildImpactDataToTeamWf_Bid relation
  │
  └── BuildImpactData artifacts (one per impacted build)
        │
        └── JaxTeamWorkflow (sibling team workflows, created per BID)
```

## Extension Points

- `WfeBitTab.creatingSibling()` — hook for adding info during sibling creation
- `WfeBitTab.isValidBidWorkflow()` — validates artifacts for BID operations
- `WfeBitTab.getBuildImpactDataType()` — returns artifact type (extensible)
- `setXBitXViewerFactoryAms()` — allows overriding the factory for custom columns

## Configurable Build Sort Order

The BIT table supports a configurable sort order for the Build column, allowing builds to be displayed in a project-specific sequence rather than alphabetically.

### Configuration

A `GeneralData` artifact named `BitUtil.BIT_BUILD_ORDER_ART_NAME` ("Build Impact Table - Build Order") is stored as a child of the Team Definition (e.g., SAW PL PR TeamDef). Its `GeneralStringData` attribute contains build names in the desired order, one per line:

```
SAW_PL_SBVT1
SAW_PL_SBVT2
SAW_PL_SBVT3
```

### Database Initialization

Created via `IAtsConfigTxTeamDef.andBitBuildOrder(String... buildNames)`:

```java
sawPlTeam.createChildTeamDef(sawPlTeam.getTeamDef(), DemoArtifactToken.SAW_PL_PR_TeamDef)
   .andBitBuildOrder("SAW_PL_SBVT1", "SAW_PL_SBVT2", "SAW_PL_SBVT3")
```

### Loading and Caching

- `WfeBitTab.loadBitBuildOrder()` reads the artifact from the Team Def's children
- Result is cached in `cachedBuildOrder` for the editor session lifetime (loaded once, not on every refresh)
- The order is set on `BuildImpactDatas.buildOrder` and passed to the viewer

### XViewer Sort Integration

- `XViewerColumn.sortOrder` — a `List<String>` field on any XViewer column that defines a fixed sort order
- `XViewerSorter.getCompareForSortOrder()` — checks the column's `sortOrder` before standard sort types
- `XBitXViewerFactory.getDefaultTableCustomizeData()` — sets Build as the default sort column (forward)
- `XBitViewer.loadTable()` — sets the `sortOrder` on the Build column and pre-sorts input for initial display

### Behavior

- If build order is configured: items sort by the list position (SBVT1, SBVT2, SBVT3)
- If not configured: standard alphabetical sort applies (no behavior change)
- User can click Build column header to reverse the configured order
- User can click other columns to sort by those instead (standard XViewer behavior)
- Customizations save/load the sort column ID and direction but NOT the `sortOrder` list (it's always loaded fresh from the Team Def)

### Key Constants

- `BitUtil.BIT_BUILD_ORDER_ART_NAME` — artifact name constant shared between db init and table loading
- `BitUtil.BIT_AI` — existing constant for BIT actionable item tag
