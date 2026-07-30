---
summary: "Markdown editor component with formatting toolbar, image upload, table editing, preview, fullscreen, and VS Code-style undo/redo"
tags: [web, angular, markdown, editor, images, preview, toolbar]
fileMatch: "**/markdown-editor*,**/markdown-image*"
---

# Markdown Editor

## Overview

The markdown editor (`MarkdownEditorComponent`) is an Angular component providing a rich editing experience for markdown content stored in OSEE artifacts. It features:

- Responsive toolbar with split buttons, formatting actions, and progressive section collapse
- Split-pane editing with live HTML preview (supports dark mode)
- Image upload (via dialog or drag-and-drop) with size selection
- Table creation and editing (via `MarkdownTableDialogComponent`)
- Image preview mode (renders `<image-link>` tags as actual images)
- Fullscreen mode with resizable divider
- VS Code-style undo/redo with word-level grouping
- Insert-at-cursor for all toolbar actions

## Key Files

| File | Purpose |
| :-- | :-- |
| `markdown-editor.component.ts` | Main component logic |
| `markdown-editor.component.html` | Template with toolbar, textarea, preview pane |
| `markdown-editor-actions.ts` | Formatting actions, list options, heading options |
| `markdown-image.service.ts` | HTTP service for uploading and fetching images |
| `toolbar-section-dropdown.component.ts` | Collapsed section dropdown (used by ribbon collapse) |
| `split-button/split-button.component.ts` | Reusable split button (icon + dropdown arrow) |

## Toolbar

The toolbar is organized into sections separated by pipe dividers:

1. **History** — Undo, Redo (never collapses)
2. **Media** — Upload Image, Insert Table, Select Table, Figure Caption, Table Caption (collapses last)
3. **Format** — Heading (split button), Bold, Italic, Strikethrough (collapses second)
4. **Insert** — List (split button), Inline Code, Link, Blockquote, Code Block, HR (collapses first)
5. **View** — Preview, Panel toggle, Fullscreen, Help (never collapses)

### Split Buttons

Heading and List use `osee-split-button`: clicking the icon performs the default action (H2 / Bulleted List), clicking the dropdown arrow shows all variants. Uses CDK overlay with `FullscreenOverlayContainer` for proper positioning in fullscreen.

### Responsive Collapse

When the toolbar container becomes too narrow, sections collapse progressively into compact `osee-toolbar-section-dropdown` buttons (icon + arrow → dropdown with all section actions). Collapse order: Insert → Format → Media. History and View never collapse.

### Formatting Actions

Defined in `markdown-editor-examples.ts`. Each action has:
- `name`, `icon`, `markdown` (full snippet for block insertion)
- Optional `prefix`/`suffix` for wrapping selected text (inline formatting)
- Optional `placeholder` for text inserted when nothing is selected
- `group` (`'inline'` | `'block'` | `'media'`) determines toolbar section placement

## Undo/Redo

VS Code-style intelligent grouping via `handleInputForUndoGrouping()`:

- Consecutive typing at the same cursor position groups into a single undo entry
- Groups break on: word boundaries (whitespace after non-whitespace), newlines, cursor jumps, non-typing input (paste/cut/delete), idle timeout (1 second)
- Toolbar actions (formatting, image insert, table dialog) commit as discrete undo steps via `commitExplicitUndoState()`
- Ctrl+Z / Ctrl+Y intercepted on the textarea for custom undo/redo
- `isUndoRedoAction` flag prevents undo/redo operations from recording themselves

## Image Handling

### `<image-link>` Syntax

Images are stored in markdown content as custom tags:

```
<image-link>ARTIFACT_ID</image-link>
<image-link size="m">ARTIFACT_ID</image-link>
```

The optional `size` attribute controls rendered dimensions during publishing:
- `xs` — 25% of max width
- `s` — 50% of max width
- `m` — 75% of max width
- `l` — 100% of max width (still capped by native resolution)
- Omitted — auto sizing with max cap (same as `l`)

### Upload Flow

1. User clicks the image toolbar button or drops an image on the editor
2. `UploadImageDialogComponent` opens for file selection, caption, and size
3. `MarkdownImageService.uploadImageArtifact()` uploads the file to the server
4. `insertImageLink()` inserts the `<image-link>` tag at cursor position

### Preview Mode

`toggleShowImages()` fetches image object URLs from the server and replaces `<image-link>` tags with `<img>` elements for visual preview. The editor textarea is disabled during preview.

### Drag-and-Drop

Files dragged onto the editor are validated against supported formats and uploaded directly (bypasses the dialog — no caption or size selection for drag-drop).

## Captions

Figure and table captions register entries in the List of Figures / List of Tables when publishing:

```
<figure-caption>caption text</figure-caption>
<table-caption>caption text</table-caption>
<table-caption position="above">caption text</table-caption>
```

## Table Integration

The editor detects tables at the cursor position via `parseTableAtSelection()` and opens `MarkdownTableDialogComponent` in create or edit mode accordingly. See `docs/ai/web/markdown-table-dialog.md`.

## Layout and Interactions

### Split Pane with Resizable Divider

The editor uses a side-by-side layout: textarea on the left, live HTML preview on the right, separated by a draggable divider.

- **Mouse drag** — `onDividerMouseDown` tracks horizontal position relative to container, updates `editorWidthPercent` (clamped between 20% and 80%)
- **Accessibility** — Divider has `role="separator"`, `tabindex="0"`, `aria-orientation="vertical"`, `aria-valuemin="20"`, `aria-valuemax="80"`, `aria-valuenow` reflects current percentage

### Preview Collapse

`togglePreviewCollapsed()` hides the preview pane entirely so the editor takes full width. Saves and restores textarea selection across the toggle.

### Fullscreen Mode

`toggleFullscreen()` uses the browser Fullscreen API on the editor's wrapper element:
- Saves textarea selection before entering/exiting
- Listens to `fullscreenchange` events to restore selection and update `isFullscreen` signal
- The upload image dialog exits fullscreen before opening (re-enters after close)
- All toolbar dropdowns (split buttons, collapsed sections) use `FullscreenOverlayContainer` to remain visible in fullscreen

### Configurable Height

The `height` input accepts any valid CSS value (default: `clamp(260px, 40dvh, 720px)`). Only applies in non-fullscreen mode.

### Card Container

The entire editor is wrapped in a rounded-corner bordered card (`tw-rounded-lg tw-border`). The toolbar has a slightly tinted background. In fullscreen, rounding and border are removed.

## Disabled States

| Condition | What's disabled |
| :-- | :-- |
| `disabled()` input is true | Everything — textarea, all toolbar buttons |
| `showImages()` is true (preview mode) | Textarea, formatting buttons, insert buttons |
| `artifactId()` is empty | Image upload button |
| `isUploading()` is true | Image upload button |

Disabled buttons show explanatory tooltips (per coding standards).

## Patterns

- Uses `signal()` for all local state
- Uses `model.required<string>()` for two-way binding with parent
- Image service calls return `Observable` (not `httpResource`) because uploads are mutations
- Focus monitoring via `FocusMonitor` for keyboard accessibility styling
- Selection tracking on blur to preserve cursor position for toolbar actions
- `takeUntilDestroyed` on all subscriptions for automatic cleanup
- `(mousedown)="$event.preventDefault()"` on dropdown arrow buttons preserves textarea text selection
