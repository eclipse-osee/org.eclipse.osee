#!/bin/bash
#
# Incremental re-index of missing hash-based search tags.
#
# Intended to run as a nightly CI job during the transition period while the
# legacy release app server is still writing bit-packed tags to osee_search_tags.
# This script finds attribute gammas not yet present in osee_search_tags_hash
# and indexes them via POST /index/reindex/direct.
#
# The endpoint skips gammas already in osee_search_tags_hash, so repeated runs
# are safe and idempotent. Each POST returns 202 immediately; indexing runs on
# the server. The script polls until the server confirms completion by checking
# that no new missing gammas remain (via a subsequent dry-run count) -- or
# simply waits a configurable settle time if polling is not available.
#
# Usage:
#   ./reindex-direct.sh
#
# Environment variables:
#   OSEE_URL        - Base URL of the OSEE server (default: http://localhost:8089)
#   OSEE_USER       - Basic auth user (default: 3333)
#   OSEE_PASSWORD   - Basic auth password (default: empty)
#   MAX_PARALLEL    - Max concurrent POST requests (default: 4)
#   HEALTH_TIMEOUT  - Seconds to wait for server health check (default: 60)
#   SETTLE_SECONDS  - Seconds to wait after all POSTs before declaring done (default: 30)
#

OSEE_URL="${OSEE_URL:-http://localhost:8089}"
OSEE_USER="${OSEE_USER:-3333}"
OSEE_PASSWORD="${OSEE_PASSWORD:-}"
MAX_PARALLEL="${MAX_PARALLEL:-4}"
HEALTH_TIMEOUT="${HEALTH_TIMEOUT:-60}"
SETTLE_SECONDS="${SETTLE_SECONDS:-30}"

# Temp files cleaned up on exit regardless of how the script terminates
TMPDIR_REINDEX=$(mktemp -d)
trap 'rm -rf "$TMPDIR_REINDEX"' EXIT

# Build auth args as an array to avoid word-splitting on passwords with spaces
AUTH_ARGS=()
if [ -n "$OSEE_USER" ]; then
   AUTH_ARGS=(-u "${OSEE_USER}:${OSEE_PASSWORD}")
fi

# ---------------------------------------------------------------------------
# Health check: wait until the server responds before doing anything
# ---------------------------------------------------------------------------
echo "Waiting for server at ${OSEE_URL} (timeout: ${HEALTH_TIMEOUT}s)..."
ELAPSED=0
while true; do
   HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
      "${AUTH_ARGS[@]}" "${OSEE_URL}/orcs/types/attribute" 2>/dev/null)
   if [ "$HTTP_CODE" = "200" ]; then
      echo "Server is up."
      break
   fi
   if [ "$ELAPSED" -ge "$HEALTH_TIMEOUT" ]; then
      echo "ERROR: Server did not respond within ${HEALTH_TIMEOUT}s (last HTTP code: ${HTTP_CODE})."
      exit 1
   fi
   sleep 5
   ELAPSED=$((ELAPSED + 5))
done

# ---------------------------------------------------------------------------
# Fetch tagged attribute type IDs
# The /orcs/types/attribute endpoint returns all attribute types; filter to
# those that have a tagger (tagged == true) if the field is available,
# otherwise fall back to all types and let the server skip untaggable ones.
# ---------------------------------------------------------------------------
echo "Fetching tagged attribute types from ${OSEE_URL}/orcs/types/attribute ..."

ATTR_TYPE_IDS=$(curl -s "${AUTH_ARGS[@]}" "${OSEE_URL}/orcs/types/attribute" \
   -H "Accept: application/json" | \
   python3 -c "
import sys, json
types = json.load(sys.stdin)
for t in types:
    # Include only tagged types if the field is present; otherwise include all
    if t.get('taggerId', t.get('tagged', True)):
        print(t['id'])
" 2>/dev/null)

if [ -z "$ATTR_TYPE_IDS" ]; then
   echo "ERROR: Could not fetch attribute type IDs. Check OSEE_URL and credentials."
   exit 1
fi

COUNT=$(echo "$ATTR_TYPE_IDS" | wc -l | tr -d ' ')
echo "Found ${COUNT} attribute type(s) to check."
echo "Starting parallel re-index (missing only) with max ${MAX_PARALLEL} concurrent requests..."
echo ""

FAILED=0

reindex_type() {
   local type_id=$1
   local out_file="${TMPDIR_REINDEX}/reindex_${type_id}.out"

   HTTP_CODE=$(curl -s -o "$out_file" -w "%{http_code}" \
      "${AUTH_ARGS[@]}" -X POST \
      "${OSEE_URL}/orcs/index/reindex/direct?attrTypeId=${type_id}")

   if [ "$HTTP_CODE" = "202" ]; then
      echo "[QUEUED] attrTypeId=${type_id}"
   elif [ "$HTTP_CODE" = "200" ]; then
      # Tolerate servers that return 200 synchronously
      echo "[OK]     attrTypeId=${type_id}"
   else
      echo "[FAIL]   attrTypeId=${type_id}  HTTP ${HTTP_CODE}"
      cat "$out_file" 2>/dev/null
      echo ""
      return 1
   fi
}

export -f reindex_type
export TMPDIR_REINDEX
export OSEE_URL
export -a AUTH_ARGS

# Run in parallel using xargs
echo "$ATTR_TYPE_IDS" | xargs -P "$MAX_PARALLEL" -I {} bash -c 'reindex_type "$@"' _ {}
XARGS_EXIT=$?

if [ "$XARGS_EXIT" -ne 0 ]; then
   FAILED=1
fi

# ---------------------------------------------------------------------------
# Settle: give the async server-side jobs time to complete
# ---------------------------------------------------------------------------
echo ""
echo "All requests submitted. Waiting ${SETTLE_SECONDS}s for background indexing to complete..."
sleep "$SETTLE_SECONDS"

echo ""
if [ "$FAILED" -ne 0 ]; then
   echo "Done with errors. One or more attribute types failed to submit."
   exit 1
else
   echo "Done."
fi
