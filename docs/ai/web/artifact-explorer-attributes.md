---
summary: "Artifact Explorer attribute editor: add, delete, grouped multi-instance (shared AttributeFieldGroupComponent), inline delete mode, and the create-child-artifact dialog (defaults, red required marker, create & add another, multi-instance persistence)"
tags: [web, artifact-explorer, attributes, editor, create]
fileMatch: "**/artifact-explorer/lib/components/editor/attributes-editor-panel/**,**/artifact-operations-context-menu/**,**/create-child-artifact-dialog/**,**/shared/components/attributes-editor/**,**/shared/matchers/**,**/attributes/constants/multiplicity-rules*"
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

- **Toolbar button**: `add_circle_outline` icon in the sticky toolbar (the shared
  `osee-attribute-toolbar`, visible when the attributes section is active and the
  artifact is editable).
- **Dialog**: `AddAttributeDialogComponent` fetches valid attribute types from `/orcs/types/artifact/{typeId}/attributes` and shows types where the existing instance count is below the multiplicity maximum.
- **Multiplicity enforcement**:
  - EXACTLY_ONE (id=2) / ZERO_OR_ONE (id=3): only shows if 0 instances exist
  - ANY (id=1) / AT_LEAST_ONE (id=4): always shows, with quantity input (1–50)
- **Transaction**: Uses `CurrentTransactionService.modifyArtifactAndMutate` with `{ add: newAttributes }` where each new attribute has `id: '-1'`, `gammaId: '-1'`.

## Deleting attributes

- **Toolbar button**: `delete_outline` / `delete_sweep` icon toggles delete mode
  on/off (part of the shared `AttributeToolbarComponent`, see below).
- **Delete mode**: Shows the shared `AttributeDeleteButtonComponent`
  (`osee-attribute-delete-button`) next to every attribute:
  - **Deletable**: red `remove_circle_outline` icon, tooltip "Delete Attribute
    Instance", emits `delete` — the parent deletes via transaction.
  - **Non-deletable**: greyed disabled icon wrapped in a tooltip span explaining
    why (Name, applicability, or at the minimum multiplicity count).
- **Transaction**: Uses `modifyArtifactAndMutate` with `{ delete: [attr] }`. The operator maps this to `deleteAttributes: [{id: attrInstanceId}]` in the JSON payload, which the server processes via `deleteByAttributeId`.

## Shared presentational controls (delete button + toolbar)

The add/delete UX is shared between the artifact editor and the create dialog so
it stays consistent and isn't maintained twice. Two presentational components
live in `shared/components/attributes-editor/` (exported from
`@osee/shared/components`):

- **`AttributeDeleteButtonComponent`** (`osee-attribute-delete-button`) — renders
  the per-instance delete control. Inputs: `deletable` (enabled warning button
  vs. disabled-in-tooltip-span variant), `ariaLabel`, `disabledReason`,
  `disabledAriaLabel`. Output: `delete`. It holds no deletion logic and does no
  persistence — the parent wires `delete` to its own memory mutation (create
  dialog) or transaction (artifact editor). Used at every per-instance delete
  site: the panel (Name/Applicability disabled variants + single-instance),
  `AttributeGroupComponent`, and the shared `AttributesEditorComponent` (single +
  grouped).
- **`AttributeToolbarComponent`** (`osee-attribute-toolbar`) — the add button
  (`add_circle_outline`, gated by `showAdd`) + delete-mode toggle
  (`delete_outline` ↔ `delete_sweep`, warning when active). Outputs: `add`,
  `toggleDeleteMode`. Optional `addHelpAnchor` / `deleteHelpAnchor` string inputs
  apply the `oseeHelpAnchor` directive (default `''` = no-op, so consumers
  without the help drawer, like the create dialog, ignore them). Used by both the
  artifact editor toolbar and the create dialog title bar.

Do **not** re-inline this markup — extend the shared components instead.

## Shared multiplicity rules (delete eligibility)

The rule for whether an attribute instance may be deleted is a pure function
shared by all three sites (create dialog, panel, group) so it can't drift.
`attributes/constants/multiplicity-rules.ts` (exported from
`@osee/attributes/constants`):

- `isRequiredMultiplicity(attr)` — true for `EXACTLY_ONE` / `AT_LEAST_ONE`.
- `isAttributeInstanceDeletable(attr, allAttributes)` — Name is never deletable;
  optional types (`ANY` / `ZERO_OR_ONE`) always are; required types only when
  more than one instance of that type exists (the minimum count must remain).

`AttributesEditorComponent.canDelete` (gated by `allowDelete()`),
`AttributesEditorPanelComponent.isDeletable` / `deleteInlineAttribute`, and
`AttributeGroupComponent.isDeletable` all delegate to this util. Do not
reimplement the rule inline.

## Multi-instance grouping

When multiple attributes of the same type exist, they are rendered by
`AttributeGroupComponent`, which wraps them in the shared
`AttributeFieldGroupComponent` (header + bordered box) and projects a persisted
editor per instance:

- The header shows the type name + instance count ("Type Name (N)") — provided by
  the shared shell, not inline markup.
- Individual editors use `[showLabel]="false"` (label omitted since the header provides context)
- Text inputs show placeholder "Enter value..." when empty
- The `@for` loop tracks by `attr.id` (the instance ID — immutable across edits)
- Sort order: `typeId` then `id`
- Per-instance delete uses the shared `osee-attribute-delete-button` (shown in
  delete mode); `isDeletable` delegates to the shared multiplicity rule.
- **Collapse behavior**: Groups with more than 5 instances show only the first 5, with a "Show N more..." link to expand. "Show less" collapses back.
- The component encapsulates its own expand/collapse state and emits
  `deleteAttribute` events to the parent panel.

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
- The dialog **title bar** (`h1 mat-dialog-title`) holds the shared
  `osee-attribute-toolbar` (add icon button + delete-mode toggle) — the same
  component the artifact editor uses. There is no separate sticky Name/Type
  header; Name and Type are ordinary fields in the dialog content, and the
  attribute editors are listed below them.
- The add button opens the shared `AddAttributeDialogComponent` (reused from the
  attributes-editor-panel) with `allAttributeTypes` and the current
  `visibleAttributes` as `existingAttributes`. Chosen types are appended to
  `visibleAttributes` — **with their default `value` seeded**, same as the
  panel's add flow. The toolbar's `showAdd` is bound to `hasAddableAttributes()`.
- The toolbar's `toggleDeleteMode` drives a `deleteMode` signal, bound to the
  editor's `[allowDelete]`. Only in delete mode do removable attributes show a
  delete control; the editor's `deleteAttribute` output calls `removeAttribute`,
  which filters `visibleAttributes`.

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
  render the shared `osee-attribute-delete-button`. The create dialog binds this
  to its `deleteMode` toggle so deletes only appear in delete mode.
- `deleteAttribute = output<attribute>()` — emits the attribute to remove.
- Opt-in (default off) so the three other consumers are unchanged.

**`canDelete()` is per-instance, not per-type.** It gates on `allowDelete()` then
delegates to the shared `isAttributeInstanceDeletable(attr, this.attributes())`
(see "Shared multiplicity rules" above): `false` for Name; `true` for optional
types; and for required types only when more than one instance of that type is
present — so extra instances of a required type **are** deletable and only the
last one is locked. Do not simplify it back to `!isRequired()` — that wrongly
hides the delete button on the 2nd..Nth instance of a repeatable required type.

#### Grouped layout — shared `AttributeFieldGroupComponent`

The `Name (count)` header + bordered box shell is a single shared component,
`AttributeFieldGroupComponent` (`@osee/shared/components`), used by **both** the
artifact editor's panel and the create dialog (via the shared editor). It's a
presentational wrapper: `name` + `count` inputs and an `<ng-content>` slot, so
each consumer projects its own instance editors (the panel projects persisted
per-field editors + its collapse toggle; the shared editor projects in-memory
fields). Do not re-inline this grouping — extend the shared component instead.

Grouping is controlled by its **own** input, `groupByType = input<boolean>(false)`
— **independent of `allowDelete`**. When `groupByType` is `true` (the create
dialog sets `[groupByType]="true"`), the shared editor renders the **grouped
layout**: attributes grouped by type; a type with multiple instances uses
`AttributeFieldGroupComponent`; single-instance types render as a plain field.
The default consumers (`groupByType` off) keep the **flat** one-field-per-
attribute layout. The store-type field switch is shared between both layouts via
an `<ng-template #attrField>` + `ngTemplateOutlet`. Native-content (Input Stream)
attributes render only in the flat layout (the create dialog has no native
content).

> Keep grouping (`groupByType`) separate from the delete affordance
> (`allowDelete`). They were coupled once (grouping gated on `allowDelete`),
> which meant turning delete mode off in the create dialog silently disabled
> multi-instance grouping. Delete buttons inside the grouped layout are still
> gated by `allowDelete()` via `canDelete()`.

#### Multiple instances of the same type need unique form-control names

Each rendered field's `[name]` must be unique **per instance**, not just per
type. The editor keys names as `'attr<StoreType>' + attribute.typeId + $index`
(via `let attrIndex = $index` on the `@for`). If the name is keyed only by
`typeId`, two instances of the same type register the same template-driven form
control, Angular collapses them, and the second instance's `ngModel` never binds
— its value is silently lost. Keep the `$index` suffix on every `[name]`.

### Required-field highlighting

The create dialog uses **Angular Material's default** error behavior — a field
turns red only after it's touched/dirty (`ShowOnDirtyErrorStateMatcher`). No
`[errorStateMatcher]` override is set, and the editor is **not** given
`highlightRequiredImmediately`. (An earlier immediate-highlight-on-render variant
was removed in favor of default behavior; `ImmediateErrorStateMatcher` still
exists in `@osee/shared/matchers` and the editor still exposes
`highlightRequiredImmediately` for other potential consumers, but the create
dialog no longer opts in.)

The required-field **asterisk is red** via the app-wide pattern (see
`web-coding-standards.md` → "Required field marker"): each `mat-form-field` sets
`hideRequiredMarker` and renders a red `*` (`tw-text-warning`) inside its
`<mat-label>`; the enum dropdown applies the same internally. There is **no**
`--mat-form-field-required-marker-color` token (it doesn't exist in Material 21).

A required-field **legend lives in the dialog title bar**: when the form is
invalid it shows the red "Required fields (\*) are not all filled out"; when
valid it shows the informational "\* indicates a required field". The Create /
Create & add another buttons stay disabled until the form is valid.

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

### Multiple attribute instances: create, then add the extras

The create transaction sends **separate `{ typeId, value }` objects per
instance** — not an array value. But the two ORCS paths behave differently:

- `createArtifacts[].attributes` → `readAttributes` → `setSoleAttributeFromString`
  (one instance per type; repeated same-type nodes overwrite each other).
- `modifyArtifacts[].addAttributes` → `createAttributeFromString` per node
  (**no dedup** — every node becomes a new attribute instance, even identical
  values). This is the same path the artifact editor's "Add attribute" uses.

So the opener (`createArtifactTransaction`) does a **two-step** transaction via
`splitAttributeInstances`:

1. Create the artifact with the **first** instance of each type
   (`firstPerType`, scalar nodes under `createArtifacts[].attributes`).
2. If any type had extra instances (`extras`), read the new artifact id from the
   create response (`result.results.ids[0]`) and fire a second `performMutation`
   with `modifyArtifacts: [{ id, addAttributes: extras }]` — the non-deduping add
   path — so all instances persist, including identical values.

> Do **not** try to create multiple same-type instances through
> `createArtifacts[].attributes` (array or repeated scalar) — the create path
> collapses them. Use the follow-up `addAttributes` modify for the extras.

### E2E persistence verification pattern

The Playwright coverage (`create-delete.e2e-spec.ts`) verifies these behaviors
**end-to-end against the running backend**, not just in the dialog. Two
verification styles, depending on the case:

- **Single-instance values** (default persists, added-but-untouched persists,
  values carry across "Create & add another"): capture the create response
  (`res.url().includes('orcs/txs')`), read the new id from `results.ids[0]`, GET
  `/orcs/branch/{branchId}/artifact/{id}/related/direct?viewId=-1&includeRelations=false&includeAttributes=true`
  (the JSON endpoint — the plain `/artifact/{id}` path returns HTML) and assert
  on `artifact.attributes`.
- **Multiple instances of the same type** (distinct or identical values): verify
  by **opening the created artifact in the artifact editor** and asserting the
  grouped `attribute-group-header` shows the right count (e.g. "(2)"/"(3)") with
  the expected values. Do **not** verify multi-instance counts via
  `related/direct` — that JSON endpoint does **not** reliably return every
  instance of the same attribute type (it collapses them), so counting from it
  under-reports. The editor loads all instances and is the accurate check. Open
  the new child from the hierarchy tree (it appears under the expanded parent);
  the `searchAndOpenArtifact` helper is flakier mid-suite.

> Enum (`osee-attribute-enums-dropdown`) writes its selected value back to the
> model on an `auditTime(500)` delay. When a test selects an enum value and then
> immediately submits, wait past that window first (e.g. `waitForTimeout(1000)`)
> or the value won't be in the payload. This is real component latency, not just
> a test artifact. The same-value multi-instance test sidesteps this entirely by
> using a type seeded at its default (Qualification Method = Unspecified) and
> adding more instances without any enum selection.
