---
summary: "Artifact explorer architecture: layout, hierarchy, tabs, auto-saving editors, relations, history, path expansion, refresh patterns"
tags: [artifact-explorer, attributes, gamma, history, persisted-editors, relations, hierarchy, tabs]
fileMatch: "**/ple/artifact-explorer/**,**/ArtifactWithRelations*,**/artifact-with-relations/**"
---

# Artifact Explorer

## Layout

The artifact explorer uses a VS Code-inspired layout with a vertical activity bar, collapsible side panel, and tabbed editor:

```
┌──┬─────────────────┬──┬────────────────────────────────────┐
│🌳│ Hierarchy Panel │▌▌│ Tab Group (Editor Area)             │
│🔍│                 │▌▌│                                     │
│🔀│ ┌─────────────┐ │▌▌│ ┌────────────────────────────────┐ │
│  │ │ Branch Pick │ │▌▌│ │ Tab Bar (draggable, closable)  │ │
│  │ ├─────────────┤ │▌▌│ ├────────────────────────────────┤ │
│  │ │ Active:     │ │▌▌│ │ Section Toolbar: 📝 ↔️ 🕒 ℹ️    │ │
│  │ │ • Hierarchy │ │▌▌│ ├────────────────────────────────┤ │
│  │ │ • Search    │ │▌▌│ │ Active Section Content         │ │
│  │ │ • Branches  │ │▌▌│ └────────────────────────────────┘ │
│  │ └─────────────┘ │▌▌│                                     │
└──┴─────────────────┴──┴────────────────────────────────────┘
 ↑          ↑          ↑
activity  panel    resize handle
  bar
```

- **Activity bar** — thin vertical strip (48px) on the far left with icon buttons for Hierarchy, Search, Branch Management. Clicking the active section toggles the panel closed; clicking a different section switches and opens.
- **Hierarchy panel** — 25% default width, resizable (15–50%) via accessible drag divider with keyboard support. Fully collapsible.
- **Editor area** — fills remaining space; holds the tab group.
- Connected via `CdkDropListGroup` for drag-and-drop between hierarchy and relations editor.

### Component: `ArtifactExplorerComponent`

Root component. Manages:
- Activity bar with `toggleSection(section)` — VS Code-style collapse/switch
- `panelCollapsed` / `panelWidthPercent` / `activeSection` signals
- Accessible resizable divider (`role="separator"`, `aria-orientation="vertical"`, keyboard ArrowLeft/ArrowRight) using `fromEvent` pattern
- `window:beforeunload` guard via `ArtifactEditorDirtyService`
- Branch/view routing from `@Input()` setters → `UiService`

## Visual Design

### Background colors

The artifact explorer uses a **two-tone system** — one base background for all surfaces, with borders and shadows providing separation:

| Surface | Class | Purpose |
|---------|-------|---------|
| Base (everything) | inherited (no explicit bg) | Activity bar, hierarchy panel, tab bar, toolbar, editor content — all the same color |
| Hover highlight | `hover:tw-bg-background-hover` | Unified hover fill for list items in hierarchy/relations (matches Material icon button hover) |

There are no competing grey shades between adjacent areas. Separation is achieved via:
- `tw-border-*-background-hover` borders between activity bar, panel, and editor
- `tw-shadow-[0_2px_4px_-1px_rgba(0,0,0,0.2)]` (light mode) / `dark:tw-shadow-[0_4px_6px_-1px_rgba(0,0,0,0.4)]` (dark mode) beneath the branch picker and editor toolbar

### Hover & interaction states

- **Hierarchy rows**: full opacity text, `hover:tw-bg-background-hover` background highlight. No dimming.
- **Relations rows**: same treatment — full opacity, background highlight on hover.
- **Expanded state**: indicated only by the rotated chevron and visible children. No background or border accent.
- **Activity bar icons**: `tw-opacity-60` inactive, blue accent when active section matches.
- **Tab close (×)**: hidden by default, appears on tab hover, neutral grey background on × hover.

### Spacing: expandable list rows

All expandable rows (hierarchy tree, relation types, relation sides) follow the same spacing:

| Element | Spacing | Class |
|---------|---------|-------|
| Row left padding | 16px | `tw-pl-4` |
| Chevron → Icon | 4px | `tw-pl-1` |
| Icon → Text | 6px | `tw-pl-1.5` |
| Row vertical padding | 4px top/bottom | `tw-py-1` |
| Child indent | 32px | `tw-pl-8` |

### Toolbars

Two toolbar variants, both using `mat-icon-button` with 24px icons:

| Toolbar | Orientation | Width/Height | Class | Icons |
|---------|-------------|--------------|-------|-------|
| Activity bar | Vertical | 48px wide (`tw-w-12`) | Fixed left strip, full height | Section switches (hierarchy, search, branch) |
| Section toolbar | Horizontal | 40px tall (Material `mat-icon-button` default) | Sticky top, full width | Panel actions (attributes, relations, history, info) |

Both toolbars:
- Use `tw-gap-1` between buttons
- Active icon: `tw-text-osee-blue-7 dark:tw-text-primary`
- Inactive icon: `tw-opacity-60`
- All buttons have `aria-label` and `matTooltip` for accessibility

## Hierarchy Panel

### Component: `ArtifactHierarchyPanelComponent`

Receives `activeSection` as an input from the parent (driven by the activity bar). Contains three sections shown/hidden via `[class.tw-hidden]`:
1. **Hierarchy** (🌳) — recursive tree via `ArtifactHierarchyComponent`
2. **Search** (🔍) — `ArtifactSearchComponent` with advanced filters
3. **Branch Management** (🔀) — `BranchManagementPanelComponent`

Always visible at top: `BranchPicker` + `CurrentViewSelector` (if PLE branch).

### Component: `ArtifactHierarchyComponent` (recursive)

Each instance renders one level of the tree. Key behavior:

- **Data loading**: `GET /orcs/branch/{id}/artifact/{id}/children` — lightweight payload (id, name, icon, typeId, gammaId, applicability, operationTypes). No attributes/relations.
- **Refresh**: `combineLatest([paths, branchId, viewId, uiService.update])` with `debounceTime(100)` prevents request spam.
- **Expand/collapse**: Managed by `ArtifactHierarchyArtifactsExpandedService` (signal-based; resets on branch/view change).
- **Path-driven expansion**: `ArtifactHierarchyPathService` calls `GET .../getPathToArtifact` and expands all parent→child pairs along returned paths. Used by "Show in Hierarchy" from search.
- **Context menu**: Right-click opens `ArtifactOperationsContextMenuComponent` (create child, delete, publish).
- **Drag**: Each artifact row is `cdkDrag` for drop into relations editor.
- **Tab open**: Double-click (or single click) calls `tabService.addArtifactTab(artifact)`.

### Expand State Service: `ArtifactHierarchyArtifactsExpandedService`

Signal-based tree expansion tracking:
- Stores `Array<{ artifactId, childArtifactIds[] }>` — maps parent → expanded children
- `expandArtifact(parentId, childId)` / `collapseArtifact(parentId, childId)` / `isExpanded(parentId, childId)`
- Auto-clears on branch or view change via `linkedSignal` + `effect`

### Path Service: `ArtifactHierarchyPathService`

- `navigateToArtifact(id)` — clears all expansion, fetches path, expands from root to target
- `updatePaths(id)` — fetches path without clearing existing expansion
- Path response is `string[][]` (multiple paths if artifact has multiple hierarchy parents; each path is target→root order)

## Tab System

### Service: `ArtifactExplorerTabService`

Manages open editor tabs:
- `tabs` — `linkedSignal` that auto-clears tabs for committed branches (listens to `BranchCommitEventService`)
- `addArtifactTab(artifact)` — deduplicates by artifact ID + branch; focuses existing tab if already open
- `removeTab(index)` / `removeTabByArtifactId(id)` — close tabs
- `updateTabTitle(artifactId, newTitle)` — syncs tab title when artifact name is edited
- `onTabDropped(event)` — reorder tabs via `CdkDragDrop`
- Tabs are draggable and closable (with dirty guard)
- Tab names truncate at 20 characters with `...` and show a tooltip with the full name on hover

### Type: `tab` (currently just `artifactTab`)

```typescript
type artifactTab = {
  tabId: string;
  tabType: 'Artifact';
  tabTitle: string;
  artifact: artifactWithRelations;
  branchId: string;
  branchName: string;
  viewId: string;
};
```

## Editor (per-tab content)

### Component: `ArtifactEditorComponent`

Renders one artifact tab. Has a sticky section toolbar with four sections (hidden/shown via `[class.tw-hidden]` to preserve state):

| Section | Icon | Component |
|---------|------|-----------|
| Attributes | `edit_note` | `AttributesEditorPanelComponent` |
| Relations | `swap_horiz` | `RelationsEditorPanelComponent` |
| History | `history` | `ArtifactHistoryPanelComponent` |
| Info | `info_outline` | `ArtifactInfoPanelComponent` |

Sections use `[class.tw-hidden]` instead of `@if` so state (scroll position, form values) is preserved when switching.

## Attributes Editor

### Two-stage loading

1. **Hierarchy** returns no attributes (lightweight children).
2. **Editor tab** uses `httpResource` to fetch full artifact via `/related/direct?includeRelations=false`.

### Component: `AttributesEditorPanelComponent`

- Fetches attributes via `httpResource` keyed on `(branchId, artifactId, viewId)`
- Shows applicability dropdown (if PLE branch) via `PersistedApplicabilityDropdownComponent`
- Renders per-attribute editors sorted and tracked by `typeId`

### Component: `PersistedArtifactAttributeEditorComponent`

Each attribute field auto-saves independently. Template switches on `attr().storeType`:

| storeType | Widget | Save trigger |
|-----------|--------|-------------|
| `Boolean` | `MatSlideToggle` | Immediate on toggle |
| `Enumeration` | `MatSelect` with `enumOptions` | On selection change |
| Default (String/Int/Long/Date) | `FocusLostInputComponent` | On blur |
| Name=`Markdown Content` | `MarkdownEditorComponent` | On `focusout` of wrapper |

Save mechanism:
```typescript
currentTxService.modifyArtifactAndMutate(comment, artifactId, applicability, {
  set: [updatedAttr]   // existing attribute (id/gammaId != '-1')
  // OR
  add: [newAttr]       // new attribute (id === '-1' && gammaId === '-1')
})
```

Guards against redundant saves via `previousValue` signal (skips if value unchanged).

### Dirty Tracking: `ArtifactEditorDirtyService`

- `markDirty(key)` / `markClean(key)` — tracks editors with unsaved input
- `hasDirtyEditors()` — used by `window:beforeunload` and tab close guard
- Key format: `${artifactId}-${typeId}`
- `PersistedArtifactAttributeEditorComponent` calls `markDirty` on focus-in/input and `markClean` after successful save or on `ngOnDestroy`

## Relations Editor

### Component: `RelationsEditorPanelComponent`

- Fetches full artifact with relations via `getartifactWithRelations(branch, artifact, view, includeRelations=true)`
- Refresh via `repeat({ delay: () => uiService.updateArtifact.pipe(filter(id => id === artifact)) })`
- Expandable relation groups per relation type / side
- **Drop target** (`cdkDropList` connectedTo hierarchy): dragging an artifact from the tree creates a relation
- **Delete relation**: opens `RelationDeleteDialogComponent`, calls `currentTxService.deleteRelationAndMutate()`
- **Open related artifact**: click calls `tabService.addArtifactTabOnBranch()`

### Cross-tab refresh on delete

When an artifact is deleted from the hierarchy:
1. Tab for the deleted artifact is closed
2. Expand state for the deleted artifact is collapsed
3. All remaining open artifact tabs receive `uiService.updatedArtifact = tab.artifact.id` — this triggers their relations panels to refetch (removes stale references to the deleted artifact)
4. `uiService.updated = true` triggers hierarchy children refetch

## Artifact History

### Backend endpoints

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/orcs/branch/{id}/artifact/{id}/history?pageNum=N&count=M` | Paginated change history |
| GET | `/orcs/branch/{id}/artifact/{id}/latestTransaction` | Latest tx details |
| PUT | `/orcs/branch/{id}/artifact/{id}/revert` | Revert to state before latest tx |

### Component: `ArtifactHistoryPanelComponent`

- Uses `httpResource` with pagination signals (`currentPage`, `pageSize`)
- Groups changes by transaction ID into `historyGroup[]` (txId, comment, date, changes)
- Sorted most-recent-first
- Click row → opens `ArtifactHistoryDiffDialogComponent` showing before/after values
- "Revert" in dialog calls the revert endpoint, triggers `uiService.updated = true` on success

### Types

```typescript
type artifactHistoryResult = {
  changes: artifactHistoryEntry[];
  transactions: Record<string, transactionInfo>;
};

type artifactHistoryEntry = {
  changeType: string;
  itemId: string;
  itemTypeId: string;
  artId: string;
  baselineVersion: changeVersion;
  currentVersion: changeVersion;
  destinationVersion?: changeVersion;
  netChange?: changeVersion;
  synthetic: boolean;
};

type transactionInfo = { id: string; comment: string; timestamp: number };
```

## Refresh / Update Patterns

| Signal | Source | Consumers |
|--------|--------|-----------|
| `uiService.updated` (boolean) | Mutations, creates, deletes | Hierarchy `children$` (via `uiService.update` startWith+combineLatest) |
| `uiService.updatedArtifact` (string ID) | Relation add/delete, attribute save, cross-tab delete | Relations panel `artWithRelation$` (via `repeat` + `filter(id === artifact)`) |
| `httpResource` reload | Signal dependency changes (branchId, artifactId, viewId) | Attributes panel, history panel |

## Attribute Type

The entire web uses ONE attribute type from `@osee/attributes/types`:

```typescript
attribute<T, U extends ATTRIBUTETYPEID>
```

Optional metadata fields (`name?`, `storeType?`, `multiplicity?`, `gammaId?`, `enumOptions?`) are populated by the artifact explorer API but absent in MIM responses. Do NOT use `Required<attribute<T,U>>` — it conflicts with optional metadata.

## Java Serialization

`ArtifactWithRelations` uses `AttributePojo<String>` for all attributes. The `toStringPojo` method coerces non-string values (enums, booleans) to strings via reflection (`getName()` fallback). `AttributePojo` exposes `getName()`, `getStoreType()`, and `getMultiplicity()` getters from `AttributeTypeToken`.

## Key Files

| Purpose | Path |
|---------|------|
| Root component | `web/.../artifact-explorer/artifact-explorer.component.ts` |
| Hierarchy panel | `web/.../hierarchy/artifact-hierarchy-panel/` |
| Recursive tree | `web/.../hierarchy/artifact-hierarchy/artifact-hierarchy.component.ts` |
| Context menu (create/delete/publish) | `web/.../hierarchy/artifact-operations-context-menu/` |
| Expand state service | `web/.../services/artifact-hierarchy-artifacts-expanded.service.ts` |
| Path service | `web/.../services/artifact-hierarchy-path.service.ts` |
| Tab service | `web/.../services/artifact-explorer-tab.service.ts` |
| HTTP service | `web/.../services/artifact-explorer-http.service.ts` |
| Dirty tracking service | `web/.../services/artifact-editor-dirty.service.ts` |
| Editor component | `web/.../editor/artifact-editor/artifact-editor.component.ts` |
| Attributes panel | `web/.../editor/attributes-editor-panel/attributes-editor-panel.component.ts` |
| Per-field editor | `web/.../editor/attributes-editor-panel/persisted-artifact-attribute-editor/` |
| Relations panel | `web/.../editor/relations-editor-panel/relations-editor-panel.component.ts` |
| History panel | `web/.../editor/artifact-history-panel/artifact-history-panel.component.ts` |
| History diff dialog | `web/.../editor/artifact-history-panel/artifact-history-diff-dialog/` |
| Info panel | `web/.../editor/artifact-info-panel/artifact-info-panel.component.ts` |
| Tab type | `web/.../types/artifact-explorer.ts` |
| History types | `web/.../types/artifact-history.ts` |
| Attribute type definition | `web/apps/osee/src/app/attributes/types/attribute.ts` |
| Artifact with relations type | `web/apps/osee/src/app/artifact-with-relations/types/artifact-with-relations.ts` |
| Java ArtifactWithRelations | `plugins/org.eclipse.osee.framework.core/.../ArtifactWithRelations.java` |
| Java AttributePojo | `plugins/org.eclipse.osee.framework.core/.../AttributePojo.java` |
| Java ArtifactEndpointImpl | `plugins/org.eclipse.osee.orcs.rest/.../ArtifactEndpointImpl.java` |
| Java ArtifactHistoryResult | `plugins/org.eclipse.osee.orcs.rest.model/.../ArtifactHistoryResult.java` |
| Playwright tests | `web/apps/osee/playwright/specs/artifact-explorer/` |

## Playwright Test Architecture

Tests run **in parallel file-by-file** (each file is a worker). To prevent suites from interfering with each other, every test file creates its own isolated working branch via the REST API and purges it on teardown.

### Pattern (mandatory for all new test files)

```typescript
const BRANCH = 'AE <Suite Name> Tests';
let branchId: string;

test.describe('<Suite Name>', () => {
  test.describe.configure({ mode: 'serial' });

  test.beforeAll(async ({ browser, request }) => {
    branchId = await createBranchViaApi(request, BRANCH);
    const page = await browser.newPage();
    await openBranch(page, BRANCH);
    await createArtifact(page, '<parent>', '<child>', '<type>');
    await page.close();
  });

  test.afterAll(async ({ request }) => {
    await purgeBranchViaApi(request, branchId);
  });

  test('should do something', async ({ page }) => {
    await openBranch(page, BRANCH);  // each test re-navigates (fresh page)
    // ...assertions
  });
});
```

### Rules

1. **One branch per file.** The branch name must be unique across all test files (prefix with `AE <Suite> Tests`).
2. **Serial mode required.** All files use `test.describe.configure({ mode: 'serial' })` because tests within a suite may depend on ordering.
3. **API-based branch creation in `test.beforeAll`.** Use `createBranchViaApi(request, BRANCH)` to create the working branch via the REST API. Then use `browser.newPage()` to open the branch and create test artifacts via the UI. Close the page when done.
4. **API-based branch cleanup in `test.afterAll`.** Always add `test.afterAll(async ({ request }) => { await purgeBranchViaApi(request, branchId); })` to permanently delete the test branch after the suite finishes. This prevents branch accumulation across test runs.
5. **Setup only what the suite needs.** Don't create artifacts that other suites need — each file is self-contained.
6. **Re-navigate in each test.** Each test gets a fresh page, so call `openBranch(page, BRANCH)` at the start of every test.
7. **No shared setup project.** The `playwright.config.ng.ts` "Artifact Explorer Tests" project depends only on the global "Setup" (database init). There is no intermediate setup project.
8. **Use helpers from `utils/helpers.ts`.** `createBranchViaApi`, `purgeBranchViaApi`, `openBranch`, `createArtifact`, `expandArtifact`, `searchAndOpenArtifact`, etc.
