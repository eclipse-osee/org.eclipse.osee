#!/bin/bash
#
# git-bundle-update.sh
#
# Creates a git bundle from the local repo and uploads it to the OSEE server
# via the GitEndpoint REST API. Verifies the local and server SHAs match
# before creating the bundle.
#
# Usage: ./git-bundle-update.sh --auth <authorization-string> [--dry-run]
#

set -euo pipefail

# ============================================================================
# Configuration — adjust these to match your environment
# ============================================================================
OSEE_SERVER="http://localhost:8111"
OSEE_BRANCH_ID="88"                          # OSEE branch ID
REPO_NAME="your-repo-name"                    # Repository name as registered in OSEE
GIT_BRANCH="dev"                              # Git branch to sync
REF_SPEC="refs/remotes/origin/${GIT_BRANCH}:refs/remotes/origin/${GIT_BRANCH}"
BUNDLE_DIR="."                                # Where to write the temporary bundle file

# ============================================================================
# Parse arguments
# ============================================================================
DRY_RUN=false
AUTH_STRING=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        --auth)
            AUTH_STRING="${2:-}"
            if [[ -z "${AUTH_STRING}" ]]; then
                echo "ERROR: --auth requires a value (e.g. --auth dXNlcjpwYXNz)"
                exit 1
            fi
            shift 2
            ;;
        --dry-run)
            DRY_RUN=true
            echo "[DRY RUN] Will show what would happen without making changes."
            shift
            ;;
        *)
            echo "Unknown argument: $1"
            echo "Usage: $0 --auth <authorization-string> [--dry-run]"
            exit 1
            ;;
    esac
done

if [[ -z "${AUTH_STRING}" ]]; then
    echo "ERROR: --auth is required."
    echo "Usage: $0 --auth <authorization-string> [--dry-run]"
    exit 1
fi

AUTH_HEADER="Authorization: Basic ${AUTH_STRING}"

# ============================================================================
# Step 1: Get the local repo's latest SHA on origin/<branch>
# ============================================================================
echo "==> Fetching latest from origin..."
git fetch origin "${GIT_BRANCH}"

LOCAL_SHA=$(git rev-parse "origin/${GIT_BRANCH}")
echo "    Local  origin/${GIT_BRANCH} SHA: ${LOCAL_SHA}"

# ============================================================================
# Step 2: Get the server's latest imported SHA via REST API
# ============================================================================
echo "==> Querying server for latest imported SHA..."
SERVER_SHA=$(curl -sf \
    -H "${AUTH_HEADER}" \
    "${OSEE_SERVER}/define/git/${OSEE_BRANCH_ID}/repo/${REPO_NAME}/latestSha" \
    2>/dev/null || echo "")

if [[ -z "${SERVER_SHA}" ]]; then
    echo "    WARNING: Server returned empty SHA. This may be the first import."
    echo "    The bundle will include all commits on origin/${GIT_BRANCH}."
    BUNDLE_RANGE="origin/${GIT_BRANCH}"
else
    echo "    Server SHA: ${SERVER_SHA}"

    # Verify the server SHA exists in our local repo
    if ! git cat-file -t "${SERVER_SHA}" &>/dev/null; then
        echo "    ERROR: Server SHA ${SERVER_SHA} not found in local repo."
        echo "    Make sure your local repo is up to date with the remote."
        exit 1
    fi

    # Check if they're already in sync
    if [[ "${SERVER_SHA}" == "${LOCAL_SHA}" ]]; then
        echo "    Server is already up to date. Nothing to do."
        exit 0
    fi

    BUNDLE_RANGE="${SERVER_SHA}..origin/${GIT_BRANCH}"
fi

# ============================================================================
# Step 3: Create the git bundle
# ============================================================================
DATE_STAMP=$(date +%Y%m%d-%H%M%S)
BUNDLE_FILE="${BUNDLE_DIR}/server-update_${DATE_STAMP}.bundle"

echo "==> Creating bundle: ${BUNDLE_FILE}"
echo "    Range: ${BUNDLE_RANGE}"

if [[ "${DRY_RUN}" == true ]]; then
    echo "    [DRY RUN] Would run: git bundle create ${BUNDLE_FILE} ${BUNDLE_RANGE}"
else
    git bundle create "${BUNDLE_FILE}" ${BUNDLE_RANGE}
    echo "    Bundle size: $(du -h "${BUNDLE_FILE}" | cut -f1)"
fi

# ============================================================================
# Step 4: Upload the bundle to the OSEE server
# ============================================================================
echo "==> Uploading bundle to OSEE server..."

ENCODED_REF_SPEC=$(python -c "import urllib.parse; print(urllib.parse.quote('${REF_SPEC}'))" 2>/dev/null \
    || echo "${REF_SPEC}")

UPLOAD_URL="${OSEE_SERVER}/define/git/${OSEE_BRANCH_ID}/repo/${REPO_NAME}/bundle?refSpec=${ENCODED_REF_SPEC}&gitBranchName=${GIT_BRANCH}"

if [[ "${DRY_RUN}" == true ]]; then
    echo "    [DRY RUN] Would POST ${BUNDLE_FILE} to:"
    echo "    ${UPLOAD_URL}"
else
    HTTP_RESPONSE=$(curl -s -w "\n%{http_code}" \
        -X POST \
        -H "Content-Type: application/octet-stream" \
        -H "${AUTH_HEADER}" \
        --data-binary "@${BUNDLE_FILE}" \
        "${UPLOAD_URL}")

    HTTP_BODY=$(echo "${HTTP_RESPONSE}" | head -n -1)
    HTTP_CODE=$(echo "${HTTP_RESPONSE}" | tail -n 1)

    if [[ "${HTTP_CODE}" -ge 200 && "${HTTP_CODE}" -lt 300 ]]; then
        echo "    Success (HTTP ${HTTP_CODE}): ${HTTP_BODY}"
    else
        echo "    ERROR (HTTP ${HTTP_CODE}): ${HTTP_BODY}"
        exit 1
    fi
fi

# ============================================================================
# Step 5: Cleanup
# ============================================================================
if [[ "${DRY_RUN}" == false ]]; then
    echo "==> Cleaning up bundle file..."
    rm -f "${BUNDLE_FILE}"
fi

echo "==> Done."
