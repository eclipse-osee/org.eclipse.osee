---
summary: "Design approach for exporting ATS Program config and workflows to SysML V2 textual notation"
tags: [ats, sysml, export, program, workflow, change-request, problem-report, bit]
fileMatch: "**/org.eclipse.osee.ats*/**/sysml/**"
---

# ATS Program to SysML V2 Export — Design Approach

## Goal

Export an ATS Program's configuration objects and workflows to SysML V2 textual notation (`.sysml` files). The export produces two schemas:

1. **Config Schema** — Program, Team Definitions, Actionable Items, Versions (structural/organizational)
2. **Workflow Schema** — Actions, Team Workflows, Tasks, Reviews (work execution instances referencing config objects)

---

## Approach: Hand-Rolled Textual Output (Option 1)

No external SysML library. Generate valid SysML V2 textual syntax from a small strongly-typed Java model. Rationale:

- Zero new dependencies in the OSEE build
- The SysML V2 subset needed is small and well-defined
- OSEE's `AttributeTypeToken` type system provides type introspection for automatic mapping
- Textual `.sysml` is the most universally importable format

---

## Core Design Principle: Flat Objects + Strongly-Typed Connections

ATS stores data as **artifacts** (flat objects with attributes) connected by **typed relation links** (from `AtsRelationTypes`). The SysML V2 export mirrors this exactly:

- Every ATS artifact → a flat `part` instance (no nesting)
- Every `part` instance carries an `attribute artifactId : Integer` — the OSEE artifact ID (64-bit), used for cross-referencing
- Every ATS relation link → a `connection` instance typed by a `connection def`
- Every `AtsRelationTypes` entry → a `connection def` with named, typed ends and multiplicity
- Every `AtsAttributeTypes` entry → a typed `attribute` (Boolean, Integer, Real, String, or generated `enum def`)
- Every `AtsArtifactTypes` entry → a `part def` with its declared attributes

**There is no parent/child nesting in the export.** All relationships are explicit, named connections. This is the correct SysML V2 idiom and it directly represents how OSEE's data model works.

### Artifact ID References

Some OSEE attributes store another artifact's ID directly (e.g., `TeamDefinitionReference` on a workflow holds the team def's artifact ID as an `Integer` / `Long`). These are typed as `isArtifactId()` on the `AttributeTypeToken`.

In the SysML export, these are represented as `Integer` attributes whose value matches the `artifactId` attribute on the target `part` instance. A consumer can join on `artifactId` to resolve the reference:

```sysml
// Every part carries its OSEE artifact ID
part def TeamDefinition {
    attribute artifactId : Integer;  // OSEE artifact ID (64-bit) — used for cross-referencing
    attribute name : String;
    // ...
}

part def TeamWorkflow {
    attribute artifactId : Integer;
    attribute name : String;
    // TeamDefinitionReference stored as artifact ID attribute (not a relation)
    attribute teamDefinitionReference : Integer;  // value = target TeamDef's artifactId
    // ...
}

// Instances
part 'SAW PL Code' : TeamDefinition {
    attribute artifactId = 12345;
    attribute name = "SAW PL Code";
}

part 'TW00043' : TeamWorkflow {
    attribute artifactId = 67890;
    attribute teamDefinitionReference = 12345;  // → resolves to 'SAW PL Code'
}
```

This pattern applies to all `isArtifactId()` attributes: `TeamDefinitionReference`, `ActionableItemReference`, `VersionReference`, `BaselineBranchId` (where it references a branch artifact), etc. The `artifactId` field on every `part` makes them joinable.

---

## Strongly-Typed Generation via OSEE's Type System

`AttributeTypeToken` provides `isBoolean()`, `isDate()`, `isDouble()`, `isInteger()`, `isLong()`, `isEnumerated()`, `isString()`, `isArtifactId()`. For enums, `AttributeTypeEnum.getEnumValues()` returns the valid literals.

### Type Mapping

| `AttributeTypeToken` check | SysML V2 type |
|---|---|
| `isBoolean()` | `Boolean` |
| `isInteger()` | `Integer` |
| `isLong()` | `Integer` (SysML V2 integers are arbitrary precision — covers 64-bit) |
| `isDouble()` | `Real` |
| `isDate()` | `String` (ISO 8601 value) |
| `isString()` | `String` |
| `isEnumerated()` | Generated `enum def` from `getEnumStrValues()` |
| `isArtifactId()` | `Integer` (reference to another element's `artifactId`) |

### Attribute Multiplicity Mapping

OSEE artifact type declarations specify multiplicity per attribute. This maps directly to SysML V2 attribute multiplicity:

| OSEE Declaration | Meaning | SysML V2 |
|---|---|---|
| `.exactlyOne(attr)` | 1..1 | `attribute x : Type;` (implied [1]) |
| `.zeroOrOne(attr)` | 0..1 | `attribute x : Type[0..1];` |
| `.any(attr)` | 0..* | `attribute x : Type[0..*];` |

For instance data, only attributes that have actual values are emitted.

### Enum Generation Example

For `AtsAttributeTypes.ChangeType` (enumerated):

```sysml
enum def ChangeType {
    enum Improvement;
    enum Problem;
    enum Refinement;
    enum Support;
}
```

The enum values come directly from `attrType.toEnum().getEnumStrValues()`.

---

## Two-Schema Design

### Schema 1: Config Objects (`program-config.sysml`)

Exports the organizational structure for a program. Uses SysML V2 `part def` / `part` pattern (def = type, usage = instance). All relationships use `connection def` / `connection` from `AtsRelationTypes` — same pattern as workflows.

**Objects exported:**

| ATS Object | SysML V2 Element | Source |
|---|---|---|
| Program | `part def Program` + `part` instance | `AtsArtifactTypes.Program` |
| Team Definition | `part def TeamDefinition` + `part` instances | `AtsArtifactTypes.TeamDefinition` |
| Actionable Item | `part def ActionableItem` + `part` instances | `AtsArtifactTypes.ActionableItem` |
| Version | `part def Version` + `part` instances | `AtsArtifactTypes.Version` |

**Relationships (via `connection def` from `AtsRelationTypes`):**

```
Program ──(TeamDefinitionToProgram)──> TeamDefinition
TeamDefinition ──(TeamActionableItem)──> ActionableItem
TeamDefinition ──(TeamDefinitionToVersion)──> Version
TeamDefinition ──(TeamDefinitionToBitProgram)──> Program (BIT programs)
TeamDefinition ──(DefaultHierarchical)──> TeamDefinition (parent/child hierarchy)
ActionableItem ──(DefaultHierarchical)──> ActionableItem (parent/child hierarchy)
TeamDefinition ──(TeamLead)──> User
TeamDefinition ──(TeamMember)──> User
Version ──(ParallelVersion)──> Version (parallel/child versions)
```

**Attributes on each def** are derived from `AtsArtifactTypes` definitions. Every attribute defined on the artifact type gets a corresponding `attribute` in the SysML `part def`, with proper type from the mapping table above.

Example output:

```sysml
package SAW_PL_Program_Config {

    // Enum defs generated from enumerated attribute types
    enum def WorkType {
        enum Code;
        enum Requirements;
        enum 'Software Test';
        enum 'Problem Report';
        // ...
    }

    enum def ClosureState {
        // values from AtsAttributeTypes.ClosureState enum
    }

    // Part defs from artifact type attribute definitions (attributes only, no nested parts)
    part def Program {
        attribute name : String;
        attribute description : String;
        attribute namespace : String;
        attribute csci : String[*];
        attribute closureState : ClosureState;
        attribute programId : String;
    }

    part def TeamDefinition {
        attribute name : String;
        attribute active : Boolean;
        attribute workflowDefinitionReference : String;
        attribute requireTargetedVersion : Boolean;
        attribute teamUsesVersions : Boolean;
        attribute baselineBranchId : Integer;
        attribute allowCreateBranch : Boolean;
        attribute allowCommitBranch : Boolean;
        attribute atsIdPrefix : String;
        attribute workType : WorkType[*];
    }

    part def ActionableItem {
        attribute name : String;
        attribute active : Boolean;
        attribute actionable : Boolean;
        attribute allowUserActionCreation : Boolean;
        attribute workType : WorkType[*];
        attribute programId : String;
    }

    part def Version {
        attribute name : String;
        attribute active : Boolean;
        attribute baselineBranchId : Integer;
        attribute allowCreateBranch : Boolean;
        attribute allowCommitBranch : Boolean;
        attribute released : Boolean;
        attribute versionLocked : Boolean;
        attribute nextVersion : Boolean;
        attribute closureState : ClosureState;
        attribute releaseDate : String;
        attribute estimatedReleaseDate : String;
    }

    // Connection defs from AtsRelationTypes
    connection def DefaultHierarchical {
        end parent : Artifact[1];
        end child : Artifact[*];
    }

    connection def TeamDefinitionToProgram {
        end teamDefinition : TeamDefinition[*];
        end program : Program[1];
    }

    connection def TeamActionableItem {
        end teamDefinition : TeamDefinition[1];
        end actionableItem : ActionableItem[*];
    }

    connection def TeamDefinitionToVersion {
        end teamDefinition : TeamDefinition[1];
        end version : Version[*];
    }

    connection def ParallelVersion {
        end parent : Version[*];
        end child : Version[*];
    }

    connection def TeamLead {
        end teamDefinition : TeamDefinition[*];
        end user : User[*];
    }

    connection def TeamMember {
        end teamDefinition : TeamDefinition[*];
        end user : User[*];
    }

    // User (kept simple — userId and name only)
    part def User {
        attribute userId : String;
        attribute name : String;
    }

    // ─── Instances (flat parts) ───

    part 'SAW PL Program' : Program {
        attribute name = "SAW PL Program";
        attribute programId = "SAW_PL";
    }

    part 'SAW PL SW Design' : TeamDefinition {
        attribute name = "SAW PL SW Design";
        attribute active = true;
        attribute workType = WorkType::'SW_Design';
    }

    part 'SAW PL Code' : TeamDefinition {
        attribute name = "SAW PL Code";
        attribute active = true;
        attribute workType = WorkType::Code;
    }

    part 'SAW PL Test' : TeamDefinition {
        attribute name = "SAW PL Test";
        attribute active = true;
        attribute workType = WorkType::'Software Test';
    }

    part 'SAW PL PR' : TeamDefinition {
        attribute name = "SAW PL PR";
        attribute active = true;
        attribute workType = WorkType::'Problem Report';
    }

    part 'SAW_PL_SBVT1' : Version {
        attribute name = "SAW_PL_SBVT1";
        attribute released = false;
        attribute allowCreateBranch = true;
    }

    part 'SAW_PL_SBVT2' : Version {
        attribute name = "SAW_PL_SBVT2";
        attribute released = false;
        attribute nextVersion = true;
    }

    part 'SAW PL Code AI' : ActionableItem {
        attribute name = "SAW PL Code AI";
        attribute actionable = true;
    }

    part 'SAW PL Test AI' : ActionableItem {
        attribute name = "SAW PL Test AI";
        attribute actionable = true;
    }

    // Users (simple: userId + name)
    part 'jsmith' : User {
        attribute userId = "jsmith";
        attribute name = "Joe Smith";
    }

    part 'mjones' : User {
        attribute userId = "mjones";
        attribute name = "Mary Jones";
    }

    // ─── Connections (all from AtsRelationTypes) ───

    // Hierarchy (DefaultHierarchical — parent/child for TeamDefs and AIs)
    connection : DefaultHierarchical connect 'SAW PL Program' to 'SAW PL SW Design';
    connection : DefaultHierarchical connect 'SAW PL Program' to 'SAW PL Code';
    connection : DefaultHierarchical connect 'SAW PL Program' to 'SAW PL Test';
    connection : DefaultHierarchical connect 'SAW PL Program' to 'SAW PL PR';

    // TeamDef → Program
    connection : TeamDefinitionToProgram connect 'SAW PL SW Design' to 'SAW PL Program';
    connection : TeamDefinitionToProgram connect 'SAW PL Code' to 'SAW PL Program';
    connection : TeamDefinitionToProgram connect 'SAW PL Test' to 'SAW PL Program';
    connection : TeamDefinitionToProgram connect 'SAW PL PR' to 'SAW PL Program';

    // TeamDef → AIs
    connection : TeamActionableItem connect 'SAW PL Code' to 'SAW PL Code AI';
    connection : TeamActionableItem connect 'SAW PL Test' to 'SAW PL Test AI';

    // TeamDef → Versions
    connection : TeamDefinitionToVersion connect 'SAW PL Code' to 'SAW_PL_SBVT1';
    connection : TeamDefinitionToVersion connect 'SAW PL Code' to 'SAW_PL_SBVT2';
    connection : TeamDefinitionToVersion connect 'SAW PL Test' to 'SAW_PL_SBVT1';
    connection : TeamDefinitionToVersion connect 'SAW PL Test' to 'SAW_PL_SBVT2';

    // Team leads and members
    connection : TeamLead connect 'SAW PL Code' to 'jsmith';
    connection : TeamMember connect 'SAW PL Code' to 'jsmith';
    connection : TeamMember connect 'SAW PL Code' to 'mjones';
}
```

### Schema 2: Workflow Objects (`program-workflows.sysml`)

Exports workflow instances. Uses generic `part def` / `part` for workflow types and `state def` for state machines.

**Objects exported:**

| ATS Object | SysML V2 Element | Source |
|---|---|---|
| Action | `part def Action` + `part` instances | `AtsArtifactTypes.Action` |
| Team Workflow | `part def TeamWorkflow` + `part` instances | `AtsArtifactTypes.TeamWorkflow` |
| PR Team Workflow | `part def ProblemReportWorkflow :> TeamWorkflow` | `AtsArtifactTypes.ProblemReportTeamWorkflow` |
| CR Team Workflow | `part def ChangeRequestWorkflow :> TeamWorkflow` | `AtsArtifactTypes.ChangeRequestTeamWorkflow` |
| Task | `part def Task` + `part` instances | `AtsArtifactTypes.Task` |
| Review | `part def Review` + `part` instances | `AtsArtifactTypes.DecisionReview`, `PeerToPeerReview` |
| Build Impact Data | `part def BuildImpactData` + `part` instances | `AtsArtifactTypes.BuildImpactData` |

**Relationships:**

ALL relationships between workflow objects are explicit, strongly-typed connections derived from `AtsRelationTypes`. There is no parent/child nesting — every object is a flat `part` instance and every link is a named `connection` instance. This directly mirrors how OSEE stores data: artifacts + typed relation links.

```
Action ──(ActionToWorkflow)──> TeamWorkflow
TeamWorkflow ──(TeamWfToTask)──> Task
TeamWorkflow ──(TeamWorkflowToReview)──> Review
TeamWorkflow ──(TeamWorkflowTargetedForVersion)──> Version
TeamWorkflow ──(TeamWorkflowToFoundInVersion)──> Version
TeamWorkflow ──(TeamWorkflowToIntroducedInVersion)──> Version
TeamWorkflow ──(ProblemReportToBid)──> BuildImpactData
BuildImpactData ──(BuildImpactDataToTeamWf)──> TeamWorkflow (CR)
BuildImpactData ──(BuildImpactDataToVer)──> Version
TeamWorkflow ──(Derive)──> TeamWorkflow
TeamWorkflow ──(ResolvedBy)──> TeamWorkflow
```

Workflow-to-user relationships are represented via attributes (`createdBy`, `completedBy`, `cancelledBy` as userId strings) and `currentStateAssignee` as a multi-valued userId attribute. Users are defined in the config schema and referenced via `import`.

No `ref part` or nested `part` is used to represent relationships. The SysML `connection def` pattern matches OSEE's `RelationTypeToken` 1:1 — both are strongly-typed, named, directional links with multiplicity on each end.

The **PR → BIT → CR** chain uses `connection` instances (not nested parts) because the relationships are already defined in `AtsRelationTypes`:

```sysml
// From AtsRelationTypes.ProblemReportToBid
connection def ProblemReportToBid {
    end prTeamWf : TeamWorkflow[1];
    end buildImpactData : BuildImpactData[*];
}

// From AtsRelationTypes.BuildImpactDataToTeamWf
connection def BuildImpactDataToTeamWf {
    end buildImpactData : BuildImpactData[*];
    end teamWf : TeamWorkflow[*];
}

// From AtsRelationTypes.BuildImpactDataToVer
connection def BuildImpactDataToVersion {
    end buildImpactData : BuildImpactData[*];
    end version : Version[1];
}

// Instance connections for a PR with BIT rows and child CRs
connection 'TW00042_bid_BID001' : ProblemReportToBid
    connect 'TW00042' to 'BID_001';

connection 'BID001_ver_SBVT1' : BuildImpactDataToVersion
    connect 'BID_001' to 'SAW_PL_SBVT1';

connection 'BID001_cr_TW00043' : BuildImpactDataToTeamWf
    connect 'BID_001' to 'TW00043';

connection 'BID001_cr_TW00044' : BuildImpactDataToTeamWf
    connect 'BID_001' to 'TW00044';
```

This keeps the model consistent — ALL relationships between objects use `connection def` / `connection` derived from `AtsRelationTypes`, whether it's config→config, workflow→config, or PR→BIT→CR.

### Standalone CRs (No Problem Report)

CRs created directly for features or improvements exist independently — no PR parent, no BIT. They are exported as `part` instances of `ChangeRequestWorkflow` (or `TeamWorkflow`) with only the connections that actually exist on the artifact:

```sysml
// Standalone CR — feature work, no PR
part 'ACTION_99999' : Action {
    attribute atsId = "ACTION_99999";
    attribute name = "Add new telemetry display";
    attribute changeType = ChangeType::Improvement;
}

part 'TW00099' : ChangeRequestWorkflow {
    attribute atsId = "TW00099";
    attribute currentState = "Implement";
    attribute changeType = ChangeType::Improvement;
}

// Only the connections that exist — no ProblemReportToBid, no BuildImpactDataToTeamWf
connection 'ACTION_99999_to_TW00099' : ActionToWorkflow
    connect 'ACTION_99999' to 'TW00099';

connection 'TW00099_targetedFor_SBVT2' : TeamWorkflowTargetedForVersion
    connect 'TW00099' to 'SAW_PL_SBVT2';
```

The exporter only emits `connection` instances for relations that have actual links in the database. This means:
- **PR-driven work**: PR → BIT → CR connections are present
- **Standalone CRs**: Only `ActionToWorkflow`, `TeamWorkflowTargetedForVersion`, etc. — no BIT connections
- **Both cases use the same `part def` and `connection def` types** — the schema doesn't change, only which connections are instantiated

**State machines** are generated from the `WorkDefinition` of each workflow type:

```sysml
state def CRStateMachine {
    entry state endorse;
    state analyze;
    state authorize;
    state implement;
    state completed;
    state cancelled;

    transition endorse_to_analyze first endorse then analyze;
    transition analyze_to_authorize first analyze then authorize;
    transition authorize_to_implement first authorize then implement;
    transition implement_to_completed first implement then completed;
    // cancelled transitions from each working state
}

state def PRStateMachine {
    entry state open;
    state analyzed;
    state monitor;
    state closed;
    state cancelled;

    transition open_to_analyzed first open then analyzed;
    transition open_to_monitor first open then monitor;
    transition monitor_to_open first monitor then open;
    transition monitor_to_analyzed first monitor then analyzed;
    transition analyzed_to_closed first analyzed then closed;
    // ...
}
```

**Workflow instances** are flat `part` instances connected by strongly-typed `connection` instances:

```sysml
package SAW_PL_Program_Workflows {
    import SAW_PL_Program_Config::*;

    // Part defs (schema) — no nested part declarations for relationships
    part def Action {
        attribute atsId : String;
        attribute name : String;
        attribute changeType : ChangeType;
        attribute priority : Priority;
    }

    part def TeamWorkflow {
        attribute atsId : String;
        attribute name : String;
        attribute currentState : String;
        attribute currentStateType : String;
        attribute createdDate : String;
        attribute createdBy : String;
        attribute changeType : ChangeType;
        attribute priority : Priority;
        attribute description : String;
        attribute estimatedHours : Real;
        attribute percentComplete : Integer;
        // ... additional attributes from AtsAttributeTypes on AbstractWorkflowArtifact / TeamWorkflow

        exhibit state workflowState : CRStateMachine;
    }

    part def ProblemReportWorkflow :> TeamWorkflow {
        attribute howFound : String;
        attribute ship : String;
        attribute testNumber : String;
        attribute flightNumber : String;
        attribute systemAnalysis : String;
        attribute softwareAnalysis : String;

        exhibit state workflowState : PRStateMachine;
    }

    part def ChangeRequestWorkflow :> TeamWorkflow {
        // CR-specific attributes beyond base TeamWorkflow
        attribute rootCause : String;
        attribute proposedResolution : String;
    }

    part def Task {
        attribute atsId : String;
        attribute name : String;
        attribute currentState : String;
        attribute percentComplete : Integer;
    }

    part def Review {
        attribute atsId : String;
        attribute name : String;
        attribute currentState : String;
        attribute reviewType : String;
    }

    part def BuildImpactData {
        attribute state : BuildImpactState;
    }

    // Connection defs (from AtsRelationTypes — strongly typed, not parent/child)
    connection def ActionToWorkflow {
        end action : Action[1];
        end teamWorkflow : TeamWorkflow[*];
    }

    connection def TeamWfToTask {
        end teamWorkflow : TeamWorkflow[1];
        end task : Task[*];
    }

    connection def TeamWorkflowToReview {
        end teamWorkflow : TeamWorkflow[*];
        end review : Review[*];
    }

    connection def TeamWorkflowTargetedForVersion {
        end teamWorkflow : TeamWorkflow[*];
        end version : Version[1];
    }

    connection def TeamWorkflowToFoundInVersion {
        end teamWorkflow : TeamWorkflow[*];
        end version : Version[1];
    }

    connection def ProblemReportToBid {
        end prTeamWf : TeamWorkflow[1];
        end buildImpactData : BuildImpactData[*];
    }

    connection def BuildImpactDataToTeamWf {
        end buildImpactData : BuildImpactData[*];
        end teamWf : TeamWorkflow[*];
    }

    connection def BuildImpactDataToVersion {
        end buildImpactData : BuildImpactData[*];
        end version : Version[1];
    }

    // ─── Instances (flat parts) ───

    part 'ACTION_12345' : Action {
        attribute atsId = "ACTION_12345";
        attribute name = "Fix navigation display";
        attribute changeType = ChangeType::Problem;
    }

    part 'TW00042' : ProblemReportWorkflow {
        attribute atsId = "TW00042";
        attribute name = "Fix navigation display";
        attribute currentState = "Analyzed";
    }

    part 'BID_001' : BuildImpactData {
        attribute state = BuildImpactState::InWork;
    }

    part 'TW00043' : ChangeRequestWorkflow {
        attribute atsId = "TW00043";
        attribute name = "Fix nav display - Code";
        attribute currentState = "Implement";
    }

    part 'TW00044' : ChangeRequestWorkflow {
        attribute atsId = "TW00044";
        attribute name = "Fix nav display - Test";
        attribute currentState = "Analyze";
    }

    // ─── Connections (all from AtsRelationTypes) ───

    // Action → PR workflow
    connection : ActionToWorkflow connect 'ACTION_12345' to 'TW00042';

    // PR → BIT
    connection : ProblemReportToBid connect 'TW00042' to 'BID_001';

    // BIT → Version (which build is impacted)
    connection : BuildImpactDataToVersion connect 'BID_001' to 'SAW_PL_SBVT1';

    // BIT → CR workflows (the fix work)
    connection : BuildImpactDataToTeamWf connect 'BID_001' to 'TW00043';
    connection : BuildImpactDataToTeamWf connect 'BID_001' to 'TW00044';

    // CR workflows → targeted version
    connection : TeamWorkflowTargetedForVersion connect 'TW00043' to 'SAW_PL_SBVT1';
    connection : TeamWorkflowTargetedForVersion connect 'TW00044' to 'SAW_PL_SBVT1';

    // CR workflow → found-in version
    connection : TeamWorkflowToFoundInVersion connect 'TW00042' to 'SAW_PL_SBVT1';
}
```

---

## Relation Mapping via AtsRelationTypes

SysML V2 represents relationships using `connection def` (the type) and `connection` (the instance). Each `AtsRelationTypes` entry maps directly to a `connection def` preserving the OSEE relation name, side names, and multiplicity.

### How AtsRelationTypes Work

Each relation is defined as:
```java
RelationTypeToken ActionToWorkflow = ats.add(id, "ActionToWorkflow", ONE_TO_MANY, UNORDERED, Action, "Action", TeamWorkflow, "Team Workflow");
```

This gives us:
- **Relation name**: `"ActionToWorkflow"` → becomes the `connection def` name
- **Side A name**: `"Action"` → the source end name
- **Side B name**: `"Team Workflow"` → the target end name
- **Multiplicity**: `ONE_TO_MANY` → SysML multiplicity `[1]` to `[*]`
- **Side A type**: `Action` → SysML participant type
- **Side B type**: `TeamWorkflow` → SysML participant type

### SysML V2 Connection Def Pattern

```sysml
// Generated from AtsRelationTypes.ActionToWorkflow
connection def ActionToWorkflow {
    end action : Action[1];
    end teamWorkflow : TeamWorkflow[*];
}

// Generated from AtsRelationTypes.TeamActionableItem
connection def TeamActionableItem {
    end teamDefinition : TeamDefinition[1];
    end actionableItem : ActionableItem[*];
}

// Generated from AtsRelationTypes.TeamDefinitionToVersion
connection def TeamDefinitionToVersion {
    end teamDefinition : TeamDefinition[1];
    end version : Version[*];
}
```

### Multiplicity Mapping

| OSEE Multiplicity | Side A SysML | Side B SysML |
|---|---|---|
| `ONE_TO_ONE` | `[1]` | `[1]` |
| `ONE_TO_MANY` | `[1]` | `[*]` |
| `MANY_TO_ONE` | `[*]` | `[1]` |
| `MANY_TO_MANY` | `[*]` | `[*]` |

### Key Relations for Export

**Config relations:**

| AtsRelationTypes constant | Name | Side A → Side B |
|---|---|---|
| `CoreRelationTypes.DefaultHierarchical` | "Default Hierarchical" | parent → child (TeamDef hierarchy, AI hierarchy) |
| `TeamDefinitionToProgram` | (via CoreRelationTypes.SupportingInfo) | TeamDef → Program |
| `TeamActionableItem` | "TeamActionableItem" | TeamDefinition → ActionableItem |
| `TeamDefinitionToVersion` | "TeamDefinitionToVersion" | TeamDefinition → Version |
| `TeamDefinitionToBitProgram` | "TeamDefinitionToBitProgram" | TeamDefinition → Program (BIT) |
| `CountryToProgram` | "Country To Program" | Country → Program |
| `ProgramToInsertion` | "Program To Insertion" | Program → Insertion |
| `InsertionToInsertionActivity` | "Insertion To Insertion Activity" | Insertion → InsertionActivity |
| `ParallelVersion` | "ParallelVersion" | Version (Parent) → Version (Child) |
| `TeamLead` | "TeamLead" | TeamDefinition → User |
| `TeamMember` | "TeamMember" | TeamDefinition → User |

**Workflow relations:**

| AtsRelationTypes constant | Name | Side A → Side B |
|---|---|---|
| `ActionToWorkflow` | "ActionToWorkflow" | Action → TeamWorkflow |
| `TeamWfToTask` | "TeamWfToTask" | TeamWorkflow → Task |
| `TeamWorkflowToReview` | "TeamWorkflowToReview" | TeamWorkflow → Review |
| `TeamWorkflowTargetedForVersion` | "TeamWorkflowTargetedForVersion" | TeamWorkflow → Version |
| `TeamWorkflowToFoundInVersion` | "TeamWorkflowToFoundInVersion" | TeamWorkflow → Version |
| `TeamWorkflowToIntroducedInVersion` | "TeamWorkflowToIntroducedInVersion" | TeamWorkflow → Version |
| `Derive` | "Derive" | Workflow (From) → Workflow (To) |
| `Port` | "Port" | TeamWorkflow (From) → TeamWorkflow (To) |
| `ResolvedBy` | "ResolvedBy" | TeamWorkflow → TeamWorkflow |

**BIT relations:**

| AtsRelationTypes constant | Name | Side A → Side B |
|---|---|---|
| `ProblemReportToBid` | "ProblemReportToBid" | PR TeamWf → BuildImpactData |
| `BuildImpactDataToTeamWf` | "BuildImpactDataToTeamWf" | BuildImpactData → TeamWorkflow (CR) |
| `BuildImpactDataToVer` | "BuildImpactDataToVersion" | BuildImpactData → Version |

### Connection Instances

When exporting actual data, connections are instantiated using the relation type name as the connection def:

```sysml
// Connection usage — typed by AtsRelationTypes name, no per-instance name needed
connection : ActionToWorkflow connect 'ACTION_12345' to 'TW00042';

connection : TeamWorkflowTargetedForVersion connect 'TW00042' to 'SAW_PL_SBVT2';

connection : TeamActionableItem connect 'SAW PL Code' to 'SAW PL Code AI';
```

### Java Implementation

The mapper reads `RelationTypeToken` metadata directly:

```java
public SysmlConnectionDef mapRelationType(RelationTypeToken relType) {
    return new SysmlConnectionDef(
        relType.getName(),                          // connection def name
        toSysmlName(relType.getArtifactTypeSideA()), // end A type
        relType.getSideAName(),                      // end A name
        toSysmlMultiplicity(relType, SIDE_A),        // end A multiplicity
        toSysmlName(relType.getArtifactTypeSideB()), // end B type
        relType.getSideBName(),                      // end B name
        toSysmlMultiplicity(relType, SIDE_B)         // end B multiplicity
    );
}
```

For instance data, the exporter queries actual relation links on each artifact and emits `connection` usages.

---

## Implementation Plan

### Phase 1: Java Model + Serializer (in `ats.api` or new `ats.sysml` package)

Small Java model representing the SysML V2 AST subset:

```
org.eclipse.osee.ats.api.sysml/
  model/
    SysmlPackage.java           -- top-level package container
    SysmlEnumDef.java           -- enum def { enum literals }
    SysmlPartDef.java           -- part def with attributes only (no nested parts)
    SysmlPartUsage.java         -- part instance with attribute values
    SysmlAttributeDef.java      -- attribute name : Type (with multiplicity)
    SysmlAttributeValue.java    -- attribute name = value
    SysmlStateMachine.java      -- state def with states and transitions
    SysmlState.java             -- individual state
    SysmlTransition.java        -- transition between states
    SysmlConnectionDef.java     -- connection def with typed ends and multiplicity (from RelationTypeToken)
    SysmlConnection.java        -- connection instance linking two parts (from actual relation links)
  writer/
    SysmlTextWriter.java        -- serializes model → .sysml text
  mapper/
    OseeTypeToSysmlMapper.java  -- AttributeTypeToken → SysML type string
    OseeRelationToSysmlMapper.java -- RelationTypeToken → SysmlConnectionDef
    ConfigExporter.java         -- Program/TeamDef/AI/Version → SysmlPackage
    WorkflowExporter.java       -- Action/TeamWf/Task/Review/BIT → SysmlPackage
```

### Phase 2: REST Endpoint (in `ats.rest`)

```
GET /ats/action/{programId}/sysml/config
    → returns .sysml text (Content-Type: text/plain) for all config objects under that program

GET /ats/action/{programId}/sysml/workflows
    → returns .sysml text (Content-Type: text/plain) for all workflows under that program's teams
```

### Phase 3: IDE Navigate Item (in `ats.ide`)

An `AtsNavigateItem` (or `XNavigateItemAction`) in the ATS Navigator:

- Name: "Export Program to SysML V2"
- Hard-coded for now: `DemoArtifactToken.SAW_PL_Program`
- On activation:
  1. Calls REST endpoint with the program token
  2. REST endpoint resolves all config objects (Program → TeamDefs → Versions + AIs)
  3. REST endpoint resolves all workflows (query by team defs under program)
  4. Generates two `.sysml` files
  5. Writes to `C:/Tools/SawPlProgramSysMl/`
     - `saw-pl-program-config.sysml`
     - `saw-pl-program-workflows.sysml`

### Phase 4: Iterate

- Make program selectable (not hard-coded)
- Make output directory configurable
- Add filtering (by version, by date range, by workflow state)
- Add BIT detail expansion

---

## Attribute Selection Strategy

For each artifact type, the exportable attributes come directly from `AtsArtifactTypes` definitions. The artifact type declaration lists every attribute with its multiplicity:

```java
// From AtsArtifactTypes.TeamWorkflow:
.zeroOrOne(ChangeType)        // enum → enum def
.zeroOrOne(Priority)          // enum → enum def
.zeroOrOne(Description)       // string
.zeroOrOne(EstimatedHours)    // double → Real
.any(PcrId)                   // multi-valued string
```

The exporter reads the artifact type's attribute definitions and generates the `part def` with all declared attributes, properly typed. No manual curation needed — the schema IS the artifact type definition.

For instance data, only attributes that have actual values on the artifact are exported as `attribute x = value;`.

---

## File Output Structure

```
C:/Tools/SawPlProgramSysMl/
  saw-pl-program-config.sysml      -- Config schema + instances
  saw-pl-program-workflows.sysml   -- Workflow schema + instances
```

Future enhancement: split into one file per workflow or per team.

---

## Key Source References

| Concept | File |
|---|---|
| Program artifact type & attributes | `plugins/org.eclipse.osee.ats.api/.../data/AtsArtifactTypes.java` |
| All ATS attribute types with value classes | `plugins/org.eclipse.osee.ats.api/.../data/AtsAttributeTypes.java` |
| ATS relation types (Program↔TeamDef, etc.) | `plugins/org.eclipse.osee.ats.api/.../data/AtsRelationTypes.java` |
| Demo program token | `plugins/org.eclipse.osee.ats.api/.../demo/DemoArtifactToken.java` (`SAW_PL_Program`) |
| Navigate item examples | `plugins/org.eclipse.osee.ats.ide/src/.../navigate/` |
| REST endpoints | `plugins/org.eclipse.osee.ats.rest/src/.../internal/` |
| WorkDefinition (state machines) | `plugins/org.eclipse.osee.ats.api/.../workdef/model/WorkDefinition.java` |
| AttributeTypeToken (type introspection) | `plugins/org.eclipse.osee.framework.core/.../data/AttributeTypeToken.java` |
| AttributeTypeEnum (enum values) | `plugins/org.eclipse.osee.framework.core/.../data/AttributeTypeEnum.java` |
| BuildImpactData model | `org.eclipse.osee.ats.api/src/.../workflow/cr/bit/model/` |
