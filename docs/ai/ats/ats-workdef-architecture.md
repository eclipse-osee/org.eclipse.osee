---
summary: "ATS Work Definition architecture: how workflow states, transitions, widgets, and rules are defined programmatically with the WorkDefBuilder pattern. Covers AbstractWorkDef/AbstractDemoWorkDef, tokens, the WorkDefinition model (AttrWidgetDef/WidgetDef/CompositeLayoutItem/SignDateWidgetDef), widget layout patterns, state configuration, rules, and registration."
tags: [ats, workdef, work-definition, workflow, builder, states]
fileMatch: "**/*WorkDef*,**/workdef/**,**/*WorkDefinition*"
---

# ATS Work Definition Architecture

## Overview

ATS (Action Tracking System) uses Work Definitions to define the states, transitions, widgets, and rules for workflow artifacts. Work Definitions are built programmatically using the `WorkDefBuilder` pattern.

## Key Classes

### Builder Pattern
- **`WorkDefBuilder`** — Fluent builder for constructing `WorkDefinition` instances
- **`AbstractWorkDef`** — Base class for all work definition builders. Constructor takes `AtsWorkDefinitionToken`.
- **`AbstractDemoWorkDef`** — Demo-specific base in `org.eclipse.osee.ats.core.demo` for Code/Req/Test work defs with shared custom header

### Tokens
- **`AtsWorkDefinitionToken`** — Identifies a work definition (NOT `WorkDefinitionToken`)
- **`DemoWorkDefinitions`** — Constants for demo work definition tokens
- **`StateToken`** — Standard state name tokens (Analyze, Authorize, Implement, Completed, Cancelled, etc.)

### Model
- **`WorkDefinition`** — The built result containing states, layouts, rules
- **`AttrWidgetDef`** — Widget backed by an attribute type
- **`WidgetDef`** — Widget backed by a WidgetId (for non-attribute widgets like Sprint, Feature, Branch)
- **`CompositeLayoutItem`** — Multi-column layout container (first arg is column count)
- **`SignDateWidgetDef`** — Approval sign-off widget (user + date pair)
- **`ChangeReportTasksWidgetDef`** — Task creation from change report

## Work Definition Structure

```java
public class MyWorkDef extends AbstractWorkDef {

   public MyWorkDef() {
      super(MyWorkDefinitions.MyWorkDefToken);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      // Optional: artifact type override
      // WorkDefBuilder bld = new WorkDefBuilder(workDefToken, AtsArtifactTypes.MyType);

      // Points attribute
      bld.andPointsAttributeType(PointsEnum);

      // Custom header (shows above all states)
      bld.andCustomHeader()
         .andLayout(...)
         .showMetricsHeader(false);

      // States
      bld.andState(1, "Endorse", StateType.Working).isStartState()
         .andToStates(StateToken.Analyze, StateToken.Cancelled)
         .andColor(StateColor.BLACK)
         .andLayout(widgets...);

      bld.andState(2, "Analyze", StateType.Working)
         .andToStates(StateToken.Implement, StateToken.Cancelled)
         .andLayout(widgets...);

      // Terminal states
      bld.andState(5, "Completed", StateType.Completed)
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview);

      bld.andState(6, "Cancelled", StateType.Cancelled);

      return bld.getWorkDefinition();
   }
}
```

## Demo Work Definitions

### AbstractDemoWorkDef (shared base for Code, Req, Test)

Provides:
- `addDemoCustomHeader(bld)` — Standard header:
  - Row 1: ChangeType composite + WorkPackage
  - Row 2: Sprint + Feature + AgilePiTracking
  - No metrics header
- `addCompletedState(bld, stateNum)` — Completed with AddDecisionValidateBlockingReview rule
- `addCancelledState(bld, stateNum)` — Standard Cancelled state

### Demo Work Defs
| Class | Token | Notes |
|-------|-------|-------|
| `WorkDefTeamDemoCode` | `WorkDefTeamDemoCode` | Uses DemoCodeTeamWorkflow artifact type |
| `WorkDefTeamDemoReq` | `WorkDefTeamDemoReq` | Has branch/commit widgets in Implement |
| `WorkDefTeamDemoTest` | `WorkDefTeamDemoTest` | Has RequireStateHourSpentPrompt rules |
| `WorkDefTeamDemoSwDesign` | `WorkDefTeamDemoSwDesign` | Simple header, no layout |
| `WorkDefTeamDemoProblemReport` | `WorkDefTeamDemoProblemReport` | NoTargetedVersion, IsProblemReport options |
| `WorkDefTeamDemoChangeRequest` | `WorkDefTeamDemoChangeRequest` | Change request specific |

## Widget Layout Patterns

### CompositeLayoutItem
```java
new CompositeLayoutItem(4,  // 4 columns
   widget1,
   widget2,
   widget3,
   widget4
)
```

### Attribute Widget
```java
new AttrWidgetDef(AtsAttributeTypes.Description)           // default options
new AttrWidgetDef(AtsAttributeTypes.Description, REQUIRED) // with options
```

### Non-Attribute Widget (by WidgetId)
```java
new WidgetDef(XXAgileSprintSelWidget)
new WidgetDef(XXWorkflowApplicWidget, REQUIRED)
```

> **Attribute-backed widgets must be declared with `AttrWidgetDef`, not `WidgetDef`.** `WidgetDef` no longer accepts
> an attribute type (the attr-taking constructors were removed and `WidgetDef.setAttributeType` throws). Only
> `AttrWidgetDef` carries an attribute type. This matters for transition validation, which runs on the **server**
> against the `WidgetDef` from the work definition — not the client `XWidget` (where a widget's attribute type is set
> via `setAttributeType`/`setAsProviderFor`, invisible to the server). `AtsDefaultAttrValidator` only enforces a
> REQUIRED/RFC widget when the def carries the attribute type. So for an **attribute-backed** widget,
> `new WidgetDef(XXDecisionRevWidget, REQUIRED)` (no attr type on the def) would be **silently never enforced** at
> transition — the workflow transitions even with the field empty. (Non-attribute widgets are a different story:
> they may enforce themselves via the client `XWidget.isValid()` or via domain-specific transition logic, so this
> guidance is specifically about attribute-backed widgets.)
> ```java
> // WRONG - attr-backed widget as a plain WidgetDef; required is never enforced
> new WidgetDef(XXDecisionRevWidget, REQUIRED)
> // RIGHT - AttrWidgetDef carries the attribute type; AtsDefaultAttrValidator enforces it
> new AttrWidgetDef(Decision, XXDecisionRevWidget, REQUIRED)
> ```

## Validation: two layers, fail fast

Work-def validation is split into two layers by what data each check needs. **Push every check as early as it can
run.** Fail fast, fail hard, and fail where the developer defined the problem.

### Layer 1 — quick static checks at build time (`WorkDefBuilder`)

These are pure checks over the `WorkDefinition` model itself — no datastore, no client widget registry, no OSGi.
Per the fail-fast principle (see Java coding standards), `WorkDefBuilder.getWorkDefinition()` records problems into
the work def's `XResultData`, and `AtsWorkDefinitionProviderService` **throws `OseeArgumentException` at load** if
any registered work def has errors. A bad work def fails at startup/registration, right where the developer defined
it. **Add new work-def invariants here whenever the check only needs the model.**

`WorkDefBuilder` currently fails on:
- Missing/duplicate state ordinals, duplicate state names, more than one start state.

Only put checks here that are **provable from the model alone with no false positives**. In particular, do NOT
try to flag "REQUIRED widget that can't be enforced" at build time. Whether a REQUIRED widget is enforced depends
on runtime facts the model can't see — the client `XWidget` may validate its own value via an `isValid()` override
(e.g. the defect viewer, applicability), and some widgets are enforced by domain-specific transition logic rather
than an attribute type or a registered `AtsXWidgetValidator`. A build-time check based only on "has attribute type
or known validator" produces false positives for those widgets, and because a build error makes
`AtsWorkDefinitionProviderService` throw when the work def's builder binds (at registration), a false positive takes
down all of ATS at startup. That belongs in Layer 2, where the actual `XWidget` can be inspected.

Because these checks are model-only, they are covered by a **standalone JUnit test** — `WorkDefBuilderTest`. It
constructs a `WorkDefBuilder`, calls `getWorkDefinition()`, and asserts on the returned `XResultData`
(`isSuccess()` / `isFailed()` + a substring of the error). No database, no framework, no OSGi — so it runs fast in
plain JUnit. **Every new build-time check must get a paired failing/passing test here.**

### Layer 2 — advanced checks at runtime (`ValidateWorkDefXWidgetOperation`)

Some invariants can't be seen from the model alone — they need the running client with its widget registry, or the
datastore. These live in runtime operations, not the builder. `ValidateWorkDefXWidgetOperation` runs automatically
in three places (in addition to the on-demand report/navigate item):

- **Client startup** — `Activator.earlyStartup()` (ats.ide) schedules a background `Job` after the workbench is up
  that runs the operation and, if `rd.isErrors()`, logs `Level.SEVERE` to the Error Log so a developer sees it
  immediately and fixes it. It runs off the UI thread and never blocks or fails startup. Because it logs SEVERE,
  the `SevereLoggingMonitor` in the integration harness catches it too: a bad work def surfaces as a failure in
  `DemoDbInitTest` (the client comes up during dbinit), not only in the dedicated widget test below. (Verified: a
  plain `WidgetDef(XXDecisionRevWidget, REQUIRED)` — Decision is attribute-backed — fails `DemoDbInitTest` via the
  SEVERE monitor and shows the same SEVERE in a live client's Error Log.)
- **ATS integration test suite** — `AtsWorkDefinitionXWidgetTest.workDefXWidgetTest()` runs the operation and
  asserts `rd.isSuccess()`, so a bad work def fails the build. Reachable from the top-level suite via
  `AtsIdeIntegrationTestSuite → AtsTest_AllAts_Suite → AtsTest_Ats_Suite → AtsTest_WorkDef_Suite`.
- **On demand** — the REST endpoint / navigate item described below.

The operation accumulates every problem into a single `XResultData` (it does not stop on the first error), so one
run reports all bad widgets across all work definitions.

- **`ValidateWorkDefXWidgetOperation`** (`org.eclipse.osee.ats.ide`) instantiates each layout widget via
  `XWidgetService.get().createXWidget(widgetId)`. It catches:
  - Widgets whose WidgetId doesn't resolve to a registered XWidget.
  - A REQUIRED/RFC widget declared as a plain `WidgetDef` (no attr type on the def) whose instantiated **XWidget is
    attribute-backed** (`widget.getAttributeType().isValid()`). That means the author should have used
    `AttrWidgetDef` — the server never sees the client widget's attribute type, so this can only be detected on the
    client. This check is deliberately narrow: it fires only when the XWidget is attribute-backed, so it does not
    false-positive on non-attribute widgets that validate themselves in the UI (e.g. defect viewer, applicability).
- **`IAtsWorkDefinitionService.validateWorkDefinitions()`** (`ValidateWorkDefinitionsOperation`, also on the
  `AtsWorkDefEndpointImpl.validate()` REST endpoint and a navigate item) covers checks that need registered data —
  e.g. widget attribute types resolving to a valid registered type, and Team Workflows missing their Work
  Definition Reference attribute.

These operations depend on `XWidgetService` / the datastore, so they are **not** standalone-JUnit testable; they run
in the live IDE/server.

**Rule of thumb:** if a check needs only the `WorkDefinition` model, it belongs in `WorkDefBuilder` (Layer 1) with a
standalone test. Only when a check genuinely requires the client widget registry or the running datastore does it
belong in a runtime operation (Layer 2).

### Relation Widget
```java
new WidgetDef("Related Tasks", AtsRelationTypes.TeamWfToTask_Task, XXListRelationWidget, VAL_NO_SEL)
```

### Sign-Off Widget
```java
new SignDateWidgetDef("Manager Approved", ApproveRequestedHoursBy, ApproveRequestedHoursByDate)
   .andImage(AtsImage.CHECK_CLIPBOARD)
```

## State Configuration

### State Types
- `StateType.Working` — Active work states
- `StateType.Completed` — Terminal success state
- `StateType.Cancelled` — Terminal cancel state

### Common Rules
- `RuleDefinitionOption.AddDecisionValidateBlockingReview` — Requires review before entering state
- `RuleDefinitionOption.RequireStateHourSpentPrompt` — Prompts for hours spent on transition
- `WorkDefOption.NoTargetedVersion` — Disables targeted version requirement
- `WorkDefOption.IsProblemReport` — Marks as problem report type

### Transition Listeners
```java
.andTransitionListener(TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges)
```

## Registration

Each work definition registers itself as an OSGi Declarative Services component:
`@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)` on the concrete `AbstractWorkDef`
subclass, with a matching hand-written `OSGI-INF/<fqcn>.xml` descriptor in the owning bundle (bundles use
`Service-Component: OSGI-INF/*.xml`). This mirrors how BLAMs (`AbstractBlam`) and XWidgets (`XWidget`) register.

The collector `AtsWorkDefinitionProviderService` (`@Component`) gathers every registered `IAtsWorkDefinitionBuilder`
via a `MULTIPLE/DYNAMIC/GREEDY @Reference`, calls `build()` on each applicable one, and caches the result by id. It
enforces unique ids and throws `OseeArgumentException` on bind if a work def has build errors (fail-fast, see below).

- **Environment gating:** `IAtsWorkDefinitionBuilder.isApplicable()` defaults to `true`; demo/test work defs override
  it to return `DemoUtil.isDemoDatabase()` so they only register on a demo database.
- **No clearing:** work definitions are immutable code bound once at startup; DS never re-injects, so the collector's
  `clearCaches()` is an intentional no-op.
- **Adding a work def:** create the `AbstractWorkDef`/`AbstractDemoWorkDef` subclass, add the `@Component` annotation,
  and add its `OSGI-INF/<fqcn>.xml`. No provider edit needed - there is no central list to update.

(History: this replaced a two-tier mechanism where `IAtsWorkDefinitionProvider` implementations - e.g.
`AtsWorkDefinitionProvider`, `AtsWorkDefinitionProviderDemo` - enumerated all work defs in code. Those providers and
the `IAtsWorkDefinitionProvider` interface were removed.)
