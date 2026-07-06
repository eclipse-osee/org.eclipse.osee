---
summary: "Markdown editor component with image upload, table editing, preview, fullscreen, and undo/redo"
tags: [web, angular, markdown, editor, images, preview]
fileMatch: "**/markdown-editor*,**/markdown-image*"
---

# Markdown Editor

## Overview

The markdown editor (`MarkdownEditorComponent`) is an Angular component providing a rich editing experience for markdown content stored in OSEE artifacts. It features:

- Split-pane editing with live HTML preview
- Image upload (via dialog or drag-and-drop) with size selection
- Table creation and editing (via `MarkdownTableDialogComponent`)
- Image preview mode (renders `<image-link>` tags as actual images)
- Fullscreen mode with resizable divider
- Undo/redo with history tracking

## Key Files

| File | Purpose |
| :-- | :-- |
| `markdown-editor.component.ts` | Main component logic |
| `markdown-editor.component.html` | Template with toolbar, textarea, preview pane |
| `markdown-image.service.ts` | HTTP service for uploading and fetching images |

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
4. `insertImageLink()` inserts the `<image-link>` tag (with optional `size` attribute) at cursor position

### Preview Mode

`toggleShowImages()` fetches image object URLs from the server and replaces `<image-link>` tags with `<img>` elements (with `max-width` style if sized) for visual preview. The editor textarea is disabled during preview.

### Drag-and-Drop

Files dragged onto the editor are validated against supported formats and uploaded directly (bypasses the dialog — no caption or size selection for drag-drop).

## Table Integration

The editor detects tables at the cursor position via `parseTableAtSelection()` and opens `MarkdownTableDialogComponent` in create or edit mode accordingly. See `docs/ai/web/markdown-table-dialog.md`.

## Toolbar Buttons

| Icon | Action |
| :-- | :-- |
| `undo` / `redo` | Undo/redo content changes |
| `lightbulb` | Examples menu (insert markdown snippets) |
| `image` | Open upload image dialog |
| `table_chart` | Open table dialog (create or edit) |
| `select_all` | Select table at cursor |
| `visibility` / `edit` | Toggle image preview mode |
| `fullscreen` / `fullscreen_exit` | Toggle fullscreen |
| `view_sidebar` / `vertical_split` | Toggle preview pane |

## Layout and Interactions

### Split Pane with Resizable Divider

The editor uses a side-by-side layout: textarea on the left, live HTML preview on the right, separated by a draggable divider.

- **Mouse drag** — `onDividerMouseDown` tracks horizontal position relative to container, updates `editorWidthPercent` (clamped between 20% and 80%)
- **Keyboard** — Divider has `role="separator"`, `tabindex="0"`, and responds to ArrowLeft/ArrowRight (2% step per keypress)
- **Accessibility** — `aria-orientation="vertical"`, `aria-valuemin="20"`, `aria-valuemax="80"`, `aria-valuenow` reflects current percentage

### Preview Collapse

`togglePreviewCollapsed()` hides the preview pane entirely so the editor takes full width. Toggled via the `view_sidebar` / `vertical_split` button. Saves and restores textarea selection across the toggle.

### Fullscreen Mode

`toggleFullscreen()` uses the browser Fullscreen API on the editor's wrapper element:
- Saves textarea selection before entering/exiting (selection is lost when DOM restructures for fullscreen)
- Listens to `fullscreenchange` events to restore selection and update `isFullscreen` signal
- Error handling: shows snackbar if fullscreen is unavailable (e.g., iframe context)
- The upload image dialog exits fullscreen before opening (re-enters after close)

### Configurable Height

The `height` input accepts any valid CSS value (default: `clamp(260px, 40dvh, 720px)`). Only applies in non-fullscreen mode. Uses `dvh` units for responsive viewport adaptation.

## Disabled States

| Condition | What's disabled |
| :-- | :-- |
| `disabled()` input is true | Everything — textarea, all toolbar buttons |
| `showImages()` is true (preview mode) | Textarea, image upload, table buttons, select-table |
| `artifactId()` is empty | Image upload button (no artifact to attach image to) |
| `isUploading()` is true | Image upload button (prevents double upload) |

Disabled buttons show explanatory tooltips (per coding standards).

## Patterns

- Uses `signal()` for all local state
- Uses `model.required<string>()` for two-way binding with parent
- Image service calls return `Observable` (not `httpResource`) because uploads are mutations
- Focus monitoring via `FocusMonitor` for keyboard accessibility styling
- Selection tracking on blur to preserve cursor position for toolbar actions
- `takeUntilDestroyed` on all subscriptions for automatic cleanup
