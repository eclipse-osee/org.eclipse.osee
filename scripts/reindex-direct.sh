#!/bin/bash
#
# Incremental re-index of missing hash-based search tags.
#
# Intended to run as a nightly CI job during the transition period while the
# legacy release app server is still writing bit-packed tags to osee_search_tags.
# This script triggers the direct reindex endpoint which finds attribute gammas
# not yet present in osee_search_tags_hash and indexes them.
#
# The endpoint skips gammas already in osee_search_tags_hash, so repeated runs
# are safe and idempotent.
#
# Server-side concurrency control: The server uses a shared single-thread
# executor for all reindex requests. Even if this script submits multiple
# requests (one per attr type), they queue up and execute one at a time on the
# server. This prevents database overload from parallel indexing.
#
# Usage:
#   ./reindex-direct.sh [--all]
#
#   --all   Submit a single request with no attrTypeId, letting the server
#           process all tagged types in one background job. This is the default.
#
#   Without --all, the script fetches tagged type IDs and submits one request
#   per type. The server still serializes them, but you get per-type status
#   output from the script.
#
# Environment variables:
#   OSEE_URL        - Base URL of the OSEE server (default: http://localhost:8089)
#   OSEE_USER       - Basic auth user (default: 3333)
#   OSEE_PASSWORD   - Basic auth password (default: empty)
#   HEALTH_TIMEOUT  - Seconds to wait for server health check (default: 60)
#

set -euo pipefail

OSEE_URL="${OSEE_URL:-http://localhost:8089}"
OSEE_USER="${OSEE_USER:-3333}"
OSEE_PASSWORD="${OSEE_PASSWORD:-}"
HEALTH_TIMEOUT="${HEALTH_TIMEOUT:-60}"

MODE="${1:---all}"

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
      "${AUTH_ARGS[@]}" "${OSEE_URL}/orcs/types/attribute" 2>/dev/null) || true
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
# Submit reindex request(s)
# ---------------------------------------------------------------------------
if [ "$MODE" = "--all" ]; then
   echo ""
   echo "Submitting single reindex request for all tagged attribute types..."
   HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
      "${AUTH_ARGS[@]}" -X POST \
      "${OSEE_URL}/orcs/index/reindex/direct")

   if [ "$HTTP_CODE" = "202" ] || [ "$HTTP_CODE" = "200" ]; then
      echo "Accepted (HTTP ${HTTP_CODE}). Server will process all types sequentially in the background."
      echo "Monitor server logs for progress."
   else
      echo "FAILED (HTTP ${HTTP_CODE})"
      exit 1
   fi
else
   # Per-type mode: fetch type IDs and submit individually
   echo "Fetching tagged attribute types from ${OSEE_URL}/orcs/types/attribute ..."

   ATTR_TYPE_IDS=$(curl -s "${AUTH_ARGS[@]}" "${OSEE_URL}/orcs/types/attribute" \
      -H "Accept: application/json" | \
      python3 -c "
import sys, json
types = json.load(sys.stdin)
for t in types:
    if t.get('taggerId', t.get('tagged', True)):
        print(t['id'])
" 2>/dev/null)

   if [ -z "$ATTR_TYPE_IDS" ]; then
      echo "ERROR: Could not fetch attribute type IDs. Check OSEE_URL and credentials."
      exit 1
   fi

   COUNT=$(echo "$ATTR_TYPE_IDS" | wc -l | tr -d ' ')
   echo "Found ${COUNT} attribute type(s). Submitting one request per type..."
   echo "(Server serializes execution — only one runs at a time regardless of submission rate)"
   echo ""

   FAILED=0
   SUBMITTED=0

   for TYPE_ID in $ATTR_TYPE_IDS; do
      SUBMITTED=$((SUBMITTED + 1))

      HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" \
         "${AUTH_ARGS[@]}" -X POST \
         "${OSEE_URL}/orcs/index/reindex/direct?attrTypeId=${TYPE_ID}")

      if [ "$HTTP_CODE" = "202" ] || [ "$HTTP_CODE" = "200" ]; then
         echo "[${SUBMITTED}/${COUNT}] Queued attrTypeId=${TYPE_ID} (HTTP ${HTTP_CODE})"
      else
         echo "[${SUBMITTED}/${COUNT}] FAILED attrTypeId=${TYPE_ID} (HTTP ${HTTP_CODE})"
         FAILED=$((FAILED + 1))
      fi
   done

   echo ""
   if [ "$FAILED" -ne 0 ]; then
      echo "Done. ${FAILED}/${COUNT} requests failed to submit."
      exit 1
   else
      echo "Done. All ${COUNT} types queued. Server is processing them sequentially in the background."
      echo "Monitor server logs for progress."
   fi
fi
