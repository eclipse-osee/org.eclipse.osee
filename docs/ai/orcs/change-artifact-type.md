---
summary: "Server-side artifact type changes through ORCS transaction history: TransactionBuilder.setArtifactType, the admin-gated changeArtifactTypeById REST endpoint, how it differs from the client BLAM and changeArtifactTypeOutsideofHistory, and the DB/gamma effects"
tags: [orcs, transaction, artifact-type, rest, admin, gamma, history]
fileMatch: "**/TransactionBuilder*.java,**/ArtifactEndpoint*.java,**/ArtifactImpl.java,**/OrcsAdminImpl.java"
---

# Changing Artifact Type (Server Side)

## Overview

An artifact's type is not separate metadata; it is the `art_type_id` column on the
artifact row in `osee_artifact`, alongside that row's own `gamma_id` and `guid`. So
changing an artifact's type is a normal versioned write: a new artifact row with a new
gamma is inserted, and `osee_txs.tx_current` is repointed to it on the committing
branch. Attributes and relations are untouched; they keep their own gammas and stay
attached to the same `art_id`.

## The three ways to change type

1. **Client BLAM** -- `org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType`
   (IDE side). Loads every artifact into memory, walks and deletes attributes/relations
   invalid for the new type, then writes through `SkynetTransaction`. Correct for the
   general case, but the join it uses to find current gammas
   (`osee_join_id jid1, osee_join_id jid2, osee_txs`) can blow up (cartesian) when many
   artifacts span many branches, and the full attribute/relation reconciliation is wasted
   work when nothing invalid is actually present.

2. **`OrcsAdmin.changeArtifactTypeOutsideofHistory`** -- raw
   `UPDATE osee_artifact SET art_type_id = ? WHERE art_id = ?`. No new gamma, no new
   `osee_txs` row: it mutates the existing row in place, bypassing history. This is the
   path behind the older `changeArtifactType(branch, oldType, newType, names)` endpoint.
   Admin/bootstrapping only; does not behave like a branch-scoped transactional change.

3. **`TransactionBuilder.setArtifactType` + `ArtifactEndpoint.changeArtifactTypeById`**
   (the history-friendly server path; see below). Use this when the new type supports the
   same relations and at least the attributes actually in use, so no cleanup is needed.

## TransactionBuilder.setArtifactType

`TransactionBuilder.setArtifactType(ArtifactId, ArtifactTypeToken)` delegates to the
internal `Artifact.setArtifactType` (`ArtifactImpl`). That method:

- sets the type on the in-memory orcs data,
- marks edit state `ARTIFACT_TYPE_MODIFIED`, and
- if the artifact is already in storage, sets `ModificationType.MODIFIED`.

That is what makes the artifact dirty and eligible for the commit writer, which then
inserts a new `osee_artifact` row (new gamma, new `art_type_id`, same `art_id`/`guid`),
inserts a new `CURRENT` `osee_txs` row, and flips the previous txs row to `NOT_CURRENT`.

**Precondition (not enforced):** the builder does NOT validate or delete attributes or
relations that are invalid for the new type. Callers must guarantee none are in use, or
those rows are orphaned. This is intentional -- it is the whole reason this path avoids
the client BLAM's reconciliation cost.

## REST: changeArtifactTypeById

`POST /orcs/branch/{branch}/artifact/new-type/{newType}/change` with a JSON array of
artifact ids as the body. Returns the `TransactionToken` for the single transaction that
retyped all artifacts.

- Operates by artifact id, so there is no name/old-type cartesian matching.
- Does one transaction for the whole batch.
- **Admin gated:** first line calls
  `orcsApi.userService().requireRole(CoreUserGroups.OseeAccessAdmin)`, which throws
  `OseeAccessDeniedException` for non-admins before any transaction is created. The gate
  is unconditional (not just in production) because artifacts holding different types on
  different branches is only valid in special cases.
- Does NOT filter on the old type: any id in the list is retyped even if it was not the
  expected source type. There is deliberately no old-type guard.

Example:

```bash
curl -X POST \
  "http://localhost:8089/orcs/branch/8214776116211838963/artifact/new-type/1932745746/change" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '[9914064, 9737117, 9847279, 9733759, 10621053]'
```

## Branch scope

The change is current only on the committing branch, because `tx_current` lives in
`osee_txs` and every txs row is branch scoped. Other branches still see the old gamma
with the old type until the change is committed up the branch hierarchy. To verify the
flip on a branch:

```sql
SELECT art.art_id, art.art_type_id, art.gamma_id, txs.transaction_id, txs.mod_type, txs.tx_current
FROM osee_artifact art, osee_txs txs
WHERE art.gamma_id = txs.gamma_id
  AND txs.branch_id = <branchId>
  AND art.art_id IN (<ids>)
ORDER BY art.art_id, txs.transaction_id;
```

You will see two rows per `art_id`: the old gamma (old type) now `NOT_CURRENT`, and the
new gamma (new type) `CURRENT`.
