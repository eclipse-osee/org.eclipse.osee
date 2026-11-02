---
summary: "History of the OSEE XWidget migration: what the old widget architecture was, why it was re-architected into the XX widget framework, what changed (naming, builder pattern, lifecycle, providers/merge), and the conversion scripts used to perform the migration. Organized by migration area (General, BLAM, Work Definition, Artifact Editor, Workflow Editor), each with Improvements, Steps to Migrate, Tests, Debugging Features, and Metrics. Read this to understand where the widget code came from and how it got to its current shape."
tags: [ide, xwidget, xxwidget, migration, history, scripts]
fileMatch: "**/widgets/xx/**,**/*XX*Widget*"
---

# XWidget to XXWidget Migration

This documents what the OSEE widget framework looked like before the XX re-architecture, why it changed, and how the migration was carried out. For how the current framework works, see `ide-xxwidget-architecture`; for the rules new widgets must follow, see `ide-xxwidget-standards`.

> **DO NOT DELETE — cross-product retention.** This file (and especially the XWidget → XXWidget rename reference below) must be retained even after this product's migration is complete. Other OSEE-based products still use the old `X...` widget framework and will perform this same migration when they catch up to this version. The old→new name mapping and the conversion procedure are required for those downstream migrations. Keep this document until every downstream product has migrated.

## Executive Summary

- **What**: re-architected the entire OSEE widget framework from the inconsistent `X...` family into one standardized `XX...` framework built on `XXAbstractBaseXWidget<T>`, driven by `XOption`/`XWidgetData` and referenced by `WidgetId` tokens.
- **Scale**: commit `feature[TW29349]` — **1424 files changed, ~155,413 insertions, ~52,070 deletions**. Widget consolidation ≈ **1.6 old → 1 new** (154 old names → 98 new XXWidgets, 41 deleted). `[verify per-area breakdowns]`
- **Why it matters**: consistent look/feel, far fewer widgets/tests to maintain, open→edit→auto-persist (fixes lost-edit and concurrent-save issues), and a small common persistence surface — a prerequisite for **server-side persist** and for moving UIs (WFE, BLAMs, ArtEd) to the **web**.
- **Risk**: high blast radius (every WFE/ArtEd/BLAM/dialog/work-def renders through these). See Risk & Blast Radius and Reviewer Guide below.
- **Cross-product**: downstream OSEE products will run this same migration; this doc (and the rename reference) is the playbook.

## Status & Next Steps

_Working notes so this can be picked up across sessions._

**Done (this doc + companions):**
- This migration doc is structured into 5 areas (General XWidget, BLAM, Work Definition, Artifact Editor, Workflow Editor), each with Improvements / Steps to Migrate / Tests / Debugging Features / Metrics / Screenshots (Pre/Post) / Things to Do. (A "Dialog" area was removed as fluff.)
- Content was filled from 6 code-verified sub-agent investigations (ggg HEAD `feature[TW29349]` vs `git_try` pre-migration). Real per-area git `--stat` numbers and counts are in each Metrics section.
- Top-level Executive Summary, Risk & Blast Radius, Reviewer Guide sections added.
- Master rename reference populated (file-standardization renames, semantic old→new consolidation, deleted widgets, and a "Needs Resolution" list).
- Debugging tools distributed to each area (Ctrl+click widget debug, Open XWidget XOption Report, Validate OSEE Types, Open All Registered BLAMs, Open Sample Workflows, WFE "Fill Required", ArtEd Widget Debug report).
- Companion docs updated: `ide-xxwidget-architecture` (added "Rendering Pipeline & Data Flow": XWidgetPage / XWidgetWorkDefRenderer / XWidgetSwtRenderer, XWidgetData→XWidget merge order, OSGi + attribute-provider resolution order, WorkflowDataCreator caveat), `ide-xxwidget-standards`, `ide-blam-standards`, `ide-navigator-architecture`.
- Each area's 7 subsections are prefixed with the area short name for readability: `XWidget - `, `BLAM - `, `WorkDef - `, `ArtEd - `, `WFE - `.
- Fixed the `XXRemainginHoursWfeWidget` misspelling → `XXRemainingHoursWfeWidget` (done by user in code; the rename reference reflects the corrected name).

**Code changes made (this session — beyond doc-only):**
- `BlamOverviewPage` / `BlamUsageSection`: the "User Group Access:" label now **always** shows (Everyone when no groups) and was **moved into the "Description and Usage" twistie**, just above the description text box (was at the top of the overview page). DARK_YELLOW, Courier-New-12-bold.
- **BLAM Migration Reference** section added (after the XWidget rename reference): consolidated old→new BLAM class mapping (renamed / suffix-standardized / unchanged / deleted / new / needs-resolution), reconciled from a pre- vs post-migration class scan. Quantifies the XML→builder conversion (98 `getXWidgetsXml` → 0; 114 `getXWidgetItems`).

**Notes:** the doc is the durable record and auto-loads via steering (`fileMatch` on XX widget files). Durable `CodeClean*`/`CodeReview*` tooling is named; throwaway one-time `Run*` migration scripts are described generically (they'll be deleted).

### Session update — WorkDef→OSGi, review packet, CI fix, output relocation (pre-merge push)

Work done getting the commit review-ready. Several items below SUPERSEDE older text elsewhere in this doc; where
they conflict, this block wins.

- **WorkDef registration moved to OSGi `@Component`.** Work Definitions no longer come from the enumerated
  provider classes. Each WorkDef now self-registers via `@Component(service = IAtsWorkDefinitionBuilder.class,
  immediate = true)` with a no-arg ctor; demo WorkDefs gate via `isApplicable()` → `DemoUtil.isDemoDatabase()`. The
  old `AtsWorkDefinitionProvider` / `AtsWorkDefinitionProviderDemo` / `IAtsWorkDefinitionProvider` classes + their
  OSGI-INF XML were deleted. Collector `AtsWorkDefinitionProviderService` is now `@Component` with a
  MULTIPLE/DYNAMIC/GREEDY `@Reference` (bind/unbind) that RETAINS the bound builders and rebuilds the applicable set
  on `clearCaches()`, re-evaluating `isApplicable()` each time. (An earlier "clearCaches = no-op" approach was
  REVERSED — it broke the CI populate path; see below.)
- **CI build failure fixed** ("Demo Work Definitions not Loaded" in `Pdd20CreateCommittedAction`). Root cause: the
  no-op `clearCaches()` + `isApplicable()` evaluated once at bind time meant a server NOT launched with
  `-Dosee.db=orgdemo` (the CI/CD pipeline) never contributed demo WorkDefs. The retained-builders rebuild (above)
  restores the `AtsDbConfigDemoOp` "set osee.db + internalClearCaches → demo defs appear" behavior.
- **Pre-existing bug fixed:** `AttrWidgetDef.setAttributeType2()` was calling `setAttributeType()` (attr 1) — surfaced
  by the migration making `WorkDefTeamProductLine` actually build.
- **Review packet built** (throwaway tooling in `coderev/packet`, output to `~/CodeXWidgetReview/`): `XWidget_Overview`
  + per-area Consolidation/Rename, Diff, Testing pages, all self-contained/offline, generated by
  `GenerateFullReviewPacket`. The XWidget "rename" page is now a categorized **Consolidation Overview** driven by
  `ReportXWidgetMapping` (counts computed dynamically). Shared look-and-feel in `PacketStyle`. Overview carries a
  **CUI banner + the list of scanned repos** (generators now scan ALL `ws_ggg` repos: `onlyOpenSource=false`).
- **Semantic review done** → `semantic-review/2026-10-12-...-xxwidget-migration.md` (verdict NEEDS_CHANGES; all four
  blockers since resolved: committed merge-conflict in `java-coding-standards.md`, two renderers' stray
  `System.err.println` debug prints, the demo-gating regression).
- **`TestingXWidgetHierarchyView`** replaced the deleted `OpenXWidgetOptionReport` / `XWidgetReportOperations`
  (nav "Testing - Open XWidget Hierarchy View", Admin.Testing, gated to OseeAdmin to match its folder — fixed a
  `NavigateViewLoadingTest` orphan-child failure). NOTE: references elsewhere in this doc to "Open XWidget XOption
  Report" / `openWidgetReport` are STALE — that is now the Hierarchy View.
- **Test report output relocated** off `user.home` into the `OseeData` workspace folder (top level) for the runtime
  tests that have a workspace: `OpenWorkDefExamples.html` (`TestingOpenWorkflowSamples`), `atsCoverage.html`
  (`AtsRestCoverageTest`), `TransitionAssigneesOperationTest.html`, `TestAllRegisteredBlams.html`, and the BLAM nav
  report (`AbstractTestingOpenAndTestAllRegisteredBlams`). `CodeClean*`/`CodeReview*` stand-alone tooling still
  writes to `~/CodeCleanOutput` / `~/CodeReviewOutput` by design. FUTURE TASK (logged): stand-alone tooling should
  use the Windows temp dir (`java.io.tmpdir`) since it has no workspace; runtime/production uses `OseeData`.
- **SSE/ActiveMQ console noise** (not part of this commit — TW22451): dropped routine lifecycle `OseeLog` INFO→FINE
  in `ActiveMqSseBridge`/`ServerToServerEventListener`/`ServerToServerEventPublisher`; the ActiveMQ broker banners
  need `-Dlog4j.configuration=.../activemq-log4j.properties` + `-Dosee.log.default=WARNING` in the launch (launch
  config, not code).

### Next Steps to Merge

1. **Confirm the full ATS integration suite is green** on CI with the retained-builders collector (the demo-WorkDef
   fix). This is the gate.
2. **Confirm `ConsoleOutputRegressionTest` passes** — i.e. nothing writes to `user.home` anymore and no stray console
   output remains (user is re-running to verify).
3. **Clean up remaining review nits** (non-blocking): residual TBD markers in `XXAbstractBaseXWidget`,
   `XWidgetHtmlWidgetFactory`, `WfeReloadAction`, `CloneDialog`; the stale `AtsWorkDefinitionProviderDemo` comment
   reference in `AtsDbConfigDemoOp`.
4. **Decide: should stray `System.out/err.print` be an ERROR (not WARN) in `CodeReviewGeneral`?** It currently only
   warns, which is why the renderer debug prints rode along; left as-is for now.
5. **Fix stale references in THIS doc** (optional, low priority): the two "Open XWidget XOption Report" mentions →
   Hierarchy View; the scale counts (154/98/41) carry `[verify]` and should be reconciled to the final mapping
   numbers if quoted in the team presentation.
6. **Build `CodeReviewWorkDefs`** (deferred): mirror `CodeReviewBlams` — verify every WorkDef has
   `@Component(service = IAtsWorkDefinitionBuilder.class)` + matching OSGI-INF xml; register in `CodeReviewAll`.
7. **Per-proprietary-repo manual step** (when downstream products migrate): remove that repo's own WorkDef provider
   class + its `IAtsWorkDefinitionProvider` XML (the script only knows the org.eclipse.osee names).
8. **Delete the throwaway tooling** after review+merge: `coderev/packet/*`, `coderev/scripts/*`, and the
   `~/CodeXWidgetReview` folder. Keep this doc + the rename reference (cross-product retention).

## Why We Did This

- **The old XWidgets were not standardized or well-structured** — they were risky and intimidating to improve, edit, or fix.
- **Too many overlapping widgets and ways to configure them** — every widget had its own options and conventions, so there was no single obvious choice.
- **Poor performance** of widget creation, modification, and refresh.
- **Each widget loaded its own data and recomputed values independently**, duplicating work and diverging in behavior.
- **A small, common persistence surface is a prerequisite for moving the IDE client to server-side persist** — the old many-widgets/many-ways design was too varied to persist uniformly.
- **A single `XXAbstractBaseXWidget` that meets the needs of all widgets** improves performance, testability, and maintainability, and gives the whole UI a consistent look and feel.
- **A common widget model makes it far easier to move UIs to the web** (Workflow Editor, BLAMs, and others), since one widget contract can be rendered in multiple front ends.

## The Old Architecture (before XX)

- Widgets were the `X...` family (e.g. `XText`, `XCheckBox`, `XDate`, `XCombo`, plus `...Dam` variants for attribute-backed widgets). Naming was inconsistent and did not map cleanly to a `WidgetId`.
- BLAM and work-definition UIs were declared with **XML strings** (`getXWidgetsXml()` on BLAMs, XML widget definitions in work defs), parsed at runtime. This was error-prone (typos in widget-type strings, no compile-time checking) and hard to refactor.
- Widget lifecycle (load vs. paint, when to persist, when to refresh) was implicit and inconsistent across widgets, so each widget reinvented loading/refresh and frequently got ordering wrong (infinite refresh loops, values loaded in the wrong phase, etc.).
- Attribute-backed widgets used `Dam` (Data Access Model) naming; the value/action flow and option merge were spread across multiple layers with no single contract.
- Options/hints were split across four overlapping concepts: `WidgetOption` (ATS work-def), `WidgetHint` (jdk.core), `DisplayHint` (framework.core), and `XOption`.

## Migration Areas

The migration is organized below into these areas:

1. General XWidget Migration
2. BLAM Migration
3. Work Definition Migration
4. Artifact Editor Migration
5. Workflow Editor (WFE) Migration

Each area follows the same structure:

- **Improvements** — what got better and why.
- **Steps to Migrate** — the ordered procedure a downstream product follows for this area.
- **Tests Added / Performed** — tests written or manual verification done.
- **Debugging Features** — tools/toggles to diagnose issues in this area.
- **Metrics** — performance and code-change measurements (lines added/deleted/modified, timings).
- **Screenshots (Pre / Post)** — before/after captures for the team presentation.
- **Things to Do** — remaining/known-deferred work for the area.

---

## 1. General XWidget Migration

The core framework change: standardize widget naming, register via OSGi, introduce `WidgetId` tokens, and create `XXAbstractBaseXWidget<T>` ("one widget to rule them all") that most widgets are driven by via `XOption`/builders rather than by subclassing.

### XWidget - Improvements

- **Standardized naming**: `XX...Widget` for widgets extending `XXAbstractBaseXWidget`; `X...Widget` for widgets extending `XWidget` directly; `Dam` retired in favor of `Art`; class name matches its `WidgetId`.
- **OSGi component registration**: every concrete widget is `@Component(service = XWidget.class, immediate = true)` with a no-arg constructor, resolved by `WidgetId`.
- **`WidgetId` / `WidgetIdAts` tokens** allow widgets to be referenced from Work Definitions and from `api`/`core` bundles that cannot depend on UI widget classes. Proprietary bundles can define their own tokens extending `WidgetId`.
- **One base widget**: `XXAbstractBaseXWidget<T>` provides most functionality (editable/readonly label, selectable+default, filterable dialogs, clear, tooltip, sorting, validation via `isValid()`, icon, enable/disable). Most widgets should not extend it separately — behavior comes from `XOption` + builders.
- **Option/Hint consolidation**: `WidgetOption` + `WidgetHint` + widget-facing `DisplayHint` merged into a single `XOption`. `WidgetOption`/`WidgetHint` removed entirely (confirmed: present in try, gone in ggg). `DisplayHint` stays on attribute definitions but is translated by the provider — chiefly to select a `WidgetId` (e.g. `DisplayHint.XTextFlat` → `XXFlatAttrsWidget`, `XArtRef` → `XXArtifactSelWidget`, `XBranchSel` → `XXBranchSelWidget`) and for options like `DisplayHint.NoSpell` → `XOption.NO_SPELL`. (Readonly display hints are applied as `XOption.READ` by the ArtEd `AttributeFormPart`, not the core provider.) All widget config is carried by `XWidgetData`.
- **Explicit lifecycle**: `setArtifact()` sets fields → base calls `initializeSelected()` (LOAD) → `refresh()` (PAINT); persistence in `handleSelectedPersist()` (return valid/SENTINEL, never null). Single `VAL_` option per widget.
- **Open-dialog → edit → auto-persist model** (same as the web). Every XX widget follows it, which:
  - fixes "users not saving" issues (there is no separate save step to forget);
  - reduces conflicts with other users, since changes persist immediately instead of sitting in an open editor;
  - removes concurrent-save issues from text boxes left open for editing, and refresh issues where a user's un-saved edits could disappear;
  - reduces the number of saves for things like text, where the UI otherwise cannot know when to save.
- **Fewer test cases**: because all UI uses one widget per data type, each data type generally needs only a single widget test rather than testing many bespoke widgets.
- **Work Definitions usually don't specify a widget** for attribute-typed items — the widget is resolved automatically from the ORCS Types model (the attribute type's definition), so most WorkDef widget declarations are just the attribute type.
- **One `XWidgetData` (`widData`) instead of duplicated options/fields.** Previously `XWidget`, `WidgetDefinition`, `XWidgetRenderItem`, and `XWidgetXml` (and every `XWidget` extension) each had their own options/fields. These were brought into a single `XWidgetData`. WidgetDefinitions, BLAMs, and the WidgetBuilders all fill `widData` and hand it to the `XWidget`.
- **Removed the pluggable `XWidgetProvider` extension mechanism** in favor of OSGi contribution. (The two attribute-to-widget providers, `AttributeXWidgetProviderCore`/`AttributeXWidgetProviderIde`, remain but are now configured via `DisplayHint`s rather than a provider registry.)
- **Eliminated XML widget definitions entirely**: removed `XWidgetParser` and the BLAM `XWidgetSwtRenderer.fromXml` path — widgets are now built from `XWidgetData` via the builders, not parsed from XML strings.
- **Rendering reduced to a handful of methods** in `XWidgetSwtRenderer` using an OSGi widget factory (create by `WidgetId`). `[verify: "5-6 methods"]`
- **Attribute types can specify their `WidgetId` via `DisplayHint`s.**
- **`isValid()` returns `XWidgetStatus` (an `XResultData`) instead of a `Status`.** Previously many widgets constructed a new `Status` object on every `isValid()` call; the new path avoids that churn.
- **Validators are optional** — a widget only implements one when it needs checks beyond the defaults (e.g. Percent adds 0..100; Date adds before/after checks). Everything else is handled by default.
- **Naming rules** (also in `ide-xxwidget-standards`): each widget starts with `X` and ends with `Widget`; has a `WidgetId` token with the same name as the class; a `static ID`; a default constructor (abstract widgets have none); `Art`/`Attr` widgets include `ArtWidget` in the name except Viewers and Persist widgets.
- **`XHyperlink...` widgets are the future direction** — a hyperlink label with a filterable dialog.
- **`WidgetId` tokens make usages greppable** — because every widget is referenced by its token, `ctrl-shift-g` (find references) locates all uses.

> NOTE: the migration changed widget **names, constructors (added `WidgetId`), and where options live** — it generally did NOT rewrite the internal workings of each widget.

### XWidget - Steps to Migrate

1. Standardize widget class names to `X...Widget.java` (large rename pass — basis for the master rename list).
2. Add `@Component(service = XWidget.class, immediate = true)` to every widget.  This also suports ability to easily review and change widgets through AI developed scripts.
3. Create `WidgetId`/`WidgetIdAts` tokens for all widgets (and proprietary tokens extending `WidgetId` as needed).  This allows Widgets to be called out in things like Work Definitions in api/core classes that do not have IDE Widget set.  Should make it easier to relate these to Web Components.
4. Create `XXAbstractBaseXWidget<T>` and push generic functionality into it.
5. Migrate widgets one-by-one from `XWidget` to extend `XXAbstractBaseXWidget`, leaving only true differences in each concrete widget.
6. Consolidate `WidgetOption`/`WidgetHint`/`DisplayHint` into `XOption`; route all config through `XWidgetData`.
7. Run the standardization/reorg/code-review scripts (see Cross-Cutting → Conversion Scripts).

### XWidget - Tests Added / Performed

- **`CodeReviewXWidgets`** (`coderev` test suite) — durable standards check over every `X.*Widget.java`: `@Component(service = XWidget.class)`, `WidgetId ID` matches class name, default no-arg ctor, only one ctor calls `super()` (others `this()`), super/this uses `ID`, ATS widgets have `xxWid`, `setArtifact()` doesn't call `refresh()`/`setSelected()`, `initializeSelected()` uses `setSelected(value,false)`, no `validate()` override, no `getSelected()` override, no `System.err/out.print`, no TBD/TODO, copyright header, matching OSGI-INF XML. Siblings: `CodeReviewBlams`, `CodeReviewOsgiXml`, `CodeReviewGeneral`, `CodeReviewAll`.
- **Runtime widget report** (`XWidgetReportOperations.openWidgetReport()`): instantiates every registered widget and flags class-name≠WidgetId or not-exactly-one `VAL_` option.
- **Demo widget gallery** (`WorkDefTeamDemoXWidgetTest`): a WorkDef exercising nearly every XX widget/XOption combination for visual/contract verification.

### XWidget - Debugging Features

- **Ctrl+click a widget label** → widget debug report (resolved `XOption`s, attribute type, `WidgetId`, work def). Wired in `XXAbstractBaseXWidget.createLabelWidget()` (`controlBeingHeld` = Ctrl); self-labeling widgets (`XXStackedWidget`, `XXFlatAttrsWidget`) wire their own.
- **`XWidget.debugXWidgets`** — static `XResultData(false)` flag; `.syserr(...)` prints refresh/create lifecycle events.
- **Layout debug colors** (`XWidgetUtility.debugLayout`, gated by a `debugLayout` boolean): topLevel=YELLOW, group=CYAN, currentComp=GREEN, childComp=BLUE, composite=MAGENTA.
- **Open XWidget XOption Report** (OSEE Navigator, Admin → `OpenXWidgetOptionReport` → `XWidgetService.openWidgetReport()`): shows the resolution of all XOptions through the inheritance chain — WidgetId | attr type/providerFor | VAL_ mode | all XOptions | validation error.
- **Nav — "Testing - Validate OSEE Types"**: reports types defined in code but not used, and types in the database that aren't in code.

Good to know for debugging:
- A widget's `WidgetId`/`WidgetIdAts` token has the **same name as its class** — so `Ctrl-Shift-T` on the token name jumps straight to the widget class.
- A widget can be the default for an attribute type via `setAsProviderFor(AttributeTypeToken)`.
- Ctrl+click debug output is easy to **copy/paste into an AI** to help diagnose a widget.

### XWidget - Metrics

- Whole commit: **1424 files changed, 155,413 insertions(+), 52,070 deletions(-)** (confirmed).
- Per-path (`git show --stat`): `framework.core/.../widget/` **16 files, +1540, -262**; `framework.ui.skynet/.../widgets/xx/` **71 files, +10,842, -0** (new subtree); `framework.ui.skynet/.../widgets/` (all) **242 files, +11,678, -18,925** (net reduction from consolidation).
- Counts (ggg): **114** `XX*Widget.java` repo-wide (98 non-abstract unique + 11 `XXAbstract*`); removed files confirmed via diff: `WidgetOption.java -86`, `WidgetHint.java -27`, `XWidgetParser.java -192`.
- **Widget consolidation**: **154** old names mapped → **98** unique new XXWidgets; **41** deleted; **195** affected. Ratio ≈ **1.6 old → 1 new**.
- Persistence surface reduced to a single `handleSelectedPersist()` on the base (attr + relation branches) — "50+ → ~5" `[verify exact before-count]`. Rendering centralized in `XWidgetSwtRenderer.createXWidget`/`createBody`.
- WFE open/Transition "faster" — `[verify — needs runtime timing]`.

### XWidget - Screenshots (Pre / Post)

_Best "general" before/after is a widget-dense screen rendered by the same data, old vs new._
- Pre: [Insert image here] — the Demo XWidget gallery workflow (`WorkDefTeamDemoXWidgetTest`) in git_try (old `X...` widgets), OR an ArtEd Attributes tab of a multi-attribute artifact.
- Post: [Insert image here] — the same in git_ggg (XX widgets — uniform hyperlink-label editing, consistent spacing), optionally beside the "Open XWidget XOption Report" HTML report.

### XWidget - Things to Do

- No TODO/FIXME/fixthis markers in the xx widget base/renderer (CodeReviewXWidgets forbids them).
- Reconcile the rename-reference "Needs Resolution" items (see below).

---

## 2. BLAM Migration

Convert BLAMs from `getXWidgetsXml()` (XML strings) to `getXWidgetItems()` with the `XWidgetBuilder`/`XWidgetBuilderAts`, standardize structure, and add a test harness.

### BLAM - Improvements

- **Builder replaces XML**: `getXWidgetItems()` with type-safe `WidgetId` constants and convenience methods (`andTextWidget`, `andCheckBoxWidget`, ...) instead of `getXWidgetsXml()` strings.
- **Standardized structure**: class ends in `Blam`, `@Component(service = AbstractBlam.class, immediate = true)`, `BLAM_NAME`/`BLAM_DESC` constants, member ordering, static label constants shared with `variableMap` reads. See `ide-blam-standards`.
- **Display-name cleanup**: stripped "BLAM" from BLAM display-name literals (the navigator/editor add context separately).
- **Admin gating cleanup**: moved admin gating from the `XNavItemCat.OSEE_ADMIN` category to an explicit `getUserGroups()` override (AtsAdmin/OseeAdmin).
- **All BLAM classes renamed to `...Blam`.**
- **`CodeReviewBlams`** validates BLAM code against the standards (durable, kept in the code-review test suite).
- **Contributed through OSGi** (`@Component`) instead of `plugin.xml`.
- **Improved Branch and BranchView widgets and their `variableMap` handling** — every BLAM (~20) that used branch/view no longer has to set up a listener and set branch views based on the selected branch; the widgets handle it. `[verify count ~20]`

### BLAM - Steps to Migrate

1. Convert `getXWidgetsXml()` → `getXWidgetItems()`.
2. Extract duplicate labels to static constants; convert to convenience methods.
3. Convert qualified AttributeType refs to static imports.
4. Convert string widget names to `WidgetId` constants.
5. Add a `<BlamName>Test` extending `AbstractBlamTest`.
6. Strip "BLAM" from display names and migrate admin gating to an explicit `getUserGroups()` override.

### BLAM - Tests Added / Performed

- **`AbstractBlamTest`** harness (`.../blam/test/AbstractBlamTest.java`): every non-abstract BLAM has a `<BlamName>Test` in the same package, discovered by reflection (`BlamService.getBlamTest()` loads `blam.getClass().getName() + "Test"`). It verifies the editor opens, title/description are non-empty, all widgets render, dummy data can be set on each widget (`setDummyData(XWidget)`, overridable; special-cases branch/view widgets), and data round-trips through the `VariableMap`. Demo runs are wrapped in `DemoUtilCore.setDemoDbBlamTest(true/false)`.
- **`TestAllRegisteredBlamsIntegrationTest`** (`@RunWith(Parameterized.class)`) iterates `BlamService.getBlams()` and **fails any BLAM lacking a `<Name>Test`**. Confirmed: **0** non-abstract BLAMs are missing a test.
- `isDemoDbBlamTest()` guard allows BLAMs to run/demo in a demo DB while short-circuiting only during the BLAM test.

### BLAM - Debugging Features

- **BLAM Details section** (`DetailsBlamSection`, collapsed twistie "BLAM Details") shows Name / Class (simple name) / User Groups / Categories. The classname is copy-able for an easy `Ctrl-Shift-T` jump to the code.
- **User-group access label** — always shown (not just for admin-gated BLAMs), inside the "Description and Usage" twistie just above the description text box (`BlamUsageSection`). Reads `"User Group Access: <groups>"`, or `"User Group Access: Everyone"` when no user groups are specified. Rendered in **DARK_YELLOW** (Courier-New-12-bold).
- Navigator appends context to admin BLAMs (see `ide-navigator-architecture`).
- **Nav — "Testing - Open All Registered BLAMs"** (Admin): opens each BLAM to view/review widgets. Demo runtime opens the BLAM, checks title/description/widgets and runs the operation test if available; production runtime does the same **except** it doesn't run the operation test. Un-check "Close BLAM Editors" to spot-check every BLAM's widgets and layout. This is run automatically in the integration tests.

### BLAM - Metrics

- Current BLAM counts (ggg): **76** `*Blam.java` = 4 abstract + **72** concrete; **75** concrete `*BlamTest.java`; try had **48** `*Blam.java` and **0** tests.
- BLAM-path commit stats (`git show --numstat`): **~270 blam files changed, ~14,205 insertions, ~6,811 deletions**.
- BLAMs with manual branch-view listener wiring: **19** in try (the "~20" claim). `[some remain in ggg — see Things to Do]`
- The **"126 BLAMs migrated"** figure is a cross-product/historical total (old + deleted + added across products) computed at runtime by `ReportBlamChanges`, NOT the count of surviving files (72/76). `[verify basis if quoting 126 in the presentation]`

### BLAM - Screenshots (Pre / Post)

_Best subjects: `CountArtifactsOfTypeBlam` (small, admin-gated, branch + checkbox + artifact-type widgets) or `ExampleOutputBlam` (showcases `andBranchAndViewSelWidget()` auto-loading views)._
- Pre: [Insert image here] — the BLAM editor rendered from `getXWidgetsXml()` XML string widgets; no Details twistie, no user-group label.
- Post: [Insert image here] — builder-rendered widgets, the collapsed "BLAM Details" twistie, and the user-group access label; for the branch/view improvement, show a branch pick auto-populating the View list with no BLAM-side code.

### BLAM - Things to Do

- **`getXWidgetsXml()` remaining: NONE** — fully converted. **Missing tests: NONE.**
- **~24 BLAMs still guard `runOperation` with `isDemoDbBlamTest()`** (short-circuit during the harness) and their tests carry a "Future - Needs Operation Test" warning (~61 test files). Real operation tests are the main outstanding BLAM work.
- **~24 BLAMs still do manual branch-view listener wiring** (e.g. `PublishSystemLevelSSDDBlam`, `PublishWithSpecifiedTemplateBlam`) — could be simplified to `wb.andBranchAndViewSelWidget()` now that the widget handles it internally.
- No TODO/FIXME/fixthis markers in `blam/**`.

---

## 3. Work Definition Migration

Simplify Work Definitions to define widgets primarily by **attribute type** (fewer labels, no string widget names), and upgrade `WorkDefBuilder` for the new builders/XX widgets.

### WorkDef - Improvements

Work Definitions became dramatically simpler and more type-safe.

Before (string widget names, explicit labels, per-widget options, separate `WidgetOption` import):
```java
import static org.eclipse.osee.ats.api.workdef.WidgetOption.FILL_VERT;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.RFT;
...
new WidgetDef(AtsAttributeTypes.Description, "XTextDam", FILL_VERT, RFT),
new WidgetDef(AtsAttributeTypes.ValidationRequired, "XComboBooleanDam"),
new WidgetDef("Work Package", "XHyperlinkWorkPackageDam"),
new SignByAndDateWidgetDefinition("Lead Sign Off", SignedOffBy, SignedOffByDate),
```

After (attribute-typed `AttrWidgetDef`; widget/label/options resolved from the attr type; `WidgetId` tokens for non-attr widgets; `XOption`):
```java
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import org.eclipse.osee.framework.core.widget.XOption;
...
new AttrWidgetDef(Description, XOption.REQUIRED),
new AttrWidgetDef(ValidationRequired),
new AttrWidgetDef(WorkPackage),
new SignDateWidgetDef("Lead Sign Off", SignedOffBy, SignedOffByDate),
```

- **`AttrWidgetDef(attrType, ...)` replaces `WidgetDef(attrType, "XSomethingDam", ...)`** — no string widget name; the widget is resolved from the attribute type by the provider. Label defaults to the attribute's unqualified name, so most labels disappear.
- **Non-attribute widgets use `WidgetId` tokens** (`new WidgetDef(XXWorkingBranchWfeWidget)`) instead of strings.
- **Shared header helpers** (e.g. `addDemoCustomHeader(bld)` on `AbstractDemoWorkDef`) replace per-workdef header layout.
- **`CompositeLayoutItem(n, ...)`** for multi-column rows; **`SignDateWidgetDef`**, **`ChangeReportTasksWidgetDef`** replace verbose `...Definition` classes.
- **Options are `XOption`** (e.g. `XOption.REQUIRED`); per-widget builder options like `.andUserGroup(...)` gate individual widgets.
- **No need to specify a default widget for an attribute type** — the attribute type resolves to its widget automatically (see the pre/post WorkDef comparison above).
- **Everything is AUTO_SAVE** — edits persist immediately; no dirty state.

### WorkDef - Steps to Migrate

1. Convert `WidgetDef(attrType, "XSomethingDam", ...)` → `AttrWidgetDef(attrType, ...)`.
2. Replace string widget names with `WidgetId`/`WidgetIdAts` tokens for non-attribute widgets.
3. Replace `WidgetOption`/`WidgetHint` with `XOption`.
4. Extract shared headers to `Abstract*WorkDef` helpers; use `CompositeLayoutItem` for rows.
5. Standardize WorkDef token names.
6. Upgrade `WorkDefBuilder` to support the new builders and XX widgets.

### WorkDef - Tests Added / Performed

- **`TestingOpenWorkflowSamples`** (AtsAdmin nav item) — iterates all Work Definitions, finds one example workflow per WorkDef, and opens each in **both** the Workflow Editor and Artifact Editor, reporting per-editor errors into an HTML `XResultData` (`OpenWorkDefExamples.html`). The systematic "every WorkDef renders" smoke test (manual/admin-triggered).
- **`WorkDefTeamDemoXWidgetTest`** — a WorkDef built to exercise every widget type / XOption (widget-coverage showcase).
- Transition/required-widget fixtures under `ats.core/.../demo/test/` (`...ManagerTest*`) verify transition/required/review behavior. `[verify exact assertions]`

### WorkDef - Debugging Features

- **Open XWidget XOption Report** (`OpenXWidgetOptionReport` → `XWidgetService.openWidgetReport()`) shows XOption resolution through the WorkDef → WidgetDef → provider chain.
- **Nav — "Testing - Open Sample Workflows"**: opens the WFE and ArtEd for one example of each WorkDef in the database — the systematic "every WorkDef renders" check.
- **Nav — "Testing - Validate Work Definitions"**: runs a WorkDef validation report.
- **Nav — "Create Action Using All Actionable Items - Admin"**: creates an action across all actionable items (useful for generating sample workflows to inspect).

### WorkDef - Metrics

- WorkDef + demo area commit stats (`git show --stat` on `ats.core` `demo/`+`workdef/`): **34 files changed, +1093, -675**.
- Representative per-file: `AbstractDemoWorkDef.java` +75/-0 (new shared base/header); `WorkDefTeamDemoCode.java` +36/-50 (net simpler); `WorkDefTeamDemoReq.java` +39/-48; `WorkDefBuilder.java` ~53 changed.
- `WorkDef*.java` in ats.core: **~35** files. `[verify: not all are AbstractWorkDef subclasses]`

### WorkDef - Screenshots (Pre / Post)

_The code before/after is the story (widgets resolve to equivalent controls; no dramatic rendered change)._
- Pre: [Insert image here] — side-by-side source of `WorkDefTeamDemoReq.java` (git_try): string widget names (`"XTextDam"`) + `WidgetOption` imports + hand-built header.
- Post: [Insert image here] — same file (git_ggg): `AttrWidgetDef(...)` + `addDemoCustomHeader(bld)` + `WidgetId` tokens. Optionally the rendered Endorse state, or the Open XWidget XOption Report (post-only) for one attribute.

### WorkDef - Things to Do

- **String widget form (`WidgetDef(attrType, "XSomethingDam", ...)`) remaining: NONE** — grep for `"X\w+Dam"` / `new WidgetDef(attr, "X...` returned zero. Fully eliminated.
- No TODO/FIXME markers in the workdef tree.
- `AttrWidgetDef` Javadoc notes "most WidgetDefinitions with attrType should migrate to this" — demo/workdef set is already migrated; confirm across other products.

---

## 4. Artifact Editor Migration

Render attribute widgets in the Artifact Editor through the new XX framework; add graceful error widgets and a widget-debug report.

### ArtEd - Improvements

- Attribute tab renders XX widgets via `AttributeFormPart`; see `ide-artifact-editor-architecture`.
- **Error-widget pattern**: widget creation failures show a red "Error creating widget... see error log" label instead of crashing the editor/section.
- **Widget Debug report** (ArtEd toolbar bug icon, `OseeProperties.isWidgetDebug()`): HTML report per attribute — Attribute | Base Type | Multiplicity | WidgetId | VAL_ Mode | Read/Write | Other XOptions.

### ArtEd - Steps to Migrate

1. Collapse the old `pages/` + `parts/` + `sections/` structure into `header/` (`ArtEdBaseTab`, `ArtEdTabHeader`) + `tab/*` (art/attr/rel/details) + `util/`.
2. Render each attribute via `AttributeFormPart.createWidget()` → `AttributeXWidgetProviderIde.getDefaultXWidgetData(...)` → `XWidgetPage.createBody(...)` (XX widgets).
3. Set ArtEd options in the `updateMergedXWidgetData` listener hook (`NO_INLINE_EDIT`, `FILL_HORZ`, `READ` for readonly hints, and the single `VAL_` mode by multiplicity/base type).
4. Wrap widget creation in try/catch → error label instead of crashing.
5. Make the editor auto-save (`isDirty()==false`; each action persists immediately; event-driven `reloadEditor()`).

### ArtEd - Tests Added / Performed

- No dedicated `AttributeFormPart`/`ArtAttrTab` JUnit tests found — ArtEd is verified manually and via `TestingOpenWorkflowSamples` (opens each WorkDef sample in ArtEd) and the runtime Widget Debug report. `[verify — manual QA]`

### ArtEd - Debugging Features

- **Toggle Widget Debug** (bug icon, admin-only, `ArtifactEditorActionBarContributor.ToggleWidgetDebug` → `OseeProperties.setWidgetDebug`) → per-attribute widget-resolution HTML report built by `AttributeFormPart.reportWidgetDebug()`: columns **Attribute | Base Type | Multiplicity (min..max) | WidgetId | VAL_ Mode | Read/Write | Other XOptions**.
- Ctrl+click label → widget debug info (works here; self-labeling `XXStackedWidget`/`XXFlatAttrsWidget` wire their own handler). The output is easy to copy/paste into an AI to help debug the widget.

### ArtEd - Metrics

- ArtEd commit stats (`git show --stat` on `.../artifact/editor/`): **26 files changed, +1410, -1770** (net reduction).
- New: `ArtEdBaseTab` +130, `ArtEdTabHeader` +208, `ArtAttrTab` +83, `AttributeFormPart` +377. Deleted old structure: `pages/ArtifactFormPage` −387, `parts/AttributeFormPart` −471, several `sections/*` classes. Confirms the `pages`+`parts`+`sections` → `header`+`tab/*`+`util` collapse.

### ArtEd - Screenshots (Pre / Post)

_Capture the SAME widget-rich artifact's Attributes tab (e.g. a Software Requirement: Name single-text, enum → single-sel, multi-valued → multi-sel, a date, a boolean, a readonly attr)._
- Pre: [Insert image here] — old `pages/ArtifactFormPage` + `sections/AttributesFormSection` with old `X...`/`...Dam` widgets.
- Post: [Insert image here] — `ArtAttrTab` + `AttributeFormPart` with XX widgets, the 3-border content area, and per-row red "X" delete. Post-only: the "Artifact Editor Widget Debug" 7-column HTML report (bug icon → refresh).

### ArtEd - Things to Do

- No TODO/FIXME/fixthis markers under `artifact/editor/**`.
- Cleanup candidates: duplicated Ctrl+click wiring in `XXStackedWidget`/`XXFlatAttrsWidget` (could centralize); `ArtEdTabHeader.getIdInfo()` appears unused (ID now via `XXCopyIdHyperlinkWidget`). `[verify]`

---

## 5. Workflow Editor (WFE) Migration

Render work-definition state widgets through the XX framework; fix the WFE-specific merge and outline handling.

### WFE - Improvements

- State sections render XX widgets resolved+merged via `XWidgetWorkDefRenderer`.
- **The entire WFE header was hard-coded widgets; it is now dot-notation like everything else** (see the pre/post `WfeHeaderComposite` comparison). `[verify — add pre/post snippet]`
- **Faster render and refresh.** `[verify — capture timings]`
- Graceful failure: a failed state section logs without a popup and the editor still opens; failed widgets show an error label.
- Outline keyed to the (static) Work Definition — no needless rebuilds on artifact events; correct reload behavior.

### WFE - Steps to Migrate

1. Replace the hand-coded `Wfe*Header` classes with an `XWidgetBuilderAts` dot-notation chain in `WfeHeaderComposite`; render via `XWidgetPage`/`XWidgetSwtRenderer`.
2. Render state sections through `XWidgetWorkDefRenderer` (`WfeWorkflowSection.createSectionXWidgetBody()`).
3. Wrap each state-section creation in try/catch (log SEVERE, no popup) so the editor still opens on a bad section; failed widgets show an error label from the renderer's merge try/catch.
4. Key the outline to the (static) Work Definition (`WfeOutlinePage.shownWorkDefId`/`reSync()`).
5. Delete the obsolete `Wfe*Header` classes; add the corresponding `XX*WfeWidget` widgets / builder methods.

### WFE - Tests Added / Performed

- **`TestingOpenWorkflowSamples`** opens each WorkDef sample in the WFE (and ArtEd) — manual/admin smoke test.
- WFE integration JUnit tests present: `WfePrintTest`, `WfeEditorAddSupportingFilesTest`, `WfeEditorAddSupportingArtifactsTest`. No dedicated automated per-WorkDef WFE-render test. `[verify]`

### WFE - Debugging Features

- Ctrl+click state-section widget label → widget debug report including the Work Def (state widgets use `XWidgetWorkDefRenderer` as debug provider; `WfeHeaderComposite.addWorkDefToWidgetDebug()` wraps header widgets so they show the Work Def too).
- **ATS WFE Debug user group** (`AtsUserGroups.AtsWfeDebugUser`, id `82947561L`; demo member: Joe Smith): `WfeTransitionHeader` adds a dark-red "Dev Debug Mode:" row with a "Fill Required" hyperlink → `WfeDebugFillAction.fillCurrentState()` fills all required-but-empty header + current-state widgets with type-correct dummy data (sign-off widgets via `setSigned`), for rapid transition.

### WFE - Metrics

- WFE-scoped commit stats (`git show --stat` on `ats.ide/.../editor/`): **+1418, -4597** across ~51 files (large net deletion from removing hard-coded headers).
- **14 `Wfe*Header` classes deleted** (confirmed via `--diff-filter=D`): ActionableItem, Assignees, BlockedWorkflow, EstimatedHours, HoldWorkflow, Metrics, Originator, PercentComplete, StateCreatedOrig, StateNotes, TargetedVersion, TeamAndIds, Title, WorkflowNotes. Also removed: `WfeRelatedComposite`, `XRelatedWidget`. Added: `WfeDebugFillAction`, `WfeRelatedWidgets`, `XXRelatedWfeWidget`.
- `WfeHeaderComposite.java` was the largest rewrite (~579 lines changed — the dot-notation conversion).
- WFE open/Transition "faster" — `[verify — runtime timing]`.

### WFE - Things to Do

- **Merge-drop risk** (standing): `XWidgetWorkDefRenderer.mergeXWidgetWithWidgetDef()` copies WidgetDef→merged **field by field**. `relationTypeSide`, `conditions`, and `parameters` were each explicitly rescued after being dropped (prior bugs). Any WidgetDef/`XWidgetData` field NOT in the explicit copy list is silently dropped in the WFE (ArtEd's `clone()` carries them for free). **Recommend replacing the hand-rolled field copy with a clone-based merge that cannot miss fields.**
- Graceful-failure inconsistency: state sections log silently, but `WfeHeaderComposite.create()` total failure still uses `SEVERE_POPUP`.
- No TODO/FIXME/fixthis markers in the WFE subtree.

### WFE - Screenshots (Pre / Post)

_Capture the SAME workflow state / WFE header before and after._
- Pre: [Insert image here] — a WFE state section + hard-coded header rendered with old widgets.
- Post: [Insert image here] — the same state + dot-notation header rendered with XX widgets.

---

## XWidget → XXWidget Rename Reference (master rename list)

> **Retention-critical.** This is the authoritative old→new class/name mapping used to migrate a product from the `X...` widget framework to the `XX...` framework. Downstream OSEE-based products will apply this same mapping when they upgrade. Do not delete or prune entries even after this product finishes migrating.

There are **two** mappings. First an intermediate **file-standardization rename** (`X<name>.java` → `X<name>Widget.java`, abstracts → `XAbstract...Widget.java`), then the **semantic consolidation** where many old widgets collapse onto a single `XX...Widget`. Source of the consolidation map: the `ReportXWidgetMapping` tool / the `XWidget.patch` for commit `feature[TW29349]: Cleanup, tokenize and standardize XWidgets`.

### A. File-standardization renames (X<name>.java → X<name>Widget.java)

| Old file | New file |
|---|---|
| XArtifactReferencedAttributeWidget.java | XAbstractArtRefAttributeArtWidget.java |
| XAttachmentWidget.java | XAbstractAttachmentArtWidget.java |
| XButtonWithLabelDam.java | XAbstractButtonWithLabelArtWidget.java |
| XFlatWidget.java | XAbstractFlatWidget.java |
| XHyperlabelVersionSelectionWithPersist.java | XAbstractHyperlabelVersionSelPersistWidget.java |
| XHyperlinkLabelCmdValueSelDam.java | XAbstractHyperlinkLabelCmdValueSelArtWidget.java |
| XHyperlinkLabelCmdValueSelection.java | XAbstractHyperlinkLabelCmdValueSelWidget.java |
| XHyperlinkLabelValueSelection.java | XAbstractHyperlinkLabelValueSelWidget.java |
| XHyperlinkWithFilteredDialog.java | XAbstractHyperlinkWithFilteredDialogWidget.java |
| XLabelValueBase.java | XAbstractLabelValueBaseWidget.java |
| XRadioButtonsDam.java | XAbstractRadioButtonsArtWidget.java |
| XSelectFromDialog.java | XAbstractSelectFromDialogWidget.java |
| XButtonCommon.java | XAbstractSelectedWidget.java |
| XStackedWidget.java | XAbstractStackedWidget.java |
| XFileTextWithSelectionDialog.java | XAbstractTextWithFileDirSelDialogWidget.java |
| XWorkingBranchWidgetAbstract.java | XAbstractWrkBranchArtWidget.java |
| XWorkingBranchButtonAbstract.java | XAbstractWrkBranchButtonWidget.java |
| XActionableItemAllCombo.java | XActionableItemAllComboWidget.java |
| XActionableItemCombo.java | XActionableItemComboWidget.java |
| XArtifactList.java | XArtifactListWidget.java |
| XArtifactSelectWidgetWithSave.java | XArtifactSelectPersistWidget.java |
| XArtifactTypeComboViewer.java | XArtifactTypeComboViewerWidget.java |
| XAttributeTypeComboViewer.java | XAttributeTypeComboViewerWidget.java |
| XAttributeTypeMultiChoiceSelect.java | XAttributeTypeMultiChoiceSelectWidget.java |
| XBranchSelectWidgetDam.java | XBranchSelectArtWidget.java |
| XButtonViaAction.java | XButtonViaActionWidget.java |
| XButton.java | XButtonWidget.java |
| XComboEnumDam.java | XComboEnumArtWidget.java |
| XCombo.java | XComboWidget.java |
| XDateDam.java | XDateArtWidget.java |
| XDate.java | XDateWidget.java |
| XEstimatedPointsWidget.java | XEstimatedPointsArtWidget.java |
| XFoundInVersionWithPersistWidget.java | XFoundInVersionPersistWidget.java |
| XGitFetchButton.java | XGitFetchButtonArtWidget.java |
| XGoalCombo.java | XGoalComboWidget.java |
| XHyperlabelMemberSelectionDam.java | XHyperLinkMemberSelArtWidget.java |
| XHyperlabelTeamDefinitionSelection.java | XHyperlabelTeamDefinitionSelWidget.java |
| XHyperlabelWorkflowApplicabilitySelection.java | XHyperlabelWorkflowApplicSelArtWidget.java |
| XAgileFeatureHyperlinkWidget.java | XHyperlinkAgileFeatureWidget.java |
| XHyperlabelActionableItemSelection.java | XHyperlinkAiSelWidget.java |
| XHyperlinkApplicabilityWidgetDam.java | XHyperlinkApplicabilityArtWidget.java |
| XHyperlinkLabelEnumeratedArtDam.java | XHyperlinkArtEnumeratedArtWidget.java |
| XHyperlinkLabelEnumeratedArt.java | XHyperlinkArtEnumeratedWidget.java |
| XAssigneesHyperlinkWidget.java | XHyperlinkAssigneesWidget.java |
| XHyperlinkBranchViewSelect.java | XHyperlinkBranchViewSelectArtWidget.java |
| XHyperlinkChangeTypeSelectionDam.java | XHyperlinkChangeTypeSelectionArtWidget.java |
| XHyperlinkChangeTypeSelection.java | XHyperlinkChangeTypeSelectionWidget.java |
| XHyperlinkFeatureDam.java | XHyperlinkFeatureArtWidget.java |
| XHyperlabelFoundInVersionSelection.java | XHyperlinkFoundInVersionSelWidget.java |
| XHyperlabelIntroducedInVersionSelection.java | XHyperlinkIntroducedInVersionSelWidget.java |
| XHyperlinkLabelDateDam.java | XHyperlinkLabelDateArtWidget.java |
| XHyperlinkLabelDate.java | XHyperlinkLabelDateWidget.java |
| XHyperlinkLabelValueSelectionDam.java | XHyperlinkLabelValueSelectionArtWidget.java |
| XHyperlinkLabelValueStringSel.java | XHyperlinkLabelValueStringSelWidget.java |
| XHyperlinkMeetingAttendeesSelectionDam.java | XHyperlinkMeetingAttendeesSelArtWidget.java |
| XHyperlabelMemberSelection.java | XHyperlinkMemberSelWidget.java |
| XOriginatorHyperlinkWidget.java | XHyperlinkOriginatorWidget.java |
| XHyperlinkPrBuildSelection.java | XHyperlinkPrBuildSelectionWidget.java |
| XHyperlinkPrioritySelection.java | XHyperlinkPrioritySelectionWidget.java |
| XSprintHyperlinkWidgetDam.java | XHyperlinkSprintArtWidget.java |
| XSprintHyperlinkWidget.java | XHyperlinkSprintWidget.java |
| XHyperlinkTriStateBoolean.java | XHyperlinkTriStateBooleanWidget.java |
| XHyperlabelVersionSelection.java | XHyperlinkVersionSelWidget.java |
| XHyperlinkWfdForActiveAis.java | XHyperlinkWfdForActiveAisWidget.java |
| XHyperlinkWfdForConfiguration.java | XHyperlinkWfdForConfigurationWidget.java |
| XHyperlinkWfdForEnumAttrDam.java | XHyperlinkWfdForEnumAttrArtWidget.java |
| XHyperlinkWfdForEnumAttr.java | XHyperlinkWfdForEnumAttrWidget.java |
| XHyperlinkWfdForEnum.java | XHyperlinkWfdForEnumWidget.java |
| XHyperlinkWfdForObject.java | XHyperlinkWfdForObjectWidget.java |
| XHyperlinkWfdForProgramAi.java | XHyperlinkWfdForProgramAiWidget.java |
| XHyperlinkWfdForRelatedStateDam.java | XHyperlinkWfdForRelatedStateArtWidget.java |
| XHyperlinkWfdForUserAll.java | XHyperlinkWfdForUserAllWidget.java |
| XHyperlinkWfdForUser.java | XHyperlinkWfdForUserWidget.java |
| XHyperlinkWorkDefDam.java | XHyperlinkWorkDefArtWidget.java |
| XHyperlinkWorkPackageDam.java | XHyperlinkWorkPackageArtWidget.java |
| XInteger.java | XIntegerWidget.java |
| XIntroducedInVersionWithPersistWidget.java | XIntroducedInVersionPersistWidget.java |
| XLabel.java | XLabelWidget.java |
| XListViewer.java | XListViewerWidget.java |
| XLong.java | XLongWidget.java |
| XMapEntry.java | XMapEntryWidget.java |
| XMembersCombo.java | XMembersComboWidget.java |
| XPercent.java | XPercentArtWidget.java |
| XRadioButtonsBooleanTriStateDam.java | XRadioButtonsBooleanTriStateArtWidget.java |
| XStackedDam.java | XStackedArtWidget.java |
| XTargetedVersionWithPersistWidget.java | XTargetedVersionPersistWidget.java |
| XTeamDefinitionCombo.java | XTeamDefinitionComboWidget.java |
| XTextFlatDam.java | XTextFlatArtWidget.java |
| XText.java | XTextWidget.java |
| XValidateReqChangesButton.java | XValidateReqChangesButtonArtWidget.java |
| XWorkingBranchLabel.java | XWorkingBranchLabelWidget.java |
| XWorkingBranchButtonArtifactExplorer.java | XWrkBranchButtonArtifactExplorerWidget.java |
| XWorkingBranchButtonChangeReport.java | XWrkBranchButtonChangeReportWidget.java |
| XWorkingBranchButtonContextChangeReport.java | XWrkBranchButtonContextChangeReportWidget.java |
| XWorkingBranchButtonCreate.java | XWrkBranchButtonCreateWidget.java |
| XWorkingBranchButtonDeleteMergeBranches.java | XWrkBranchButtonDeleteMergeBranchesWidget.java |
| XWorkingBranchButtonDelete.java | XWrkBranchButtonDeleteWidget.java |
| XWorkingBranchButtonFavorites.java | XWrkBranchButtonFavoritesWidget.java |
| XWorkingBranchButtonLock.java | XWrkBranchButtonLockWidget.java |
| XWorkingBranchButtonWordChangeReport.java | XWrkBranchButtonWordChangeReportWidget.java |
| XWorkingBranchUpdate.java | XWrkBranchUpdateWidget.java |
| XCommitManager.java | XCommitManagerArtWidget.java |
| XDefectViewer.java | XDefectViewerWidget.java |
| XHyperlinkWfdFunctAreaDam.java | XAbstractXHyperlinkWfdFunctAreaArtWidget.java |
| XOpenSprintBurndownButton.java | XOpenSprintBurndownArtWidget.java |
| XOpenSprintBurnupButton.java | XOpenSprintBurnupArtWidget.java |
| XOpenSprintDataTableButton.java | XOpenSprintDataTableArtWidget.java |
| XOpenSprintReportsButton.java | XOpenSprintReportsArtWidget.java |
| XOpenSprintSummaryButton.java | XOpenSprintSummaryArtWidget.java |
| XOpenStoredSprintReportsButton.java | XOpenStoredSprintReportsArtWidget.java |
| XStoreSprintReportsButton.java | XStoreSprintReportsArtWidget.java |
| XHyperlinkOpenBitTab.java | XHyperlinkOpenBitTabWidget.java |
| XVersionList.java | XVersionListWidget.java |
| XCreateEscapeWfXButton.java | XAbstractCreateEscapeWfButtonArtWidget.java |
| XCreateEscapeDemoWfXButton.java | XCreateEscapeDemoWfButtonWidget.java |
| XSiblingWorldWidget.java | XAbstractSiblingWorldArtWidget.java |
| XTaskEstSiblingWorldWidget.java | XAbstractTaskEstSiblingWorldWidget.java |
| XTaskEstWidget.java | XAbstractTaskEstWidget.java |
| XHyperlinkJiraStatusSelection.java | XAbstractHyperlinkJiraStatusArtWidget.java |
| XMiniTaskWidget.java | XAbstractMiniTaskArtWidget.java |
| XMiniWorldWidget.java | XAbstractMiniWorldWidget.java |
| XArtEdAttrViewer.java | XArtEdAttrViewerWidget.java |
| XUserRoleViewer.java | XUserRoleViewerWidget.java |
| XAbstractSignByAndDateButton.java | XAbstractSignByAndDateButtonArtWidget.java |
| XSignByAndDateWidget.java | XSignByAndDateArtWidget.java |
| Xchild/XHyperlinkWfdSelectedUserGroup.java | XAbstractHyperlinkWfdSelectedUserGroupWidget.java |
| Xchild/AbstractXHyperlinkWfdSelectedChildDam.java | XAbstractXHyperlinkWfdSelectedChildArtWidget.java |
| Xchild/AbstractXHyperlinkWfdSelectedChild.java | XAbstractXHyperlinkWfdSelectedChildWidget.java |
| Xchild/AbstractXHyperlinkWfdSelectedUserGroupWithNotifyDam.java | XAbstractXHyperlinkWfdSelectedUserGroupWithNotifyArtWidget.java |

### B. Semantic consolidation (old widget → new XXWidget)

Many old widgets collapse onto one `XX...Widget`. (A few new names intentionally keep a single `X` or a non-`XX` form: `XCommitManagerWfeWidget`, `XUserRoleViewerWidget`, `ReviewFormalTypeSearchWidget`, `SignDateWidgetDef`, `WidgetDef`, `AbstractSearchWidget`.)

| Old name | New name |
|---|---|
| XText | XXTextWidget |
| XTextDam | XXTextWidget |
| XTextFlatDam | XXFlatAttrsWidget |
| XFlatDam | XXFlatAttrsWidget |
| XStackedDam | XXStackedWidget |
| XLongTextDam | XXLongSTextWidget |
| XTextInternalWidget | XXTextWidget |
| XTextResourceDropDam | XXTextResourceDropArtWidget |
| XInteger | XXIntegerSTextWidget |
| XIntegerDam | XXIntegerSTextWidget |
| XFloat | XXDoubleSTextWidget |
| XFloatDam | XXDoubleSTextWidget |
| XPercent | XXPercentSTextWidget |
| XCheckBox | XXCheckBoxWidget |
| XCheckBoxDam | XXCheckBoxWidget |
| XComboBooleanDam | XXBooleanWidget |
| XDate | XXDateSelWidget |
| XDateDam | XXDateSelWidget |
| XLabel | XXLabelWidget |
| XLabelDam | XXLabelValueWidget |
| XHyperlinkLabel | XXButtonHyperWidget |
| XButtonPush | XXButtonPushWidget |
| XCombo | XXStringsWidget |
| XComboDam | XXStringsWidget |
| XComboViewer | XXStringsWidget |
| XListViewer | XXListWidget |
| XListDropViewer | XXListDropWidget |
| XListDropViewerDam | XXListDropWidget |
| XArtifactList | XXArtifactListWidget |
| XHyperlinkArtifactSel | XXArtifactSelWidget |
| XArtifactSelectWidget | XXArtifactSelWidget |
| XHyperlinkArtifactRefIdEntryWidget | XXArtifactRefWidget |
| XBranchSelectWidget | XXBranchListWidget |
| XHyperlinkBranchSel | XXBranchListWidget |
| XHyperlinkBranchViewSelect | XXBranchViewWidget |
| XArtifactTypeComboViewer | XXArtifactTypeWidget |
| XArtifactTypeMultiChoiceSelect | XXArtifactTypeWidget |
| XAttributeTypeComboViewer | XXAttributeTypeWidget |
| XAttributeTypeMultiChoiceSelect | XXAttributeTypeWidget |
| XAttributeTypeListViewer | XXAttributeTypeWidget |
| XMembersCombo | XXUserTokenWidget |
| XMembersList | XXUserTokenWidget |
| XHyperlinkWfdForUserAll | XXUserTokenWidget |
| XFileSelectionDialog | XXFileSelWidget |
| XDirSelectionDialog | XXDirSelWidget |
| XMapEntry | XXMapEntryWidget |
| XActionableItemWidget | XXActionableItemWidget |
| XActionableItemCombo | XXActionableItemWidget |
| XActionableItemAllCombo | XXActionableItemWidget |
| XHyperlabelActionableItemSelection | XXActionableItemWidget |
| XHyperlinkWfdForActiveAis | XXActionableItemWidget |
| XAssigneesHyperlinkWidget | XXAssigneesWidget |
| XAssigneesListWidget | XXAssigneesWidget |
| XAssigneesListWorkItemWidget | XXAssigneesWidget |
| AbstractXAssigneesListWidget | XXAssigneesWidget |
| XHyperlinkChangeTypeSelection | XXChangeTypeWidget |
| XHyperlinkChangeTypeSelectionDam | XXChangeTypeWidget |
| ChangeTypeWidgetDefinition | XXChangeTypeWidget |
| XHyperlinkPrioritySelection | XXPriorityWidget |
| XHyperlinkPrioritySelectionDam | XXPriorityWidget |
| PriorityWidgetDefinition | XXPriorityWidget |
| XEstimatedPointsWidget | XXPointsEnumWidget |
| XPointsNumericWidget | XXPointsNumericWidget |
| XTargetedVersionHyperlinkWidget | XXTargetedVersionWidget |
| XTargetedVersionWithPersistWidget | XXTargetedVersionWidget |
| XHyperlabelVersionSelection | XXVersionSelWidget |
| XHyperlabelVersionSelectionWithPersist | XXVersionSelWidget |
| XFoundInVersionWithPersistWidget | XXFoundInVersionWidget |
| XHyperlabelFoundInVersionSelection | XXFoundInVersionWidget |
| XIntroducedInVersionWithPersistWidget | XXIntroducedInVersionWidget |
| XHyperlabelIntroducedInVersionSelection | XXIntroducedInVersionWidget |
| XVersionList | XXVersionListWidget |
| XOriginatorHyperlinkWidget | XXOriginatorWidget |
| XTeamDefinitionCombo | XXTeamDefinitionWidget |
| XHyperlabelTeamDefinitionSelection | XXTeamDefinitionWidget |
| XWorkingBranchWidgetAbstract | XXWorkingBranchWfeWidget |
| XWorkingBranchLabel | XXWorkingBranchWfeWidget |
| XWorkingBranchButtonAbstract | XXWorkingBranchWfeWidget |
| XWorkingBranchButtonCreate | XXWorkingBranchWfeWidget |
| XWorkingBranchButtonDelete | XXWorkingBranchWfeWidget |
| XWorkingBranchButtonArtifactExplorer | XXWorkingBranchWfeWidget |
| XWorkingBranchButtonChangeReport | XXWorkingBranchWfeWidget |
| XWorkingBranchButtonWordChangeReport | XXWorkingBranchWfeWidget |
| XWorkingBranchButtonContextChangeReport | XXWorkingBranchWfeWidget |
| XWorkingBranchButtonFavorites | XXWorkingBranchWfeWidget |
| XWorkingBranchButtonLock | XXWorkingBranchWfeWidget |
| XWorkingBranchUpdate | XXWorkingBranchWfeWidget |
| XWorkingBranchButtonDeleteMergeBranches | XXWorkingBranchWfeWidget |
| XCommitManager | XCommitManagerWfeWidget |
| CommitXManager | XCommitManagerWfeWidget |
| XDefectViewer | XXDefectViewerWidget |
| XUserRoleViewer | XUserRoleViewerWidget |
| XHyperlinkWfdForRelatedState | XXRelatedStateWfeWidget |
| XHyperlinkWfdForRelatedStateDam | XXRelatedStateWfeWidget |
| OperationalImpactXWidget | XXOperationalImpactWidget |
| OperationalImpactWithWorkaroundXWidget | XXOperImpactWithWorkArndWidget |
| XHyperlinkWorkPackageDam | XXWorkPackageEnumArtWidget |
| XHyperlabelWorkflowApplicabilitySelection | XXWorkflowApplicWidget |
| XHyperlinkApplicabilityWidget | XXWorkflowApplicWidget |
| XHyperlinkApplicabilityWidgetDam | XXWorkflowApplicWidget |
| XAtsProgramComboWidget | XXProgramWidget |
| XHyperlinkWfdForPrograms | XXProgramWidget |
| XProgramSelectionWidget | XXProgramWidget |
| XHyperlinkWfdForProgramClosure | XXProgramWidget |
| XHyperlinkWfdForProgramAi | XXProgramAiWidget |
| XAgileFeatureHyperlinkWidget | XXAgileFeatureSelWidget |
| XSprintHyperlinkWidget | XXAgileSprintSelWidget |
| XSprintHyperlinkWidgetDam | XXAgileSprintSelWidget |
| XOpenSprintSummaryButton | XXOpenSprintSummaryArtWidget |
| XOpenSprintDataTableButton | XXOpenSprintDataTableArtWidget |
| XOpenSprintBurndownButton | XXOpenSprintBurndownArtWidget |
| XOpenSprintBurnupButton | XXOpenSprintBurnupArtWidget |
| AbstractXOpenSprintBurnupButton | XXOpenSprintBurnupArtWidget |
| XOpenSprintReportsButton | XXOpenSprintReportsArtWidget |
| XOpenStoredSprintReportsButton | XXOpenStoredSprintReportsArtWidget |
| XStoreSprintReportsButton | XXStoreSprintReportsArtWidget |
| XGitFetchButton | XXGitFetchButtonWfeWidget |
| XHyperlinkMeetingAttendeesSelectionDam | XXMeetingAttendeesWfeWidget |
| MeetingAttendeeWidgetDefinition | XXMeetingAttendeesWfeWidget |
| XHyperlinkFeatureDam | XXPleFeatureWidget |
| XBlockWfeWidget | XXBlockWfeWidget |
| XHoldWfeWidget | XXHoldWfeWidget |
| StatePercentCompleteXWidget | XXPercentWfeWidget |
| XHoursSpentWfeWidget | XXHoursSpentWfeWidget |
| XRemainingHoursWfeWidget | XXRemainingHoursWfeWidget |
| XTotalHoursSpentWfeWidget | XXTotalHoursSpentWfeWidget |
| XTitleWfeWidget | XXTitleWfeWidget |
| XWorkflowNotesWfeWidget | XXWorkflowNotesWfeWidget |
| ReviewInfoXWidget | XXReviewInfoWidget |
| XReviewTypeWidget | XXReviewFormalTypeWidget |
| ReviewTypeSearchWidget | ReviewFormalTypeSearchWidget |
| TaskInfoXWidget | XXTaskInfoWfeWidget |
| XSignByAndDateButton | XXSignByAndDateArtWidget |
| SignByAndDateWidgetDefinition | SignDateWidgetDef |
| XDragDropAttachWidget | XXDragDropAttachWfeWidget |
| XHyperlinkConfigurationWidget | XXPleConfigurationWidget |
| XHyperlinkWfdForConfiguration | XXPleConfigurationWidget |
| XHyperlinkWfdForConfigurationDam | XXPleConfigurationWidget |
| XHyperlinkBuildImpactWidget | XXBuildImpactNameWidget |
| XHyperlinkBuildImpactStateWidget | XXBuildImpactStateWidget |
| XValidateReqChangesButton | XXValidateReqChangesButtonWfeWidget |
| XAttachmentExampleWidget | XXAttachmentExampleWidget |
| XCancellationReasonTextWidget | XXTextWidget |
| XArtifactReferencedAtsObjectAttributeWidget | XXAtsArtifactRefWidget |
| XHyperlinkOpenBitTab | XXHyperlinkOpenBitTabWidget |
| XHyperlinkJiraStatus | XXHyperlinkJiraStatusWidget |
| XRelatedWidget | XXRelatedWfeWidget |
| XHyperlinkPrBuildSelection | XXPrBuildSelWidget |
| XFunctAreaDemoWidget | XXFunctAreaDemoWidget |
| XFundingGoalDemoWidget | XXFundingGoalDemoWidget |
| XCreateEscapeDemoWfButton | XXCreateEscapeDemoWfButtonWidget |
| WidgetDefinition | WidgetDef |
| SpaceWidgetDefinition | SpaceWidgetDef |
| CreateChangeReportTasksWidgetDefinition | ChangeReportTasksWidgetDef |
| EnumeratedArtifactWidgetDefinition | EnumArtWidgetDef |
| HeaderDefinitionBuilder | CustomHeaderDefinitionBuilder |
| AbstractXHyperlinkEntrySearchWidget | AbstractSearchWidget |
| AbstractXHyperlinkWfdSearchWidget | AbstractSearchWidget |

### C. Deleted widgets (no replacement)

`AbstractWfeSubWorkflow`, `AtsAttributeXWidgetProvider`, `AtsExportAction`, `AtsExportBlam`, `AtsXWidgetProvider`,
`BaseXWidgetProvider`, `DetailedTestStatusBlam`, `DetailedTestStatusOld`, `DuplicateWidgetUpdateResolver`,
`FirstTimeQualityMetricReportItem`, `GenerateFullVersionReportItem`, `GenerateVersionReportItem`,
`IAtsWidgetOptionHandler`, `PublishFullVersionReportItem`, `VersionMetrics`, `VersionReportJob`, `VersionTeamMetrics`,
`WfeActionableItemHeader`, `WfeAssigneesHeader`, `WfeBlockedWorkflowHeader`, `WfeEstimatedHoursHeader`,
`WfeHoldWorkflowHeader`, `WfeMetricsHeader`, `WfeOriginatorHeader`, `WfePercentCompleteHeader`, `WfeRelatedComposite`,
`WfeStateCreatedOrigHeader`, `WfeStateNotesHeader`, `WfeTargetedVersionHeader`, `WfeTeamAndIdsHeader`, `WfeTitleHeader`,
`WfeWorkflowCustomSection`, `WfeWorkflowNotesHeader`, `WidgetOption`, `WidgetOptionHandler`,
`XAbstractHyperlinkTriStateNotifyDam`, `XAtsUserListDam`, `XGoalCombo`, `XHyperlinkWorkDefDam`,
`XTransitionToStateComboWidget`, `AttributeWidget`.

### D. Needs Resolution (ambiguities to reconcile)

These entries in the source data are inconsistent or suspect — resolve before treating the mapping as final:

- **`XWidget.java → XOperationalImpactArtWidget.java` AND `XWidget.java → XOperationalImpactWithWorkaroundArtWidget.java`** (file-rename list): `XWidget.java` is the framework base class, not a file that was renamed to those two. Likely a paste artifact; confirm the real source files for the two Operational Impact widgets.
- **`XOpenSprintBurnupButton.java` listed twice** (file-rename list) → once to `XAbstractOpenSprintBurnupArtWidget.java` and once to `XOpenSprintBurnupArtWidget.java`. Confirm which is correct (likely an abstract base + concrete split).
- **Consolidation list vs. file-rename list divergence** — the file-rename (A) targets are intermediate `X...Widget` names; the consolidation (B) targets are final `XX...Widget` names. Some widgets appear only in one list. An agent should reconcile A→B end-to-end so every old widget has a single final destination.
- **Non-`XX` final names** (`XCommitManagerWfeWidget`, `XUserRoleViewerWidget`) — confirm these are intentionally single-`X` (they are viewers/complex widgets not fully folded into the XX base) vs. pending further migration.

> Agents: verify every row against `git_ggg` (post-commit) or by diffing `git_ggg` vs `git_try`, fill any missing old→new pairs, and resolve section D.

## BLAM Migration Reference

Consolidated old→new BLAM class mapping, reconciled from a class scan of the pre-migration (`git_try`) and post-migration (`git_ggg`) trees plus the migration intent list. Every OLD class lands in exactly one bucket (renamed / suffix-standardized / unchanged / deleted); every NEW class in exactly one (rename target / suffix target / unchanged / new).

### Summary counts

| Metric | OLD (pre) | NEW (post) |
|---|---|---|
| Total BLAM classes | 138 | 128 |
| — Abstract | 5 | 6 |
| — Concrete | 133 | 122 |
| Widget method `getXWidgetsXml` (old XML style) | 98 | 0 |
| Widget method `getXWidgetItems` (new builder style) | 25 | 114 |
| No widget method reported | 15 | 14 |

| Category | Count |
|---|---|
| Renamed (beyond suffix) | 9 explicit + 1 inferred = 10 |
| Suffix-standardized (`… → …Blam`) | 52 |
| Unchanged (name identical, already `…Blam`) | 62 |
| Deleted (no NEW counterpart) | 14 |
| **OLD total** | **138** |
| New BLAMs (no OLD counterpart) | 4 |
| **NEW total** (124 matched + 4 new) | **128** |

**XML→builder conversion is the core of the migration**: 98 OLD BLAMs used `getXWidgetsXml` and **zero** NEW do; NEW has 114 using `getXWidgetItems` (up from 25). Effectively every widget-bearing BLAM was converted from XML to the builder style, with widget fields moved into `XWidgetData`.

### A. Renamed BLAMs (changed beyond adding a `Blam` suffix)

| Old | New | Reason |
|---|---|---|
| `SLEDBinaryFilesBlamV4` | `SLEDBinaryFilesV4Blam` | `Blam` moved before version suffix |
| `SLEDBinaryFilesBlamV6` | `SLEDBinaryFilesV6Blam` | `Blam` moved before version suffix |
| `SLEDTablesCFilesBlamV4` | `SLEDTablesCFilesV4Blam` | `Blam` moved before version suffix |
| `SLEDTablesCFilesBlamV6` | `SLEDTablesCFilesV6Blam` | `Blam` moved before version suffix |
| `SLEDTestSupportBlamV4` | `SLEDTestSupportV4Blam` | `Blam` moved before version suffix |
| `SLEDTestSupportBlamV6` | `SLEDTestSupportV6Blam` | `Blam` moved before version suffix |
| `CreateNewActionAtLocactionBlam` | `CreateNewActionAtLocationBlam` | typo fix (Locaction → Location) |
| `CreateNewChangeRequestBlam` | `AbstractCreateNewChangeRequestBlam` | made abstract |
| `CreateNewProblemReportBlam` | `AbstractCreateNewProblemReportBlam` | made abstract |

### B. Suffix-standardized (old → `…Blam`)

Name only gained the `Blam` suffix; matched by identical display name and/or file-path stem. All also converted `getXWidgetsXml` → `getXWidgetItems` except where the OLD had no widget method.

| Old | New |
|---|---|
| `AddTraceMarksToTraceUnits` | `AddTraceMarksToTraceUnitsBlam` |
| `CheckDefaulHierarchy` | `CheckDefaulHierarchyBlam` |
| `CheckSignalsFromArtifacts` | `CheckSignalsFromArtifactsBlam` |
| `CreateNewBoeingUserAms` | `CreateNewBoeingUserAmsBlam` |
| `CreateNewUser` | `CreateNewUserBlam` |
| `CreateTasksFromSimpleList` | `CreateTasksFromSimpleListBlam` |
| `DataRightsImporter` | `DataRightsImporterBlam` |
| `DatabaseHealth` | `DatabaseHealthBlam` |
| `DeleteInvalidAttributeTypesFromBranch` | `DeleteInvalidAttributeTypesFromBranchBlam` |
| `DeleteUnneededUnspecifiedAttributes` | `DeleteUnneededUnspecifiedAttributesBlam` |
| `ExportMdrChangeReports` | `ExportMdrChangeReportsBlam` |
| `FindMissingOrUnspecifiedAttributes` | `FindMissingOrUnspecifiedAttributesBlam` |
| `FixDuplicateEnumeratedAttributes` | `FixDuplicateEnumeratedAttributesBlam` |
| `ImportGeneralDocuments` | `ImportGeneralDocumentsBlam` |
| `ImportTasksFromSimpleList` | `ImportTasksFromSimpleListBlam` |
| `ImportTasksFromSpreadsheet` | `ImportTasksFromSpreadsheetBlam` |
| `PublishSoftwareDesignDescriptionTraceability` | `PublishSoftwareDesignDescriptionTraceabilityBlam` |
| `PublishStdStpTraceability` | `PublishStdStpTraceabilityBlam` |
| `PublishSubsystemToDesignTraceability` | `PublishSubsystemToDesignTraceabilityBlam` |
| `PublishSystemLevelSSDD` | `PublishSystemLevelSSDDBlam` |
| `PublishWithSpecifiedTemplate` | `PublishWithSpecifiedTemplateBlam` |
| `PurgeArtifactType` | `PurgeArtifactTypeBlam` |
| `ReAssignAtsObjectsToUser` | `ReAssignAtsObjectsToUserBlam` |
| `RemoveObjectIdsFromTraceUnits` | `RemoveObjectIdsFromTraceUnitsBlam` |
| `RemoveTraceMarksFromTraceUnits` | `RemoveTraceMarksFromTraceUnitsBlam` |
| `RenameFiles` | `RenameFilesBlam` |
| `RequirementsTestReport` | `RequirementsTestReportBlam` |
| `SearchItemsRelatedToReleases` | `SearchItemsRelatedToReleasesBlam` |
| `SledFaultHierarchyUpdater` | `SledFaultHierarchyUpdaterBlam` |
| `SrsEnumGeneration` | `SrsEnumGenerationBlam` |
| `SrsJUnitGeneration` | `SrsJUnitGenerationBlam` |
| `SrsToPidsReport` | `SrsToPidsReportBlam` |
| `SsddToPidsAndSrsReport` | `SsddToPidsAndSrsReportBlam` |
| `SubsystemFullTraceReport` | `SubsystemFullTraceReportBlam` |
| `SubsystemRequirementVerificationLevel` | `SubsystemRequirementVerificationLevelBlam` |
| `SubsystemToLowLevelReqTraceReport` | `SubsystemToLowLevelReqTraceReportBlam` |
| `SwitchParentBranch` | `SwitchParentBranchBlam` |
| `SystemSafetyReport` | `SystemSafetyReportBlam` |
| `SystemSubsystemReport` | `SystemSubsystemReportBlam` |
| `TestPlanComplianceReport` | `TestPlanComplianceReportBlam` |
| `ValidateArtifactsToDelete` | `ValidateArtifactsToDeleteBlam` |
| `WcafeCodeGeneration` | `WcafeCodeGenerationBlam` |
| `WcafeEnumTrimFixes` | `WcafeEnumTrimFixesBlam` |
| `WcafeImporter` | `WcafeImporterBlam` |
| `WcafeNiceNamerAndUpdater` | `WcafeNiceNamerAndUpdaterBlam` |
| `WpSpImport` | `WpSpImportBlam` |
| `XWidgetEnableDisableBlamExample` | `XWidgetEnableDisableBlamExampleBlam` |
| `XWidgetPopulateExample` | `XWidgetPopulateExampleBlam` |

### C. Unchanged names (already `…Blam` in both trees, 62)

Names were already standardized; many still changed internally (widget method converted to `getXWidgetItems`, fields moved to `XWidgetData`).

`AbstractBlam`, `AdvancedKeywordSearchBlam`, `AttributeCheckBlam`, `ChangeArtifactTypeBlam`, `CompareDoorsToOseeBlam`, `ConvertWordArtifactToHtmlBlam`, `ConvertWorkflowStatesBlam`, `CopyAtsConfigurationBlam`, `CountArtifactsOfTypeBlam`, `CreateActionFromTaskBlam`, `CreateArtifactWithIdBlam`, `CreateAtsBaselineBranchBlam`, `CreateNewActionBlam`, `CreateNewAmsChangeRequestBlam`, `CreateNewAmsProblemReportBlam`, `CreateNewDemoChangeRequestBlam`, `CreateNewDemoProblemReportBlam`, `CreateNewPacrBlam`, `CreatePerfTableCodeBlam`, `CreateSiblingWorkflowBlam`, `CreateWorkingBranchFromTxBlam`, `DevProgressMetricsBlam`, `DuplicateParentBlam`, `DuplicateWorkflowBlam`, `EmailActionsBlam`, `EmailGroupsBlam`, `ExampleOutputBlam`, `ExportChangeReportsBlam`, `FindErroneousEmbeddedLinksBlam`, `FixEmbeddedLinksBlam`, `FtrImportMarkdownRequirementsBlam`, `ImportActionsViaSpreadsheetBlam`, `ImportAgileActionsViaSpreadsheetBlam`, `ProgramClosureBlam`, `ProgramClosureMultipleBlam`, `ModifyActionableItemsBlam`, `MoveTeamWorkflowsBlam`, `MoveTeamWorkflowsBlam`, `PortPairsBlam`, `PublishingAttributeContentBlam`, `PublishingClearCachesBlam`, `PublishingTemplateBlam`, `PurgeAttributesBlam`, `PurgeUserBlam`, `RealignActionableItemsBlam`, `RelationOrderRepairBlam`, `ReportAttributeTypesUsageBlam`, `ReqTraceBlam`, `SetWorkbenchOverrideIconBlam`, `SlocProductivityBlam`, `SoftwareReqVolatilityMetricsBlam`, `SrsPidsTableBlam`, `SrsSubDdCsidTablesBlam`, `TraceReportBlam`, `WcafeReportBlam`, `WorkflowPortingBlam`.

### New BLAMs (no old counterpart, 4)

| New | Abstract? | Note |
|---|---|---|
| `AbstractAtsBlam` | Yes | New abstract base |
| `AbstractConfigureNewAtsProgramBlam` | Yes | Abstract-base extraction; replaces old `ConfigureNewAtsProgramBlam` (see §E) |

### D. Deleted BLAMs (no new counterpart, 14)

| Old | Note |
|---|---|
| `ConfigureNewAtsProgramBlam` | Was abstract; NEW `AbstractConfigureNewAtsProgramBlam` in same package — likely refactored, not truly removed. See §E. |
| `DetailedTestStatusBlam` | Confirmed deleted. |
| `ExperimentalBlam` | Confirmed deleted. |
| `ExportArtifacts` | OLD name has no suffix; no NEW match. Confirmed deleted. |
| `FindArtifactsWithMinAttributeConstraintProblems` | Confirmed deleted. |
| `FindDuplicateArtifactNames` | Confirmed deleted. |
| `OseeBuildMemoBlam` | Confirmed deleted. |
| `PopulateUserGroupBlam` | Confirmed deleted. |
| `RequirementReorderOperation` | OLD name has no suffix; no NEW match. Confirmed deleted. |
| `WcafeMassUpdate` | Confirmed deleted. |
| `ChangeArtifactName` | Cited as suffix-standardized, but no `ChangeArtifactNameBlam` in NEW. See §E. |
| `XWidgetsExampleBlam` | Already `…Blam`; no NEW counterpart. See §E. |

## Risk & Blast Radius

This is one of the highest-blast-radius changes in the product: **every** Workflow Editor, Artifact Editor, BLAM, search dialog, and work definition renders through these widgets. A change to a base class (`XXAbstractBaseXWidget`, `XWidget`, `XWidgetSwtRenderer`, `XWidgetWorkDefRenderer`) touches essentially the entire UI.

- **What could break**: widget rendering/values in any WFE state, ArtEd attribute, BLAM, or dialog; work-definition layouts; per-widget options lost in the merge; auto-persist writing (or not writing) at the wrong time.
- **How it was mitigated**: one common base + single value model reduces surface; graceful error widgets keep a single failure from crashing an editor; durable code-review/organization tooling in the test suite keeps code conformant; the widget report validates every registered widget.
- **What was tested**: durable code-review checks (`CodeReviewXWidgets`/`CodeReviewBlams`/`CodeReviewOsgiXml`/`CodeReviewGeneral`) run in the suite; the runtime widget report validates every registered widget (one `VAL_` option, class==WidgetId); `AbstractBlamTest` + `TestAllRegisteredBlamsIntegrationTest` cover every BLAM (0 missing tests); `TestingOpenWorkflowSamples` opens every WorkDef in WFE + ArtEd; `WorkDefTeamDemoXWidgetTest` is a full widget gallery. Gaps: no dedicated ArtEd/dialog/per-WorkDef-render JUnit tests (manual QA); ~24 BLAMs still short-circuit their operation under `isDemoDbBlamTest()` pending real operation tests.
- **Rollback story**: the migration is the single commit `feature[TW29349]` (HEAD in ggg). `[verify — confirm no follow-on commits before quoting as a clean single-commit revert]`
- **Known deferred items**: see each area's Things to Do and the rename reference "Needs Resolution". Notable: the WFE `XWidgetWorkDefRenderer` field-by-field merge (drop risk).

## Reviewer Guide

How to review a change this large:

- **Start here**, then read `ide-xxwidget-architecture` (how it works) and `ide-xxwidget-standards` (the rules).
- **Suggested review order**: General XWidget (the base + `XWidgetData` + tokens) → Work Definition (how widgets are declared) → BLAM (builders) → ArtEd/WFE (rendering).
- **Focus areas**: the two merge paths (`XWidgetWorkDefRenderer` WFE vs `XWidgetSwtRenderer` ArtEd) must stay in sync; the lifecycle contract (load in `initializeSelected`, paint in `refresh`); exactly one `VAL_` option per widget; auto-persist correctness.
- **Spot-check at runtime**: open a workflow per work definition in both WFE and ArtEd, open representative BLAMs and dialogs, and confirm values display/edit/persist/reload.
- **Per-area "Key files touched"** (read these first):
  - General: `XXAbstractBaseXWidget`, `XOption`, `WidgetId`, `XWidgetService`, `XWidgetSwtRenderer`, `AttributeXWidgetProviderCore`.
  - BLAM: `AbstractBlam`, `AbstractBlamTest`, `BlamService`, a representative migrated BLAM (`CountArtifactsOfTypeBlam`), `XXBranchAndViewSelWidget`.
  - Work Definition: `WorkDefBuilder`, `AbstractDemoWorkDef`, `AttrWidgetDef`/`WidgetDef`, `WorkDefTeamDemoReq` (before/after).
  - Artifact Editor: `ArtifactEditor`, `ArtAttrTab`, `AttributeFormPart`, `ArtEdTabHeader`.
  - WFE: `WfeHeaderComposite`, `XWidgetWorkDefRenderer`, `WfeWorkflowSection`, `WfeOutlinePage`, `WfeDebugFillAction`.

## Cross-Cutting: How the Migration Was Performed

The migration was largely mechanical, done with one-time conversion passes plus durable code-review/organization tooling that is kept in the suite.

**One-time conversion passes** (throwaway migration scripts — run once and then deleted, since they aren't needed after migration): widget class renames, `getXWidgetsXml()` → builder (`getXWidgetItems()`), string widget names → `WidgetId` tokens, `WidgetDef` → `AttrWidgetDef`, AttributeType references → static imports, and BLAM name/gating cleanup.

**Run, incorporate data, then remove** — these tools exist only to GENERATE data that belongs in this document. Once their output has been captured here (the rename reference and the Metrics sections), the tools should be deleted:
- `ReportXWidgetMapping` — produces the old→new widget name mapping (source for the master rename reference above).
- `ReportXWidgetChanges` / `ReportBlamChanges` / `ReportWorkDefChange` — scan `ws_ggg` (new) + `ws_try` (old) and
  produce the review packet in `~/CodeXWidgetReview/`: per-area Rename, Diff, and Testing reports plus
  `*_classes_<repo>.json` and portable highlighted source copies.
- `ReportXWidgetOverview` — produces `XWidget_Overview.html`, the single entry page that links the nine per-area
  reports (XWidget / BLAM / WorkDef × Rename / Diff / Testing).

**Durable tooling** (in `org.eclipse.osee.ats.ide.integration.tests...coderev`; open each class's Javadoc/source for
details). Two families — see `ide-xxwidget-architecture.md` "Code-Review & Cleanup Tooling" for the full map:
- **CodeReview** (read-only, base `AbstractCodeReview`, runs in the test suite): `CodeReviewXWidgets`,
  `CodeReviewBlams`, `CodeReviewOsgiXml`, `CodeReviewGeneral`, `CodeReviewAll`.
- **CodeClean** (mutating, base `AbstractCodeClean`, standalone-only — never in a JUnit suite):
  `CodeCleanOrganizeXWidgets`, `CodeCleanOrganizeBlams`, `CodeCleanFormatAndOrganize`,
  `CodeCleanRevertNonFunctional`, `CodeCleanAll`. Every clean op defaults `changeFiles=false` (report-only).
- Both families extend a shared base `AbstractQual` (result, `plugins`-anchored workspace-root resolution,
  `plugins`-scoped discovery, HTML reporting). Discovery anchors on the `plugins` folder (workspace root = two dirs
  above `plugins`) with no hard-coded path.
- Reports write to `~/CodeReviewOutput/` and `~/CodeCleanOutput/` via `CodeQualityUtil` (folder auto-created with a
  README; safe to delete).

### Running the code-review tooling (folded in from the retired `coderev/CodeReviewReadMe.md`)

- **Run one review standalone:** open the review class (e.g. `CodeReviewXWidgets.java`) → Right-click → Debug As →
  Java Application. The HTML report opens in the browser. `CodeReviewAll` runs all four with a combined report.
- **Run as part of CI:** `DemoDbCodeReviewTestSuite` (in `AtsIdeIntegrationTestSuite`) runs all reviews during the
  integration tests. On errors it opens the report (local runs) and **fails the build**; otherwise it passes
  silently. Mode is detected via `OseeProperties.isInTest()` (set true in the suite's `@BeforeClass`).
- **What each review checks:**
  - `CodeReviewXWidgets` (`X.*Widget.java`): `@Component(service = XWidget.class)`; `WidgetId ID` matches class name;
    default no-arg ctor; only one ctor calls `super()` (others chain `this()`); ATS widgets have `xxWid`;
    `setArtifact()` doesn't call `refresh()`/`setSelected()`; `initializeSelected()` uses `setSelected(value,false)`;
    no `validate()`/`getSelected()` override; no `System.err/out.print`; no TBD/TODO; copyright header; matching
    OSGI-INF XML.
  - `CodeReviewBlams` (`*Blam.java`): `@Component(service = AbstractBlam.class)`; builder (`getXWidgetItems()`) not
    XML; `BLAM_NAME` constant; default ctor; `getDescriptionUsage()` present; shared `static final String` labels.
  - `CodeReviewOsgiXml` (OSGI-INF `*.xml`): implementation class in the correct bundle; bind/unbind naming; bind/unbind
    methods exist.
  - `CodeReviewGeneral` (all Java): no stray prints; no TBD; hoisted `Pattern.compile()`; auth filter not disabled;
    no commented-out `@Suite.SuiteClasses` entries.
- **Suppress one intentional violation:** add a trailing `// CodeReview.IgnoreLine` comment to the offending line.
- **Add a new review:** create `CodeReviewMyThing extends AbstractCodeReview` (implement `getReviewName()`,
  `getFilePattern()`, `performReview(List)`, and a `main()`), then add `runReview(new CodeReviewMyThing())` to
  `CodeReviewAll`.
- **Discovery:** `AbstractQual` resolves the workspace root by anchoring on the `plugins` folder (two dirs above it),
  then scans each sibling repo's `plugins/`, skipping `.git`, `target`, `bin`, and (by default) `*.integration.tests*`.

### Running the review-packet generators (`coderev.Report*`)

- These are standalone `main()` apps in the `org.eclipse.osee.ats.ide.integration.tests` bundle (so they compile with
  that bundle and get Jackson from its classpath). Run each with Right-click → Run As → Java Application.
- They scan two workspace roots side by side: `C:/Tools/git_ggg` (new) and `C:/Tools/git_try` (old). The `git_try`
  checkout of `org.eclipse.osee` must be present or the "old" side is skipped with a warning.
- Order: run `ReportXWidgetChanges`, `ReportBlamChanges`, and `ReportWorkDefChange` (each writes its JSON, source
  copies, and three reports), then `ReportXWidgetOverview` for the entry page. All output lands in
  `~/CodeXWidgetReview/`; zip that folder to share the packet (links are relative).

## Cross-Cutting: Post-Merge Fixes / Lessons

The system worked before the re-architecture, so most post-merge bugs were "the new path dropped something that used to flow." Recurring themes:
- The WFE merge (`XWidgetWorkDefRenderer`) rebuilds widget data field-by-field and repeatedly dropped WidgetDef-carried fields (parameters, `relationTypeSide`, conditions) that the ArtEd path got for free via `clone()`. When adding a new WidgetDef-carried field, wire it into BOTH paths.
- Load-vs-paint contract violations (loading in `refresh()`, calling `setSelected` in `setArtifact`) caused blank or looping widgets; the fix was to inherit/respect the base lifecycle, not to patch symptoms.
- Options are a creation-time snapshot; runtime enable/disable must flow through the live control, not by re-reading a stale option.
