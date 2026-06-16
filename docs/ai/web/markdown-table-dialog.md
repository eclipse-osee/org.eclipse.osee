---
summary: "Markdown table dialog for creating, editing, and managing tables with colspan support in the markdown editor"
tags: [web, angular, markdown, table, dialog]
fileMatch: "**/markdown-table-dialog*,**/markdown-editor*"
---

# Markdown Table Dialog

## Overview

The markdown table dialog (`MarkdownTableDialogComponent`) provides a visual UI for creating and editing markdown tables. It integrates with the markdown editor toolbar and supports:

- Creating new tables with configurable dimensions
- Editing existing tables (parsed from markdown at cursor position)
- Flexmark colspan syntax for header spanning (`||`)
- Row height resizing via drag handles
- Undo/redo with state snapshots
- Progressive batch loading for large tables
- Newline encoding (`<br>`) and pipe escaping (`\|`) in cell content

## Architecture

```
markdown-editor.component.ts     ← Toolbar buttons, table parsing, selection logic
  └── markdown-table-dialog/
        ├── component.ts          ← Dialog logic, signals, undo/redo, span management
        └── component.html        ← Table grid with sticky headers, row resize handles
```

### Key types (exported from `@osee/shared/dialogs`)

- `MarkdownTableDialogData` — Input data for the dialog (headers, cells, alignments, spans, isEdit)
- `MarkdownTableDialogResult` — Output (`{ markdown: string }`)
- `ColumnAlignment` — `'left' | 'center' | 'right'`

## Table parsing

The markdown editor's `parseTableAtSelection()` method:
1. Walks upward from the cursor through consecutive `|`-starting lines to find a separator line
2. If not found going up (e.g., cursor is on the header row), checks the line immediately below the cursor for a separator
3. If there's a text selection that overlaps a table anywhere in the document, falls back to a full scan
4. Once the separator is found, identifies the header row above and data rows below
5. Detects Flexmark colspan syntax (`||` = consecutive empty raw cells between pipes)
6. Returns headers, headerSpans, cells, alignments, and character indices for selection/replacement

### Separator detection

A line is a valid separator if it starts/ends with `|` and every cell between pipes matches `:?-+:?`.

### Colspan detection

In `parseHeaderRowWithSpans()`, the raw (untrimmed) split is checked — only truly empty strings (`""`) between pipes indicate a span. Whitespace-only cells (`"   "`) are independent empty headers.

## Header span management

- **Merge left**: Absorbs a column (and its existing span) into the left neighbor
- **Unmerge**: Splits a span at the clicked column — that column becomes the new owner of the right portion
- **Column insert within span**: New column inherits span=0 and owner's span increases
- **Column removal**:
  - Owner removed → transfers ownership + text to next column
  - Spanned-over removed → reduces owner's span

## Performance

- Tables ≤500 cells load synchronously (instant)
- Tables >500 cells batch-load: first 10 rows immediately, then 45 rows per tick
- Row drag uses direct DOM manipulation during drag, commits to signal on mouseup
- Dialog teardown clears cells/headers signals before closing to speed up DOM destruction

## Undo/redo

- Snapshot-based: captures full state (headers, spans, cells, alignments) before each structural operation
- Text edits captured on `(focus)` — entering a field saves the pre-edit state
- Max 50 history entries
- Ctrl+Z / Ctrl+Y / toolbar buttons
- Listens on `document:keydown` (works regardless of focus position within dialog)

## Markdown generation

- Pipes in cell content escaped as `\|`
- Newlines in cells encoded as `<br>`
- Colspan syntax: header cell followed by empty `|` for each spanned column
- Whitespace trimmed from cells; empty cells output as single space

## Toolbar integration

The markdown editor toolbar provides:
- **Table button** (`table_chart`): Opens create/edit dialog based on cursor position
- **Select table button** (`select_all`): Highlights the full table at cursor in the textarea
- Both are disabled during image preview mode with explanatory tooltips

## Dialog UX

- `disableClose: true` — backdrop click doesn't close; Escape key does
- Max size: 50 columns × 100 rows (with snackbar messages on limit)
- Size inputs update on blur (not keystroke) to avoid erasing mid-type
- Loading indicator in toolbar for large tables
- Row resize via drag handles between rows (Excel-style)
- Sticky header (thead) and left gutter for scrolling

## Testing

Tests live in `web/apps/osee/playwright/specs/artifact-explorer/tests/markdown-editor.e2e-spec.ts`.

Run table-specific tests:
```bash
npx playwright test --project "Setup" --project "Artifact Explorer Tests" --grep "Table Dialog"
```

Test coverage includes:
- Create mode (open, insert, cancel)
- Edit mode (parse headers, cells, alignments, spans from existing markdown)
- Select table (full highlight including edge detection)
- Controls (add/remove rows/columns, alignment cycling)
- Undo/redo (keyboard + buttons, multiple operations)
- Header spans (merge, unmerge, colspan output, colspan parsing, span with insert)
- Edge cases (backdrop click, Escape, `<br>` encoding, pipe escaping/unescaping, size limits)

## Known limitations

- **Undo captures on focus, not on change**: Focusing 50 cells without editing fills the undo stack with duplicate snapshots. The 50-entry cap keeps memory bounded.
- **Ctrl+Z intercepts native undo**: The `document:keydown` listener captures Ctrl+Z globally while the dialog is open. Pressing Ctrl+Z in a cell undoes the last structural change (not the text just typed). Browser-native text undo within individual textareas is overridden.
- **No column width resize**: Column widths are auto-sized with a 160px minimum. Manual column resize was attempted but removed due to complexity with sticky headers.
- **Large table close delay**: Closing a 50×100 table has a brief lag as Angular destroys ~5000 DOM nodes. The `cells.set([])` pre-clear mitigates but doesn't eliminate this.

## Import path

```typescript
import {
  MarkdownTableDialogComponent,
  MarkdownTableDialogData,
  MarkdownTableDialogResult,
  ColumnAlignment,
} from '@osee/shared/dialogs';
```
