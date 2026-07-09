#!/bin/bash
#
# Parallel direct re-index of all tagged attribute types.
# Fetches tagged attr type IDs from the server, then fires off concurrent
# POST requests to /index/reindex/direct for each type.
#
# Usage:
#   ./reindex-direct.sh
#
# Environment variables:
#   OSEE_URL       - Base URL of the OSEE server (default: http://localhost:8089)
#   OSEE_USER      - Basic auth user (default: 3333)
#   OSEE_PASSWORD  - Basic auth password (default: empty)
#   MAX_PARALLEL   - Max concurrent requests (default: 4)
#

OSEE_URL="${OSEE_URL:-http://localhost:8089}"
OSEE_USER="${OSEE_USER:-3333}"
OSEE_PASSWORD="${OSEE_PASSWORD:-}"
MAX_PARALLEL="${MAX_PARALLEL:-4}"

AUTH=""
if [ -n "$OSEE_USER" ]; then
   AUTH="-u ${OSEE_USER}:${OSEE_PASSWORD}"
fi

echo "Fetching tagged attribute types from ${OSEE_URL}/orcs/types/attribute ..."

# Get all attribute type IDs - the endpoint returns JSON array of {id, name} objects
ATTR_TYPE_IDS=$(curl -s $AUTH "${OSEE_URL}/orcs/types/attribute" \
   -H "Accept: application/json" | \
   python3 -c "import sys,json; [print(t['id']) for t in json.load(sys.stdin)]" 2>/dev/null)

if [ -z "$ATTR_TYPE_IDS" ]; then
   echo "ERROR: Could not fetch attribute type IDs. Check OSEE_URL and credentials."
   exit 1
fi

COUNT=$(echo "$ATTR_TYPE_IDS" | wc -l)
echo "Found $COUNT attribute types to index."
echo "Starting parallel re-index with max $MAX_PARALLEL concurrent requests..."
echo ""

# Track progress
COMPLETED=0
FAILED=0

reindex_type() {
   local type_id=$1
   local start_time=$(date +%s)

   HTTP_CODE=$(curl -s -o /tmp/reindex_${type_id}.out -w "%{http_code}" \
      $AUTH -X POST "${OSEE_URL}/orcs/index/reindex/direct?attrTypeId=${type_id}")

   local end_time=$(date +%s)
   local elapsed=$((end_time - start_time))

   if [ "$HTTP_CODE" = "200" ]; then
      echo "[OK]  attrTypeId=${type_id}  (${elapsed}s)"
   else
      echo "[FAIL] attrTypeId=${type_id}  HTTP ${HTTP_CODE}  (${elapsed}s)"
      cat /tmp/reindex_${type_id}.out 2>/dev/null
      echo ""
   fi
   rm -f /tmp/reindex_${type_id}.out
}

export -f reindex_type
export OSEE_URL AUTH

# Run in parallel using xargs
echo "$ATTR_TYPE_IDS" | xargs -P $MAX_PARALLEL -I {} bash -c 'reindex_type "$@"' _ {}

echo ""
echo "Done."
