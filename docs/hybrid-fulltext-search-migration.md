# Search Tag Encoding Migration

## Problem Statement

The original `TagEncoder` used a bit-packing approach to encode text tokens into 64-bit integers
stored in `osee_search_tags`. The encoder had a fundamental bug: it used a 31-character alphabet
(requiring 5 bits per character) but only allocated 4-bit slots. This caused overflow/bleed between
adjacent character slots, resulting in collisions where different words produced identical tags.

Examples of collisions:
- "TW8" and "TW9" → same tag (2522)
- "CR0091" and "CR1091" → same tag
- "CR091" and "CR1910" → same tag

This meant keyword searches could return incorrect results.

## Solution: FNV-1a Hash-Based Tag Encoding

The bit-packing encoder was replaced with FNV-1a 64-bit hashing:
- One word = one hash = one tag
- No collisions in practice (64-bit hash space)
- Case-insensitive (normalizes to lowercase before hashing)

## Gradual Rollout: Dual-Table Transition

Because the release, release-candidate, and nightly tracks share a single Oracle database,
the migration cannot be done as a single cutover. The approach:

- `osee_search_tags` — kept intact. The legacy `release` app server continues writing
  bit-packed tags here and querying from here. No changes to release code.
- `osee_search_tags_hash` — new table with identical schema. The `nightly`/`rc` app servers
  write hash-based tags here and query from here.

The nightly Java code dual-writes during normal attribute indexing: every tag written by the
`IndexingTaskDatabaseTxCallable` goes to both tables. This covers attributes indexed while the
nightly server is running. However, attributes created by the `release` server are only indexed
into `osee_search_tags`. A nightly CI job bridges this gap.

### Nightly CI job

Run `scripts/reindex-direct.sh` on a nightly schedule pointed at the nightly app server.
The script calls `POST /index/reindex/direct` for each tagged attribute type. The endpoint
skips gammas already present in `osee_search_tags_hash` (idempotent), so repeated runs are
safe. New attributes created by `release` since the last run will be picked up and indexed
into the hash table.

The maximum gap between a `release`-created attribute and its availability in nightly search
is one CI cycle (~24 hours). This is acceptable for a non-production environment.

```
# Example GitLab CI job (.gitlab-ci.yml)
reindex-hash-tags:
  stage: post-deploy
  script:
    - OSEE_URL=http://nightly-server:8089 OSEE_PASSWORD=$OSEE_PASSWORD ./scripts/reindex-direct.sh
  rules:
    - if: '$CI_PIPELINE_SOURCE == "schedule"'
```

### Cutover checklist (when release is retired)

Once all release tracks are running the new encoding:

1. Remove dual-write from `IndexingTaskDatabaseTxCallable.storeTags()` (marked with comment).
2. Remove hash table deletes from `DeleteTagSetDatabaseTxCallable` and `DeleteFromAllTablesWithGammaId`.
3. Update `IndexBranchesDatabaseCallable` MISSING queries back to `osee_search_tags`.
4. Revert `AttributeTokenSqlHandler` query table back to `osee_search_tags`.
5. Run: `TRUNCATE TABLE osee_search_tags_hash; DROP TABLE osee_search_tags_hash;`
6. Optionally rename `osee_search_tags_hash` → `osee_search_tags` instead of steps 4–5
   if you want a clean rename, updating `OseeDb.java` accordingly.
7. Retire the nightly CI reindex job.

## Database Setup

Before deploying nightly code, create the hash table on the shared Oracle database:

```sql
-- scripts/create-search-tags-hash.sql
CREATE TABLE osee_search_tags_hash (
   coded_tag_id NUMBER(19) NOT NULL,
   gamma_id     NUMBER(19) NOT NULL,
   CONSTRAINT osee_srch_tags_hash_pk PRIMARY KEY (coded_tag_id, gamma_id)
);
CREATE INDEX osee_srch_tags_hash_c_idx ON osee_search_tags_hash (coded_tag_id);
CREATE INDEX osee_srch_tags_hash_g_idx ON osee_search_tags_hash (gamma_id);
```

## Files Modified

| File | Change |
|------|--------|
| `plugins/org.eclipse.osee.orcs/src/org/eclipse/osee/orcs/OseeDb.java` | Added `OSEE_SEARCH_TAGS_HASH_TABLE` |
| `plugins/org.eclipse.osee.orcs.db/.../tagger/TagEncoder.java` | Replaced bit-packing with FNV-1a 64-bit hash |
| `plugins/org.eclipse.osee.orcs.db/.../handlers/AttributeTokenSqlHandler.java` | Query reads from `osee_search_tags_hash` |
| `plugins/org.eclipse.osee.orcs.db/.../indexer/QueryEngineIndexerImpl.java` | Re-index endpoints write to hash table |
| `plugins/org.eclipse.osee.orcs.db/.../indexer/callable/consumer/IndexingTaskDatabaseTxCallable.java` | Dual-write to both tables on normal indexing |
| `plugins/org.eclipse.osee.orcs.db/.../indexer/callable/DeleteTagSetDatabaseTxCallable.java` | Deletes from both tables |
| `plugins/org.eclipse.osee.orcs.db/.../indexer/callable/producer/IndexBranchesDatabaseCallable.java` | MISSING queries check hash table |
| `plugins/org.eclipse.osee.orcs.core/.../DeleteFromAllTablesWithGammaId.java` | Gamma purges clean both tables |
| `plugins/org.eclipse.osee.orcs.rest.model/.../IndexerEndpoint.java` | Added re-index REST endpoints |
| `plugins/org.eclipse.osee.orcs.rest/.../IndexerEndpointImpl.java` | Implemented re-index endpoints (async, 202) |
| `plugins/org.eclipse.osee.orcs.db/.../DatabaseCreation.java` | Removed HSQL syntax workaround |

## Files Created

| File | Purpose |
|------|---------|
| `scripts/create-search-tags-hash.sql` | Oracle DDL to create the hash table |
| `scripts/reindex-direct.sh` | Nightly CI script to fill missing hash tags |
| `plugins/.../handlers/AttributeTokenSqlHandlerHybridQueryTest.java` | Tests for query-time tag consistency |
| `plugins/.../tagger/TagEncoderTest.java` | Rewritten tests for hash-based encoder |

## Full Cutover Migration Plan (when all tracks are ready)

### Step 1: Deploy new code and create hash table

Run `scripts/create-search-tags-hash.sql`, then deploy the updated app server.

### Step 2: Re-index all attributes into the hash table

```
POST /index/reindex/direct
```

Returns `202 Accepted`. Indexing runs in the background. For a large database use
`scripts/reindex-direct.sh` which parallelizes across attribute types.

Or to target a specific type:

```
POST /index/reindex/direct?attrTypeId=<type_id>
```

### Step 3: Truncate the old table

```sql
TRUNCATE TABLE osee_search_tags;
```

Safe because the table is a pure search optimization with no FK references.

### Step 4: Remove dual-write code and drop transition table

Follow the cutover checklist above.

## Test Considerations

The `TagProcessorTest` previously used 9 `.tags.txt` data files containing hardcoded expected
tag values from the old bit-packing encoder. The tests have been rewritten to validate consistency
and determinism without coupling to specific encoded values.

## Future Considerations

When documents are moved from the file system into the database (planned CLOB sub-table), native
DB full-text search could replace the `osee_search_tags` table entirely. At that point the
`TagEncoder` and token-based query path would become unnecessary.
