---
summary: "Artifact Explorer attribute editor: add, delete, grouped multi-instance, inline delete mode, and the create-child-artifact dialog (defaults, immediate required highlighting, create & add another)"
tags: [web, artifact-explorer, attributes, editor, create]
fileMatch: "**/artifact-explorer/lib/components/editor/attributes-editor-panel/**,**/artifact-operations-context-menu/**,**/create-child-artifact-dialog/**,**/shared/components/attributes-editor/**,**/shared/matchers/**"
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

## Creating artifacts (create-child dialog)

Creating a new artifact from the hierarchy right-click menu uses a **different**
editor than the panel above. The dialog renders `osee-attributes-editor`
(`AttributesEditorComponent` from `@osee/shared/components`) — the in-memory,
not-yet-persisted form editor — whereas the panel uses
`PersistedArtifactAttributeEditorComponent` (auto-save per field). The dialog
collects all values first and creates the artifact in a single transaction.

Component: `CreateChildArtifactDialogComponent`
(`.../artifact-operations-context-menu/dialogs/create-child-artifact-dialog/`).
Opened by `ArtifactOperationsContextMenuComponent.createChildArtifact`.

### Default attribute values

When the user picks an artifact type, the dialog fetches its valid attribute
types and **pre-fills any that have a default value** so the user doesn't retype
common values (e.g. `Extension = "md"` on Markdown types).

**Backend** — `GET /orcs/types/artifact/{artifactTypeId}/attributes`
(`TypesEndpointImpl.getArtifactTypeAttributes`) returns `AttributePojo<?>[]`
where the `value`/`displayableString` carries the default:

- `buildDefaultValuedAttribute(artType, attrToken)` builds each pojo.
- The default comes from `artType.getAttributeDefault(attrType)` — the
  **artifact-type-scoped** default (e.g. Markdown's `.exactlyOne(Extension, "md")`),
  **not** `attrType.getBaseAttributeTypeDefaultValue()`. The base default is
  often `null` (String's base default is null), so the artifact-type default is
  what carries `"md"`. Falls back to empty string when there's no usable default.
- There is **no separate `defaultValue` field** on the wire `attribute` type —
  the seeded `value` is the default.

**Frontend** — in the dialog's `_attributes` stream, a `tap` copies any
non-empty `value` into `this.data.attributes` so seeded defaults are saved even
if the user never edits that field. `AttributesEditorComponent` renders `value`
as the starting input value; any edit emits `(updatedAttributes)` which
overwrites `data.attributes` via `handleUpdatedAttributes`.

> Gotcha: attributes with an empty value are filtered out of the seed so we
> don't submit blank attributes. Only defaulted/edited attributes are sent.

### Immediate required-field highlighting

By default Angular Material only shows a field red after it's touched/dirty
(`ShowOnDirtyErrorStateMatcher`). In the create dialog that hides which of the
many attribute fields (shown after a type is picked) are blocking the disabled
Create button. To fix this, required fields highlight red **immediately on
render**.

- `ImmediateErrorStateMatcher` (`@osee/shared/matchers`) returns
  `control.invalid` regardless of touched/dirty/submitted state.
- `AttributesEditorComponent` exposes `highlightRequiredImmediately`
  (`input<boolean>`, default `false`). When `true`, its `errorMatcher` computed
  swaps in `ImmediateErrorStateMatcher`; otherwise it stays
  `ShowOnDirtyErrorStateMatcher`.
- **Opt-in on purpose.** The default is `false` so the other
  `osee-attributes-editor` consumers (attributes-editor-panel, merge-manager,
  actra-workflow) keep the standard touch-to-highlight behavior. Only the create
  dialog sets `[highlightRequiredImmediately]="true"`.
- The dialog's own name/type fields use the same matcher directly
  (`protected readonly errorMatcher = new ImmediateErrorStateMatcher()`).

### Create & add another

Users typically create many artifacts of the same type under the same parent.
Rather than re-opening the dialog each time, the dialog has two submit buttons:

| Button | Method | Behavior |
|--------|--------|----------|
| **Create** | `createAndClose()` | Emits the create request (`keepOpen: false`) then closes the dialog. |
| **Create & add another** | `createAndAddAnother()` | Emits the create request (`keepOpen: true`), clears **only the name**, and refocuses the name input. Type and attribute values carry over. |

Both use `exact: true` when matched in Playwright (Create is a prefix of
Create & add another).

Design decisions (from user preference):
- **Per-typed name, no auto-numbering.** The user must type a new name for each
  artifact — no `name (1)`, `name (2)` auto-increment.
- **Attributes carry over** on "add another" (not reset to defaults) so shared
  values are entered once.
- **Same type + parent per session.** Different parent or type = re-open the
  dialog. No multi-parent support inside one dialog.
- Chosen over a multi-line "one name per line" textarea — matches the industry
  standard (Django admin, Jira, Salesforce), gives per-item feedback, lower risk.

### Emit-and-subscribe-once pattern (no nested subscribe)

The dialog does **not** run the create transaction itself. It exposes an RxJS
`Subject` and the opener owns a single bounded subscription. This keeps the flow
as one reactive chain instead of a subscribe inside the dialog plus a subscribe
in the opener.

```typescript
// Dialog: a Subject (not Angular output() — output has no .pipe())
readonly create = new Subject<{
  data: createChildArtifactDialogData;
  keepOpen: boolean;
}>();

createAndAddAnother() {
  this.create.next({ data: this.snapshotData(), keepOpen: true });
  this.data.name = '';
  this.nameInput()?.nativeElement.focus();
}
createAndClose() {
  this.create.next({ data: this.snapshotData(), keepOpen: false });
  this.dialogRef.close();
}
// Deep-enough copy so each emitted request is independent of later edits
private snapshotData() {
  return { ...this.data, attributes: this.data.attributes.map((a) => ({ ...a })) };
}
```

```typescript
// Opener (ArtifactOperationsContextMenuComponent.createChildArtifact):
// one chain, one terminal subscribe, bounded by the dialog lifecycle.
this.branchId$
  .pipe(
    take(1),
    switchMap((branchId) => {
      const dialogRef = this.dialog.open(CreateChildArtifactDialogComponent, {
        data: { name: '', artifactTypeId: '0', parentArtifactId: this.artifactId(),
                attributes: [], operationType },
        minWidth: '60%',
      });
      return dialogRef.componentInstance.create.pipe(
        takeUntil(dialogRef.afterClosed()),        // stream ends when dialog closes
        filter(({ data }) => data && data.name !== '' &&
                data.artifactTypeId !== '0' && data.parentArtifactId !== '0'),
        mergeMap(({ data }) => this.createArtifactTransaction(branchId, data))
      );
    })
  )
  .subscribe();
```

Why these operators:
- **`mergeMap`, not `switchMap`**, for the per-create step — `switchMap` would
  cancel an in-flight create if the user clicks "Create & add another" rapidly.
  `mergeMap` lets every create run to completion.
- **`takeUntil(dialogRef.afterClosed())`** bounds the `create` stream so it
  completes when the dialog closes — no leaked open subscription.
- **`take(1)`** on `branchId$` so the outer stream completes after one open.
