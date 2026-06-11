---
summary: "Cold storage tiered data lifecycle for OSEE transaction and branch data"
tags: [cold-storage, archival, transactions, branches, purge, restore]
fileMatch: "**/ColdStorage*,**/TxsColdStorage*,**/TxPurgeColdStorage*"
---

# Cold Storage Strategy

## Overview

Cold storage provides a tiered data lifecycle for OSEE's transaction and branch data. As branches are committed, rebaselined, or deleted, their transaction data moves from active tables into archived tables, and eventually into compressed binary files on the filesystem — freeing database tablespace while preserving the ability to restore data on demand.

## Storage Tiers

| Tier | Location | Data State | Access Speed |
|------|----------|-----------|--------------|
| Hot | `osee_txs` | Active branches | Immediate |
| Warm | `osee_txs_archived` | Recently committed/rebaselined/deleted | Immediate (same DB) |
| Cold | `{server_data}/purge/cold_storage/*.gz` | Exported from DB, catalog tracked | Restore required |

## Branch Cold Storage

### How It Works

When a branch has been in committed, rebaselined, or deleted state for longer than the retention period (default 365 days), its data becomes eligible for cold storage. The archive process:

1. Exports `osee_branch`, `osee_tx_details`, and `osee_txs_archived` rows to a gzip-compressed binary file
2. Purges the rows from the database (using existing `PurgeBranchDatabaseCallable`)
3. Inserts a catalog row into `osee_txs_cold_storage` tracking the branch name, file location, and row counts

### Catalog Table

`osee_txs_cold_storage` tracks all cold-stored branches:

| Column | Type | Description |
|--------|------|-------------|
| BRANCH_ID | BIGINT | Primary key |
| BRANCH_NAME | VARCHAR(200) | Branch name at time of archival |
| EXPORT_FILE | VARCHAR(200) | Filename in cold storage directory |
| EXPORT_DATE | TIMESTAMP | When the export occurred |
| TXS_ROW_COUNT | BIGINT | Number of txs_archived rows exported |
| TX_DETAILS_ROW_COUNT | BIGINT | Number of tx_details rows exported |
| BRANCH_STATE | SMALLINT | Branch state at time of archival |

### REST Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/orcs/txs/cold/archive?limit=100&retentionDays=365` | Sweep: archive eligible branches |
| POST | `/orcs/txs/cold/restore/{branchId}` | Restore a branch from cold storage |
| GET | `/orcs/txs/cold` | List all cold-stored branches |
| DELETE | `/orcs/txs/cold/{branchId}` | Permanently discard from cold storage |
| GET | `/orcs/txs/cold/preview/{branchId}` | Download SQL preview of restore (zip) |

### Automated Sweep (CI Job)

The archive endpoint is designed to be called by a CI job on a schedule:

```bash
# Archive up to 100 branches per call, repeat until none remain
while true; do
  RESULT=$(curl -s -X POST "https://server/orcs/txs/cold/archive?limit=100&retentionDays=365")
  echo "$RESULT"
  if echo "$RESULT" | grep -q "No eligible branches"; then
    break
  fi
done
```

### Restore Process

Restoring re-inserts data using a two-phase approach to satisfy circular FK constraints between `osee_branch` and `osee_tx_details`:

1. Insert `osee_branch` with `baseline_transaction_id = 1` (temporary)
2. Insert `osee_tx_details` rows
3. Update `osee_branch` to the real `baseline_transaction_id`
4. Insert `osee_txs_archived` rows

## Branch Purge with Cold Storage

When a user explicitly purges a branch via `DELETE /branch/{branchId}`, the branch data is automatically exported to cold storage before deletion (default behavior). This allows accidental purges to be recovered.

| Parameter | Default | Description |
|-----------|---------|-------------|
| `recurse` | false | Also purge child branches |
| `coldStorage` | true | Export to cold storage before purging |

Internal batch operations (`purgeDeletedBranches`, `purgeWorkingBranchesOfClosedPrograms`) pass `coldStorage=false` since deleted branches don't need recovery.

## Transaction Purge Cold Storage

When transactions are purged via `DELETE /orcs/txs/{tx-ids}`, the following data is exported to a compressed binary file before deletion:

- `osee_txs` rows for the transaction
- `osee_tx_details` row for the transaction
- Backing data: `osee_artifact`, `osee_attribute`, `osee_relation_link`, `osee_relation` rows for all gammas in that transaction

### REST Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/orcs/txs/cold/restore/tx/{txId}?fileName=...` | Restore a purged transaction |
| GET | `/orcs/txs/cold/tx` | List purged transaction archive files |
| GET | `/orcs/txs/cold/preview/tx/{txId}` | Download SQL preview of restore (zip) |

The `fileName` parameter is optional — the system auto-discovers the archive file by matching the transaction ID in filenames or scanning file contents.

### Restore Order

Transaction restore inserts in FK-safe order:

1. Backing data (artifacts, attributes, relations)
2. `osee_tx_details`
3. `osee_txs`

## File Format

Both branch and transaction cold storage use the same approach:

- **Compression:** GZIP (`java.util.zip.GZIPOutputStream`)
- **Serialization:** Java `DataOutputStream` (binary, length-prefixed)
- **Location:** `{osee.application.server.data}/purge/cold_storage/`
- **Naming:**
  - Branch sweeps: `txs_cold_YYYYMMDD_HHmmss.gz`
  - Single branch: `txs_cold_branch_{id}_YYYYMMDD_HHmmss.gz`
  - Transaction purge: `tx_purge_{txId}_YYYYMMDD_HHmmss.gz`

Files are not human-readable (binary format for compactness). Use the preview endpoints to inspect contents as SQL INSERT statements.

## Preview

Both branch and transaction cold storage support a preview endpoint that returns a downloadable zip file containing SQL INSERT statements — one `.sql` file per table. This allows reviewing exactly what a restore would do before executing it.

## Key Implementation Files

| File | Purpose |
|------|---------|
| `OseeDb.java` | `TXS_COLD_STORAGE_TABLE` catalog table definition |
| `DatabaseCreation.java` | Creates the catalog table on DB init |
| `TxsColdStorage.java` | Branch cold storage: archive, restore, preview, list, purge |
| `TxPurgeColdStorage.java` | Transaction purge cold storage: export, restore, preview, list |
| `TransactionEndpoint.java` | REST interface definitions |
| `TransactionEndpointImpl.java` | REST implementations |
| `BranchEndpointImpl.java` | Branch purge integration with cold storage |
