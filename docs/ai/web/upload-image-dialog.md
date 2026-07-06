---
summary: "Upload image dialog with file selection, caption input, caption position toggle, and publish size selector"
tags: [web, angular, dialog, images, upload]
fileMatch: "**/upload-image-dialog*"
---

# Upload Image Dialog

## Overview

The upload image dialog (`UploadImageDialogComponent`) provides a UI for selecting an image file, configuring its caption, and choosing a publish size before uploading to OSEE as an Image artifact.

## Key Files

| File | Purpose |
| :-- | :-- |
| `upload-image-dialog.component.ts` | Component logic, types (`ImageSize`, `UploadImageDialogData`, `UploadImageDialogResult`) |
| `upload-image-dialog.component.html` | Template with drag-and-drop, preview, caption, size selector |

## Dialog Flow

1. **File selection** — Drag-and-drop area or file picker. Only supported image formats are accepted (see `SUPPORTED_IMAGE_FORMATS_LABEL`).
2. **Preview** — Once a file is selected, an image preview is shown with the filename.
3. **Caption** — Optional text input for a `<figure-caption>` tag. Position toggle (above/below) determines placement relative to the image.
4. **Publish Size** — Button toggle group with options: Auto, XS, S, M, L. Defaults to Auto (no size attribute).
5. **Submit** — Returns `UploadImageDialogResult` with file, caption, captionPosition, and size.

## Types

```typescript
export type ImageSize = 'xs' | 's' | 'm' | 'l';

export type UploadImageDialogData = {
  readonly artifactId: string;
};

export type UploadImageDialogResult = {
  readonly file: File;
  readonly caption: string;
  readonly captionPosition: 'above' | 'below';
  readonly size: ImageSize | null;
};
```

## Size Selector

The publish size determines how large the image renders in published documents:

| Value | % of max width | Effect |
| :-- | :-- | :-- |
| Auto (null) | 100% | Default behavior — image scales to max allowed width, capped by native resolution |
| `xs` | 25% | Quarter width |
| `s` | 50% | Half width |
| `m` | 75% | Three-quarter width |
| `l` | 100% | Full max width (same as auto, explicit) |

When a size is selected, the markdown editor inserts `<image-link size="m">ID</image-link>` which the publishing pipeline converts to an `<img>` tag with `style="max-width:75%;height:auto"`.

## UX Notes

- The size selector is only visible after a file is selected
- "Auto" is pre-selected — no action needed for default behavior
- Caption label is "Figure Caption" (no "(optional)" — the empty field and enabled Upload button communicate optionality)
- Dialog cannot be closed by backdrop click (`disableClose: true`)
- Preview object URL is revoked on destroy or file removal to prevent memory leaks
