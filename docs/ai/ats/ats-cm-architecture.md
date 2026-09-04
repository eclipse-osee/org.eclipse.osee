---
summary: "Architecture and design of the ATS (Action Tracking System) for CM workflows including PCR, Problem Report, BIT, and Change Request tracking"
tags: [ats, cm, workflow, pcr, change-request, problem-report, build-impact, bit, team-definition, actionable-item, version]
fileMatch: "**/org.eclipse.osee.ats*/**"
---

# ATS (Action Tracking System) Architecture for Configuration Management

## Overview

The Action Tracking System (ATS) is the core workflow and configuration management engine within OSEE. It implements formal change management processes (PCR/CR tracking) using a state-machine workflow model with configurable team structures, actionable items, and version-based release management.

ATS tracks all work through **Actions** that spawn **Team Workflows**, each governed by a **Work Definition** (state machine) and owned by a **Team Definition** which links to **Actionable Items** and **Versions**.

---

## High-Level Problem/Change Flow

ATS supports two primary entry paths for change management:

```
Problem Report (PR)
  |  reports an issue for analysis
  v
Build Impact Table (BIT) / BID artifacts
  |  mark which builds/programs are impacted
  v
Change Requests (CRs) created from BITs
  |  implement the fix (Req, Code, Test workflows)

Feature / Improvement (no PR)
  |  enhancement request or planned work
  v
Change Requests (CRs) created directly
  |  implement the work (Req, Code, Test workflows)
```

**Summary:**
- **Problem Reports (PRs)** capture problems that require analysis. They determine which builds/programs and functional areas are impacted.
- **Build Impact Table (BIT) / BID artifacts** are created to identify which builds are impacted by the problem. For each impacted build, sibling team workflows (CR team workflows) are created.
- **Change Requests (CRs)** perform the work to fix the problem for each impacted build. Child workflows represent disciplines such as Requirements, Code, and Test. BIT states track progress through Open → InWork → Promoted → Closed.
- **For features or improvements**, CRs are created directly without a Problem Report; BIT can still track build impacts and sibling workflows.

---

## Problem Report (PR) Concept

A Problem Report is the entry point for reporting a problem that requires engineering analysis. It is marked with `WorkDefOption.IsProblemReport` and uses artifact type `ProblemReportTeamWorkflow`.

### Purpose

- Capture the problem description, how it was found, and field conditions (ship, test number, flight number)
- Analyze root cause, system impact, and proposed resolution
- Determine which builds/programs are impacted (via the Build Impact Table)
- Track the problem through analysis and resolution lifecycle

### PR Workflow States

```
Open (START) --> Analyzed --> Closed
    |               |
    v               v
  Monitor <------+
    |
    v
  Closed

(All working states can also transition to Cancelled)
(Monitor can transition BACK to Open or Analyzed)
```

| State | Type | Purpose |
|-------|------|---------|
| Open | Working | Initial report: problem description, build impact, analysis fields, manager signoff |
| Analyzed | Working | Problem fully analyzed (reuses Open layout) |
| Monitor | Working | Watch-and-wait state; can regress to Open/Analyzed |
| Closed | Completed | Problem resolved |
| Cancelled | Cancelled | Problem report withdrawn |

### Key PR Attributes

- Description, How Found, Ship, Test Number, Flight Number, Test Date
- Crash/Blank Display, Found-In Version, Introduced-In Version
- System Analysis, Software Analysis, Proposed Resolution
- Feature(s) Impacted, Applicability, Customer Description
- Manager Signoff (required in Open state)
- Build Impacts (via BIT tab)

### PR → BIT Relationship

Once a PR is created and analysis identifies impacted builds, the **Build Impact Table (BIT)** tab on the PR workflow editor shows which programs/builds are affected. Each BIT row (`BuildImpactData` artifact) tracks:
- The impacted program and build/version
- A state (Open, InWork, Promoted, Closed, Deferred, Cancelled)
- Child CR team workflows created to fix the problem for that specific build

---

## Build Impact Table (BIT) Integration

The BIT bridges Problem Reports (or standalone CRs) to the actual work of fixing/implementing changes across builds.

### Data Model

| Class | Purpose |
|-------|---------|
| `BuildImpactDatas` | Collection wrapper: list of BID entries + parent team workflow reference |
| `BuildImpactData` | Single impacted build row: program, build/version, state, child team workflows |
| `BuildImpactState` | State enum: Open, InWork, Promoted, Closed, Deferred, Cancelled |
| `JaxTeamWorkflow` | Lightweight DTO for child CR workflows (atsId, name, state, team) |

### BIT States

| State | Meaning |
|-------|---------|
| Open | Build impact identified, no work started |
| InWork | CR workflows created and actively being worked |
| Promoted | Changes promoted/committed for this build |
| Closed | All work complete for this build |
| Deferred | Impact deferred to a later release |
| Cancelled | Impact determination cancelled |

### Tree Structure (BIT Tab)

```
BuildImpactDatas (input = parent CR/PR team workflow)
  +-- BuildImpactData (one per impacted build)
  |     +-- JaxTeamWorkflow (Requirements CR)
  |     +-- JaxTeamWorkflow (Code CR)
  |     +-- JaxTeamWorkflow (Test CR)
  +-- BuildImpactData (another impacted build)
        +-- JaxTeamWorkflow (...)
```

### Key Relationships

```
PR/CR Team Workflow (parent)
  |
  +-- BuildImpactDataToTeamWf_Bid relation
  |
  +-- BuildImpactData artifacts (one per impacted build)
        |
        +-- JaxTeamWorkflow (sibling team workflows created per BID)
```

### BIT Tab UI

- Tab ID: `"ats.bit.tab"`, shown in the ATS Workflow Editor
- XViewer columns: Program, Build, Config, State, Id, CR State, CR Type, CR Title
- State column is multi-editable (alt-click or multi-select)
- Supports drag-and-drop, live event refresh
- Configurable build sort order via `GeneralData` artifact on Team Definition

### Model Package

- **API model:** `org.eclipse.osee.ats.api.workflow.cr.bit.model`
- **IDE tab:** `org.eclipse.osee.ats.ide.editor.tab.bit`
- **Server endpoint:** `atsApi.getServerEndpoints().getActionEndpoint().getBidsById(teamWfArtId)`

---

## Bundle Architecture

ATS follows a layered OSGi architecture:

```
+-----------------------------------+
|  org.eclipse.osee.ats.ide        |  Eclipse RCP UI: editors, views, navigators
+-----------------------------------+
|  org.eclipse.osee.ats.rest       |  JAX-RS REST endpoints (AtsApiServer)
+-----------------------------------+
|  org.eclipse.osee.ats.core       |  Shared domain logic (server + IDE)
+-----------------------------------+
|  org.eclipse.osee.ats.api        |  Pure interfaces, domain model, DTOs
+-----------------------------------+
|  org.eclipse.osee.framework.*    |  OSEE Framework (OseeApi, OrcsApi, data layer)
+-----------------------------------+
```

| Bundle | Purpose |
|--------|---------|
| `org.eclipse.osee.ats.api` | Interfaces, domain model, artifact types, attribute types. Zero implementation. |
| `org.eclipse.osee.ats.core` | Shared logic used by server and IDE: services, work def builders, transition engine |
| `org.eclipse.osee.ats.rest` | Server-side REST endpoints via `AtsApiServer` (extends `AtsApi` with `OrcsApi` access) |
| `org.eclipse.osee.ats.ide` | Eclipse IDE client: editors, views, BLAMs, navigators |
| `org.eclipse.osee.ats.core.demo` | Demo/sample data population and demo work definitions |

---

## Central Service Facade: `AtsApi`

`AtsApi` (extends `OseeApi`) is the single entry point aggregating all ATS services:

| Method | Returns | Purpose |
|--------|---------|---------|
| `getTeamDefinitionService()` | `IAtsTeamDefinitionService` | Team hierarchy, branch policies, AI resolution |
| `getActionableItemService()` | `IAtsActionableItemService` | AI hierarchy and lookup |
| `getVersionService()` | `IAtsVersionService` | Version/release management |
| `getWorkDefinitionService()` | `IAtsWorkDefinitionService` | Work def resolution and computation |
| `getActionService()` | `IAtsActionService` | Action/workflow creation |
| `getTaskService()` | `IAtsTaskService` | Sub-task management |
| `getQueryService()` | `IAtsQueryService` | Search and query |
| `getBranchService()` | `IAtsBranchService` | Working branch lifecycle |
| `getReviewService()` | `IAtsReviewService` | Decision and peer reviews |
| `getConfigService()` | `IAtsConfigurationsService` | Central config cache (`AtsConfigurations`) |
| `createChangeSet(comment)` | `IAtsChangeSet` | Persist changes atomically |

**Key source:** `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/AtsApi.java`

---

## Domain Model

### Type Hierarchy

```
IAtsObject (NamedId + Description + ArtifactType)
  |
  +-- IAtsConfigObject (+ isActive())
  |     +-- IAtsTeamDefinition
  |     +-- IAtsActionableItem
  |     +-- IAtsVersion
  |
  +-- IAtsWorkItem (+ state, assignees, lifecycle, workDefinition)
        +-- IAtsTeamWorkflow (+ teamDef, workingBranch, actionableItems)
        +-- IAtsTask
        +-- IAtsGoal
        +-- IAtsAbstractReview
              +-- Decision Review
              +-- Peer-to-Peer Review
```

### Artifact Type Hierarchy (Persisted)

```
Artifact
  +-- ats.Ats Artifact (abstract, id=63)
  |     +-- ats.Workflow Artifact / AbstractWorkflowArtifact (abstract, id=71)
  |           +-- Team Workflow (id=73)
  |           |     +-- ProblemReportTeamWorkflow (id=6410317324151198012)
  |           |     +-- ChangeRequestTeamWorkflow (id=4938)
  |           +-- Goal (id=72)
  |           |     +-- Agile Sprint / Agile Backlog
  |           +-- Task (id=74)
  |           +-- ats.Review (abstract, id=64)
  |                 +-- Decision Review (id=66)
  |                 +-- Peer-To-Peer Review (id=65)
  |
  +-- ats.Ats Config Artifact (abstract, id=801)
        +-- ats.Ats Team Definition or AI (abstract, id=803)
        |     +-- Team Definition (id=68)
        |     +-- Actionable Item (id=69)
        +-- Version (id=70)
        +-- Program, Country, Insertion, InsertionActivity
```

**Key source:** `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/data/AtsArtifactTypes.java`

---

## Configuration Objects

### Team Definition

Represents an organizational team that owns workflow execution.

| Field | Type | Description |
|-------|------|-------------|
| `parentId` | Long | Parent TeamDef ID (hierarchy) |
| `ais` | Set\<Long\> | Linked Actionable Item IDs |
| `versions` | Set\<Long\> | Version IDs owned by this team |
| `children` | Set\<Long\> | Child TeamDefinition IDs |
| `workTypes` | List\<WorkType\> | What work this team handles |
| `programId` | String | Associated program identifier |
| `cscis` | List\<String\> | CSCI identifiers |
| `active` | boolean | Whether this team is active |

**Persisted attributes on the artifact include:** `WorkflowDefinitionReference`, `TeamWorkflowArtifactType`, `RequireTargetedVersion`, `TeamUsesVersions`, `BaselineBranchId`, `AllowCreateBranch`, `AllowCommitBranch`, `AtsIdPrefix`, `AtsIdSequenceName`, `WorkType` (multi-valued), `RelatedTaskWorkflowDefinitionReference` (multi), `RelatedPeerWorkflowDefinitionReference` (multi).

**Key source:** `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/config/TeamDefinition.java`

### Actionable Item

Represents a "thing" users file actions against. Forms a hierarchy of its own.

| Field | Type | Description |
|-------|------|-------------|
| `parentId` | Long | Parent AI in hierarchy |
| `teamDefId` | Long | Owning TeamDefinition (the critical AI→Team link) |
| `children` | Set\<Long\> | Child AI IDs |
| `actionable` | boolean | Whether this AI can be directly selected by users |
| `allowUserActionCreation` | boolean | Whether users can create actions here |

**Key relationship:** When a user creates an Action and selects AIs, the system resolves impacted teams via `IAtsTeamDefinitionService.getImpactedTeamDefs(Collection<IAtsActionableItem>)`.

**Key source:** `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/ai/ActionableItem.java`

### Version

Represents a software release/baseline that workflows target.

| Field/Method | Type | Description |
|--------------|------|-------------|
| `getBaselineBranch()` | BranchId | OSEE branch for this version's code baseline |
| `isAllowCreateBranch()` | boolean | Can working branches be created from this baseline? |
| `isAllowCommitBranch()` | boolean | Can working branches be committed to this baseline? |
| `isReleased()` | boolean | Has this version been released? |
| `isLocked()` | boolean | Is this version locked to changes? |
| `isNextVersion()` | boolean | Is this the upcoming release? |
| `getClosureState()` | String | Closure/state tracking |

Versions are owned by a team (or inherited up the team hierarchy). Workflows target a version for release scheduling.

**Key source:** `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/version/IAtsVersion.java`

### Configuration Cache: `AtsConfigurations`

All config objects are loaded into a central in-memory cache:

```java
Map<Long, ActionableItem> idToAi;
Map<Long, TeamDefinition> idToTeamDef;
Map<Long, Version> idToVersion;
Map<Long, AtsUser> idToUser;
Map<Long, JaxProgram> idToProgram;
Map<Long, Long> teamDefToProgram;
Map<String, String> atsConfig; // key/value config pairs
```

Accessed via `AtsApi.getConfigService().getConfigurations()`.

**Key source:** `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/config/AtsConfigurations.java`

---

## Work Definition (State Machine Model)

### WorkDefinition

A complete workflow definition containing:

| Field | Type | Description |
|-------|------|-------------|
| `states` | List\<StateDefinition\> | Ordered list of states |
| `startState` | StateDefinition | Initial state of the workflow |
| `headerDef` | HeaderDefinition | Shared header widgets across all states |
| `options` | List\<WorkDefOption\> | Workflow-level options (see below) |
| `changeTypes` | List\<ChangeTypes\> | Allowed change type values |
| `priorities` | List\<Priorities\> | Allowed priority values |
| `transitionHooks` | List\<IAtsTransitionHook\> | Global transition hooks |
| `conditions` | List\<ConditionalRule\> | Conditional behavior rules |
| `createTasksDefs` | List\<CreateTasksDefinition\> | Auto-task-creation definitions |
| `pcrIdComparator` | Comparator\<String\> | Custom PCR ID sorting |

**WorkDefOption values:**

| Option | Effect |
|--------|--------|
| `IsChangeRequest` | Marks workflow as a Change Request (affects UI, cloning restrictions) |
| `IsProblemReport` | Marks workflow as a Problem Report |
| `RequireTargetedVersion` | Blocks transition without a targeted version |
| `RequireAssignee` | Requires assignee for transitions |
| `NoTargetedVersion` | Disables version targeting entirely |

**Key source:** `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/workdef/model/WorkDefinition.java`

### StateDefinition

Each state in the workflow:

| Field | Type | Description |
|-------|------|-------------|
| `StateType` | StateType | `Working`, `Completed`, or `Cancelled` |
| `ordinal` | int | Ordering of states |
| `toStates` | List\<StateDefinition\> | Valid transition targets |
| `stateItems` | List\<LayoutItem\> | UI widgets for this state |
| `ruleMgr` | RuleManager | Named rules (e.g., `RequireTargetedVersion`, `ForceAssigneesToTeamLeads`) |
| `decisionReviews` | List\<IAtsDecisionReviewDefinition\> | Auto-triggered decision reviews |
| `peerReviews` | List\<IAtsPeerReviewDefinition\> | Auto-triggered peer reviews |
| `transitionListeners` | List\<IAtsTransitionHook\> | State-specific transition hooks |
| `transitionUserGroup` | IUserGroupArtifactToken | Restricts who can transition into this state |
| `recommendedPercentComplete` | Integer | Suggested % when in this state |
| `color` | StateColor | UI rendering color |

**Key source:** `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/workdef/model/StateDefinition.java`

### StateType

```java
enum StateType {
   Working,    // Active state where work happens
   Completed,  // Terminal success state
   Cancelled   // Terminal abort state
}
```

---

## Workflow Types for PCR/CM Tracking

### Change Request (CR) Workflow

Marked with `WorkDefOption.IsChangeRequest`. Artifact type: `ChangeRequestTeamWorkflow`.

**State flow:**
```
Endorse (START) --> Analyze --> Authorize --> Implement --> Completed
    |                  |            |             |
    v                  v            v             v
 Cancelled         Cancelled     Cancelled     Cancelled
```

| State | Type | Purpose |
|-------|------|---------|
| Endorse | Working | Initial problem statement, description, impact assessment |
| Analyze | Working | Root cause analysis, risk assessment, proposed resolution |
| Authorize | Working | Management approval, resource authorization |
| Implement | Working | Actual code/design changes |
| Completed | Completed | Terminal success |
| Cancelled | Cancelled | Terminal abort |

**Endorse state captures:** External Reference, Description, NeedBy, How to Reproduce, Workaround, Crash/Blank Display, Impact to Mission/Crew, Feature Impact, Found-In/Introduced-In Version, Problem First Observed date.

**Analyze state captures:** Risk Analysis, Revisit Date, Description, Workaround, Root Cause, Proposed Resolution, Task Estimates.

**Key source:** `plugins/org.eclipse.osee.ats.core/src/org/eclipse/osee/ats/core/demo/WorkDefTeamDemoChangeRequest.java`

### Problem Report (PR) Workflow

Marked with `WorkDefOption.IsProblemReport` and `WorkDefOption.NoTargetedVersion`. Artifact type: `ProblemReportTeamWorkflow`.

See also: [Problem Report (PR) Concept](#problem-report-pr-concept) and [Build Impact Table (BIT) Integration](#build-impact-table-bit-integration) above for the full PR → BIT → CR flow.

**State flow:**
```
Open (START) --> Analyzed --> Closed
    |               |
    v               v
  Monitor <------+
    |
    v
  Closed

(All working states can also transition to Cancelled)
(Monitor can transition BACK to Open or Analyzed)
```

| State | Type | Purpose |
|-------|------|---------|
| Open | Working | Initial report: problem description, build impact, analysis, manager signoff |
| Analyzed | Working | Problem analyzed (reuses Open layout) |
| Monitor | Working | Watch-and-wait; can regress back to Open/Analyzed |
| Closed | Completed | Terminal success |
| Cancelled | Cancelled | Terminal abort |

**Key differences from CR:**
- No targeted version requirement (`WorkDefOption.NoTargetedVersion`)
- Has a "Monitor" state (watch-and-wait)
- Backward transitions allowed (Monitor → Open, Monitor → Analyzed)
- Requires Manager Signoff in Open state
- Captures field-specific data: Ship, Test Number, Flight Number, Test Date, System/Software Analysis
- Uses the Build Impact Table (BIT tab) to identify impacted builds and spawn sibling CR workflows

**Key source:** `plugins/org.eclipse.osee.ats.core/src/org/eclipse/osee/ats/core/demo/WorkDefTeamDemoProblemReport.java`

### Standard Team Workflow

For general work items not classified as CR or PR:

```
Endorse (START) --> Analyze --> Implement --> Completed
                                                |
                                            Cancelled
```

Default work definition token: `AtsWorkDefinitionTokens.WorkDefTeamDefault`

---

## Action and Workflow Creation Flow

```
User selects Actionable Items
         |
         v
System resolves impacted TeamDefinitions
  (via IAtsTeamDefinitionService.getImpactedTeamDefs())
         |
         v
Creates one IAtsAction (top-level grouping)
         |
         v
For EACH impacted team:
  Creates one IAtsTeamWorkflow
    - Links to TeamDefinition
    - Links to selected ActionableItems
    - Resolves WorkDefinition (from TeamDef.WorkflowDefinitionReference or hierarchy)
    - Sets artifact type (from TeamDef.TeamWorkflowArtifactType or default)
    - Initializes state machine at startState
    - Assigns to team leads or specified users
```

### Stored References on Created Workflows

When a Team Workflow is created, the following are stored directly on the workflow artifact as attributes:
- **TeamDefinitionReference** (artifact ID of the Team Definition)
- **ActionableItemReference** (artifact IDs of the selected Actionable Items, multi-valued)
- **WorkflowDefinitionReference** (token ID of the WorkDefinition governing the workflow)

Tasks and Reviews are children of Team Workflows (via `TeamWfToTask` and `TeamWorkflowToReview` relations) and do not store their own Team Definition or Actionable Item references -- they inherit context from their parent Team Workflow.

**Exception:** Stand-alone Reviews (not children of a Team Workflow) do store `ActionableItemReference` since they have no parent workflow to inherit from.

**Key API:**
```java
NewActionData data = atsApi.getActionService().createActionData("op", "Title", "Desc")
    .andChangeType(ChangeTypes.Problem)
    .andPriority("3")
    .andAis(actionableItems);
NewActionData result = atsApi.getActionService().createAction(data);
```

---

## Work Definition Resolution

`IAtsWorkDefinitionService` resolves which `WorkDefinition` applies:

1. Check `WorkflowDefinitionReference` attribute on the work item itself
2. Check `WorkflowDefinitionReference` attribute on the team definition
3. Walk up the team definition hierarchy looking for a reference
4. Fall back to `AtsWorkDefinitionTokens.WorkDefTeamDefault`

Work definitions are registered via `IAtsWorkDefinitionProviderService` which collects all `IAtsWorkDefinitionProvider` implementations (OSGi declarative services pattern). Each provider returns a collection of `WorkDefinition` objects built using the `WorkDefBuilder` API.

### Work Definition Assignment Chain

Each `WorkDefinition` has a token with an ID (e.g., `AtsWorkDefinitionToken(72301L, "WorkDefTeamDefault")`). This ID is stored as the `WorkflowDefinitionReference` attribute on the Team Definition and/or the workflow itself.

The assignment chain for a new workflow is:

```
User selects ActionableItem(s)
  --> resolves to TeamDefinition (via teamDefId on AI)
    --> TeamDefinition.WorkflowDefinitionReference determines the WorkDefinition
      --> WorkDefinition (state machine) governs the workflow lifecycle
```

Different Team Definitions can use different Work Definitions. For example:
- "SAW PL Code" team might use `WorkDefTeamDefault` (Endorse -> Analyze -> Implement -> Completed)
- "SAW PL PR" team uses `WorkDefTeamDemoProblemReport` (Open -> Analyzed -> Monitor -> Closed)
- A CR team uses `WorkDefTeamDemoChangeRequest` (Endorse -> Analyze -> Authorize -> Implement -> Completed)

The `TeamWorkflowArtifactType` attribute on the Team Definition also controls which artifact subtype is created (e.g., `ProblemReportTeamWorkflow`, `ChangeRequestTeamWorkflow`, or plain `TeamWorkflow`).

**Key source:** `plugins/org.eclipse.osee.ats.core/src/org/eclipse/osee/ats/core/workdef/internal/AtsWorkDefinitionProviderService.java`

---

## Transition Control

### IAtsTransitionHook

The primary mechanism for controlling workflow transitions:

| Method | Phase | Use Case |
|--------|-------|----------|
| `transitioning(results, workItem, from, to, assignees, user, atsApi)` | Pre-transition | Validate and block if errors added to results |
| `transitioned(workItem, from, to, assignees, user, changes, atsApi)` | During persist | Add changes to the same transaction |
| `transitionPersisted(workItems, stateMap, toState, user, atsApi)` | Post-persist (foreground) | Immediate post-processing |
| `transitionPersistedBackground(workItems, stateMap, toState, user, atsApi)` | Post-persist (background) | Long-running post-processing |
| `getOverrideTransitionToStateName(workItem)` | Pre-transition | Dynamically redirect to a different state |

Hooks can be registered at the **WorkDefinition level** (global for all transitions) or **StateDefinition level** (specific to transitions from that state).

**Key source:** `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/workflow/hooks/IAtsTransitionHook.java`

### State Rules (RuleDefinitionOption)

Rules applied to individual states:

| Rule | Effect |
|------|--------|
| `RequireStateHourSpentPrompt` | Popup for hours spent on transition |
| `AddDecisionValidateBlockingReview` | Auto-create blocking decision review |
| `AddDecisionValidateNonBlockingReview` | Auto-create non-blocking review |
| `AllowTransitionWithWorkingBranch` | Allow transition without committing branch |
| `ForceAssigneesToTeamLeads` | Reassign to team leads on entering state |
| `RequireTargetedVersion` | Block transition if no version targeted |
| `RequireAssignee` | Require assignee for transition |
| `AllowTransitionWithoutTaskCompletion` | Allow transition with incomplete tasks |

---

## Classification Types

### ChangeTypes (Extensible Enum)

Classifies the nature of a change:

| Value | Description |
|-------|-------------|
| `Problem` | Bug fix |
| `Improvement` | Enhancement |
| `Refinement` | Small change |
| `Support` | Support-related |
| `InitialDev` | Initial development |
| `Fix` | Explicit fix |
| `None` | No type assigned |

Default set: `[Improvement, Problem, Refinement, Support]`

### WorkType (Extensible Enum)

Categorizes what type of work a team/AI handles:

| Value | Description |
|-------|-------------|
| `ProblemReport` | PR/PCR-type work |
| `ChangeRequest` | Top-level change request |
| `Code`, `MissionCode` | Code development |
| `Requirements`, `ImplDetails` | Requirements work |
| `Software`, `Hardware`, `Systems` | Discipline-specific |
| `Test`, `SoftwareTest`, `IntegrationTest` | Testing |
| `SW_Design`, `SW_TechAppr` | Design activities |
| `MIM` | Message Interface Modeling |
| `ARB` | Architecture Review Board |

---

## Work Definition Builder Pattern

Work definitions are constructed programmatically using the builder API:

```java
public class MyWorkDef extends AbstractWorkDef {
   public MyWorkDef() {
      super(myWorkDefToken);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andWorkDefOption(WorkDefOption.IsChangeRequest);

      bld.andState(1, "Endorse", StateType.Working).isStartState()
         .andToStates(StateToken.Analyze, StateToken.Cancelled)
         .andColor(StateColor.BLACK)
         .andLayout(
            new WidgetDef(AtsAttributeTypes.Description, "XTextDam", FILL_VERT, RFT, SAVE),
            new WidgetDef(AtsAttributeTypes.NeedBy, "XHyperlinkLabelDateDam")
         );

      bld.andState(2, "Analyze", StateType.Working)
         .andToStates(StateToken.Implement, StateToken.Completed, StateToken.Cancelled)
         .andLayout(...);

      bld.andState(3, "Completed", StateType.Completed);
      bld.andState(4, "Cancelled", StateType.Cancelled);

      return bld.getWorkDefinition();
   }
}
```

**Builder classes:**
- `WorkDefBuilder` — top-level builder
- `StateDefBuilder` — state configuration
- `AbstractWorkDef` — base class for all work definition implementations

---

## Key Relationships Diagram

```
Action (IAtsAction)
  +-- 1..* TeamWorkflow (IAtsTeamWorkflow)
         |-- references TeamDefinition (IAtsTeamDefinition)
         |     +-- owns Versions (IAtsVersion)
         |     +-- owns ActionableItems
         |     +-- has WorkflowDefinitionReference
         |     +-- has TeamWorkflowArtifactType
         |-- references 1..* ActionableItem (IAtsActionableItem)
         |     +-- links back to TeamDefinition (teamDefId)
         |-- governed by WorkDefinition
         |     +-- contains StateDefinitions (state machine)
         |     +-- has WorkDefOptions (IsChangeRequest, IsProblemReport, etc.)
         |     +-- has TransitionHooks
         |-- optionally targets a Version (IAtsVersion)
         |     +-- has baseline branch
         |     +-- release/lock state
         +-- may have child Tasks (IAtsTask) and Reviews
```

---

## Detecting CR/PR Work Items

A work item is a **Change Request** if:
- Its artifact type inherits from `AtsArtifactTypes.AbstractChangeRequestWorkflow`, OR
- Its `WorkDefinition` has `WorkDefOption.IsChangeRequest`

A work item is a **Problem Report** if:
- Its artifact type inherits from `AtsArtifactTypes.ProblemReportTeamWorkflow`, OR
- Its `WorkDefinition` has `WorkDefOption.IsProblemReport`

These checks are available via `IAtsWorkItem.isChangeRequest()` and `IAtsWorkItem.isProblemReport()`.

---

## Relevance for SysML V2 Export

When exporting ATS workflows to SysML V2 format, the key entities to map are:

1. **Work Items (TeamWorkflows)** → SysML actions or activities
2. **States and Transitions** → SysML state machines
3. **Team Definitions** → organizational blocks/participants
4. **Actionable Items** → requirements or system elements being changed
5. **Versions** → configuration baselines or releases
6. **Change Types / Work Types** → stereotypes or classifier values
7. **Assignees / Implementers** → actors/participants
8. **Problem Reports** → SysML issue/defect elements with analysis metadata
9. **Build Impact Data (BIT/BID)** → SysML allocation or impact relationships linking problems to affected configurations
10. **BIT → CR relationships** → SysML dependency/satisfy relationships showing how CRs address BIT entries

The PR → BIT → CR chain maps naturally to a SysML V2 structure:
- PR = the problem/issue element
- BIT rows = impact allocations to specific configurations/builds
- CRs = resolution actions allocated to each impacted configuration

The `AtsApi.getQueryService()` provides programmatic access to query workflows, and each `IAtsTeamWorkflow` exposes its full state, history (`getLog()`), assignees, attributes, and relationships needed for export mapping. BIT data is accessible via `atsApi.getServerEndpoints().getActionEndpoint().getBidsById(teamWfArtId)`.
