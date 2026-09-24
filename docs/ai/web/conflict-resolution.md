---
---
summary: "Reusable attribute conflict-resolution for SSE-enabled editors: detect remote-change-while-dirty, resolve via dialog, apply + refresh"
tags: [web, conflict-resolution, sse, editors, attributes]
fileMatch: "**/conflict-resolution/**,**/attribute-conflict**,**/conflict-resolution-banner**"
---

# Attribute Conflict Resolution

Shared, reusable conflict resolution for any SSE-enabled editor. When another user
changes an entity while the current user has unsaved edits, the editor shows a
banner and lets the user resolve the divergence per attribute (take yours / take
server's / take both / manual / re-add a deleted attribute) instead of blindly
overwriting.

Lives in `@osee/shared/conflict-resolution`. The artifact editor and the ACTRA
workflow editor both use it.

## Architecture

The feature is split into small, single-responsibility pieces so the reusable
parts are pure and testable and the page supplies only what is page-specific.

| Piece | Kind | Responsibility |
|---|---|---|
| `categorizeConflicts` | pure fn | base + server + pending -> `{ conflicts, autoSaveAttrs }`. Models the server-deleted case. |
| `mapResolutionsToOperations` | pure fn | `resolvedConflict[]` -> `{ set, add }` attribute operations. |
| `EditorDirtyService` | root service | tracks which editors are dirty, keyed `${entityId}-${attributeId}`. |
| `PendingAttributeValuesService` | component service | per-editor map of unsaved values (for editors that track edits continuously). |
| `AttributeConflictResolutionDialogComponent` | component | resolution dialog: interactive card per true conflict + read-only "Your Other Changes" for non-conflicting edits. |
| `ConflictResolutionBannerComponent` | component | presentation-only banner (message + resolve/discard). Page owns placement/stickiness. |
| `ConflictResolutionService` | root service | orchestrates fetch -> categorize -> dialog -> apply -> commit -> **refresh**. |
| `ConflictController` | per-editor object | bundles detection (`conflicted`/`resolving` signals) + `resolve()`/`discard()`. **Preferred entry point.** |

### The refresh guarantee

After any committed resolution (and on the "nothing to persist" path), the service
calls the page's `refresh()`. This is required, not optional: an editor suppresses
its own local SSE echo while dirty (to protect in-progress edits), so the echo
cannot refresh the acting view. Without the guaranteed refresh, the acting tab
would show stale local state after resolving (e.g. a manual value would appear for
other users but not for the user who entered it).

## Presentation semantics (colors + what the dialog shows)

Keep these consistent across pages so the affordances don't mislead:

- **Field ring = amber (caution), not red.** A dirty field shows an amber ring
  (`tw-ring-osee-yellow-10 dark:tw-ring-osee-amber-9`) while a remote change is
  pending, meaning "unsaved edit, blocked until you resolve." It does **not** claim
  this specific field conflicts: whether a field truly conflicts is only known once
  the dialog fetches server state. Reserve red for the confirmed conflicts shown in
  the dialog.
- **The dialog shows every changed field, not just conflicts.** True conflicts are
  interactive cards; non-conflicting edits (server untouched, from
  `categorizeConflicts`' `autoSaveAttrs`) are listed read-only under "Your Other
  Changes" so the count the user sees matches the fields flagged in the editor.
  This avoids the "I had N rings but the dialog shows fewer -- did I lose changes?"
  confusion. No extra server request: `categorizeConflicts` already returns both
  buckets from the single on-demand fetch.
- **An aggregate "must resolve" control may be red.** A whole-entity blocked
  control (e.g. the workflow's disabled Save button) is correctly red -- it is a
  single "there is a conflict to resolve" signal, not a per-field claim. To show a
  color through Material's disabled state, set the token
  (`[--mdc-icon-button-disabled-icon-color:var(--osee-red-28)]`), since Material's
  disabled color otherwise overrides text color.

## Adopting it on a new SSE page (recipe)

Use `ConflictResolutionService.controller(config, destroyRef)`. Wire it once and
bind its signals in the template.

```ts
private readonly conflictResolution = inject(ConflictResolutionService);
private readonly destroyRef = inject(DestroyRef);

protected readonly conflict = this.conflictResolution.controller(
  {
    // 1. Detection: per-entity SSE change stream + a dirty predicate.
    changes: this.changeNotification.forArtifact(branchId, artifactId),
    hasUnsavedChanges: () => this.hasChanges(),

    // 2. What to compare.
    entityName: () => this.entity().name,
    entityId: () => `${this.entity().id}`,
    baseAttrs: () => this.entity().attributes,
    fetchServerAttrs: () => this.fetchLatest().pipe(map((e) => e.attributes)),
    pendingValues: () => this.buildPendingMap(),
    keyOptions: { keyOf: (a) => a.id }, // default; see key contract below

    // 3. How to persist + refresh.
    commit: (ops) => this.persist(ops),      // return the mutation observable
    refresh: () => this.reloadEntity(),       // reload the view from server
    clearLocalState: () => this.clearEdits(), // clear pending/dirty
    onError: (msg) => (this.uiService.ErrorText = msg),
  },
  this.destroyRef
);
```

Template (banner placement/stickiness is the page's responsibility):

```html
@if (conflict.conflicted()) {
  <osee-conflict-resolution-banner
    message="Another user changed this while you were editing. Resolve the differences to continue."
    [resolving]="conflict.resolving()"
    (resolve)="conflict.resolve()"
    (discard)="conflict.discard()" />
}
```

Disable the page's Save while `conflict.conflicted()` is true so resolution drives
the merge rather than a blind overwrite.

## The pending-key contract (read this)

`pendingValues` keys, `baseAttrs`, and `serverAttrs` must all match under the same
`keyOptions.keyOf`. Two valid strategies:

- **Instance id (default).** Use when the editor tracks live, persisted attribute
  instances (each edit maps to a real `attribute.id`). This is the artifact editor.
- **`typeId`** (`keyOptions: { keyOf: (a) => a.typeId }`). Use when edits can
  originate from type definitions with a placeholder id (`-1`) and the save path is
  typeId/value based. This is the workflow editor. Keying by instance id here would
  silently drop conflicts on not-yet-persisted attributes.

If keys do not match `keyOf`, `categorizeConflicts` finds no matching base/server
attr and skips the edit -- the conflict goes undetected. Pick the strategy that
matches how your edits are keyed.

## DI shapes

- `ConflictResolutionService` and `EditorDirtyService` are `providedIn: 'root'`
  (app-wide singletons). Inject them directly.
- `PendingAttributeValuesService` is **not** root-provided; add it to the editor
  component's `providers` so each open editor has its own pending-value map. Only
  needed if you track pending values continuously (instance-id strategy). Editors
  that reconstruct the pending map at resolve time (typeId strategy) don't need it.

## When to use `resolve()` directly instead of the controller

`ConflictResolutionService.resolve(config)` is the lower-level entry point that runs
only the resolution flow (no detection). Use it when the page already owns a
multi-purpose SSE subscription and its own conflict flag -- the **artifact editor**
does this, because its parent component's SSE handler also closes tabs on delete,
guards the shared-resource reload, and drives the history panel. Forcing that page
onto the controller would duplicate its subscription. For a fresh page, prefer the
controller.

## Multi-instance caveat

`take-both` produces an `add` operation (a new attribute instance). A save path that
is set-by-typeId only (e.g. the workflow's legacy transaction) cannot create a
distinct instance, so `take-both` degrades to a single value there. The dialog only
offers `take-both` for multi-valued attribute types, which such forms typically do
not use. If your page needs true multi-instance semantics, ensure your `commit`
honors `ops.add` as new instances.
