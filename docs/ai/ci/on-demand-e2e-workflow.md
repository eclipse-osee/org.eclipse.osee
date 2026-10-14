# On-Demand E2E Workflow (design)

Status: **design only — not yet implemented.** This document captures the intended design so the
workflow can be created later without re-deriving the details.

## Goal

Give developers a **manually triggered** GitHub Actions workflow that runs the full Playwright e2e
suite against a server built from an arbitrary branch, **faster** than the normal build-validation
pipeline, for iterating on e2e failures (especially the SSE/presence real-time tests).

Two speedups vs the normal pipeline:

1. **Skip the slow Java bits that e2e doesn't need** — Java unit tests (`-DskipTests`) and the
   desktop client product build (`-Dskip-osee-client-all-product`). The server runtime zip
   (`plugins/org.eclipse.osee.server.p2/target/org.eclipse.osee.server.runtime.zip`) still builds
   with the product skipped (confirmed), which is all the container needs.
2. **Optionally skip building Java entirely** — reuse an existing server image when the change is
   web/test-only.

The full suite is always run (no grep filter): flake diagnosis wants the whole suite under the same
parallel load as CI.

## How it is triggered

`on: workflow_dispatch`. This adds a **Run workflow** button.

- **Web UI:** repo → **Actions** → select the workflow → **Run workflow** → pick the **branch** →
  set inputs → run.
- **CLI:** `gh workflow run "<workflow name>" --ref <branch> [-f server_source=build]`, then
  `gh run watch`.

**Gotcha:** the Run-workflow button only appears once the workflow file exists **on the default
branch (main)**. It can then execute against any branch. So the workflow file must be merged to main
before first use, even though it runs a feature branch's code.

## Inputs

One `choice` input, `server_source`:

| Value | Behavior | When to use |
|-------|----------|-------------|
| `build` (default) | Build the server image **locally on the runner from the dispatched branch**, with Java tests + product build + Angular tests skipped. **Do not push to GHCR.** | You changed Java and want it tested now. |
| `main-latest` | Pull `ghcr.io/<repo>/osee-server:latest` (main's image). | Web/test-only debugging; no Java change. |

`branch-latest` was considered and **rejected** — see "Why not branch-latest" below.

## Server-source detail

### `build` — build locally, never push (recommended default)

- Reuse the existing `./.github/actions/build-osee-binary` with:
  - `SKIP_JAVA_TESTS: "true"` → adds `-DskipTests -Dskip-help-tests`
  - `SKIP_PRODUCT_BUILD: "true"` → adds `-Dskip-osee-client-all-product`
  - `SKIP_ANGULAR_TESTS: "true"` (as the current e2e path already does)
  - `ANGULAR_BUILD_TYPE`: same as the e2e path (`forced_sso_java_release`) so the bundled web app
    matches what the container serves. (Note: the e2e browser tests themselves run against the
    dev-served app per the standards; confirm whether the container needs the web bundle for these
    specs or whether the compose server is API-only for e2e.)
- Build the docker image from `.github/docker/osee-server/Dockerfile` with a **fixed local tag**
  (e.g. `osee-server:dispatch`) using `docker build` (no `push`).
- Generate a compose file that references that **local tag** instead of a GHCR ref. The existing
  `./.github/actions/generate-docker-compose` hardcodes a GHCR `osee_image`; for local mode either
  parameterize it or write a small compose inline that points at `osee-server:dispatch`.

### `main-latest` — pull existing image

- Use `ghcr.io/<repo>/osee-server:latest` as the compose `osee_image`. Always exists. No build.

## Cleanup

### Running containers (server + postgres)

- End the job with `docker compose down` guarded by **`if: always()`** (not `!cancelled()`), so a
  cancelled run still tears down.
- The e2e job runs on GitHub-hosted `ubuntu-latest` (ephemeral VM), so the runner is discarded at
  job end regardless — container cleanup is hygiene, not strictly required. (If this is ever moved
  to a self-hosted runner, the `if: always()` teardown becomes essential.)

### GHCR image package (osee-server)

- **`build` mode does not push**, so there is **no GHCR package to clean up** — the local image dies
  with the runner. This is the main reason to prefer build-locally over push.
- **`main-latest` pushes nothing** (pull only).
- Therefore this workflow adds **no** GHCR image accumulation and needs no delete step.

## Why not `branch-latest`

An earlier idea was a `branch-latest` mode that reuses the last server image CI built for the
branch. Rejected because the ref schemes do not line up:

- `./.github/actions/current-ref` derives the ref from **`github.ref_name`**:
  - PR-triggered build-validation: `github.ref_name` is `<pr-number>/merge` → normalized
    `<pr-number>-merge`. The PR pipeline pushes `osee-server/<pr-number>-merge` and
    `clean-up.yml` (on PR close) deletes exactly that.
  - `workflow_dispatch` on branch `webSSE`: `github.ref_name` is `webSSE` → `docker-current-ref`
    yields `/webSSE` → image path `osee-server/webSSE`.
- So a dispatch cannot reliably find the PR pipeline's image (different ref), and if a dispatch
  *pushed* `osee-server/webSSE`, **`clean-up.yml` would never delete it** (it only deletes
  `<pr-number>-merge`) — a registry leak. Avoiding push (the `build` mode) sidesteps this entirely.

## Playwright configuration already in place

The Playwright config (`web/apps/osee/playwright.config.ng.ts`) and the existing `web_e2e` job were
updated to aid debugging (already committed separately):

- `retries: 2` in CI — a genuinely flaky test is retried and reported as "flaky" rather than
  "failed".
- `trace: 'on-first-retry'` + `video: 'on-first-retry'` — a CI-only failure produces a replayable
  trace/video on the retry.
- The `web_e2e` job uploads `test-results/`, `playwright-report/`, `results/` as an artifact on
  failure (before `docker compose down`), so screenshots/traces/videos are retrievable.

The on-demand workflow should reuse these (same config, same upload step, `if: always()` teardown).

## Sketch (not final)

```yaml
name: E2E On Demand
on:
  workflow_dispatch:
    inputs:
      server_source:
        description: How to obtain the osee-server image
        type: choice
        default: build
        options: [build, main-latest]

jobs:
  e2e:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v6

      # build mode: build server binary (tests + product skipped), then a LOCAL docker image
      - if: inputs.server_source == 'build'
        uses: ./.github/actions/build-osee-binary
        with:
          SKIP_JAVA_TESTS: "true"
          SKIP_PRODUCT_BUILD: "true"
          SKIP_ANGULAR_TESTS: "true"
          ANGULAR_BUILD_TYPE: "forced_sso_java_release"
      - if: inputs.server_source == 'build'
        run: docker build -f .github/docker/osee-server/Dockerfile -t osee-server:dispatch .

      # compose up (compose file references osee-server:dispatch OR ghcr .../osee-server:latest),
      # then web-setup, then full suite.
      - run: docker compose up -d --wait
      - uses: ./.github/actions/web-setup
      - run: pnpm -r run playwright

      - if: failure()
        uses: actions/upload-artifact@v4
        with:
          name: playwright-results
          path: |
            web/apps/osee/test-results/
            web/apps/osee/playwright-report/
            web/apps/osee/results/
          retention-days: 7
          if-no-files-found: ignore

      - if: always()
        run: docker compose down
```

## Open items to resolve at implementation time

1. **Compose image parameterization.** `generate-docker-compose` hardcodes a GHCR `osee_image`.
   For `build` (local, no push) the compose must reference `osee-server:dispatch`. Either add an
   input to that action or write the compose inline for the local case.
2. **Does the compose server need the bundled web app for these e2e specs?** The web-standards say
   e2e runs against the dev-served app (`ng serve`, DEV mode). Confirm whether `web-setup` +
   Playwright's own web server serve the app, and the compose container is API-only — if so, the
   `ANGULAR_BUILD_TYPE` bundling in `build` mode may be unnecessary for e2e and could be skipped for
   further speed.
3. **Runner type.** Confirmed `ubuntu-latest` (GitHub-hosted, ephemeral) for the current e2e job;
   keep it so container cleanup stays free.
4. **Maven cache.** `build-osee-binary` caches `~/.m2` keyed on `hashFiles('**/pom.xml')`; a warm
   cache makes repeat `build` runs much faster. No action needed, just noted.
```
