---
summary: "Architecture and patterns for Change Report and Markdown Change Report pages"
tags: [web, angular, change-report, markdown, diff, pagination]
fileMatch: "**/change-report*,**/markdown-diff*,**/BranchEndpoint*"
---

# Change Reports

## Overview

Two change report pages exist under `/ple/`:

| Page | Route | Purpose |
|------|-------|---------|
| Change Report | `/ple/change-report` | Shows all attribute/artifact/relation changes on a working branch vs. its parent |
| Markdown Change Report | `/ple/markdown-change-report` | Shows only Markdown Content attribute changes with side-by-side diff panels |

Both open in new browser tabs from the branch management section (`BranchManagementComponent`) in the artifact explorer sidebar.

## Routing & Branch Selection

- Both pages use **query params** (`?branchId=...&branchType=...`) — not path params.
- Branch selection is done via a dialog (opened by a `merge_type` icon button in the title bar) containing `<osee-branch-picker />`.
- `BranchRoutedUIService` must be injected in the page component so it syncs URL query params to `UiService` on initial load. Without this, data won't load until the dialog is opened.
- When no branch is selected, show a hint: "Click the branch icon in the top-right to select a branch."

## Backend Endpoint

```
GET /orcs/branches/{branch1}/changes/{branch2}/filtered
GET /orcs/branches/{branch1}/changes/{branch2}/filtered/count
```

**Query params:**

| Param | Type | Default | Purpose |
|-------|------|---------|---------|
| `filter` | string | `""` | Text search across name, ID, changeType, itemType, itemKind, and derived description |
| `pageNum` | long | `0` | Zero-based page index |
| `count` | long | `0` | Page size (0 = return all) |
| `attributeType` | string | `""` | Filter to a specific attribute type name (e.g., "Markdown Content"). Empty = all changes |

The endpoint is generic — any consumer can paginate/filter any attribute type. The Markdown Change Report passes `attributeType=Markdown Content`; the Change Report page passes no `attributeType`.

**Implementation:** `BranchEndpointImpl.getFilteredPaginatedChangeReport` fetches all changes via the existing `TransactionFactory.getTxChangeReport`, then filters in-memory for the attribute type and text filter, then paginates with an in-memory `subList` (`count`/count endpoint returns the filtered `.size()`).

The page bounds are computed in `long` (`pageNum * pageSize`) and clamped to the list size before the `subList` cast so a large `pageNum`/`pageSize` cannot overflow `int` into a negative index. A `pageSize <= 0` or `pageNum < 0` request returns the full filtered list, and a start offset past the end returns an empty list.

> **Paging is in-memory, not DB-level — and this applies to _both_ report pages equally (they share this endpoint).** Unlike artifact search (`ArtifactEndpointImpl.getSearchQueryBuilder` → `QueryBuilder.isOnPage(pageNum, pageSize)` + `.getCount()`, which push limit/offset/COUNT to SQL), a branch change report is **not** a pageable query. The delta path `getTxChangeReport → compareTxs → OrcsBranch.compareBranch → LoadDeltasBetweenTxsOnTheSameBranch.compareTransactions` must compute the **entire** change set before any row is correct: `ChangeItemUtil.computeNetChanges` aggregates each item's net mod-type across the whole set, and `AddSyntheticArtifactChangeData` synthesizes artifact rows from the full attr/rel change set. Stable ordering is likewise applied in memory (sort by `artA` id) after the fact — there is no DB `ORDER BY` driving it, and no ORCS query API returns `ChangeItem`/`ChangeReportRowDto` deltas with limit/offset/count. `ArtifactEndpointImpl.getArtifactHistoryPaginated` uses the same load-all-then-`subList` approach, so this is the established pattern for tx-delta paging, not an oversight.
>
> So moving pagination to the server (done for both pages) reduces the **payload** shipped to the client and does filtering server-side, but the server still computes the full delta per request (and the count endpoint recomputes it). True DB-level paging would require new backend work: materialize the computed change set into a temp/join table with a deterministic order key, then add a paged/count query API that does limit/offset/COUNT **after** the net-change + synthetic-artifact steps (materialize-then-page). That's a shared ORCS/DB change that would benefit both report pages, not markdown-specific.

### Pagination (both pages use the server)

Both pages use **server-side pagination** against the same `/filtered` + `/filtered/count` endpoints — each page change or filter change is a new request:

- **Change Report** (`ChangeReportTableComponent`) calls `/filtered` with `pageNum`/`count` (no `attributeType`), and a separate `/filtered/count` call for the paginator length.
- **Markdown Change Report** (`MarkdownDiffComponent`) does the same but passes `attributeType=Markdown Content` so the server scopes to markdown changes. It keeps two server-driven signals: `allEntries` (the current page, refetched on branch/filter/`pageNum`/`pageSize` change) and `filteredCount` (the total, refetched on branch/filter change via `/filtered/count`).

Because the component only holds one page at a time, **export re-fetches the full matching set** on demand via `getMarkdownChanges(..., pageSize=0)` (server treats `count=0` as "return all") rather than reading an in-memory full set. See [Export](#export).

## Frontend Architecture

### Services

| Service | Location | Role |
|---------|----------|------|
| `ChangeReportHttpService` | `change-report-table/services/` | HTTP calls to the `/filtered` (page) and `/filtered/count` endpoints, plus the tx-delta `/changes/{tx1}/{tx2}` endpoint |
| `ChangeReportService` | `change-report-table/services/` | Facade adding branch info, action, tx info lookups |
| `MarkdownDiffHttpService` | `markdown-diff/services/` | Calls the server `/filtered` (page) and `/filtered/count` (total) endpoints with `attributeType=Markdown Content`; maps rows to display entries. `getMarkdownChanges(..., pageSize=0)` returns the full set for export |
| `MarkdownDiffService` | `markdown-diff/services/` | Facade for markdown diff page |

### Components

| Component | Purpose |
|-----------|---------|
| `ChangeReportTableComponent` | Paginated, filtered table of all branch changes |
| `MarkdownDiffComponent` | Page with summary table + side-by-side diff panels |
| `MarkdownDiffEntryComponent` | Single artifact's diff panel (side-by-side Previous/Current) |
| `BranchPickerDialogComponent` | Shared dialog wrapping `<osee-branch-picker />` |

### Diff Algorithm

`utils/compute-diff.ts` implements a side-by-side diff using LCS (Longest Common Subsequence):

- When `wasValue` is empty/whitespace → all lines shown as "added" (no LCS)
- When `isValue` is empty/whitespace → all lines shown as "removed" (no LCS)
- Otherwise → LCS pairs matching lines, shows removed/added for non-matching sections

### Change Description Derivation

The raw `changeType` from the server (`"New"`, `"Modified"`, `"Artifact Deleted"`) is mapped to verbose descriptions client-side:

| Raw changeType | Derived description |
|----------------|-------------------|
| `"New"` | "Markdown Content Added" |
| `"Modified"` | "Markdown Content Modified" |
| Contains "deleted" | "Artifact Deleted" |
| `"Applicability"` | "Applicability Changed" |

## UI Patterns

- **Summary table** (both pages): a filter row (search icon + input + count) and paginator live inside one bordered card. Non-obvious gotcha: the paginator's page-size select shows an unwanted outline — suppress it with `--mdc-outlined-text-field-outline-color: transparent` scoped to `mat-paginator`.
- **Diff panels** (Markdown page): each artifact is a card with a header (icon + name + change badge + artifact ID) and a side-by-side Previous/Current table. Removed lines tint red, added lines tint green; scroll-to-diff reuses the `osee-help-highlight` pulse animation.
- **Change badges / icons**: New/Added → green + `add`; Deleted → red/warning + `delete`; Modified → blue/primary + `edit`.

## Export

Export menu (icon button with `download` icon) offers:
- **Markdown (.md)** — fenced `diff` code blocks with `+`/`-` prefixes
- **HTML (.html)** — self-contained styled document with embedded CSS, suitable for printing/PDF

Both exports use the derived `changeDescription` for labels and properly color diff rows (green for added, red for removed). Export always uses the **full** filtered set, never the current page. Since the component only holds one page in memory (server pagination), `exportReport` re-fetches every matching entry via `getMarkdownChanges(..., pageSize=0)` before generating the file.

### Async + batched generation (prevents UI lock-up)

Both `generateMarkdownExport` and `generateHtmlExport` are **`async`** and `await` a yield to the event loop every `EXPORT_BATCH_SIZE` (25) entries. A report with thousands of entries — each running an LCS diff — would otherwise freeze the tab. Rules for this code:

- Keep the generators async and keep the periodic `await yieldToEventLoop()` in the entry loop. Do not collapse them back to a single synchronous `map`/`join`.
- The caller (`exportReport`) sets an `exporting` signal, disables the export button, shows a spinner, and wraps generation in `try/finally` so the busy state always clears. Surface failures via `uiService.ErrorText`.

### Fenced-block escaping (Markdown export)

Diff lines are wrapped in a fence whose length is computed by `fenceFor()` — one backtick longer than the longest backtick run found in the content (minimum 3). **Never hardcode a `` ```diff `` fence.** Artifact content can itself contain ``` ``` ``` fences, which would otherwise close the block early and corrupt the export. HTML export routes every interpolated value through `escapeHtml` for the same reason.

## Entry points (branch management)

The live branch-management UI is **`BranchManagementComponent`** (selector `osee-branch-management`), rendered inside `ArtifactExplorerSidebarComponent`. It renders both report links inline as full-width `mat-button` rows (matching the other management actions), each linking to its route with `branchId`/`branchType` query params and opening in a new tab (`target="_blank"` with `rel="noopener noreferrer"`):

- **Change Report** — `differences` icon → `/ple/change-report`
- **Markdown Change Report** — `difference` icon → `/ple/markdown-change-report`

`ChangeReportButtonComponent` (a standalone `mat-icon-button`) still exists and is used by the **Actra workflow editor** (`ActraWorkflowEditorComponent`); it resolves the branch from its `inputBranchId`/`inputBranchType` inputs, falling back to `UiService`, and links to the query-param `/ple/change-report` route.

> There is no branch-management "panel" component and no dedicated markdown-diff button component — an earlier duplicate (`BranchManagementPanelComponent`) and `MarkdownDiffButtonComponent` were removed because nothing rendered them. Add new report entry points to `BranchManagementComponent` directly.

## Preventing regressions

Rules baked in from bugs already fixed here — follow them to avoid reintroducing them:

- **Wire branch actions into the one rendered component** (`BranchManagementComponent`). A duplicate panel previously received new buttons while the rendered component kept a dead link, so the buttons never appeared.
- **Use the query-param routes only.** There is no `:branchId` path route — linking to `/ple/change-report/<id>` 404s.
- **`@for` over entries must `track $index`** (or another guaranteed-unique key), not `artifactId` — one artifact can span multiple rows, and duplicate track keys throw NG0955. Build scroll-to DOM ids from the index (`diff-<index>`), since `getElementById` on a non-unique id matches only the first.
- **Debounce filter-driven fetches** (`debounceTime` + `distinctUntilChanged`) on both the page query (`{ branchId, parentBranchId, filter, pageNum, pageSize }`) and the count query (`{ branchId, parentBranchId, filter }`) so typing doesn't issue a request per keystroke. The paginator length comes from the `/filtered/count` endpoint, not the current page's length (the page is only a slice).
- **Re-fetch the full set for export** via `getMarkdownChanges(..., pageSize=0)` — the component no longer holds all entries in memory, so export cannot read them from a signal.
- **Compute export fences, never hardcode them** (see [Fenced-block escaping](#fenced-block-escaping-markdown-export)).
- **Keep export/diff generation async + batched** with a busy signal (see [Async + batched generation](#async--batched-generation-prevents-ui-lock-up)).

## End-to-end coverage

Both report pages have Playwright e2e specs under `web/apps/osee/playwright/specs/artifact-explorer/tests/` (they auto-discover under the existing "Artifact Explorer Tests" project — no `playwright.config.ng.ts` change needed). Each spec creates a working branch plus test artifacts via the API in `beforeAll` and purges the branch in `afterAll`.

- **`change-report.e2e-spec.ts`** — Change Report page:
  - Empty state: the "select a branch" prompt and branch-picker button show when no branch is set.
  - Navigation: loads via `?branchId&branchType=working` query params and shows the branch name.
  - Populated table: renders change rows with a "Change Type" column and a `/\d+ result\(s\)/` count.
  - Filter: filtering by name/ID/change type narrows rows; "Clear filter" restores them.
  - Pagination: changing the paginator page size updates the table.
- **`markdown-change-report.e2e-spec.ts`** — Markdown Change Report page:
  - Empty state: the "select a branch" prompt shows and the export button is disabled.
  - Populated table: summary table renders with "Markdown Content Added" and `osee-markdown-diff-entry` diff entries.
  - Export: the export button enables with results and its menu opens (Markdown / HTML options).
  - Filter: filtering by name narrows entries; "Clear filter" restores them.
  - Pagination: changing the paginator page size updates the results.

When changing report behavior (columns, filter, paginator, export menu, empty state), update the matching spec so the coverage stays accurate.
