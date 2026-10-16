# Code Quality Tooling (CodeClean + CodeReview) — Status & Stage 3 Plan

# XWidget Migration — Progress & Working Notes

Working notes for the XWidget &rarr; XXWidget migration workstream and its supporting tooling in this package
(`org.eclipse.osee.ats.ide.integration.tests.coderev`). Covers the code-quality tooling (CodeReview/CodeClean), the
review packet (`coderev.packet.*`), the throwaway refactor scripts (`coderev.scripts`), and the WorkDef &rarr; OSGI
`@Component` conversion. Lets a new session resume without losing context. Open each class's Javadoc/source for
authoritative detail — this doc is a map, not a spec.

## Review packet — open notes / TODOs

Notes captured while iterating on the XWidget review packet (the `coderev.packet.*` tooling). These are reminders
for the author to resolve before publishing the packet; they are intentionally kept OUT of the generated review
documents.

- **Review the Scale numbers** in the Overview ("What changed & why" section of `ReportXWidgetOverview`): confirm the
  ~1,424 files changed / ~155,413 insertions / ~52,070 deletions and the 154&rarr;98 (41 deleted) consolidation
  against the actual git stats and the `ReportXWidgetChanges` output before publishing.
- **Chat acronyms steering file added** (`.kiro/steering/auto/acronyms.md`, always-loaded): SF = steering file(s),
  AT = after that (next step, not an interrupt), KC = keep concise, SP = store progress. Extend as new shorthand is
  agreed.

### CodeRefactor_UpdateWorkDefAndColors (new script) + AbstractQual onlyOpenSource

New throwaway refactor script: `coderev/scripts/CodeRefactor_UpdateWorkDefAndColors.java` (new `coderev.scripts`
package). Extends `AbstractCodeClean`, overrides `performClean()` for a per-line transform.
- **What it does:** in each WorkDef `build()` chain, removes a `.andColor(StateColor.X)` line when X is `BLACK`
  (stale placeholder) OR equals the `TeamState` default color for that state name (redundant), letting the color
  resolve from the default (`TeamState.resolveStateColor`: explicit -> TeamState-by-name -> BLACK). Keeps a non-BLACK
  color that differs from the TeamState default (intentional override). Tracks current state via regex
  `andState(<n>, "Name"`; `andColor` is always on its own line (verified - no same-line case), so whole-line removal
  is safe. `TeamState.values()` is called first to force class init so `TeamState.valueOf(name)` resolves.
- **Report-only** by default (`changeFiles = false`, inherited). Flip to true to write.

**Shared feature added to `AbstractQual`:** `protected boolean onlyOpenSource = true` + `OPEN_SOURCE_REPO =
"org.eclipse.osee"`. `findFiles()` skips sibling repos != org.eclipse.osee when true. `logOnlyOpenSource()` helper
logs the setting + meaning; called from `AbstractCodeClean.run()` right after the changeFiles note. `findFiles()` now
also logs each "Scanning repo plugins dir: ...". NOTE: this made the whole CodeReview/CodeClean family default to
open-source-only - confirm that's desired for those families (previously they scanned all repos).

**OPEN BUG (tomorrow):** with `onlyOpenSource = false`, discovery correctly finds 90+ WorkDefs across all repos, but
`processWorkDef` only processes org.eclipse.osee ones. Cause: the guard `original.contains("super(AtsWorkDefinitionTokens")`
- proprietary repos' WorkDefs call `super(...)` with a DIFFERENT tokens class, so they're found by the `WorkDef*.java`
name pattern but rejected by the exact `AtsWorkDefinitionTokens` string. Fixes to consider:
  1. Quick: relax guard to any `super(...WorkDefinitionTokens.` (or match extending the common WorkDef base).
  2. Big: register WorkDefs via OSGI @Component annotations like BLAMs/XXWidgets - removes filename/token-string
     filtering entirely, fits the "common structure" story, but large blast radius (discovery, loading, every WorkDef).
  Need to see a proprietary WorkDef constructor shape before choosing. User leaning toward option 2 but wary of blast
  radius.

### WorkDef -> OSGI @Component conversion (CodeRefactor_WorkDefsToOsgi) - IN PROGRESS

Goal: convert WorkDefs from the two-tier provider-enumerated-in-code mechanism to per-class OSGI DS
`@Component(service = IAtsWorkDefinitionBuilder.class)` registration, like BLAMs/XWidgets. One Java script in
`coderev.scripts` (CodeClean-style: extends AbstractCodeClean, changeFiles=false dry-run default, onlyOpenSource=true
default; toggle to hit all ws_ggg repos). DECIDED: Java (not Python - user must review/run, trusts Java diffs).

Current mechanism (verified via context-gatherers):
- WorkDef classes are plain (no annotations). Two providers enumerate them in code: `AtsWorkDefinitionProvider`
  (production ~13, added PROGRAMMATICALLY in `ensureLoaded()`, not even OSGI-wired) and `AtsWorkDefinitionProviderDemo`
  (OSGI-registered via `OSGI-INF/ats.work.definition.provider.demo.xml`, gated on `DemoUtil.isDemoDatabase()`).
- Collector `AtsWorkDefinitionProviderService` (`OSGI-INF/ats.work.definition.provider.service.xml`) binds providers
  via 0..n dynamic ref `addWorkDefinitionProvider`, caches static `idToWorkDef`, unique-id check in `handleProvider`.
- Public `AtsWorkDefinitionServiceImpl` delegates to the collector. Base: `AbstractWorkDef implements
  IAtsWorkDefinitionBuilder`, contract `WorkDefinition build()`. `AbstractDemoWorkDef extends AbstractWorkDef`.
- ats.core manifest already has `Service-Component: OSGI-INF/*.xml` (new per-class XML auto-discovered).

Target pattern (BLAM/XWidget, verified): `@Component(service = X.class, immediate = true)` per concrete class;
collector `@Component` with MULTIPLE/DYNAMIC/GREEDY `@Reference(bind=...)`; hand-written `OSGI-INF/<fqcn>.xml`
(SCR 1.1.0, immediate=true, provide + implementation); `Service-Component: OSGI-INF/*.xml`. OSGI-INF XML is
HAND-WRITTEN repo-wide (no bnd/maven/tycho DS annotation processing).

DECISIONS: (1b) detect WorkDef by `extends AbstractWorkDef` OR `extends AbstractDemoWorkDef` (skip the abstract bases
themselves) - robust across repos regardless of token class naming. (2) ONE script does everything. (3) add
`default boolean isApplicable(){return true;}` to IAtsWorkDefinitionBuilder; demo WorkDefs override to
`return DemoUtil.isDemoDatabase();`. (4) migrate ALL, delete all providers + old mechanism.

Script does:
- Per-WorkDef (every repo): add `@Component(service = IAtsWorkDefinitionBuilder.class, immediate=true)` above class +
  imports (Component, IAtsWorkDefinitionBuilder); generate `OSGI-INF/<fqcn>.xml` in owning bundle; for
  `extends AbstractDemoWorkDef` add `isApplicable()` override returning `DemoUtil.isDemoDatabase()`.
- One-time shared (only in ats.core/ats.api bundle - the fragile string-rewrite part): add isApplicable() to
  IAtsWorkDefinitionBuilder; rewrite AtsWorkDefinitionProviderService to @Component + @Reference collecting
  IAtsWorkDefinitionBuilder (build() on bind if isApplicable(), keep unique-id check, rework ensureLoaded/caching);
  delete AtsWorkDefinitionProvider, AtsWorkDefinitionProviderDemo, both provider XMLs, and IAtsWorkDefinitionProvider
  (after grep for refs); add CodeReviewWorkDefs (mirror CodeReviewBlams).

RISKS: collector load-timing shift (provider-list -> per-builder binds; ensureLoaded rework) is the one non-mechanical
runtime risk - test WorkDef loading in a running IDE after. Deleting IAtsWorkDefinitionProvider - grep refs first.
Steps 5-8 (shared rewrites) are fragile via string manipulation; make them re-runnable/detect-already-done.

STATUS as of this session: SCRIPT BUILT, DRY-RUN VALIDATED, and APPLIED to org.eclipse.osee with CLEAN COMPILE.
File: `coderev/scripts/CodeRefactor_WorkDefsToOsgi.java`. 31 WorkDefs annotated + 31 per-class + 1 collector OSGI-INF
descriptors written; 5 files deleted (2 providers, IAtsWorkDefinitionProvider, 2 provider XMLs); 3 shared files edited
(IAtsWorkDefinitionBuilder isApplicable(), collector rewrite, IAtsWorkDefinitionProviderService trim). Workspace
compiles clean. RUNTIME-VERIFIED in a dev IDE - WorkDefs load, demo gating works.

COLLECTOR CACHE BUG FOUND + FIXED: first collector rewrite had `getAll()/getWorkDefinition()` read a static cache
with NO repopulation, and `clearCaches()` just emptied it. Integration test failed server-side
(AtsTestUtilCore.reset -> reloadServerAndClientCaches -> createAction -> getWorkDefinition(WorkDefTeamDefault) == null).
Root principle agreed: WorkDef cache must NEVER depend on DS re-injecting (DS binds references ONCE at activation,
never again); WorkDefs are immutable code so nothing legitimately goes stale. FIX (applied to live file AND synced
into the script's COLLECTOR_NEW_SOURCE): retain bound builders in a CopyOnWriteArrayList; `ensureLoaded()` rebuilds
`idToWorkDef` lazily from the retained builders (re-eval isApplicable) + an `added` map for test-injected WorkDefs;
getAll/getWorkDefinition call ensureLoaded; clearCaches only empties the built map (survives because rebuild source is
the retained builders, not a DS re-bind). Added unbind=removeWorkDefinitionBuilder to the @Reference + the collector
OSGI-INF descriptor. After this fix the integration suite PASSED - but we do NOT fully understand why it flipped to
passing (bind-timing? packaging?), so treat as not-fully-root-caused.

TOMORROW: revert all WorkDef->OSGI changes (git), re-run CodeRefactor_WorkDefsToOsgi (now carries the fixed collector),
re-run the integration suite to confirm the failure is gone with the fixed script from a clean slate. If it fails
again, root-cause the server bind/packaging path rather than the cache. THEN: build CodeReviewWorkDefs; run script on
other ws_ggg repos (onlyOpenSource=false).

### SESSION 2 (resumed): COLLECTOR DESCRIPTOR BUG FOUND + FIXED
After reverting and re-running the script, the SERVER failed to start the component:
`Component ...AtsWorkDefinitionProviderService validation failed: Implementation class name missing` (Felix SCR).
ROOT CAUSE: the collector OSGI-INF descriptor the script generates in `rewriteCollector()` was missing the required
`<implementation class="..."/>` element (it had only <reference> + <service>). SCR rejected the whole collector ->
no IAtsWorkDefinitionProviderService registered -> WorkDefs never served. (The 31 per-WorkDef descriptors were fine;
only the hand-built collector XML was wrong.) This likely also explains Session-1's intermittent "WorkDef not
computed" - the collector component wasn't registering at all.
FIX: added `<implementation class="org.eclipse.osee.ats.core.workdef.internal.AtsWorkDefinitionProviderService"/>` and
`unbind="removeWorkDefinitionBuilder"` to (a) the live descriptor file on disk and (b) the script's generator string.
CONFIRMED: server starts clean, component registers, WorkDefs load.

### SESSION 2 - ROOT CAUSE NAILED + FINAL FIX (clearCaches no-op)

Two separate bugs were masking each other across sessions:
1. Collector OSGI-INF descriptor was missing `<implementation class=.../>` -> SCR "Implementation class name missing"
   -> collector never registered. FIXED (added <implementation> + unbind to the live XML AND the script generator).
   Server then started clean.
2. After #1, integration test still failed server-side: `getWorkDefinition(WorkDefTeamDefault)=null` during
   AtsTestUtilCore.reset -> createAction. DIAGNOSTIC LOGGING (temporarily added to the collector, since removed)
   PROVED: all 31 WorkDefs (incl. WorkDefTeamDefault id=72301) DO bind at startup via DS. So registration is fine.
   The cache was being EMPTIED mid-test by a clearCaches() call (via internalClearCaches from DB-config/populate), and
   since DS fires the @Reference binds only ONCE at startup and NEVER re-injects, the eager-build-on-bind collector
   could not repopulate -> permanent empty cache -> null lookups.

FINAL FIX (agreed + applied): `clearCaches()` is now an INTENTIONAL NO-OP in AtsWorkDefinitionProviderService.
Rationale: WorkDefs are immutable code, bound once at startup; nothing about them changes at runtime; DS won't
re-inject, so clearing could only ever break things. Cache populates once as the 31 builders bind and lives for the
JVM. `addWorkDefinition(WorkDefinition)` still works for test-injected WorkDefs. This no-op is applied to BOTH the
live file (plugins/org.eclipse.osee.ats.core/.../AtsWorkDefinitionProviderService.java) AND the script's
COLLECTOR_NEW_SOURCE string, so a clean re-run reproduces it. All WORKDEF-DIAG logging has been removed.

NOTE on the earlier "retain builders + lazy ensureLoaded" idea from session 1: that was a more complex alternative;
we settled on the simpler no-op clearCaches since the cache never legitimately needs clearing. The live collector no
longer has builders-list/ensureLoaded machinery - it's the simple eager-bind cache + no-op clear.

NEXT: rebuild ats.core, restart server, RE-RUN THE INTEGRATION SUITE - must pass fully before accepting changes.
If green: remove diagnostics (done), then build CodeReviewWorkDefs, then run script on all ws_ggg repos
(onlyOpenSource=false, dry-run first).

*** MILESTONE: FULL ATS INTEGRATION SUITE PASSES on org.eclipse.osee with the WorkDef->OSGI migration applied. ***
Gate cleared. Migration proven end-to-end: 31 WorkDefs self-register via @Component, demo isApplicable() gating works,
clearCaches() no-op holds, collector descriptor valid. Now running the script + suite on the other ws_ggg repos.

PRE-EXISTING BUG found while testing (NOT caused by our refactor, but surfaced by it): `AttrWidgetDef.setAttributeType2`
was calling `overrideWidData.setAttributeType(...)` (attr 1) instead of `setAttributeType2(...)` - copy-paste bug. So
any widget with a 2nd attr type (e.g. SignDateWidgetDef in WorkDefTeamProductLine - "PL ARB Approved" with
ProductLineApprovedDate) left attr2 = SENTINEL and failed the build. Latent until the OSGI migration made
WorkDefTeamProductLine actually build/register. FIXED in
plugins/org.eclipse.osee.ats.api/.../workdef/model/AttrWidgetDef.java (now delegates to setAttributeType2). Rebuild
ats.api + ats.core and re-run. The orphaned "Team WF [TasksFromAction]" leftover was collateral from the aborted test,
not a cleanup regression (AtsTestUtilCore cleanup logic is intact/untouched).

CROSS-REPO NOTE: script annotated other ws_ggg repos' WorkDefs fine, but it does NOT auto-remove a proprietary repo's
OWN WorkDef provider class + its IAtsWorkDefinitionProvider OSGI-INF xml (script only knows the org.eclipse.osee
provider names). Those must be removed MANUALLY per repo (user did so this session). Could generalize later to detect
any `implements IAtsWorkDefinitionProvider`.

### SESSION 2: detection broadened for cross-repo + ready for all-repos run
- Detection roots changed from the hardcoded set {AbstractWorkDef, AbstractDemoWorkDef} to a regex
  `WORKDEF_ROOT = ^Abstract\w*WorkDef$` so proprietary bases (AbstractXyzWorkDef) in other ws_ggg repos are also
  treated as closure roots. Anchored `$` still excludes AbstractWorkDefItem (the earlier false-positive base).
- Script is idempotent (annotation/import/xml/shared-edit guards + deleteIfPresent no-op), so re-running on all repos
  is safe without reverting org.eclipse.osee.
- NEXT: flip onlyOpenSource=false, DRY-RUN all ws_ggg repos, review per-repo WorkDef counts + confirm NO shared-file
  edits fire outside org.eclipse.osee, then changeFiles=true. WATCH: other repos' bundles must have
  `Service-Component: OSGI-INF/*.xml` + build.properties including OSGI-INF/ for their new descriptors to register
  (script does not manage that). THEN: build CodeReviewWorkDefs.

Earlier status (superseded by the fix above): SCRIPT BUILT, DRY-RUN VALIDATED, APPLIED to org.eclipse.osee, CLEAN
COMPILE. 31 WorkDefs (18 demo + 13 production), 31 per-class + 1 collector OSGI-INF descriptors, 5 files deleted,
3 shared files edited.

Script design (final):
- Detection = TWO-PASS transitive closure (Option A). Pass 1 builds className->superclass across all .java (skipping
  `/coderev/`); `computeWorkDefClosure` finds every class whose extends-chain reaches AbstractWorkDef or
  AbstractDemoWorkDef (abstract roots excluded). This was needed because some WorkDefs extend other concrete WorkDefs
  (e.g. WorkDefReviewPeerToPeerDemo extends WorkDefReviewPeerToPeer; WorkDefTaskDemoForCrEstimating extends
  WorkDefTaskDefault) - a flat "extends AbstractWorkDef" check missed 4 of them.
- Demo gating: a WorkDef is demo (gets isApplicable() override returning DemoUtil.isDemoDatabase()) if its package
  contains `.demo` OR class name contains `Demo`. Catches all 18 demo WorkDefs incl. the 2 in non-demo packages.
- Bugs found+fixed during dry-run: (1) `extends AbstractWorkDef` substring-matched AbstractWorkDefItem (model base
  of WorkDefinition/StateDefinition/LayoutItem) -> fixed via closure + word boundary; (2) script annotated ITSELF ->
  skip any path containing `/coderev/`; (3) demo detection missed subclass-of-concrete-WorkDef cases -> closure fix.

DRY-RUN RESULT (correct, verified against the two old providers' enumerated lists):
- 31 WorkDefs = 18 demo (all show + isApplicable()) + 13 production. Exactly matches AtsWorkDefinitionProvider (13)
  + AtsWorkDefinitionProviderDemo (18). No false positives, no self-reference.
- 31 per-WorkDef OSGI-INF descriptors + 1 collector descriptor to write.
- Shared edits: isApplicable() added to IAtsWorkDefinitionBuilder; AtsWorkDefinitionProviderService rewritten to
  @Component + @Reference(IAtsWorkDefinitionBuilder) (COLLECTOR_NEW_SOURCE full replacement); addWorkDefinitionProvider
  removed from IAtsWorkDefinitionProviderService.
- Deletes: AtsWorkDefinitionProvider.java, AtsWorkDefinitionProviderDemo.java, IAtsWorkDefinitionProvider.java,
  ats.work.definition.provider.service.xml, ats.work.definition.provider.demo.xml.

VERIFIED FACTS:
- No-arg constructor concern RESOLVED: DS instantiates @Component via public no-arg ctor; every detected WorkDef
  already has one (the old providers called `new X()`, so it compiled -> ctor exists). WorkDefTaskDefault,
  WorkDefReviewPeerToPeer, WorkDefTaskDemoForCrEstimating all confirmed to have both no-arg + token-arg ctors.
- IAtsWorkDefinitionProvider only referenced by the 2 providers + collector + the service-interface method (all being
  removed) - safe to delete.
- ats.core MANIFEST already has `Service-Component: OSGI-INF/*.xml` -> new descriptors auto-register.
- ensureLoaded() on the collector is internal-only (not on the interface) - safe to remove in the rewrite.

NEXT STEPS (to resume): 
1. Set `changeFiles = true` in CodeRefactor_WorkDefsToOsgi and run on org.eclipse.osee (onlyOpenSource=true). Review
   git diff. HIGH-RISK to eyeball: the rewritten AtsWorkDefinitionProviderService (COLLECTOR_NEW_SOURCE) compiles +
   logic reads right.
2. Check ats.core build.properties includes OSGI-INF/ so the 32 new XMLs are packaged in a real build.
3. RUNTIME VERIFY in a dev IDE: WorkDefs actually load (open a workflow, check work def list); demo WorkDefs only
   appear on a demo DB (isApplicable gating works). This is THE risk - old async ensureLoaded model replaced by DS binds.
4. Build CodeReviewWorkDefs (step 8, NOT yet done): mirror CodeReviewBlams - check @Component(service =
   IAtsWorkDefinitionBuilder.class), extends a WorkDef base, matching OSGI-INF xml exists; register in CodeReviewAll.
5. Once working here, set onlyOpenSource = false to apply across all ws_ggg repos (other repos only get per-WorkDef
   usage edits - no shared framework files there).

HOW TO RESUME IN A NEW SESSION: say "resume WorkDef->OSGI work, read XWidgetsProgress.md". The script + this doc + git
are the full state; nothing else is needed.

### WorkDef review reports (ReportWorkDefChange) - updated this session
- Scan filter now requires `super(AtsWorkDefinitionTokens` in file content (plus `WorkDef*.java` name).
- Rename report simplified to a plain name list ("Work Definitions keep their names across this migration, nothing to
  see here.") with an "Unexpected changes (review)" red callout if any add/remove/rename is detected. Names NOT linked
  (decided against - confusing).
- Diff report now shows whole old-file vs new-file side by side (no per-method rows); `diffHowToUse` simplified;
  `readSource(path)` helper added. "Things to note" gained an auto-detection bullet (FILL_VERT/HORZ_LABEL now default
  and removed, XWidget name auto-detected, most entries just the AttributeType, andColor auto-defaulted).

### Overview (ReportXWidgetOverview) - heavy polish this session
Many sections reworked in the live edit loop (user runs it in Eclipse after each edit; AI cannot run it). Added: NEW
XXWidget Architecture / Major Features section; reference-source copy+link mechanism (`REFERENCE_SOURCES` +
`SUPPORT_TOPICS` -> `reference_src/` highlighted copies; `refLink`); Supporting Information section (WidgetId etc.);
CodeReview/CodeClean/Migration-generator lists now linked + ordered; `.note` boxes indented. Title-Case bold
lead-ins. Verified XXAbstractBaseXWidget lifecycle against source (removed "never returns null" claim).

### IN PROGRESS (not compiling) — Overview "Supporting Information" + auto-link refactor

Working file: `coderev/packet/ReportXWidgetOverview.java`. We are in a tight loop: user requests a change, I edit,
user runs `ReportXWidgetOverview` standalone in Eclipse (Run As → Java Application, which regenerates and opens
`~/CodeXWidgetReview/XWidget_Overview.html`). I cannot run it myself (Eclipse PDE classpath). Only edit the
`append*` methods; keep edits scoped.

**Goal of the current step:** curated-allowlist auto-linking of class-name mentions.
- Classes only (NOT methods/options). User-approved classes so far: `WidgetId`, `WidgetIdAts`, `XWidgetData`,
  `WidgetDef`, `AttrWidgetDef`. Ask the user when unsure whether a token is a linkable class.
- Rule: inline `<code>ClassName</code>` mentions link to that class's **Supporting Information** anchor
  (`#support-<lowercase>`); the Supporting Information entry links to the copied source. (XOption is the exception —
  it links straight to its copied source; leave as-is for now.)

**Already done in the file:**
- Reference-source copy mechanism: `REFERENCE_SOURCES` map (className → repo path), copied into
  `~/CodeXWidgetReview/reference_src/<Class>.java.html` by `copyReferenceSources(dir)`; `refLink(class, visible)`
  returns an anchor (or plain `<code>` if copy missing). Helpers `writeHighlightedCopy` / `escapeHtml` /
  `highlighterCss` / `highlighterScript` are present. `REFERENCE_SOURCES` currently has XOption,
  XXAbstractBaseXWidget, WidgetId, WidgetIdAts.
- XOption inline mentions auto-link to source (plural "XOptions" handled before singular).
- `SUPPORT_TOPICS` map added (className → {repoPath, description}) for WidgetId, XWidgetData, WidgetDef,
  AttrWidgetDef. `appendSupportingInformation(h)` now renders from `SUPPORT_TOPICS` (h3 per topic + description +
  `Source:` refLink; WidgetId also appends the WidgetIdAts refLink). `supportAnchor(class)` = `support-<lowercase>`.
- Supporting Information section wired into `run()` (before `</body>`) + TOC entry `#support`.
- Inline `<code>WidgetId</code>` → `#support-widgetid` replace is in `run()`.

**STILL TO DO (file will NOT compile / work until done):**
1. `copyReferenceSources` only iterates `REFERENCE_SOURCES`, so the `SUPPORT_TOPICS`-only classes
   (`XWidgetData`, `WidgetDef`, `AttrWidgetDef`) are never copied → their `refLink` falls back to plain `<code>`
   (no broken link, but no source link either). FIX: also loop `SUPPORT_TOPICS` in `copyReferenceSources`
   (copy `value[0]` path, key = className), or merge the two maps. (This was the edit in flight when we paused.)
2. Generalize inline auto-linking: replace the single hardcoded `<code>WidgetId</code>` replace with a loop over
   `SUPPORT_TOPICS` keys, each doing
   `html.replace("<code>"+c+"</code>", "<a href='#"+supportAnchor(c)+"'><code>"+c+"</code></a>")`.
   ORDER MATTERS: replace longer/more-specific names before shorter ones that are substrings
   (e.g. `AttrWidgetDef` and `WidgetDef` both end in `WidgetDef`, but since each is wrapped in its own
   `<code>...</code>` token the exact-token replace is safe — still, do WidgetIdAts-style care if adding bare-text
   linking later). Keep WidgetIdAts NOT auto-linked inline (reachable via WidgetId's entry).
3. After edits, user runs it to verify: Supporting Information shows 4 topics each with a Source link; inline
   WidgetId/XWidgetData/WidgetDef/AttrWidgetDef mentions are links to their anchors; XOption still links to source.

**Verified source paths (under `C:/Tools/git_ggg/org.eclipse.osee`):**
- WidgetId: `plugins/org.eclipse.osee.framework.core/src/org/eclipse/osee/framework/core/widget/WidgetId.java`
- WidgetIdAts: `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/util/WidgetIdAts.java`
- XWidgetData: `plugins/org.eclipse.osee.framework.core/src/org/eclipse/osee/framework/core/widget/XWidgetData.java`
- WidgetDef: `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/workdef/model/WidgetDef.java`
- AttrWidgetDef: `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/workdef/model/AttrWidgetDef.java`
- XOption: `plugins/org.eclipse.osee.framework.core/src/org/eclipse/osee/framework/core/widget/XOption.java`
- XXAbstractBaseXWidget:
  `plugins/org.eclipse.osee.framework.ui.skynet/src/org/eclipse/osee/framework/ui/skynet/widgets/xx/XXAbstractBaseXWidget.java`

See `.kiro/steering/conditional/overview-edit-task.md` for the live-loop workflow for this task.

## Two families

Both families extend a shared base **`AbstractQual`** (result, `plugins`-anchored workspace-root resolution,
`plugins`-scoped file discovery, file read/normalize, HTML reporting via `CodeQualityUtil`). See "How discovery finds
its root" below.

**CodeReview — read-only, base `AbstractCodeReview` (extends `AbstractQual`).** Validates code against standards, never modifies files,
runs in the ATS integration test suite (via `runAndAssert()`), also runnable standalone via `main()`.
- `CodeReviewXWidgets` — `XX*Widget.java` standards.
- `CodeReviewBlams` — `*Blam.java` standards.
- `CodeReviewOsgiXml` — OSGI-INF component XML (bind/unbind, class resolves, correct bundle). Overrides discovery
  with its own `findOsgiFiles` (matches paths containing `OSGI-INF`, ending `.xml`).
- `CodeReviewGeneral` — cross-cutting over all `*.java`: no `System.out/err.print` (warn), no TBD markers (warn),
  `Pattern.compile()` should be a `static final` field not compiled per call (warn), `AuthenticationRequestFilter`
  unauthorized() not commented out (error), no commented-out `@Suite.SuiteClasses` entries (error).
- `CodeReviewAll` — runs all four, merges results. `main()` = standalone, `runAndAssert()` = JUnit entry.

**CodeClean — mutating, base `AbstractCodeClean` (extends `AbstractQual`).** Reorganize/format/revert source. MUTATES files, so NOT wired
into any JUnit suite; run via `main()` or `CodeCleanAll`.
- `CodeCleanOrganizeXWidgets` — reorganizes `XX*Widget.java` into standard section order.
- `CodeCleanOrganizeBlams` — reorganizes `*Blam.java` into standard BLAM section order.
- `CodeCleanFormatAndOrganize` — runs Eclipse JDT formatter (incl. import expand/collapse/sort) on files changed in
  the branch. Needs `ECLIPSEC_EXE` env var; reports+skips (warning) if not configured. Git-based discovery over
  sibling repos under the resolved workspace root (`getWorkspaceRoot()`, no hardcoded path).
- `CodeCleanRevertNonFunctional` — over ALL unmerged commits (base = merge-base with the branch's upstream, resolved
  at runtime via `@{u}`, NOT hardcoded; falls back to `HEAD~1`), reverts files whose only changes are non-functional
  (whitespace/blank-line/formatting). Import-only or import+formatting changes are NOT reverted — flagged
  "REVIEW (imports involved)" with a callout to run `CodeCleanFormatAndOrganize` (which owns imports). Report lists
  the commits reviewed at the top. Git-based discovery over sibling repos under the resolved workspace root (no
  hardcoded path).
- `CodeCleanAll` — runs all four in pipeline order (Format → OrganizeBlams → OrganizeXWidgets → Revert), merges.

## Conventions

- **`changeFiles` flag** (CodeClean): each op sets `changeFiles = false` in a constructor at the top of the class.
  Default always report-only; flip to `true` manually to write. Base default is `false`. Report states the mode at
  top; summary label is `FILES CHANGED` (true) vs `FILES THAT WOULD CHANGE` (false).
- **Report output** via `CodeQualityUtil` (renamed from `CodeQualityOutput` in Stage 3; now the home for any shared
  quality-run utility): CodeClean → `~/CodeCleanOutput/`, CodeReview → `~/CodeReviewOutput/`. Folder auto-created on
  first use with a `README.txt` saying what made it and that it's safe to delete.
- Member-based reorganizers are idempotent (strip section headers before re-emit); report compares rebuilt output
  to disk so both live and report-only runs show the true change set.

## Work completed this session

1. Moved+renamed the two migration scripts into this package: `RunPatch_14_FormatAndOrganize` →
   `CodeCleanFormatAndOrganize`, `RunPatch_15_RevertNonFunctional` → `CodeCleanRevertNonFunctional`.
2. Created `CodeCleanAll` (aggregator, standalone-only, no JUnit hook).
3. Stage 1: created `AbstractCodeClean`; rebased both Organize ops onto it (dedup: run loop, member parse/field
   heuristics, `Member`, discovery, HTML report). Blams + XWidgets keep only their categorize/rebuild + `getCleanName`
   + `getFilePattern` + `shouldProcess`.
4. Stage 2: converted the two git/System.out scripts to extend `AbstractCodeClean`, produce the same XResultData HTML
   report (dual-log to console + `rd`); simplified `CodeCleanAll` to a uniform `runClean(AbstractCodeClean)`.
5. `CodeCleanRevertNonFunctional`: branch-wide (merge-base with runtime-detected upstream); import changes split into
   their own REVIEW outcome (not reverted) with a callout to `CodeCleanFormatAndOrganize`; per-file reason logged
   inline; commits-reviewed listed at top; `describeDifferences` returns the reason string.
6. Report label reflects `changeFiles`; top-of-report notes the flag state.
7. Perf (behavior-preserving): compile `getFilePattern()` once (both bases); single disk read in
   `AbstractCodeClean.processFile`; literal `.replace()` instead of regex for EOL normalize; cached
   `getBaseSearchDir()`; cached `CodeReviewGeneral.getIgnoreDirs()`; hoisted `CodeReviewXWidgets` DOTALL regex +
   `AbstractCodeClean` header-strip regex to `static final Pattern`; dropped redundant re-normalize in
   `extractWidgetId`.
8. New `CodeReviewGeneral` check: `Pattern.compile()` not in a `static final` field → warning (respects
   `CodeReview.IgnoreLine`).
9. Created `CodeQualityOutput` and routed all 4 report writers to `~/CodeCleanOutput` / `~/CodeReviewOutput` with
   README.
10. Steering docs updated: `ide-xxwidget-architecture.md` (rewrote Code-Review & Cleanup Tooling section),
    `ide-xwidget-to-xxwidget-migration.md` (durable tooling list), `ats-coding-standards.md` (added tooling note).

All changes are uncommitted in the working tree. Everything above has been validated by the user's test runs except
the very latest doc edits.

## How discovery finds its root (after Stage 3)

- **Shared, in `AbstractQual`.** `getWorkspaceRoot()` anchors on the `plugins` folder: walk up from the running class
  to the nearest ancestor named `plugins`; the workspace root is the dir **two levels above `plugins`** (repo dir is
  one above, root is two). No directory-name matching (a clone may be named anything), no hard-coded path. Returns
  `null` + logs an error if no `plugins` ancestor is found (safe degrade → discovers nothing).
- `findFiles()` then, for each sibling repo under the root, descends into that repo's `plugins/` dir only, matching
  simple names against the compiled-once `getFilePattern()`.
- The `org.eclipse.osee`-name `replaceFirst` strip survives ONLY in `getRepoShortName()` (display labeling), guarded
  to error if it strips nothing.
- Canonical `ignoreDirs` (`AbstractQual.DEFAULT_IGNORE_DIRS`) = `.git, \target\, \bin, .integration.tests.`.
  `CodeReviewGeneral` removes `.integration.tests.` via `super.getIgnoreDirs()`. `CodeReviewXWidgets` / `CodeReviewBlams`
  keep their own fuller override lists (harmless leftovers under `plugins` scoping, kept to preserve behavior exactly).
  `CodeReviewOsgiXml` overrides `findFiles()` entirely (per-repo `plugins/` walk collecting `OSGI-INF/*.xml`).
- The two git-based CodeClean ops (Format/Revert) derive their scan root from `getWorkspaceRoot()` (no more hard-coded
  `WS_DIR`) and iterate sibling repos with a `.git` dir; they override `performClean()` and don't use the filesystem
  pattern walk.

## Stage 3 (COMPLETED)

Unified the two families' shared machinery and cleaned up discovery. All five steps done:

1. **Renamed `CodeQualityOutput` → `CodeQualityUtil`** (via `smart_relocate`), broadened its Javadoc to "shared
   quality-run utilities," updated the 4 call sites (`AbstractCodeReview`, `AbstractCodeClean`, `CodeReviewAll`,
   `CodeCleanAll`). Constants/methods kept their names.
2. **Created `AbstractQual`** (new shared base). Holds `rd` + `getResults()`, `breakAfterFile`, `basePath`,
   `readFile` (EOL-normalized), `shortPath`, `getClassName`, compiled-once `filePattern()`, abstract `getFilePattern()`,
   `getIgnoreDirs()` (canonical list + override hook), `getWorkspaceRoot()` (plugins-anchored, above), `getRepoShortName()`
   (guarded), `findFiles()` + `findFilesByPattern()` (plugins-scoped walk), `writeAndOpenReport()` (delegates to
   `CodeQualityUtil`), and abstract `getName()`/`getOutputDirName()`/`getToolLabel()`. Rebased both bases:
   `AbstractCodeReview extends AbstractQual` (keeps FileResult + sorted output + runAndAssert/JUnit) and
   `AbstractCodeClean extends AbstractQual` (keeps changeFiles/changedFiles + member pipeline + FILES CHANGED report
   label). `AbstractCodeClean.processFile` keeps its OWN raw `Lib.fileToString` read for the exact-change comparison
   (not the normalized `readFile`).
3. **Removed hard-coded `WS_DIR`** from `CodeCleanFormatAndOrganize` + `CodeCleanRevertNonFunctional`; both resolve the
   root via `getWorkspaceRoot()` (null-guarded) and pass it to `findGitRepos(File)`. Removed the now-redundant WS_DIR
   check from `validateConfig()`.
4. **Scoped discovery to `plugins/`** (built into `AbstractQual.findFiles()` in step 2). `CodeReviewOsgiXml.findFiles()`
   updated to the same per-repo `plugins/` walk (it used to call the removed `getBaseSearchDir()`).
5. **Updated docs**: `ide-xxwidget-architecture.md` (Code-Review & Cleanup Tooling section), `ide-xwidget-to-xxwidget-migration.md`,
   and this file.

Behavior-preserving: same files discovered (within the intended `plugins/` scope), same checks, same results — only
structure/root-resolution changed. Every clean op still defaults `changeFiles = false`.

**Verification note:** not yet run through a build/test cycle (Eclipse PDE project — the user validates via their own
Eclipse runs). Recommended: run `CodeReviewAll` (via `runAndAssert()`/`main`) and each CodeClean op report-only
(`changeFiles = false`) and confirm the discovered file set + reports match pre-Stage-3 runs.

### Deferred (optional, post-Stage-3) perf follow-ons
- **B** = split each file into `String[] lines` once and pass to checks (kills repeated `split`/`contains` in the
  CodeReview* validators).
- **D** = single shared tree walk in `CodeReviewAll` handing each review its pre-filtered sublist (must preserve
  per-review discovery differences: OsgiXml = OSGI-INF xml, General = include `.integration.tests.`).

### ReportXWidgetMapping coverage audit (this session) — COMPLETE

Reworked `coderev/packet/ReportXWidgetMapping.java` to the categorized `WidgetMapping(newName, Category, String...
oldNames)` structure, then ran a code-vs-mapping coverage audit. Authoritative "real registered XWidget" signal =
a `.java` with `@Component(service = XWidget.class` AND a top-level `public [final] class X{1,2}\w+`. Audit now
balances exactly: **111 registered widgets == 111 mapping newNames, 0 missing either direction, 0 stale.**

Fixes applied this pass:
- Typos corrected: `XXBranchListWidget`→`XXBranchSelWidget`, `XXListDropWidget`→`XXListArtifactDropWidget`,
  `XXAttachmentExampleWidget`→`XXAttachmentDemoWidget`, `XXTeamDefinitionWidget`→`XXTeamDefinitionsWidget`.
- Removed stale/non-existent newNames: `XXReviewInfoWidget`, `XXAtsArtifactRefWidget` (no such class). The old name
  `XArtifactReferencedAtsObjectAttributeWidget` was folded into `XXArtifactRefWidget` (Selection).
- `XTextInternalWidget` was wrongly listed as an OLD name of `XXTextWidget`; there's a real `XXTextInternalWidget`
  class, so it's now its own BASE entry and removed from XXTextWidget's oldNames.
- Added 27 previously-missing registered widgets as entries with blank oldNames (USER TO FILL): framework base/
  selection (`XXListRelationWidget`, `XXStringSTextHEditWidget`, `XXTextInternalWidget`, `XXStringsEnumArtWidget`,
  `XXChildSelWidget`, `XXArtEdAttrViewerWidget`, `XXComputedCharacteristicWidget`, `XXCopyIdHyperlinkWidget`,
  `XXButtonViaActionWidget`, `XBranchViewerWidget`, `XHistoryWidget`, `XMergeWidget`), branch
  (`XXBranchAndViewSelWidget`), user (`XXUserTypeWidget`), review (`XXDecisionRevWidget`), workflow/state
  (`XXCreateChangeReportTasksWidget`, `XXWorkDefReferenceWidget`), ATS misc / search / demo (`XXHoldStateWidget`,
  `XXStateNameWidget`, `XXStateTypeWidget`, `XDynamicAttrValuesWidget`, `XTaskEstDemoWidget`,
  `XTaskEstSiblingWorldDemoWidget`), plus restored `XUserRoleViewerWidget` (real single-X component).
- CONFIRMED single-`X` (not XX) widgets that legitimately kept their name: `XCommitManagerWfeWidget`,
  `XUserRoleViewerWidget`, `XBranchViewerWidget`, `XHistoryWidget`, `XMergeWidget`, `XDynamicAttrValuesWidget`,
  `XTaskEstDemoWidget`, `XTaskEstSiblingWorldDemoWidget`.
- KEPT OUT (per user: "only want things that are XWidgets"): workdef model classes (`*WidgetDefinition`,
  `HeaderDefinitionBuilder`) and search renames (`ReviewFormalTypeSearchWidget`, `AbstractXHyperlink*SearchWidget`).

NEXT: user fills blank oldNames on the ~27 new entries (and verifies categories), then rework the rename-report HTML
to render by Category/Band (Framework→ATS→Agile→Deleted, ≤2-entry categories collapse to band "Misc", row =
new name + "(was: old1, old2)", no count-label sections).

### Consolidation Overview rework + packet shared look-and-feel (this session) — DONE

Reworked the XWidget "rename" page into a categorized **Consolidation Overview** and unified the whole packet's
look-and-feel. User-driven (sample iterated in chat, then wired into generators; user runs each report in Eclipse — AI
cannot run them).

**New shared helper:** `coderev/packet/PacketStyle.java` (throwaway). Provides:
- `pageCss()` — shared page shell: band theme (framework=#1f6feb blue, ats=#8250df purple, agile=#1a7f37 green,
  deleted=#cf222e red), `.wrap` container, h1/h2, `.note`/callout boxes, `.summary`/`.stat` count cards, `.cat h3`
  band-tinted sub-category chips, mapping table (`td.newname` 14.5px, `td.was`).
- `highlighterCss()` / `highlighterScript()` — the self-contained offline Java highlighter (was duplicated in 4
  files).
- `escapeHtml()`.

**Consolidation Overview** (`ReportXWidgetChanges.generateChangeReport`, output still
`XWidgets_XWidgetReview_Rename.html` so the Overview link is unchanged): now renders DIRECTLY from
`ReportXWidgetMapping.getWidgetMappings()` — no JSON scan. Grouped Band→Category (Framework→ATS→Agile→Deleted),
categories with ≤2 entries collapse into a per-band "Misc" chip, each row = new name + "(was: old1, old2)" or "new
widget"/"kept name (complex widget)" for empty oldNames. Summary cards computed live from the mapping (currently
149 old → 111 new, 5 deleted). Helpers added: card/bandLabel/bandCss/appendBand/appendCategory/appendDeletedBand.
Removed dead sumLines/widgetLines/nvl. `resolveRenames` KEPT (still used by the Diff page).

**Overview page** (`ReportXWidgetOverview`): `appendAreaSection` now takes
`(…, renameFile, renameLabel, renameDesc, diffFile, testingFile, note)`. XWidget area label = **"Consolidation
Overview"** (desc "every new widget grouped by purpose…"); BLAM label = "Renames"; WorkDef passes null,null,null.
`appendStyle` recolored to the shared palette.

**A+B applied to all packet pages** (user approved A+B; band chips NOT forced onto non-taxonomy pages = phase C held):
- All four report classes (`ReportXWidgetChanges`, `ReportBlamChanges`, `ReportWorkDefChange`, `ReportXWidgetOverview`)
  now use `PacketStyle.highlighterCss/Script` and delegate their local `escapeHtml` to `PacketStyle.escapeHtml`; the
  duplicated local `highlighterCss`/`highlighterScript` definitions were DELETED from all four.
- Every page shell recolored from the old `#1565c0`/`#e3f2fd` theme to the shared palette (CSS vars). Diff pages keep
  their own table-layout/column-resizer CSS on top (via each file's `diffReportHeadExtras`), just recolored. Testing
  pages switched to `pageCss()` + `.wrap` with a green-accent `.box`. `writeHighlightedCopy` source pages recolored.
- Verified: no `#1565c0` left in packet; no local highlighter defs left; all highlighter calls PacketStyle-qualified.

NOT compiled here (Eclipse PDE classpath) — user to run each report in Eclipse to eyeball. Stale Scale counts
(154/98/41) still in Overview desc + Scale para + both testing boxes — left pending the SLOC recount (user: "recalc
slocs later").

NEXT (optional phase C): decide whether to push band-color accents further on Diff/Testing/Overview; and the SLOC
recount to replace 154/98/41 with the real 149/111/5 + refreshed insertion/deletion totals.

### Widget counts made dynamic (this session)

The stale hardcoded 154/98/41 XWidget consolidation counts are GONE. Added 3 accessors to `ReportXWidgetMapping`:
`getOldWidgetCount()` (distinct old names across all entries incl. deleted), `getNewWidgetCount()` (non-blank new
names, excl. deleted), `getDeletedWidgetCount()`. Every widget-count display now derives from these at generation
time, so editing the mapping auto-updates them:
- `ReportXWidgetChanges` Consolidation Overview summary cards + the XWidget Testing box sentence.
- `ReportXWidgetOverview` XWidget area description + the Scale paragraph (counts AND the old→new ratio, computed).

STILL hardcoded on purpose (NOT from the mapping): the Scale paragraph's git file/insertion/deletion totals
(~1,424 / ~155,413 / ~52,070) — pending the real recount; and the BLAM testing box `getXWidgetsXml 98 → 0` (count of
converted BLAM methods, not XWidgets — coincidentally also 98).

### XWidget Testing report: hierarchy view replaces old XOption report (this session)

`ReportXWidgetChanges.generateTestingReport()` referenced two now-DELETED classes
(`XWidgetReportOperations.openWidgetReport()` and `OpenXWidgetOptionReport`). Grep confirmed those classes no longer
exist in the repo — only the stale report text referenced them. Replaced both bullets with the real replacement:
`TestingXWidgetHierarchyView` (`plugins/org.eclipse.osee.ats.ide/.../config/version/TestingXWidgetHierarchyView.java`),
nav item "Testing - Open XWidget Hierarchy View" (TOP_ADMIN_TESTING). It opens a ResultsEditor over
`XWidgetService.getRegisteredWidgetClasses()` with Hierarchy + Flat tabs; columns = Widget/WidgetId, attribute-type
provider(s), value type, default XOptions, package, registered. Edited the generator (NOT the generated HTML in
~/CodeXWidgetReview); user re-runs ReportXWidgetChanges in Eclipse to regenerate XWidgets_XWidgetReview_Testing.html.

### WorkDef review pages now call out OSGi @Component registration (this session)

Gap found: XWidget (Diff "Things to note" + Overview arch section) and BLAM (Diff "Things to note") both state their
`@Component(service = X.class)` self-registration, but the WorkDef review pages only had it buried in detection-logic
code comments — nothing human-facing. Fixed in `ReportWorkDefChange`:
- Diff "Things to note": NEW first bullet — each WorkDef self-registers via
  `@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)` + no-arg ctor (abstract bases excepted),
  replacing the old in-code provider enumeration; demo WorkDefs gate via `isApplicable()`/`DemoUtil.isDemoDatabase()`.
- Testing box: added a sentence that each WorkDef self-registers via OSGi, replacing the old provider enumeration.
- Also fixed a stale ref here: the WorkDef Testing "Open XWidget XOption Report" (`OpenXWidgetOptionReport`, deleted
  class) bullet now points at `TestingXWidgetHierarchyView` (same replacement used on the XWidget Testing page).
User re-runs ReportWorkDefChange in Eclipse to regenerate XWidgets_WorkDefReview_Diff.html + _Testing.html.

### clearCaches() no-op REVERSED — retained-builders rebuild (this session)

DECISION REVERSAL: the earlier "clearCaches() is an intentional no-op" decision was WRONG for one path and caused a
CI build failure. Root cause: `isApplicable()` is NOT constant — demo builders gate on `DemoUtil.isDemoDatabase()`
(system prop `osee.db == "orgdemo"`), which `AtsDbConfigDemoOp.run()` sets AFTER the @Component builders have already
bound. The no-op assumed the gate was decided once at startup.

- Local/dev server launches with `-Dosee.db=orgdemo`, so demo builders bind applicable → no-op was invisible there.
- CI/CD pipeline does NOT launch with that flag. Demo builders bound with isApplicable()==false, the no-op
  clearCaches() couldn't recover them, and `demoDbPopulate()` → `Pdd20CreateCommittedAction` failed with
  "Demo Work Definitions not Loaded" (WorkDefTeamDemoReq missing from getAllWorkDefinitions()).

FIX (applied to `AtsWorkDefinitionProviderService`): retain the bound builders (CopyOnWriteArrayList) instead of only
their built output; `clearCaches()` now sets loaded=false; `ensureLoaded()` rebuilds the applicable set from the
retained builders, RE-EVALUATING isApplicable() each time, plus a separate `added` map for programmatic/test
WorkDefs. getAll()/getWorkDefinition() call ensureLoaded(). This restores the old "set osee.db + clear cache → demo
defs appear" behavior while staying OSGi-correct. Also: added the `removeWorkDefinitionBuilder` unbind the OSGI-INF
descriptor already referenced but the Java lacked (latent bug), and routed the manual `addWorkDefinition()` through the
same guarded rebuild so it keeps the unique-id / build-error fail-fast checks (was review nit #8).

`AtsDbConfigDemoOp` needs NO change — its setProperty+internalClearCaches is correct again, comment accurate again.
GATE: rebuild org.eclipse.osee.ats.core, re-run demo populate / integration suite; must clear the CI failure.

### CI failure: DemoDbCodeReviewTest.testCodeReviewOsgiXml + headless visibility fix (this session)

TWO issues from a build-system run:

1. **Headless visibility (FIXED).** `DemoDbCodeReviewTest` only asserted "See HTML report in browser" — useless on CI
   (no browser, report HTML not surfaced). Changed all four `testCodeReview*` methods to a shared `runAndAssert(review)`
   that, on failure, builds the JUnit failure message from `review.getResults().getErrorStrings()` (falls back to
   `getResults().toString()`), so the actual error lines print in the CI test output. Uses `getClass().getSimpleName()`
   (not the protected `getReviewName()`). Added `import java.util.List`.

2. **Actual failure: `CodeReviewOsgiXml found errors` (OPEN — needs the error line from a rebuild).** `CodeReviewOsgiXml`
   enforces 3 rules per OSGI-INF descriptor: (a) impl class exists in the correct bundle, (b) bind/unbind naming, (c)
   bind/unbind methods exist (non-static) in the impl class. I scanned ALL org.eclipse.osee OSGI-INF descriptors against
   all 3 rules (missing impl class / missing bind-unbind method / wrong bundle namespace) — ZERO violations in the
   open-source repo. KEY: `CodeReviewOsgiXml.findFiles()` does NOT honor `onlyOpenSource`; it always walks EVERY sibling
   repo under C:/Tools/git_ggg/plugins. So the failing descriptor is almost certainly in a PROPRIETARY sibling repo
   (not visible to the AI). PRIME SUSPECT: a proprietary repo's own WorkDef provider class + its
   `IAtsWorkDefinitionProvider` OSGI-INF xml — recall the cross-repo manual step: the WorkDef→OSGi script only knew the
   org.eclipse.osee provider names, so each proprietary repo must manually delete its own provider class + descriptor.
   If the class was deleted but the descriptor left behind (or vice versa), CodeReviewOsgiXml flags "File not found" or
   "No bind method". NEXT: rebuild ats.ide.integration.tests, re-run the test, read the now-embedded error line (names
   the descriptor/class/rule), fix that proprietary descriptor.

### OseeData relocation — one exception reverted (Tycho classpath)

`TestAllRegisteredBlamsIntegrationTest` (in `org.eclipse.osee.client.test.framework`) was REVERTED back to writing
`TestAllRegisteredBlams.html` to `user.home` (via `Lib.writeStringToFile`). Reason: switching it to
`OseeData.writeToFile` dragged in a transitive `org.eclipse.core.resources` / `IFile` dependency (via `RenderingUtil`)
that this low-level bundle doesn't declare; Eclipse PDE resolved it loosely but the Tycho/Maven headless build failed
strict compile. Adding `org.eclipse.core.resources` to the manifest Import-Package did not clear it, so not worth the
churn pre-merge. The other relocations stand (they live in bundles that already import resources):
`OpenWorkDefExamples.html`, `atsCoverage.html`, `TransitionAssigneesOperationTest.html`, and the BLAM nav report all
go to OseeData. So "nothing writes to user.home" is NOT fully true — this one still does, by design, for now.
Fold into the future stand-alone-vs-runtime output cleanup task.

## Session update — Widget value flicker on save (post-migration bug fix)

**Symptom:** After the XX migration, editing one attribute (e.g. Priority) and saving made every XX hyperlink/value
widget on the page flicker — value blanks then reappears — in BOTH the Workflow Editor and Artifact Editor. The
Description box (legacy/own-content multi-line text) did NOT flicker.

**Root cause:** The save→refresh fan-out is old and unchanged (WFE: `WfeArtifactEventManager` →
`WorkflowEditor.refresh()` → tab/section loops → `widget.refresh()`; Artifact Editor: `ArtifactEditorEventManager`
→ `refreshDirtyArtifact()`). What changed is the new `XXAbstractBaseXWidget.refresh()`: on each value change it ran
`valueWidget.update()` + `valueWidget.getParent().update()` + `getParent().getParent().layout()`. Forced synchronous
paints (`update()`) per widget defeat SWT paint coalescing, and the blanket grandparent `layout()` is an O(widgets)
relayout — so one save repainted the whole page mid-cascade = flicker. The `layout()` alone can't blank a value; the
forced per-widget synchronous paints during the editor-wide loop are what made the reflow visible as blank→fill.

**Fix (SWT best practice — "only one, targeted"):** In `XXAbstractBaseXWidget.refresh()` (framework.ui.skynet,
widgets/xx), both the `valueWidget` and `valueSText` blocks now only `setText(...)`. Added private
`relayoutForValueChange()`: returns early for fixed-size cells (no fill/grab → `setText` already schedules the only
needed repaint); for fill/multi-line values it relayouts via the TARGETED `comp.getParent().layout(new
Control[]{comp})` (flushes cache only along this widget's ancestor chain), never a blanket parent relayout, and never
a forced `update()`. Heavy javadoc added to `refresh()` and the helper explaining WHY, so a future "sizing fix"
doesn't re-add `update()`/blanket `layout()` and regress the flicker.

**Decision:** per-widget `refresh()` = content only; genuine grow handled narrowly in the widget that actually
changed (guarded by its fill options), not by the container. If a container ever needs a broad reflow, do ONE
`layout(true)` after its loop — not per-widget. SWT booleans: `layout(true)` flushes size cache; `layout(Control[])`
is the surgical ancestor-chain form; `update()` forces immediate synchronous paint (avoid in refresh loops).

**Still open / not changed here:**
- Artifact Editor `refreshDirtyArtifact()` → `reloadEditor()` disposes+recreates ALL pages/widgets on save (full
  teardown). That is a separate, heavier flicker/perf source. Not touched in this fix — revisit if Artifact Editor
  still blanks after this change (would need: don't full-reload on the editor's own in-place attribute save).
- Verify in a running IDE: edit Priority in both editors, confirm only the edited widget updates and no page-wide
  flicker. Confirm fill/multi-line values (e.g. a FILL_HORIZONTAL value) still resize correctly on change.

## Session update — SWT refresh flicker RESOLVED; paused before debug cleanup (2026-10-13)

- **Verified root cause:** `WfeWorkFlowTab.refreshExpandStates()` called
  `WfeWorkflowSection.expand()` / `Section.setExpanded(true/false)` on every artifact refresh even when the SWT
  expanded state was unchanged. Same-value `Section.setExpanded(...)` invalidated the shared Eclipse Forms hierarchy
  on Windows. Runtime paint tracing showed a full `WM_PAINT` burst while every unrelated widget retained the same
  non-empty native text and never called `setText`.
- **Verified fix:** `setSectionExpanded(...)` now returns immediately when `Section.isExpanded() == requestedState`.
  It mutates expansion only on a real state transition. User rebuilt, changed Priority, and confirmed: **flicker gone**.
- **Other retained improvements:** removed per-widget forced `update()`/blanket layout in
  `XXAbstractBaseXWidget.refresh()` (faster redraw); body layout only after a real expansion change; guarded
  `WfeWorkflowSection` title/color setters and cached/disposed custom Color; fixed `setLabelFgColor()` to target the
  label/hyperlink rather than the value control.
- **Debug cleanup intentionally paused:** `XXAbstractBaseXWidget` still contains extensive gated lifecycle stack and
  SWT control-event tracing. Cleanup had just started; only constructor and `createControls ENTER` traces were removed.
  User wants to discuss how to preserve useful debug capabilities before more is deleted. Do not continue cleanup on
  resume until that decision. `XWidget.debugXWidgets` remains true and should default off before merge.
- **Pending:** decide lightweight vs deep opt-in debug modes; remove/retain instrumentation accordingly; update SWT
  docs/Javadocs to avoid overstating intermediate hypotheses; run Java/build validation; review final diff. Detailed
  evidence and handoff are in `docs/ai/ide/ide_swt_refresh_problem.md`.

## Session update — VPN performance instrumentation (2026-10-13)

Added independently gated diagnostic modes in `XWidget`. Current benchmark configuration: master=true,
performance=true, lifecycle=false, stacks=false, SWT events=false. Deep lifecycle/paint diagnostics remain available
but are disabled during timing so console I/O and stack construction do not distort results.

Performance lines now report:
- global sequence + epoch/monotonic elapsed time;
- `SkynetTransaction.execute()` duration (attribute/relation) and total `handleSelectedPersist()` duration;
- each XX widget `refresh()` and `validate()` duration;
- Workflow Editor refresh phases: header, sections, expandStates, setEditorWidgets, and total.

Capture/interpretation instructions are in `docs/ai/ide/ide_swt_refresh_problem.md` under "VPN Performance Trace Mode".
Pending: user runs repeated Priority changes through VPN and provides `XXWIDGET PERF` / `WFE REFRESH PERF` lines for
analysis. Restore the master diagnostic switch/defaults before merge after deciding which opt-in debug capability to
retain.

### VPN Priority benchmark result

Five warmed/cold Priority saves showed `transaction.execute` 1652–2721 ms (median 1875 ms), while the full WFE refresh
was 16–27 ms (median 18 ms): header median 16.7 ms, sections 1.1 ms, expand-state equality checks 0.016 ms, event
dispatch median ~9 ms. The synchronous transaction round trip on the SWT UI thread is the dominant VPN latency
(~98%+); widget refresh performance is healthy. First sample had an additional ~527 ms cold/lazy persist setup;
subsequent non-transaction persist overhead was ~0.6–0.7 ms. Detailed table is in
`docs/ai/ide/ide_swt_refresh_problem.md`.

## Session update — Shared XXAssigneesWidget empty-selection contract fixed (2026-10-13)

**Bug:** Every non-artifact use of `XXAssigneesWidget` started with `SystemUser.UnAssigned` already in `selected`
because the widget constructor seeded it. Action Search exposed the regression as a visible `UnAssigned` User filter,
but the problem affected searches, dialogs, Create Action, and every other fresh assignee widget.

**Final fix (user-corrected scope/semantics):** `XXAssigneesWidget` now starts with a truly empty selection. UnAssigned
remains in the selectable user list only as a user-facing "clear" choice; both single and collection `setSelected`
paths normalize it out, so it is never retained in `selected` or displayed as data. An empty selected collection is
the canonical widget state. Workflow persistence still maps empty to `AtsCoreUsers.UNASSIGNED_USER`, preserving the
stored workflow representation. Programmatic `clear()` now uses the base empty-selection behavior.

`UserSearchWidget` remains mapped to `XXAssigneesWidget` / `WidgetIdAts.XXAssigneesWidget`; the temporary narrower
switch to `XXUserTokenWidget` was reverted. Its `set(AtsSearchData)` retains an explicit no-user `clear()` branch so
clearing/loading a no-user saved search cannot leave a stale user selected.

**Validation:** Both files AST-parse; existing `XXAssigneesWidget` usage sites were reviewed; no constructor
`setSelected(SystemUser.UnAssigned, ...)` remains; `git diff --check` passed. Pending smoke checks: Action Search opens
blank; selecting UnAssigned clears to blank/empty; workflow with stored UnAssigned displays blank but persists empty as
UNASSIGNED; Create Action/New Role start with no selected assignee.

## Session update — Action Search Team Workflow default (2026-10-13)

User clarified "Team Definition checkbox" meant the **Team Workflow** work-item-type checkbox
(`WorkItemType.TeamWorkflow`); Team Definition itself is a separate selector. `WorkItemTypeSearchWidget` now builds the
non-review Team Workflow checkbox with `.andDefault(true)`, so new Action Search and Advanced Action Search pages start
with a valid type selected. Review Search uses a separate branch and is unaffected; fixed-type Goal/Task searches do
not render this selector.

Saved-search restoration remains authoritative: `loadWidgets()` clears all checkboxes before applying stored types, so
the builder default does not merge into saved selections. `ClearSearchAction` now explicitly adds TeamWorkflow to its
fresh data only for non-review searches that render work-item-type widgets, restoring the baseline after `clearAll()`.

Validation: both methods AST-parse and `git diff --check` passed. Pending smoke checks: new Action Search has Team
Workflow checked; explicit saved search restores exactly its stored types; Clear Search restores Team Workflow +
Working state; Review Search defaults are unchanged.

**Runtime correction:** The builder default initially populated `XXCheckBoxWidget.selected=true` but the visible SWT
`Button` remained unchecked. Root cause: `XXCheckBoxWidget` creates `checkButton` instead of the base value Text and had
no `refresh()` override, so base refresh could not synchronize model state into the checkbox. Added a guarded
`refresh()` that calls `checkButton.setSelection(getSelectedFirst())` only when different, then delegates to
`super.refresh()` for validation/lifecycle. This shared fix also makes saved-search restoration, `clearAll()`, and any
programmatic checkbox `setSelected(...)` visibly update correctly. AST parse and diff check passed; re-run the Action
Search default/Clear/saved-search smoke checks.

## Session update — Clear Search null Hold State fixed (2026-10-14)

**Bug:** Clear Search creates fresh `AtsSearchData` with `holdState=null`. `HoldStateSearchWidget.set()` passed null to
`XXAbstractBaseXWidget.setSelected()`, correctly triggering its non-null type invariant and aborting the rest of
`loadWidgets()`.

**Fix:** `HoldStateSearchWidget` now converts null or `HoldState.Not_Set` to `getWidget().clear()` and selects only
explicit `On_Hold` / `Not_On_Hold`. `AtsSearchWorkflowSearchItem.loadSearchData()` converts an empty widget's Not_Set
getter sentinel back to null, preserving AtsSearchData's no-filter representation. Also corrected
`HoldState.isNotOnHold()` from `!isOnHold()` to exact `this == Not_On_Hold`; otherwise Not_Set silently added a
not-on-hold query criterion.

Validation: all three files AST-parse; existing query tests cover explicit On_Hold and Not_On_Hold; no test asserts the
old incorrect Not_Set behavior; `git diff --check` passed. Pending smoke: Clear Search no exception, Hold State blank,
Team Workflow checked, Working state retained; explicit On Hold/Not On Hold saved searches restore and query correctly.

## Session update — Clear Search null Hold State fixed (2026-10-14)

Clear Search creates `AtsSearchData` with `holdState=null`; `HoldStateSearchWidget` passed it to the XX base's strict
non-null selection API and aborted `loadWidgets()`. The adapter now clears on null/`Not_Set` and selects only explicit
`On_Hold`/`Not_On_Hold`. `loadSearchData()` converts the empty widget's Not_Set sentinel back to null/no filter.
`HoldState.isNotOnHold()` now matches only `Not_On_Hold`, so `Not_Set` cannot silently add a not-on-hold query filter.

AST parsing and diff check passed. Existing query tests cover explicit On_Hold and Not_On_Hold; no test depends on the
old incorrect Not_Set behavior. Pending smoke: Clear Search has no exception, Hold State is blank, Team Workflow is
checked, and Working state remains selected.

## Session update — Clear Search optional Actionable Item NPE fixed (2026-10-14)

After the Hold State fix, Clear Search reached Team Definition loading and exposed the next migration contract bug.
A non-advanced Action Search can instantiate the `ActionableItemSearchWidget` wrapper without rendering its concrete
control. `ActionableItemSearchWidget.get()` returned null for that valid absent-control state; the Team Definition
modified listener synchronously recomputed Version/team data and `WorldEditorParameterSearchItem.getTeamDefs()`
iterated the null collection.

`ActionableItemSearchWidget.get()` now returns `Collections.emptyList()` when the optional control is absent, matching
the non-null selected-collection contract of XX widgets. `getIds()` now directly iterates the guaranteed collection.
Known callers preserve behavior; existing null checks are merely redundant. Broader suppression of dependency listeners
while loading a complete saved-search snapshot is a separate future cleanup because intermediate Team/AI changes can
clear dependent values, but it is not needed for this NPE.

AST parse and `git diff --check` passed. Pending smoke: Clear Search completes with no exception and restores the
expected defaults.

## Session update — WFE Create Sibling Workflow(s) restored (2026-10-14)

The WFE migration inverted the `IAtsWorkItemHookIde.createSiblingWidget(...)` fallback contract in
`WfeTransitionHeader`. Hook `true` means a product-specific sibling control was already created and the standard link
must be suppressed; migrated code instead created the standard "Create Sibling Workflow(s)" link only inside the true
branch. With no open-source hook override, no link appeared.

Restored hook-first behavior: iterate hooks, remember/break when one returns true, then create the existing direct
Hyperlink fallback only when none handled it. Preserved all original gates (`isTeamWorkflow`,
`isAllowSiblingCreation`, Work Definition showSiblingLinks, hook vetoes), tooltip, placement, and
`XSiblingActionBar.openCreateSiblingWorkflowBlam(...)` invocation. No new WidgetId/OSGi component required.

AST parse, hook-contract inspection, and `git diff --check` passed. Pending smoke: allowed team workflow shows/opens
Create Sibling; WorkDef/hook-disabled workflow hides it; proprietary custom hook returning true shows only its custom
control (no duplicate standard link).

## Session update — WFE attachment row spacing restored (2026-10-14)

Migration changed `WfeAttachmentsComposite.create()` from `ALayout.getZeroMarginLayout(1, false)` to default
`new GridLayout(1, false)`. SWT defaults added 5 px vertical spacing between every attachment row plus 5 px top/bottom
margins (and an extra horizontal margin on top of the explicit indent). Restored the established zero-margin one-column
layout and documented why. Per-row controls/layout, horizontal indent, and refresh/reflow behavior are unchanged.
AST parse and `git diff --check` passed; pending visual smoke with multiple attachments.

## Session update — WFE attachments reflow on structural change (2026-10-14)

After removing accidental inter-row spacing, dynamic attachment feedback was restored without reintroducing ordinary
refresh flicker. `WfeAttachmentsComposite.createUpdateAttached()` now returns true only when a row/heading is added,
removed, or changed. Initial creation reflows once; subsequent `refresh()` performs one local recursive layout and one
managed-form reflow only on structural changes. Ordinary artifact refreshes with unchanged attachments do no layout.

Removal now copies/clears pending relation IDs and removes disposed rows from `relIdToComp`, preventing stale map state.
The heading also returns to "No Attachments" when the last row is removed. AST parse and `git diff --check` passed.
Pending smoke: drag/drop expands immediately; delete/unrelate collapses/relabels immediately; unrelated Priority save
causes no attachments reflow/flicker.

## Session update — Safe value-widget growth restored (2026-10-14)

`XXAbstractBaseXWidget.refresh()` still short-circuits before all SWT work when `getDisplayValue()` equals current
control text. For every actual Text/StyledText change, `relayoutForValueChange()` now performs one targeted
`comp.getParent().layout(new Control[] {comp})` regardless of fill options; non-fill controls also have content-based
preferred sizes and must be allowed to grow/shrink. This is safe after the flicker fix because no-op editor fan-out
causes no setText/layout. Structural controls (attachments, sections) remain responsible for detecting add/remove/
expand changes and performing one local layout/reflow.

Updated `ide-swt-layouts.md`, `ide-xxwidget-architecture.md`, and base Javadocs to match the verified rule and correct
old wording that blamed the final flicker on forced updates. AST parse, stale-text scan, and `git diff --check` passed.
Pending smoke: change short/long values in both editors, drop/remove attachments, and confirm growth with no no-op
Priority flicker.

## Session update — WFE Operations visible after restored reload (2026-10-14)

Restored editors rebuild the Workflow tab asynchronously. State-section construction calls managed-form reflow before
`WfeOperationsSection` is created last; `createAtsBody()` then only laid out `atsBody`, so the ScrolledForm could retain
an extent ending above the Operations title with no scrollbar path to it. This latent omission became visible after
removing broad no-op refresh layouts for the flicker fix.

Added exactly one `managedForm.reflow(true)` at the end of `createAtsBody()`, after header/state/undefined/Operations
controls are initialized and `atsBody.layout()` completes. This is construction-only structural reflow; ordinary
refresh remains change-aware and flicker-safe. AST parse and `git diff --check` passed. Pending smoke: first open and
IDE restart/restored reload both show collapsed Operations at the bottom; expanding it shows all operation groups.
## Session update — Value changes propagate through managed forms (2026-10-14)

Targeted parent layout alone recomputed an XXWidget row after a real Text/StyledText value change, but did not update
the enclosing ScrolledForm's minimum size and scrollbar extent; long values could remain clipped until reopening or a
later structural refresh. `XXAbstractBaseXWidget.relayoutForValueChange()` now follows the targeted parent layout with
one `getManagedForm().reflow(true)` when the widget is hosted in a ready managed form.

The call remains inside the displayed-text inequality branch, so unchanged values perform no `setText`, layout, or
reflow and preserve the verified no-flicker behavior. Standalone/non-form widgets retain local layout only. Updated
`ide-swt-layouts.md` and `ide-xxwidget-architecture.md` with this local-layout-versus-form-extent distinction. AST
parsing, stale-wording scan, and `git diff --check` passed. Pending smoke: short-to-long and long-to-short values resize
immediately in Workflow and Artifact Editors; an unrelated Priority refresh still causes no flicker.
## Session update — Managed-form reflow alone did not expand changed values (2026-10-14)

Runtime smoke disproved the prior assumption: adding `managedForm.reflow(true)` after the existing targeted layout did
not make changed value controls expand. Source tracing found that the targeted call reported `comp` as changed even
though the preferred-size change occurs on its child `Text`/`StyledText`. Reflow can therefore recompute the outer form
while the leaf-owning `GridLayout` retains its cached bounds.

`relayoutForValueChange(...)` now receives the actual changed value control and passes that leaf to
`comp.getParent().layout(new Control[] {changedControl})`, then retains the guarded managed-form reflow. The equality
guard is unchanged, so no-op refreshes still perform no SWT mutation. AST parsing and `git diff --check` passed.
Pending runtime smoke; if single-line Text expands but multiline StyledText does not, next inspect the one-time fixed
`widthHint`/`heightHint` installed by `XWidgetUtility.computeXTextSize(...)` rather than broadening layout scope.
## Session update — BLAM parameter values wrap instead of widening the page (2026-10-14)

The remaining reported "doesn't expand" case was specifically `BlamInputSection`: long selection/value text widened
the managed `ScrolledForm`, producing a page-level horizontal scrollbar. This is presentation policy rather than a
missing reflow. Inherited XX selection controls are native single-line read-only `Text`, so they cannot wrap.

Added opt-in `XOption.VALUE_WRAP`. `BlamInputSection` applies it to dynamic BLAM parameter definitions before widget
creation. `XXAbstractBaseXWidget` then creates only its inherited read-only value control as
`SWT.READ_ONLY | SWT.MULTI | SWT.WRAP` (plus an optional border), with horizontal fill/grab and a 200 px preferred-width
hint. It does not add `SWT.H_SCROLL`. Selection/dialog/persistence behavior is unchanged, and specialized widgets that
override value-control creation, including `XXTextWidget`, retain their existing presentation. AST parsing and
`git diff --check` passed. Pending smoke: long BLAM selection wraps, row height grows, and the outer horizontal bar no
longer appears; short and interactive parameters remain usable.