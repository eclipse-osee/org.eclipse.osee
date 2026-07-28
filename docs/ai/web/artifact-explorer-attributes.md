---
summary: "Artifact Explorer attribute editor: add, delete, grouped multi-instance, and inline delete mode"
tags: [web, artifact-explorer, attributes, editor]
fileMatch: "**/artifact-explorer/lib/components/editor/attributes-editor-panel/**"
---

# Artifact Explorer — Attribute Editor

## Overview

The attributes editor panel (`osee-attributes-editor-panel`) displays and edits attributes on an artifact within the Artifact Explorer. It supports:

- Inline auto-save editing via `PersistedArtifactAttributeEditorComponent`
- Adding new attribute instances via a dialog
- Deleting attribute instances via an inline delete mode toggle
- Grouped display for multi-instance attribute types

## Component architecture

```
ArtifactEditorComponent (artifact-editor)
├── Toolbar: section tabs + add/delete buttons (sticky)
└── AttributesEditorPanelComponent (attributes-editor-panel)
    ├── Native content editor (conditional)
    ├── Name attribute (always first)
    ├── Applicability dropdown (PLE branches only)
    └── Grouped attributes (via groupedAttrs computed)
        ├── Single-instance: rendered directly with PersistedArtifactAttributeEditorComponent
        └── Multi-instance: AttributeGroupComponent
            ├── Type header with count
            ├── Collapsible list (first 5, "show more")
            ├── Per-instance delete buttons (in delete mode)
            └── PersistedArtifactAttributeEditorComponent per instance
```

## Adding attributes

- **Toolbar button**: Green `add_circle` icon in the sticky header (visible when attributes section is active and artifact is editable).
- **Dialog**: `AddAttributeDialogComponent` fetches valid attribute types from `/orcs/types/artifact/{typeId}/attributes` and shows types where the existing instance count is below the multiplicity maximum.
- **Multiplicity enforcement**:
  - EXACTLY_ONE (id=2) / ZERO_OR_ONE (id=3): only shows if 0 instances exist
  - ANY (id=1) / AT_LEAST_ONE (id=4): always shows, with quantity input (1–50)
- **Transaction**: Uses `CurrentTransactionService.modifyArtifactAndMutate` with `{ add: newAttributes }` where each new attribute has `id: '-1'`, `gammaId: '-1'`.

## Deleting attributes

- **Toolbar button**: `delete` / `delete_sweep` icon toggles delete mode on/off.
- **Delete mode**: Shows `remove_circle_outline` icons next to every attribute:
  - **Deletable**: Red icon, clickable — immediately deletes via transaction using `deleteByAttributeId`
  - **Non-deletable**: Grey disabled icon with tooltip explaining why (Name, applicability, or at minimum multiplicity count)
- **Transaction**: Uses `modifyArtifactAndMutate` with `{ delete: [attr] }`. The operator maps this to `deleteAttributes: [{id: attrInstanceId}]` in the JSON payload, which the server processes via `deleteByAttributeId`.

## Multi-instance grouping

When multiple attributes of the same type exist, they are rendered by `AttributeGroupComponent`:

- The type name is shown as a label above the bordered container, with instance count: "Type Name (N)"
- Instances are grouped in a bordered container using `tw-border-osee-neutral-50 dark:tw-border-osee-neutral-60`
- Individual editors use `[showLabel]="false"` (label omitted since the header provides context)
- Text inputs show placeholder "Enter value..." when empty
- The `@for` loop tracks by `gammaId` for stable DOM updates
- Sort order: `typeId` then `id` (attribute instance ID — immutable across edits)
- In delete mode, the border container removes right padding (`tw-pr-0`) so × buttons align with standalone attributes
- **Collapse behavior**: Groups with more than 5 instances show only the first 5, with a "Show N more..." link to expand. "Show less" collapses back.
- The component encapsulates its own expand/collapse state, multiplicity checks for deletability, and emits `deleteAttribute` events to the parent panel.

## Key signals and computeds

| Signal/Computed | Purpose |
|---|---|
| `attributes` | All attributes sorted by typeId then id, with last-known-good caching |
| `groupedAttrs` | Attributes grouped by typeId into `{name, attrs[]}` entries |
| `otherAttrs` | Attributes minus Name, Native Content, and Extension |
| `deleteMode` | Input from parent — controls visibility of delete icons |

## Server endpoints used

- `GET /orcs/types/artifact/{artifactTypeId}/attributes` — returns all valid attribute types with multiplicity
- `POST /orcs/txs` — transaction endpoint for add/delete/modify operations

## PersistedArtifactAttributeEditorComponent inputs

| Input | Type | Default | Purpose |
|---|---|---|---|
| `attr` | `attribute` | required | The attribute to edit |
| `artifactId` | `` `${number}` `` | required | Owning artifact ID |
| `artifactApplicability` | `applic` | required | Artifact's applicability |
| `disabled` | `boolean` | `false` | Disables editing |
| `showLabel` | `boolean` | `true` | Controls mat-label visibility (false in grouped mode) |
