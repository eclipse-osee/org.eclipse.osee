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

- `MarkdownTableDialogData` — Input data for the dialog (headers, cells, alignments, spans, isEdit, caption, captionPosition)
- `MarkdownTableDialogResult` — Output (`{ markdown: string, caption: string, captionPosition: CaptionPosition }`)
- `ColumnAlignment` — `'left' | 'center' | 'right'`
- `CaptionPosition` — `'above' | 'below'`

## Table parsing

The markdown editor's `parseTableAtSelection()` method:
1. Walks upward from the cursor through consecutive `|`-starting lines to find a separator line
2. If not found going up (e.g., cursor is on the header row), checks the line immediately below the cursor for a separator
3. If the cursor is on a `<table-caption>` line, skips blank lines above and walks into the table to find the separator
4. If there's a text selection that overlaps a table anywhere in the document, falls back to a full scan
5. Once the separator is found, identifies the header row above and data rows below
6. Scans below the last data row (skipping blank lines) for a `<table-caption>` tag and includes it in the parsed result
7. Detects Flexmark colspan syntax (`||` = consecutive empty raw cells between pipes)
8. Returns headers, headerSpans, cells, alignments, caption text, and character indices for selection/replacement

### Caption detection

- A `<table-caption>Text</table-caption>` or `<table-caption position="above">Text</table-caption>` tag near a table is associated with it.
- The `position` attribute controls rendering placement: `"above"` places the caption before the table, `"below"` (or omitted) places it after.
- Captions are separated from the table by a blank line (`\n\n`) in both above and below positions for markdown parsing compatibility and visual consistency.
- The parser checks both above (before the header line) and below (after the last data row) for captions, skipping blank lines in both directions.
- When the cursor is on a caption line, the editor recognizes it as being "in" the table — both "Select Table" and "Edit Table" work from the caption position (for both above and below captions).
- The `endIndex`/`startIndex` includes blank lines and the caption line, so selection and replacement cover the full table+caption range.

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
- Rows start with explicit pixel height (62px) so `height: 100%` resolves correctly from first render

## Undo/redo

- Snapshot-based: captures full state (headers, spans, cells, alignments, caption, captionPosition) before each structural operation
- Text edits captured on `(focus)` — entering a field (including the caption input) saves the pre-edit state
- Toggling caption position also saves undo state
- Max 50 history entries
- Ctrl+Z / Ctrl+Y / toolbar buttons
- Listens on `document:keydown` (works regardless of focus position within dialog)

## Markdown generation

- Pipes in cell content escaped as `\|`
- Newlines in cells encoded as `<br>`
- Colspan syntax: header cell followed by empty `|` for each spanned column
- Whitespace trimmed from cells; empty cells output as single space
- Caption tag includes `position="above"` attribute when above is selected; omits the attribute for below (backward-compatible default)
- Caption is separated from table/image by a blank line (`\n\n`) in both positions

## Toolbar integration

The markdown editor toolbar (left to right):
- **Undo** (`undo`): Reverts the last content change in the main editor textarea
- **Redo** (`redo`): Reapplies a reverted change
- **Examples** (`lightbulb`): Opens a menu with example markdown snippets to insert; disabled during image preview
- **Upload image** (`image`): Opens the upload image dialog with `disableClose: true`; disabled when no artifact is selected or upload is in progress; shows `hourglass_empty` during upload
- **Table** (`table_chart`): Opens create/edit dialog based on cursor position (works from table body, header, separator, or caption line); disabled during image preview
- **Select table** (`select_all`): Highlights the full table + caption at cursor in the textarea (works from caption line too); disabled during image preview
- **Image preview toggle** (`visibility` / `edit`): Toggles between editing mode and rendered image preview; shows `hourglass_empty` while images load
- **Preview panel toggle** (`view_sidebar` / `vertical_split`): Collapses or expands the markdown preview panel
- **Fullscreen toggle** (`fullscreen` / `fullscreen_exit`): Enters or exits fullscreen mode

All disabled buttons wrap their tooltip in a `<span>` so the tooltip remains visible even when the button is disabled.

## Dialog UX

- `disableClose: true` — backdrop click doesn't close; Escape key does
- Max size: 50 columns × 100 rows (with snackbar messages on limit)
- Size inputs update on blur (not keystroke) to avoid erasing mid-type
- Loading indicator in toolbar for large tables
- Row resize via drag handles between rows (Excel-style)
- Default row height: 62px (minimum enforced during resize)
- Row numbers displayed between the up/down insert arrows in the left gutter
- Arrow buttons use compact 28px hit area with centered icons
- "Insert Row Above" tooltip appears above the button; "Insert Row Below" tooltip appears below
- Sticky header (thead) and left gutter for scrolling
- Caption input in the dialog actions area with `appearance="outline"`
- Caption position toggle button (vertical_align_top/bottom icon) next to caption input — toggles between above and below with explanatory tooltip
- Action buttons (Cancel, Update/Insert) grouped tightly together, separated from the caption input

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
- Undo/redo (keyboard + buttons, multiple operations, caption included)
- Header spans (merge, unmerge, colspan output, colspan parsing, span with insert)
- Edge cases (backdrop click, Escape, `<br>` encoding, pipe escaping/unescaping, size limits)
- Caption (insert with/without caption, cursor-on-caption detection, blank lines between table and caption, caption undo/redo, position toggle above/below)
- Row numbers (display, update on add, tooltip text)
- Caption examples (table caption, figure caption, table example, edit from caption)
- Image upload dialog (disableClose behavior, caption position toggle)

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
  CaptionPosition,
} from '@osee/shared/dialogs';
```
