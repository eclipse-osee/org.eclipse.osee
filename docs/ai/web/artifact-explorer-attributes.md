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

**Frontend** — the loaded attribute-type tokens (defaults live in each token's
`value`) are the source of the pre-filled values. The dialog keeps them as
signals: `allAttributeTypes` (every valid type) and `visibleAttributes` (the
ones currently in the editor). `AttributesEditorComponent` renders each
attribute's `value` as its starting input value and mutates it in place via
`ngModel`, so `visibleAttributes()` always reflects the latest edits. At submit
time `snapshotData()` builds the payload from `visibleAttributes()`.

> Every visible attribute is submitted — required ones and any the user added —
> even when left at its (possibly empty) default. An added-but-untouched
> attribute is still created, using its seeded default or an empty value when the
> type has no default. `snapshotData()` does **not** filter empties: everything
> shown in the dialog ends up on the created artifact.

### Required-only initial view + add/delete attributes

The create dialog initially shows **only the required attributes** for the
selected type (multiplicity `EXACTLY_ONE`/`AT_LEAST_ONE`), so the user isn't
faced with every optional field up front.

- An `effect()` seeds `visibleAttributes` with the required attributes (each via
  `toSeededAttribute`, which stringifies the token's default `value`) whenever
  the selected type's attributes load.
- An **Add attribute** button opens the shared `AddAttributeDialogComponent`
  (reused from the attributes-editor-panel) with `allAttributeTypes` and the
  current `visibleAttributes` as `existingAttributes`. Chosen types are appended
  to `visibleAttributes` — **with their default `value` seeded**, same as the
  panel's add flow. The button is gated by `hasAddableAttributes()` (mirrors the
  add dialog's multiplicity-based addable filter).
- Optional attributes can be removed inline: the editor's `deleteAttribute`
  output (see below) calls `removeAttribute`, which filters `visibleAttributes`.

**Default on add (panel + dialog).** When an attribute is added — in the
attributes-editor-panel *or* the create dialog — its editor is pre-filled with
the attribute type's default. The panel's `addAttributes` uses
`getSeededDefaultValue(type)` = the token's server-seeded `value` (e.g.
`Extension → "md"`), falling back to a store-type default (`getStoreTypeDefault`:
Boolean→`false`, Integer/Long→`0`, else `""`). Do **not** revert this to a
store-type-only default — the seeded token `value` is the real per-type default.

#### Shared editor opt-in delete

`AttributesEditorComponent` exposes an opt-in delete affordance so the create
dialog can remove optional attributes without affecting the panel/merge-manager/
actra consumers:

- `allowDelete = input<boolean>(false)` — when `true`, deletable attributes
  render a `close` icon-button.
- `deleteAttribute = output<attribute>()` — emits the attribute to remove.
- Same opt-in pattern as `highlightRequiredImmediately`: default off, so the
  three other consumers are unchanged.

**`canDelete()` is per-instance, not per-type.** "Required" is a property of the
attribute *type* (multiplicity `EXACTLY_ONE`/`AT_LEAST_ONE`), but only the
*minimum count* is required. `canDelete` returns `false` for Name and when
`allowDelete` is off; `true` for optional types; and for required types only when
more than one instance of that type is present (`instanceCount > 1`) — so extra
instances of a required type **are** deletable and only the last one is locked.
This mirrors the panel's `isDeletable`. Do not simplify it back to
`!isRequired()` — that wrongly hides the delete button on the 2nd..Nth instance
of a repeatable required type.

**Duplicate-value warning (`isDuplicateValue`).** The backend stores an
attribute type's values as a *set* on create (`setAttributesFromStrings`
de-duplicates via a `LinkedHashSet`), so two same-type instances with the same
value collapse to one — identical duplicates carry no information. This is
intentional, not a bug. To avoid the "my field vanished" surprise, when
`allowDelete` is on the editor shows an inline muted warning under any field that
shares its type+value with another visible instance: "Duplicate value — only one
instance of this value will be saved." It does **not** block submit; distinct
values persist normally (one attribute per distinct value).

#### Grouped layout when `allowDelete` is on

To match the artifact editor's grouping, `AttributesEditorComponent` renders a
**grouped layout** whenever `allowDelete` is `true` (the create dialog):
attributes are grouped by type, and a type with multiple instances shows a
`Name (count)` header above a bordered box containing each instance (with its own
remove button + duplicate warning). Single-instance types render as a plain
field. The default consumers (`allowDelete` off) keep the **flat** one-field-per-
attribute layout unchanged. The store-type field switch is shared between both
layouts via an `<ng-template #attrField>` + `ngTemplateOutlet` so there's no
duplication. Native-content (Input Stream) attributes render only in the flat
layout (the create dialog has no native content).

#### Multiple instances of the same type need unique form-control names

Each rendered field's `[name]` must be unique **per instance**, not just per
type. The editor keys names as `'attr<StoreType>' + attribute.typeId + $index`
(via `let attrIndex = $index` on the `@for`). If the name is keyed only by
`typeId`, two instances of the same type register the same template-driven form
control, Angular collapses them, and the second instance's `ngModel` never binds
— its value is silently lost. Keep the `$index` suffix on every `[name]`.

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
// Builds the payload from the visible attributes (editor mutates their value
// in place). Includes ALL visible attributes, even empty ones, so an
// added-but-untouched attribute is still created with its default/empty value.
// Deep-copied so each emitted request is independent of later edits.
private snapshotData() {
  const attributes = this.visibleAttributes()
    .map((attr) => ({ ...attr, value: `${attr.value ?? ''}` }));
  return { ...this.data, attributes };
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

### Multiple attribute instances: group by type into an array value

When building the create transaction, the opener groups attributes by `typeId`
(`groupAttributesByType`): a type with one instance emits a scalar
`{ typeId, value }`, but a type with **multiple** instances emits a single
`{ typeId, value: [v1, v2, …] }` with an **array** value.

This is required because of how the backend applies create attributes. The ORCS
create path (`TransactionBuilderDataFactory.readAttributes`) applies each scalar
attribute node via `setSoleAttributeFromString` — "sole" meaning one instance
per type — so **repeated scalar nodes of the same type overwrite each other
(last value wins)**. Only an **array** value routes to
`setAttributesFromStrings`, which creates one attribute instance per element.

> Do **not** emit repeated scalar `{ typeId, value }` nodes for the same type in
> a create transaction — only the last survives. Group them into one array-valued
> node. (This is a frontend workaround for the sole-set behavior; the backend
> `readAttributes` create branch was not changed.)

Note that `setAttributesFromStrings` de-duplicates the array by value
(`LinkedHashSet`), so the array creates one instance **per distinct value** —
identical values still collapse to one. This is the same intentional set-semantics
behind the duplicate-value warning above; the grouping only preserves *distinct*
multi-instance values.

### E2E persistence verification pattern

The Playwright coverage (`create-delete.e2e-spec.ts`) verifies these behaviors
**end-to-end against the running backend**, not just in the dialog:

- Capture the create transaction response (`res.url().includes('orcs/txs')`),
  read the new artifact id from `results.ids[0]`, then GET
  `/orcs/branch/{branchId}/artifact/{id}/related/direct?viewId=-1&includeRelations=false&includeAttributes=true`
  (the JSON endpoint — the plain `/artifact/{id}` path returns HTML) and assert
  on `artifact.attributes` (`[{ typeId, value, … }]`).
- Covered cases: default value persists, added-but-untouched attribute persists,
  values carry across "Create & add another", and multiple **distinct** instances
  of a repeatable type persist.

> Enum (`osee-attribute-enums-dropdown`) writes its selected value back to the
> model on an `auditTime(500)` delay. When a test selects an enum value and then
> immediately submits, wait past that window first (e.g. `waitForTimeout(700)`)
> or the value won't be in the payload. This is real component latency, not just
> a test artifact.
