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
| `createTransaction(comment)` | Creates a `Required<transaction>` synchronously using the current branch ID. |
| `createTx(comment, branchIdOverride?)` | Creates a `Required<transaction>` as an Observable (reactive). |
| `modifyArtifactAndMutate(comment, artId, applicability, attrConfig, branchIdOverride?)` | Builds a modify transaction and immediately executes it. |
| `modifyArt(comment, artId, applicability, attrConfig, branchIdOverride?)` | Builds a modify transaction without executing — returns `Observable<Required<transaction>>`. |
| `performMutation()` | RxJS pipe operator that sends the transaction to the server and triggers a UI refresh. |

### `attrConfig` shape

```typescript
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

Pass the existing attribute with its real `id` and `gammaId`. The operator maps this to `deleteAttributes: [{typeId, gamma}]` in the JSON payload:

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

- `performMutation()` sets `uiService.updated = true` which triggers global resource refreshes via `httpResource` factories that read `uiService.updateCount()`.
- Do **not** call `TransactionService.performMutation()` directly from components — always use `CurrentTransactionService` which handles branch resolution and UI refresh.
- The `transaction-legacy.ts` types exist for backward compatibility with older MIM services. New code should use `CurrentTransactionService` exclusively.
