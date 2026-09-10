---
summary: "ATS-specific coding standards: prefer enums/tokens over new Strings, short conventional field names (teamWf, taskWf, changes, atsApi), work-def builder and transition conventions"
tags: [ats, coding-standards, conventions, teamstate, workdef, transition, naming]
fileMatch: "**/org.eclipse.osee.ats*/**"
---

# ATS Coding Standards

ATS-specific conventions layered on top of the general Java standards. These reflect patterns already established across the ATS bundles (`org.eclipse.osee.ats.api`, `ats.core`, `ats.rest`, `ats.ide`, `ats.core.demo`). Follow the surrounding code first; this doc captures the recurring rules.

## Prefer enums and tokens over new String literals

Do not hard-code strings that already exist as a token or enum. Reusing the canonical constant keeps names consistent, survives renames, and prevents subtle typos (e.g. "Analyze" vs "Analyzed").

- **State names:** use `TeamState` (`org.eclipse.osee.ats.core.workflow.state.TeamState`) constants and call `getName()` where a String is required. `TeamState` already defines `Open`, `Analyze`, `Analyzed`, `Monitor`, `Endorse`, `Authorize`, `Implement`, `Review`, `Test`, `Completed`, `Closed`, `Cancelled`, etc. Reuse them instead of literals like `"Monitor"`.
- **Attribute types:** use `AtsAttributeTypes` / `CoreAttributeTypes` tokens, never the display-name string.
- **Relation types:** use `AtsRelationTypes` / `CoreRelationTypes` `RelationTypeSide` constants.
- **Artifact types:** use `AtsArtifactTypes` / `CoreArtifactTypes` tokens.
- **Work definition ids:** use `AtsWorkDefinitionTokens` / `DemoWorkDefinitions` tokens.
- **Users:** use `AtsCoreUsers` for system/unassigned users.

```java
// Prefer
teamWf.getCurrentStateName().equals(TeamState.Monitor.getName());
transition(atsApi, teamWf, TeamState.Analyzed, user);

// Avoid
teamWf.getCurrentStateName().equals("Monitor");
transition(atsApi, teamWf, "Analyzed", user);
```

If a needed state name is missing from `TeamState`, add it there rather than inlining a literal at the call site.

## Field and variable naming

Use the short, conventional names the ATS codebase already uses, not verbose names that echo the type. These read better in the dense service/transition code and match every existing file.

| Type | Conventional name |
|------|-------------------|
| `IAtsTeamWorkflow` | `teamWf` |
| `IAtsTask` | `taskWf` (or `task`) |
| `IAtsWorkItem` | `workItem` |
| `IAtsAbstractReview` / review | `review` |
| `IAtsChangeSet` | `changes` |
| `AtsApi` | `atsApi` |
| `IAtsTeamDefinition` | `teamDef` |
| `IAtsActionableItem` | `ai` |
| `IAtsVersion` | `version` |
| `StateDefinition` (destination) | `toStateDef` |
| `StateDefinition` (source) | `fromStateDef` |
| `WorkDefinition` | `workDef` |
| `AtsUser` | `user` (or `asUser` for the acting user) |

```java
// Prefer
IAtsTeamWorkflow teamWf = ...;
IAtsChangeSet changes = atsApi.createChangeSet("Update");

// Avoid
IAtsTeamWorkflow teamWorkflow = ...;
IAtsChangeSet atsChangeSet = ...;
```

## API access

- Get the API through the tier's accessor, not by constructing services: `AtsApiService.get()` in core/IDE runtime and integration tests; the injected `AtsApi` (often a field named `atsApi`) inside services.
- **Resolve `AtsApi` once, then reuse it.** Call `AtsApiService.get()` a single time - assign it to an `atsApi` field (or a local `atsApi` at the top of a method) - and use that reference throughout the class/method. Do not scatter `AtsApiService.get()` calls at every use site. For helper/utility classes and methods, pass `AtsApi atsApi` in as a parameter rather than re-fetching it.

```java
// Prefer - resolve once, reuse
public class MyOperation {
   private final AtsApi atsApi;

   public MyOperation(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public void run() {
      IAtsChangeSet changes = atsApi.createChangeSet("Run");
      ...
   }
}

// Prefer - pass into static helpers
static void doWork(IAtsTeamWorkflow teamWf, AtsApi atsApi) { ... }

// Avoid - AtsApiService.get() repeated everywhere
void run() {
   IAtsChangeSet changes = AtsApiService.get().createChangeSet("Run");
   AtsApiService.get().getWorkItemService()....;
   AtsApiService.get().getQueryService()....;
}
```

- Create change sets via `atsApi.createChangeSet("<comment>")` (or `getStoreService().createAtsChangeSet(comment, user)` when a specific user is needed). Always pass a meaningful comment.
- Batch related writes into a single `IAtsChangeSet` and `execute()` once; do not create a change set per attribute.

## Attributes

- Read/write through `atsApi.getAttributeResolver()` (`getSoleAttributeValue(workItem, AtsAttributeTypes.X, default)`, `setSoleAttributeValue(workItem, type, value, changes)`), passing the token, never the string name.
- Declaring an attribute type in `AtsAttributeTypes` is not enough to store it: the attribute must also be added to the artifact type's builder chain in `AtsArtifactTypes` (e.g. `.zeroOrOne(LastStateName)` on `AbstractWorkflowArtifact`). Attribute-type ids must be globally unique across all namespaces.

## Work definitions

- Build work definitions with the `WorkDefBuilder` / `StateDefBuilder` fluent API in a `build()` method; extend `AbstractWorkDef` (or `AbstractDemoWorkDef` for demo Code/Req/Test).
- Reference states with `StateToken` constants in `andToStates(...)` / `andToWaitStates(...)`, not raw strings.
- Register new work defs through the appropriate `IAtsWorkDefinitionProvider` (e.g. `AtsWorkDefinitionProviderDemo`).
- Put new transition-gating logic in `TransitionManager` rather than new transition hooks; hooks are for non-`ats.core` bundles or extension behavior (see the note in `AtsWorkItemServiceImpl.getTransitionHooks()`).

## Transitions

- Drive programmatic transitions through `atsApi.getWorkItemService().transition(TransitionData)`; assert results with `TransitionResults.isEmpty()` (success) / `isErrors()` (failure), and pass `results.toString()` as the assert message for diagnosability.
- Use `TransitionOption` flags (e.g. `OverrideAssigneeCheck`) rather than reimplementing checks.

## Tests

- Use `AtsTestUtil.cleanupAndReset(name)` to create the action/team workflow and `AtsTestUtil.cleanup()` in `@Before`/`@After`; guard with `!isProductionDb()`.
- Reuse `TeamState` (and other tokens) for state names and assertions, per the enum rule above.
- After an out-of-band change, `reloadAttributesAndRelations()` before asserting on the artifact.
- JUnit 4 (`org.junit.Test`, `Assert`), EPL-2.0 header, `@author`.

## General

- Follow the ATS bundle layering: pure interfaces/model/tokens in `ats.api`; shared logic in `ats.core`; REST in `ats.rest`; Eclipse UI in `ats.ide`; demo data in `ats.core.demo`. Do not introduce IDE/UI dependencies into `ats.core` or `ats.api`.
- Access services through the `AtsApi` facade (`getTeamDefinitionService()`, `getWorkItemService()`, `getWorkDefinitionService()`, etc.) rather than reaching into implementations.
