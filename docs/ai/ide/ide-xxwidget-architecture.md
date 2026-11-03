---
summary: "XX widget architecture: the standardized OSEE widget framework where concrete widgets extend XXAbstractBaseXWidget<T>. Covers naming, class structure, OSGi component registration, constructor rules, the full widget lifecycle (create, initializeSelected, refresh, handleSelection, clear, validate), attribute-type providers, file section ordering, code-review checks, and XWidgetBuilder usage."
tags: [ide, xxwidget, xwidget, architecture, lifecycle, osgi, widgets]
fileMatch: "**/widgets/xx/**,**/*XX*Widget*,**/XWidget.java,**/XXAbstractBaseXWidget.java"
---

# XX Widget Architecture

## Overview

The XX Widget architecture is the standardized widget framework for OSEE. All new widgets extend from `XXAbstractBaseXWidget<T>` and follow a consistent lifecycle, naming, and structure pattern.

`XXAbstractBaseXWidget<T>` is "one widget to rule them all" — it is intended to replace most other widgets and handles the functionality needed by most use cases, whether it resides in an editor backed by an artifact (e.g. Artifact Editor, Workflow Editor) or not (e.g. BLAM, Dialog). **Most widgets should NOT need to extend it separately** — behavior is driven by setting `XOption`s in the base `XWidget` class and via the widget builders, not by subclassing.

### Features provided by the base

- Selectable hyperlink label when editable; non-selectable plain label when read-only
- Ability to set selectable items and a default selected value
- Defaults an enumerated attribute's values to provide the selectable list
- Filterable dialog with multi-select (checkboxes) or single-select (list)
- Optional clear button; right-click provides clear by default
- Double-click shows a tooltip dialog when a tooltip is provided
- Selectable items sorted by default (`setComparator` for specialized sorting)
- Validation for most use cases via `isValid()` — usually no separate validator needed
- Optional widget icon via `setOseeImage`
- Enable/disable that greys the label and blocks interaction (via the live control, not a stale option)
- Ctrl+click the label opens the widget debug report (resolved options, attr type, WidgetId)

> When editing `XXAbstractBaseXWidget`, keep this feature list and the class Javadoc ("One Widget to rule them all...") in sync with each other and with this doc.

## Rendering Pipeline & Data Flow

Three cooperating pieces turn a widget request into a rendered, value-bound control:

- **`XWidgetPage`** — creates and manages a set of widgets on a page (`isComplete`, `isValid`). Used by non-work-def hosts: BLAMs, dialogs, `WorldParameterSearch`, etc.
- **`XWidgetWorkDefRenderer`** — creates and manages widgets from a Work Definition's layout (WFE state sections; also the WFE header via `XWidgetPage`).
- **`XWidgetSwtRenderer`** — used by both of the above; turns `XWidgetData` into the concrete SWT widget. It is no longer instantiated on its own — always driven through `XWidgetPage`/`XWidgetWorkDefRenderer`.

Core data objects:

- **`WidgetId` / `WidgetIdAts`** — tokens usable from `api`/`core`/etc. Same name as the widget class (enforced). Stable, but can be refactored/renamed safely since everything references the token.
- **`XWidgetData`** — carries the `WidgetId` plus all options/fields needed to render. Created for each place in the UI that wants a widget (via `WidgetDef`/`AttrWidgetDef` or the builders `XWidgetBuilder`/`XWidgetBuilderAts`).
- **`XWidget`** — the concrete widget instance (created by reflection). It holds an `XWidgetData` and uses it to decide how to render, which dialog to open, and how to store values in artifact/attribute/relation.

### `XWidgetData` → `XWidget` merge order

`XWidgetData` is populated by `WidgetDef`/`AttrWidgetDef` and the builders (XOptions and other fields). The **same** fields are also applied at widget construction so a widget can be used independently. When an `XWidget` is built from an `XWidgetData`, the values are **merged**: the widget's constructor defaults first, then the builder/WidgetDef values layered on top. This merge is inspectable via `XWidgetUtility.debugWidDataToHtml` (used by both renderers) and can be enabled in production with the bug toolbar item in the Artifact Editor.

- `AttrWidgetDef` provides only the attribute type + XOptions and relies on the renderers to resolve the label, widget, etc. All WidgetDefs backed by an attribute type should use it.
- **Wrapped Value Presentation** - `WRAP_VALUES` changes inherited read-only `Text` controls to bounded-width
  `SWT.MULTI | SWT.WRAP` displays without changing selection or persistence semantics. Apply it to widgets that need
  wrapped values rather than changing every widget globally.

### OSGi contribution & attribute-widget resolution

- Widgets are contributed via OSGi `@Component` annotations. The OSGI-INF XML files are created/updated/deleted automatically by the Eclipse editor preferences (in `osee_team_preferences`). Components are loaded immediately so they're present and instantiable.
- Widgets that are the default for an attribute type register via `setAsProviderFor(AttributeTypeToken)`.
- `AttributeXWidgetProviderCore.getDefaultXWidgetData(artType, attrType)` resolves an attribute's widget in order:
  1. an explicit `setAsProviderFor` registration,
  2. a `WidgetId` selected from the attribute type's `DisplayHint`s (e.g. `XTextFlat`→`XXFlatAttrsWidget`, `XArtRef`→`XXArtifactSelWidget`, `XBranchSel`→`XXBranchSelWidget`),
  3. resolution from the artifact/attribute type (base type + multiplicity),
  4. else fail — render a label-only `XXLabelWidget` noting the failure.
  5. any exception during resolution/creation is logged and rendered as an `XXLabelValueWidget` with a red note and "see Error Log" — so one bad widget never crashes the editor/section.

### `WorkflowDataCreator` (server/REST only — do not touch without web)

`WorkflowDataCreator` (`ats.rest`) builds an external `WorkflowData` POJO from a workflow + work def. It is used by a single REST call and is NOT used in the IDE. **Do not modify it without updating/testing the web.**

## Naming Conventions

- `XX..Widget` — concrete widgets based on `XXAbstractBaseXWidget` (e.g., `XXTextWidget`, `XXDateSelWidget`)
- `X..Widget` — old or advanced widgets like Defects, CommitManager that predate the XX architecture
- `XAbstract...Widget` or `XXAbstract...Widget` — abstract base classes
- Widget ID must match class name (e.g., `XXTextWidget` has `WidgetId ID = WidgetId.XXTextWidget`)
- A widget that extends an XX widget must be named XX; otherwise it should be X

## Class Structure

Every concrete XX widget must have:

```java
@Component(service = XWidget.class, immediate = true)
public class XXMyWidget extends XXAbstractBaseXWidget<MyType> {

   public static final WidgetId ID = WidgetId.XXMyWidget;
   // or WidgetIdAts.XXMyWidget for ATS-specific widgets

   // Instance fields (xxWid for ATS widgets)
   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
```

OSGi auto-maintains a `<fully.qualified.classname>.xml` file for component registration.

## Constructor Rules

- **Must have empty constructor** for OSGi to load
- Only one constructor should call `super()`; others chain via `this()`
- Don't call `super(WidgetId.XXSomething)` — use `super(ID, label)`
- Constructor sets: ID, Label, AttributeType/RelationType, XOptions, OseeImage

```java
public XXMyWidget() {
   this(ID, "");
}

public XXMyWidget(WidgetId widgetId, String label) {
   super(widgetId, label);
   set(XOption.CLEAR);
   setOseeImage(CoreImage.CALENDAR);
}
```

## Widget Lifecycle

### Class & Constructor (prerequisites)

- **Class naming**: `XX..Widget` for new widgets based on `XXAbstractBaseXWidget`; `X..Widget` for old/advanced widgets (e.g. Defects, CommitManager); `XAbstract...Widget` for abstract XWidgets.
- **`@Component(service = XWidget.class, immediate = true)`** on concrete widgets — OSGi auto-maintains the `<fully.qualified.name>.xml` component file.
- **Constructor**: must have an empty (no-arg) constructor for OSGi to load; the widget sets its ID, Label, Attribute/Relation type, XOptions, and OseeImage.

### 1. Create (UI Construction)

```
createControls(parent, horizontalSpan)
├── composite
├── createIconWidget()
├── createLabelWidget()
├── createValueWidget()
├── initializeSelected()   ← 1st opportunity to load selected (before refresh); also called on setArtifact()
└── refresh()
```

### 2. Initialization — `initializeSelected()` / `setArtifact()`

```
setArtifact(artifact)
├── super.setArtifact(artifact)
│   └── initializeSelected()               ← load data from art/attr/rel
│       └── setSelected(values, false)     ← false = don't trigger refresh
└── refresh()                              ← called automatically; don't call it yourself
```

**`initializeSelected()`** — load data from artifact, attributes, relations, etc. This is the ONE place a widget loads its selected value.
- Must call `setSelected(value, false)` — the `false` prevents triggering a refresh during initialization.
- Never call `setSelected(value)` (without the boolean) in `initializeSelected`.
- Called on create and again when the artifact/value changes; keeps the widget from reloading on every `refresh()`.

**ATS cached data**: ATS widgets cache a `XXWorkItemData` (`xxWid = XXWorkItemData.get(art)`) so each widget doesn't independently reload common context (parent action, version, program, tasks, reviews, etc.). This caching is an ATS-widget concern, not the framework base.

**`setArtifact` rules:**
- Do NOT call `refresh()` — refresh is called automatically after `setArtifact`.
- Do NOT call `setSelected()` — use `initializeSelected()` instead.

### 3. Refresh — `refresh()`

Refreshes ONLY the value display via `getDisplayValue()`.
- Default `getDisplayValue()` (preferred): renders `selected` (the objects in `this.selected`, NOT `getSelected()`), semicolon-delimited.
- Extended `getDisplayValue()`: when the data object(s) and the display differ and a computation is needed (e.g. `getRemainingHours()`).
- When a widget sets values during refresh-adjacent work, use `setSelected(objs, false)` — `false` so it doesn't recurse into refresh.
- **SWT contract — change-aware, targeted layout.** `refresh()` first compares `getDisplayValue()` with the current control text. Unchanged widgets perform no SWT mutation. Every actual text change may alter preferred size, so it performs `setText(...)`, one targeted `comp.getParent().layout(new Control[]{changedValueControl})`, and—when hosted in a managed form—one `reflow(true)` to update the ScrolledForm extent/scrollbars. Passing the actual `Text`/`StyledText` leaf is required so SWT flushes its owning layout's cached size. This allows fixed and fill/grab values to grow or shrink immediately. Do NOT add `update()` or blanket parent layouts. Structural widgets must similarly detect add/remove/expand changes and own one local layout/reflow; same-value `Section.setExpanded(...)` calls were the verified WFE flicker cause. See `XXAbstractBaseXWidget` Javadoc and `docs/ai/ide/ide-swt-layouts.md`.

### 4. Selection / Storage — `handleSelection()`

```
handleSelection()           ← user interaction
├── open dialog / get input
├── setSelected(value, false)
├── handleSelectedPersist() ← persist to database (returns valid/SENTINEL, never null)
└── return true             ← caller refreshes when true
```

**`getSelected()` rules:**
- Should NOT be overridden. Values come from `this.selected` objects, rendered by `getDisplayValue()`.
- This is NOT where a widget loads data (that's `initializeSelected()`).
- Set through `setSelected(object(s), false)`, or `true` if a widget refresh is needed due to the change.

**`getSentinel()`** — returns the "empty" value for the widget's type (e.g., `""` for String, `null` for Date).

### 5. Designated as an AttributeType Widget

A widget becomes the default for an attribute type either in `AttributeXWidgetProvider` (via `DisplayHint`) or by calling `setAsProviderFor(AtsAttributeTypes.RelatedToState)` in its constructor.

### 6. Clear — `clear()`

Only available if `set(XOption.CLEAR)` is configured (right-click also clears by default). `clear()` → `clear(true)` → `selected.clear()` + `refresh()`.

### 7. Validation

Override `getValidator()` to provide validation:
```java
@Override
protected IValueValidator getValidator() {
   return XXNoOpValidator.instance; // or a real validator
}
```

Do NOT implement `validate()` directly — the framework calls it.

## File Organization (Section Order)

For all `XX*Widget` files, organize the file in this order (`CodeCleanOrganizeXWidgets` reorganizes them automatically). Section banners use the format `//////////` / `// <block name>` / `//////////`:

1. **Static fields first**, with the `WidgetId`/`WidgetIdAts` `ID` field first:
   - static fields at the top
   - blank line
   - the `WidgetId`/`WidgetIdAts` `ID` field
   - blank line
   - the rest of the fields, sorted by type
2. **Constructors**: the default (no-arg) constructor and any others that call `this(...)` first, then the main constructor that calls `super(...)` last.
3. **Section blocks**, each introduced by a banner comment:
   ```
   //////////
   // <block name>
   //////////
   ```
   in this order:
   - UI Construction
   - Storage
   - Validation
   - Refresh / Lifecycle
   - any other sections that make sense
   - Initialization (always last) — `setArtifact()` (if present) then `initializeSelected()` (if present)

## Code Review Checks (CodeReviewXWidgets)

- Widget should have `@Component(service = XWidget.class)` annotation
- Widget should have ID that matches class name
- Widget should have default constructor for OSGi
- Only one constructor should call super; don't call super without ID
- ATS widget should have `xxWid` field
- `setArtifact` should not call `refresh()`
- `setArtifact` should not call `setSelected()`
- `initializeSelected` must call `setSelected(value, false)` not `setSelected(value)`
- Widget should not directly use `XWidgetUtility.setStatus`
- Widget should not implement `validate()`; override `getValidator()` instead
- Widget shouldn't override `getSelected()`
- No `System.err.print` / `System.out.print` — use `OseeLog`
- No TBD markers in production code

## XWidgetBuilder Usage (for Blams)

```java
@Override
public List<XWidgetData> getXWidgetItems() {
   createWidgetBuilder();  // ATS blams
   // or: XWidgetBuilder wb = new XWidgetBuilder();  // framework blams
   wb.andWidget(LABEL, WidgetId.XXListDropWidget);
   wb.andTextWidget(TITLE).andRequired();
   wb.andCheckBoxWidget("Persist");
   return wb.getXWidgetDatas();
}
```

- Use `WidgetId.XXxxx` constants, never string widget names
- Use static String fields for labels (shared with `variableMap` in `runOperation`)
- Don't use `.endWidget()` — not necessary
- `andCheckBoxWidget` already defaults to LABEL_AFTER
- Method names: `andTextWidget`, `andButtonPushWidget`, `andBranchSelWidget`, `andBranchViewSelWidget`, `andBranchAndViewSelWidget`

## Code-Review & Cleanup Tooling

Durable tools in `org.eclipse.osee.ats.ide.integration.tests...coderev`. **Open each class's Javadoc/source for the
authoritative, detailed behavior** — this is just a map. There are two families, both extending a shared base
`AbstractQual`.

### Shared base (`AbstractQual`)

Both `AbstractCodeReview` and `AbstractCodeClean` extend `AbstractQual`, which owns everything the two families share:
the `XResultData` result + `getResults()`, file read/line-ending normalization, path shortening, the compiled-once
file-name pattern and the recursive discovery walk, workspace-root resolution, and HTML report writing (delegating to
`CodeQualityUtil`).

- **Workspace-root resolution** anchors purely on the `plugins` folder: walk up from the running class to the nearest
  `plugins` ancestor; the workspace root is the directory **two levels above `plugins`** (repo dir is one above,
  workspace root is two). It never matches on a repo or container directory *name* — a developer may clone the repo
  under any folder name — and there is no hard-coded path. If no `plugins` ancestor is found it logs an error and
  discovers nothing (safe degrade). The `org.eclipse.osee`-name strip survives only in `getRepoShortName()` for
  display labeling, guarded to error if it strips nothing.
- **Discovery is scoped to `plugins/`**: from the resolved root, discovery fans out across all sibling repos, and
  within each repo it descends into that repo's `plugins` directory only. The canonical ignore list is
  `{.git, \target\, \bin, .integration.tests.}`; subclasses override `getIgnoreDirs()` to adjust (e.g. `CodeReviewGeneral`
  re-includes `.integration.tests.`). `CodeReviewOsgiXml` overrides discovery entirely to collect `OSGI-INF/*.xml`.

### CodeReview (read-only, base `AbstractCodeReview`)

Validate code against standards; they never modify files, and they run in the integration test suite so
new/edited code stays conformant. Each writes an HTML report and can also run standalone via `main()`.

- `CodeReviewXWidgets` — `XX*Widget.java` standards (see "Code Review Checks" above).
- `CodeReviewBlams` — `*Blam.java` standards.
- `CodeReviewOsgiXml` — OSGI-INF component XML (bind/unbind methods, class resolves, right bundle).
- `CodeReviewGeneral` — cross-cutting checks over all Java: no `System.out/err.print` (warning), no TBD markers
  (warning), `Pattern.compile()` should be a `static final` field not compiled per call (warning),
  `AuthenticationRequestFilter` unauthorized call not commented out (error), no commented-out `@Suite.SuiteClasses`
  entries (error).
- `CodeReviewAll` — runs all four and merges results; `runAndAssert()` is the JUnit entry, `main()` the standalone.

`AbstractCodeReview` adds the review-specific machinery on top of `AbstractQual`: per-file `FileResult` collection,
errors-then-warnings-then-clean sorted output, and the JUnit `runAndAssert()` entry point.

### CodeClean (mutating, base `AbstractCodeClean`)

Reorganize / format / revert source. **They MUTATE files, so they are NOT wired into any JUnit suite** — run them
manually via `main()` or through `CodeCleanAll`.

- `CodeCleanOrganizeXWidgets` — reorganizes `XX*Widget.java` into the standard section order (above).
- `CodeCleanOrganizeBlams` — reorganizes `*Blam.java` into the standard BLAM section order.
- `CodeCleanFormatAndOrganize` — runs the Eclipse JDT formatter (incl. import expand/collapse/sort) on files changed
  in the branch. Requires the `ECLIPSEC_EXE` env var; reports and skips gracefully (warning) if not configured.
- `CodeCleanRevertNonFunctional` — over all unmerged commits (base = merge-base with the branch's detected upstream;
  the upstream is resolved at runtime, never hard-coded), reverts files whose only changes are non-functional
  (whitespace/blank-line/formatting). **Import-only or import+formatting changes are NOT reverted** — they are flagged
  "REVIEW (imports involved)" with a callout to run `CodeCleanFormatAndOrganize`, which owns import handling. The
  report lists the commits reviewed at the top.
- `CodeCleanAll` — runs all four in pipeline order (Format → OrganizeBlams → OrganizeXWidgets → Revert) and merges
  results.

Conventions specific to `AbstractCodeClean` (on top of the shared `AbstractQual` machinery):
- **`changeFiles` flag** — every clean op declares `changeFiles = false` in a constructor at the top of the class.
  Default is always report-only; flip to `true` manually only when ready to write. The report states the mode at the
  top and labels the summary `FILES CHANGED` (true) vs `FILES THAT WOULD CHANGE` (false).
- The member-reorganization pipeline: single disk read per file, idempotent re-runs (section headers stripped before
  re-emit), and per-file "would change" vs "changed" reporting.
- The two git-based ops (`CodeCleanFormatAndOrganize`, `CodeCleanRevertNonFunctional`) do their own git-based
  discovery over sibling repos under the resolved workspace root (no hard-coded path); they override `performClean()`
  and share only the result, reporting, and `changeFiles` guard — not the filesystem walk.

### Report output

Reports go to dedicated home-directory folders via `CodeQualityUtil`: CodeClean → `~/CodeCleanOutput/`, CodeReview
→ `~/CodeReviewOutput/`. The folder is created on first use with a `README.txt` explaining it and noting it is safe
to delete anytime.

### Migration-only (not durable)

One-time data-generation tools like `ReportXWidgetMapping` and `ReportXWidgetChanges`/`ReportBlamChanges` are
migration-only — their output is captured in the migration doc, then they are removed.
