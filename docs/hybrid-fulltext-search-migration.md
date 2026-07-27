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

All attributes (inline and external) continue to be indexed into `osee_search_tags` using
this new encoding. The query path remains unchanged — token-based lookup via `coded_tag_id`.

## Files Modified

| File | Change |
|------|--------|
| `plugins/org.eclipse.osee.orcs.db/src/org/eclipse/osee/orcs/db/internal/search/tagger/TagEncoder.java` | Replaced bit-packing with FNV-1a 64-bit hash |
| `plugins/org.eclipse.osee.orcs.db/src/org/eclipse/osee/orcs/db/internal/search/handlers/AttributeTokenSqlHandler.java` | Minor cleanup |
| `plugins/org.eclipse.osee.orcs.rest.model/src/org/eclipse/osee/orcs/rest/model/IndexerEndpoint.java` | Added re-index REST endpoints |
| `plugins/org.eclipse.osee.orcs.rest/src/org/eclipse/osee/orcs/rest/internal/IndexerEndpointImpl.java` | Implemented `reindexBaselineBranches()` and `reindexAllCurrent()` |
| `plugins/org.eclipse.osee.orcs.db/src/org/eclipse/osee/orcs/db/internal/DatabaseCreation.java` | Removed HSQL syntax workaround |

## Files Created

| File | Purpose |
|------|---------|
| `plugins/org.eclipse.osee.orcs.db.test/src/org/eclipse/osee/orcs/db/internal/search/handlers/AttributeTokenSqlHandlerHybridQueryTest.java` | Tests for query-time tag consistency |
| `plugins/org.eclipse.osee.orcs.db.test/src/org/eclipse/osee/orcs/db/internal/search/tagger/TagEncoderTest.java` | Rewritten tests for hash-based encoder |

## Migration Plan

### Step 1: Deploy new code

Deploy the updated Java code to the application server (keep it stopped).

### Step 2: Truncate old search tags

```sql
TRUNCATE TABLE osee_search_tags;
```

This is safe because:
- The table is purely a search optimization, no FK references
- Old bit-packed values are incompatible with new hash values

### Step 3: Start application server

### Step 4: Re-index all attributes

Call the new REST endpoint to regenerate hash-based tags for all current attributes:

```
POST /index/reindex/all
```

Returns `202 Accepted` immediately. Indexing runs in the background on the server.

Or to re-index a specific attribute type:

```
POST /index/reindex/all?attrTypeId=<type_id>
```

Alternatively, re-index by branch:

```
POST /index/reindex/baseline
```

Or with working branches included:

```
POST /index/reindex/baseline?includeWorking=true
```

The `/reindex/all` and `/reindex/direct` endpoints skip gammas that are already present in `osee_search_tags`,
so they are safe to call on a partially-indexed database without causing duplicate-key errors.

## Test Considerations

The `TagProcessorTest` previously used 9 `.tags.txt` data files containing hardcoded expected
tag values from the old bit-packing encoder. The tests have been rewritten to validate consistency
and determinism without coupling to specific encoded values.

## Future Considerations

When documents are moved from the file system into the database (planned CLOB sub-table), native
DB full-text search could replace the `osee_search_tags` table entirely. At that point the
`TagEncoder` and token-based query path would become unnecessary.
