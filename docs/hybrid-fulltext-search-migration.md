# Hybrid Full-Text Search Migration

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

## Solution: Hybrid Full-Text Search

Rather than just fixing the bit-packing bug, we implemented a hybrid approach:

1. **Native DB full-text search** for inline attribute values (stored directly in `osee_attribute.value`)
2. **Hash-based token table** (`osee_search_tags`) for external documents stored on the file system

This eliminates the entire class of encoding bugs for inline attributes and provides better search
quality (stemming, ranking) via the database engine, while maintaining DB-agnostic search capability
for external resources.

## Architecture

### Query Path (AttributeTokenSqlHandler)

When the database supports native FTS (Oracle, PostgreSQL):
```sql
-- Inline attributes: native full-text search
SELECT gamma_id FROM osee_attribute att WHERE CONTAINS(att.value, ?) > 0
UNION
-- External documents: hash-based token lookup
SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?
INTERSECT
SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?
```

When the database does NOT support native FTS (H2, HSQL):
```sql
-- Token-based search only (fallback)
SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?
INTERSECT
SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?
```

### Indexing Path (IndexingTaskDatabaseTxCallable)

- If DB supports FTS AND attribute is inline → skip tagging (native index handles it)
- If attribute is external (URI-based) → hash the tokens and store in `osee_search_tags`

### Tag Encoding (TagEncoder)

Replaced bit-packing with FNV-1a 64-bit hashing:
- One word = one hash = one tag
- No collisions in practice (64-bit hash space)
- Case-insensitive (normalizes to lowercase before hashing)

## Files Modified

| File | Change |
|------|--------|
| `plugins/org.eclipse.osee.jdbc/src/org/eclipse/osee/jdbc/DatabaseType.java` | Added `supportsFullTextSearch()`, `getFullTextSearchSql()`, `getFullTextIndexDdl()` |
| `plugins/org.eclipse.osee.orcs.db/src/org/eclipse/osee/orcs/db/internal/search/tagger/TagEncoder.java` | Replaced bit-packing with FNV-1a 64-bit hash |
| `plugins/org.eclipse.osee.orcs.db/src/org/eclipse/osee/orcs/db/internal/search/tagger/TagProcessor.java` | Removed debug code |
| `plugins/org.eclipse.osee.orcs.core/src/org/eclipse/osee/orcs/core/ds/IndexedResource.java` | Added `isExternalResource()` default method |
| `plugins/org.eclipse.osee.orcs.db/src/org/eclipse/osee/orcs/db/internal/search/indexer/data/IndexerDataSourceImpl.java` | Implemented `isExternalResource()` |
| `plugins/org.eclipse.osee.orcs.db/src/org/eclipse/osee/orcs/db/internal/search/indexer/callable/consumer/IndexingTaskDatabaseTxCallable.java` | Skip tagging inline attributes when DB supports FTS |
| `plugins/org.eclipse.osee.orcs.db/src/org/eclipse/osee/orcs/db/internal/search/handlers/AttributeTokenSqlHandler.java` | Hybrid query generation (native FTS UNION token lookup) |
| `plugins/org.eclipse.osee.orcs.rest.model/src/org/eclipse/osee/orcs/rest/model/IndexerEndpoint.java` | Added `POST /index/reindex/baseline` endpoint |
| `plugins/org.eclipse.osee.orcs.rest/src/org/eclipse/osee/orcs/rest/internal/IndexerEndpointImpl.java` | Implemented `reindexBaselineBranches()` |
| `plugins/org.eclipse.osee.orcs.db/src/org/eclipse/osee/orcs/db/internal/DatabaseCreation.java` | Creates FTS index during programmatic DB creation |
| `.github/docker/utility/osee-postgres/files/init.sql` | Added GIN index for PostgreSQL deployments |

## Files Created

| File | Purpose |
|------|---------|
| `plugins/org.eclipse.osee.orcs.db/src/org/eclipse/osee/orcs/db/internal/search/fulltext/FullTextIndexDdl.java` | Utility for creating FTS index on existing databases (idempotent) |
| `plugins/org.eclipse.osee.orcs.db.test/src/org/eclipse/osee/orcs/db/internal/search/tagger/TagEncoderTest.java` | Rewritten tests for hash-based encoder |

## Migration Plan (Oracle 12c)

### Prerequisites

- Oracle user must have: `GRANT CTXAPP TO osee_user; GRANT EXECUTE ON CTXSYS.CTX_DDL TO osee_user;`
- Verify free space in OSEE_INDEX tablespace (need 8-12 GB for 6.4 GB of source text)
- Schedule during downtime (no concurrent DML)

### Check available space

```sql
SELECT tablespace_name,
       ROUND(SUM(bytes) / 1024 / 1024 / 1024, 2) AS free_gb
FROM dba_free_space
WHERE tablespace_name = 'OSEE_INDEX'
GROUP BY tablespace_name;
```

### Add space if needed

```sql
ALTER TABLESPACE OSEE_INDEX ADD DATAFILE SIZE 12G AUTOEXTEND ON NEXT 1G MAXSIZE 20G;
```

### Step 1: Deploy new code

Deploy the updated Java code to the application server (keep it stopped).

### Step 2: Truncate old search tags

```sql
TRUNCATE TABLE osee_search_tags;
```

This is safe because:
- The table is purely a search optimization, no FK references
- Old bit-packed values are incompatible with new hash values
- Inline attribute search will work via native FTS index immediately after Step 3

### Step 3: Create the full-text index

```sql
CREATE INDEX osee_attr_fts_idx ON osee_attribute(value)
   INDEXTYPE IS CTXSYS.CONTEXT
   PARAMETERS ('MEMORY 4G SYNC (ON COMMIT)')
   TABLESPACE OSEE_INDEX
   PARALLEL 20;

ALTER INDEX osee_attr_fts_idx NOPARALLEL;
```

With 40 cores, PARALLEL 20, and 4G memory on 6.4 GB of text: estimated ~5-10 minutes.

### Step 4: Start application server

### Step 5: Re-index external documents

Call the new REST endpoint to regenerate hash-based tags for external resources:

```
POST /index/reindex/baseline
```

Or with working branches included:

```
POST /index/reindex/baseline?includeWorking=true
```

## Test Considerations

The `TagProcessorTest` has 9 `.tags.txt` data files containing hardcoded expected tag values from
the old bit-packing encoder. These must be regenerated with the new FNV-1a hash values before
the test suite will pass.

## Future Considerations

When documents are moved from the file system into the database (planned CLOB sub-table), the
`osee_search_tags` table and `TagEncoder` become unnecessary entirely. The native FTS index would
cover all content, and the hybrid UNION in the query handler simplifies to just native FTS across
the strings and CLOBs tables.
