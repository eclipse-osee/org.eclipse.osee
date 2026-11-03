---
summary: "CurrentTransactionService patterns for building and executing transactions with gamma support"
tags: [web, transactions, services, gamma]
fileMatch: "**/transactions/**"
---

# Transaction Service

The web app uses `CurrentTransactionService` (from `@osee/transactions/services`) to build and execute transactions against the `/orcs/txs` endpoint. Transactions carry gamma IDs for optimistic concurrency.

## Key concepts

- **Transaction**: A JSON payload describing artifact creates, modifies, and deletes sent as a single atomic operation.
- **Gamma ID**: A version identifier on each attribute instance. The server uses it to detect conflicts (stale writes).
- **Attribute ID**: The unique identifier of a specific attribute instance. Required when modifying multi-instance attributes.

## Service API

### `CurrentTransactionService`

| Method | Purpose |
|--------|---------|
| `createTransaction(comment)` | Creates a `Required<transaction>` synchronously. Branch resolved via `resolveBranch()` (see Branch targeting below). |
| `createTx(comment, branchIdOverride?)` | Creates a `Required<transaction>` as an Observable. Branch resolved via `resolveBranch()` (override → context → global). |
| `modifyArtifactAndMutate(comment, artId, applicability, attrConfig, branchIdOverride?)` | Builds a modify transaction and immediately executes it. |
| `modifyArt(comment, artId, applicability, attrConfig, branchIdOverride?)` | Builds a modify transaction without executing — returns `Observable<Required<transaction>>`. |
| `performMutation()` | RxJS pipe operator that sends the transaction to the server, sets `uiService.updated = true` for backward compat. Local change notification is handled automatically by `TransactionService`. |

## Branch targeting (which branch a transaction writes to)

Every transaction needs a branch. `CurrentTransactionService.resolveBranch()` picks it
in this precedence order:

1. **Explicit `branchIdOverride` argument** (when valid, i.e. not the branch sentinel).
2. **The subtree's pinned branch context** (`BRANCH_CONTEXT`), when a `provideBranchContext()`
   ancestor supplied one.
3. **The global current branch** (`UiService.id`) — the default everywhere else.

Most of the app writes to "the current branch" (the branch selector / route), so tiers 1
and 2 are absent and tier 3 applies. You only need tiers 1–2 for a **pinned surface** — one
that edits a branch which may differ from the global selector.

### Pinned surfaces and `provideBranchContext()`

An artifact explorer **editor tab** pins its own branch: the same artifact can be open on
two branches at once (`ArtifactExplorerTabService.addArtifactTabOnBranch`), and a tab
survives a global branch-selector change (it is only repointed/closed on
rebaseline/commit/delete). Reads were already tab-sourced, but **writes** historically fell
back to the global branch, so editing a tab after switching the global selector silently
wrote to the wrong branch.

The fix is **branch context by DI**, not per-call arguments:

- `provideBranchContext()` (from `@osee/transactions/services`) returns providers for a
  `BranchContextSource` (a settable signal-backed `{ branchId, viewId }`), the `BRANCH_CONTEXT`
  token (aliased to that source), **and a scoped `CurrentTransactionService`**. The scoped
  instance is required because the root singleton cannot see a token provided lower in the tree.
- A host component adds it to `providers` and feeds its pinned branch in via the injected
  `BranchContextSource` (inputs are not available at provider-construction time, so an `effect`
  sets it):

```typescript
@Component({
  // ...
  providers: [provideBranchContext()],
})
export class ArtifactEditorComponent {
  private readonly branchContext = inject(BranchContextSource);
  tab = input.required<artifactTab>();
  private readonly _sync = effect(() =>
    this.branchContext.set(this.tab().branchId, this.tab().viewId)
  );
}
```

Every descendant that injects `CurrentTransactionService` (attributes panel, persisted
attribute editor, grouped editors, native content, applicability dropdown, relations editor)
now gets the **scoped** instance and writes to the tab's branch — **with no per-call
override**. This is correct-by-construction: a new editor added under the tab, and even the
no-override helpers (`createArtifact`, `addRelation`, `deleteRelation`, `createTransaction`),
target the pinned branch automatically. Nothing outside a `provideBranchContext()` subtree
is affected — the global fallback is unchanged.

### When you still need the explicit `branchIdOverride` (tier 1)

A `MatDialog` opened without a `viewContainerRef`/`injector` is created at the **overlay
root**, outside the host component's injector subtree, so it does **not** inherit the scoped
service — it gets the root (global-branch) instance. If such a dialog runs its own
transaction for a pinned surface, pass the branch explicitly. Example: the artifact history
**revert** dialog carries `data.branchId` (the tab's branch) and passes it as the
`branchIdOverride` so the revert targets the artifact's branch, not the global selector.
(Dialogs that do not run their own transaction — e.g. the add-attribute dialog returns a
selection and the panel commits, or the conflict dialog calls a `commit` callback closed over
the panel's scoped service — need nothing.)

### Surfaces that are NOT pinned (no action needed)

- **MIM / messaging / transports / connection graph / platform types / shared dropdowns** —
  branch-selector-driven; one branch at a time; tier 3 (global) is correct.
- **AcTra (workflow editor, attachments)** — sets `uiService.idValue = COMMON_BRANCH_ID` in
  `ngOnInit`, so the global branch *is* Common; tier 3 already resolves to Common.

### `attrConfig` shape

The `attrConfig` type is exported from `@osee/transactions/types` and used by both the service methods and the `modifyArtifact` function/operator:

```typescript
import { attrConfig } from '@osee/transactions/types';

// Shape:
{
  set?: attribute[];   // Modify existing attributes (requires id + gammaId)
  add?: attribute[];   // Add new attribute instances (id='-1', gammaId='-1')
  delete?: attribute[] // Delete attribute instances (requires valid id + gammaId)
}
```

## Patterns

### Immediate mutation (simple case)

Use `modifyArtifactAndMutate` when you have a single modify operation:

```typescript
this.currentTxService
  .modifyArtifactAndMutate(
    'Updating attribute',
    artifactId,
    applicability,
    { set: [modifiedAttr] }
  )
  .pipe(take(1))
  .subscribe();
```

### Build then mutate (batch operations)

Use `createTransaction` + functions + `performMutation()` when composing multiple operations into one transaction:

```typescript
import { createArtifact, deleteArtifact } from '@osee/transactions/functions';

let tx = this.currentTxService.createTransaction('Batch operation');
tx = createArtifact(tx, artTypeId, applicability, relations, undefined, ...attrs);
tx = deleteArtifact(tx, oldArtifactId);

of(tx).pipe(this.currentTxService.performMutation()).subscribe();
```

### Reactive build then mutate

Use `createTx` + operators when the branch ID needs to be resolved reactively:

```typescript
this.currentTxService
  .createTx('Modify with relations')
  .pipe(
    modifyArtifact(artId, applicability, { set: attrs }),
    addRelation(relation),
    this.currentTxService.performMutation()
  )
  .subscribe();
```

## Functions vs Operators

Both live under `@osee/transactions/functions` and `@osee/transactions/operators`:

| | Functions | Operators |
|---|---|---|
| Import | `@osee/transactions/functions` | `@osee/transactions/operators` |
| Input | Takes `tx` as first argument, returns modified `tx` | RxJS `pipe` operator on `Observable<Required<transaction>>` |
| Use when | Building synchronously with `createTransaction()` | Building reactively with `createTx()` |

Available functions/operators: `createArtifact`, `modifyArtifact`, `deleteArtifact`, `addRelation`, `addRelations`, `deleteRelation`, `deleteRelations`.

## Attribute operations detail

### Set (modify existing)

The attribute must have a valid `id` and `gammaId`. The server uses the `id` to target the specific attribute instance and `gamma` for concurrency:

```typescript
const attr: validAttribute = {
  id: '12345',          // attribute instance ID
  typeId: '67890',      // attribute type ID
  gammaId: '111',       // current gamma for concurrency
  value: 'new value',
};
// { set: [attr] }
```

### Add (create new instance)

Use `id: '-1'` and `gammaId: '-1'` to signal a new attribute:

```typescript
const attr: newAttribute = {
  id: '-1',
  typeId: '67890',
  gammaId: '-1',
  value: 'initial value',
};
// { add: [attr] }
```

### Delete (remove instance)

Pass the existing attribute with its real `id` and `gammaId`. The operator maps this to `deleteAttributes: [{id}]` in the JSON payload, targeting the specific attribute instance by its ID:

```typescript
// { delete: [existingAttr] }
```

## Error handling

Use `UiService.ErrorText` for user-visible errors:

```typescript
import { UiService } from '@osee/shared/services';

this.currentTxService.modifyArtifactAndMutate(...)
  .pipe(take(1))
  .subscribe({
    error: (err) => {
      this.uiService.ErrorText = `Operation failed: ${err?.message ?? 'Unknown error'}`;
    },
  });
```

## Important notes

- `TransactionService.performMutation()` automatically emits a **targeted local change event** via `ArtifactChangeNotificationService.emitLocalChange()` after every successful commit. Change types are derived from the transaction content (creates → `artifact_created`, deletes → `artifact_deleted`, relations → `relation_added`/`relation_deleted`, modifications → `attribute_modified`).
- `CurrentTransactionService.performMutation()` is an RxJS operator that sets `uiService.updated = true` for backward compatibility with components that have not yet migrated to the SSE notification pattern (MIM, CI Dashboard, Actra). It delegates to `TransactionService` for the actual mutation and notification.
- `TransactionService.performMutation()` sends the `X-SSE-Connection-Id` header with every request so the server can exclude the originating SSE connection from broadcast (preventing duplicate notifications on the same tab).
- Components may call `TransactionService.performMutation()` directly for cases where they build their own transaction objects (e.g., context menu delete). Local change notifications are handled automatically.
- `TransactionService.performMutation()` accepts both the modern `transaction` type and `legacyTransaction` for backward compatibility with older MIM services.
- The `transaction-legacy.ts` types exist for backward compatibility with older MIM services. New code should use `CurrentTransactionService` exclusively for the operator pattern.
- The `modifyArtifact` type includes a typed `deleteAttributes?: deleteAttributeRef[]` field (where `deleteAttributeRef = { id: string }`). The functions/operators populate this from the `delete` array in `attrConfig`, filtering to valid attributes and mapping to `{id}`.
- `transactionResult` now includes `failedGammas: string[]` — when the server rejects a save due to stale gamma, this array is populated and the local change event is NOT emitted.
- The `artifactIds` carried by the change event come from `transactionResult.results.ids`, which the server populates with **both created artifacts and modified/touched writeables** (`TransactionEndpointImpl.create`: `getTxDataReadables` + `getTxDataWriteableIds`). So relating a new artifact to an existing one (e.g. an attachment related to a workflow) reports **both** ids — the new artifact and the existing side-A artifact that the relation touched. Consumers that scope a refresh by artifact id can rely on the existing anchor artifact appearing on relate/unrelate, even though the other artifact's id may be freshly minted (create) or gone (delete).
