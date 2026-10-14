---
summary: "AcTra web feature: ATS world view, workflow editor, create-action, attachments, and the create-working-branch flow"
tags: [web, actra, ats, workflow, angular]
fileMatch: "web/apps/osee/src/app/actra/**"
---

# AcTra (ATS) Web Feature

AcTra is the web front end for OSEE's ATS (Action Tracking System). It lists a user's work items
(the "world"), opens a team workflow for editing/transitioning, creates actions, and manages
workflow attachments. All ATS work items (actions, team workflows, tasks, reviews) are artifacts on
the **Common branch** (`COMMON_BRANCH_ID`), so AcTra reads/writes ATS data on Common and uses working
branches only for the change content a workflow produces.

## Routes

`actra.routes.ts` (lazy):
- `''` → redirect to `world`.
- `world` → `world/world.routes.ts` → `ActraWorldComponent`.
- `workflow` → `workflow/workflow.routes.ts` → `ActraWorkflowEditorComponent` (workflow id via `?id=` query param).
- `action/create` → `ActraCreateActionPageComponent`.
- `''` (outlet `toolbar`) → `toolbar.routes.ts` (toolbar shell + AcTra logo).

Navigation between AcTra surfaces opens the workflow editor in a new tab with `?id=<teamWfId>`.

## Components

### `ActraWorldComponent` (`world/`)
The "My World" table of the current user's work items.
- Data via `ActraWorldHttpService.getWorldDataMy()` (`/ats/world/my`) or, with `?op`/`collId`/`custId`
  query params, `getWorldData(collId, custId)` (`/ats/world/coll/{collId}/json/{custId}`).
- Renders a `MatTableDataSource<worldRow>` with sort + free-text filter over all columns.
- **Relevance-based refresh:** re-fetches on a `repeat({ delay })` that fires when an ATS-branch
  change is relevant to this user — either the change newly associates them (their id appears in the
  event's `associatedUsers`, `artId` encoding) or it touches a work item already in their list
  (matched by row id) — or on `resync$` (reconnect). This avoids refreshing on unrelated system-wide
  workflow changes with no server-side per-user query. See `docs/ai/web/sse-real-time.md`.

### `ActraWorkflowEditorComponent` (`workflow/`)
Edits/transitions a single team workflow (routed `?id=`).
- Loads `/ats/teamwf/details/{id}` via `ActionService.getTeamWorkflowDetails` into a `toSignal` with
  bounded `retry` + `catchError(teamWorkflowDetailsImpl)` (survives warmup 500s) and a `repeat({ delay })`
  refetch trigger merging: `changeNotification.forArtifact(COMMON, workflowId)`, `workingBranchChanges$`
  (keyed on `wf.workingBranch.id`), `branchCreatedWhileNoBranch$` (a `created` branch event matched by
  `associatedArtifactId === this workflow's artifact id`, while `workingBranch.id === '-1'`), and
  `resync$`, and `_forceRefetch$` (a manual trigger fired after the editor's own save or a conflict
  discard, so the acting tab reflects the saved state — its own SSE echo is `isLocal` and does not
  refetch). The SSE/branch legs are gated on `!hasChanges()`.
- The `forArtifact(COMMON, workflowId)` leg is further filtered to changes whose `changeTypes` include
  `attribute_modified` — the details payload reflects the workflow artifact's own attributes/state, so
  only edits, transitions, and assignee changes (all `attribute_modified`) reload it. A pure relation
  change that merely names the workflow as a touched writeable — e.g. relating/unrelating an attachment
  (a separate resource) — must not reload the whole editor.
- Attribute edits use the shared `AttributesEditorComponent` + `TransactionService`. The editor emits
  only attributes the user actually changed (it snapshots each instance's baseline value on first
  render, keyed by object reference since `[(ngModel)]` mutates the value in place, and emits an
  instance only when its current value differs — clearing a value to empty still counts as a change).
  `saveChanges` then splits the pending edits by whether the instance already exists: real
  id/gamma → `set`, a never-saved type-template (`id === '-1'`, e.g. a Boolean toggled on) → `add`.
  Without emit-only-changed the save swept every non-empty attribute into the transaction, and without
  the set/add split a newly-set attribute was dropped by the set-mapping (which requires a valid id).
- Conflict handling
  is wired via a shared `ConflictController` (`this.conflict`, from `@osee/shared/conflict-resolution`):
  a remote `attribute_modified` while dirty raises `conflict.conflicted()`, which shows the shared
  banner and disables Save (red icon); the user resolves via the shared dialog (`conflict.resolve()`)
  or discards (`conflict.discard()`). Pending edits are keyed by `typeId` (a workflow's edited
  attributes can come from type definitions with a placeholder instance id, and the save path is
  typeId/value based). See `docs/ai/web/conflict-resolution.md`.
- Hosts: action drop-down, commit manager, update-from-parent, change report, attachments, presence
  avatars, and — when the workflow has no working branch (`workingBranch.id === '-1'`) — the
  create-working-branch button (see below).
- **Presence context:** `workflow/${workflowId}` (globally unique, not branch-scoped) so everyone
  viewing the same workflow shares one context.

### `ActraCreateActionPageComponent` (`actra-create-action-page/`)
Wraps the shared `CreateActionFormComponent`. On submit calls `CreateActionService.createAction(...)`
and opens the new team workflow (`/actra/workflow?id=<newWfId>`) in a new tab.

### Attachments (`components/`)
`WorkflowAttachmentsComponent` + add/update dialogs, backed by `AttachmentService`. Attachments are
`General Document` artifacts on the **Common branch** (the ATS branch), related to the workflow via
`SUPPORTING_INFO`, carrying Name/Extension/Native Content attributes. CRUD goes through
`CurrentTransactionService`/`TransactionService` mutations (create/modify/delete artifact), which commit
on Common and emit `artifactChanged` invalidations there. `MAX_ATTACHMENT_SIZE_BYTES` = 50 MiB.

**Scoped SSE refresh (attachments live on Common, not the working branch):** the attachments list must
refetch only when its own set changes, not on every Common change. `getAttachmentsResource` keys the
refresh on `forBranch(COMMON)` filtered to invalidations touching either:
- the **workflow artifact id with a RELATION change** (`relation_added`/`relation_deleted`) — an
  upload/delete relates/unrelates the attachment, so the workflow is a touched writeable. The relation
  gate matters: a pure attribute edit on the workflow (e.g. its description) also names the workflow as
  a touched writeable, but leaves the attachment set unchanged and must **not** refetch the list.
- any **currently-listed attachment id** — an in-place update touches only the `General Document`
  artifact, not the workflow.

Plus `resync$` for changes missed during an SSE disconnect. The relevant ids are read per-emission (the
workflow id and the live loaded attachment ids), so the scope stays current without re-subscribing. The
component supplies the current ids to the service as a `currentAttachmentIds` signal. This replaced an
earlier design that keyed on the working branch (wrong — attachments are on Common) and the legacy
`uiService.updateCount()` global refresh.

## Services (`services/`)

- **`ActraWorldHttpService`** — read-only world GETs (`/ats/world/my`, `/ats/world/coll/...`).
- **`AttachmentService`** — attachment resource (`httpResource` on `/ats/teamwf/{id}/attachments`,
  refetched via the Common-scoped SSE trigger described under Attachments above) and
  upload/update/delete/download via transactions. Files are base64-encoded client-side.

Other ATS operations (workflow details, transitions, work definitions, create action, create working
branch) go through the shared `@osee/configuration-management/services` (`ActionService`,
`CreateActionService`), not AcTra-local services.

## Types (`types/actra-types.ts`)

- `world` / `worldRow` — the world table payload (`orderedHeaders`, `rows`, `title`, `collectorArt`).
  `worldRow` is a `Record<string,string>`; row identity for relevance matching is `row['id']`.
- `WorkflowAttachment` — attachment record (ids/gammas for name, extension, native-content attributes).
- `teamWorkflowDetails` (in `@osee/shared/types/configuration-management`) — the workflow-editor payload;
  `workingBranch: { id, name, branchState }` with default id `'-1'` meaning "no working branch yet".

## Create-working-branch flow

`CreateWorkingBranchFromWorkflowButtonComponent` (under `configuration-management/components`) is shown
when `workingBranch.id === '-1'`. On click it:
1. `POST /ats/config/branch` via `ActionService.createWorkingBranchForAction`, routed through the
   `MutationService` chokepoint (server-ready gated), emitting a local branch `created` notification
   that carries `associatedArtifactId` (the workflow id). This is the sole local signal — the open
   editor refreshes off it via `branchCreatedWhileNoBranch$`, matched by `associatedArtifactId`. No
   artifact emit: branch creation writes only the branch row (`associated_art_id`), not the workflow
   artifact on Common.
2. Opens the artifact explorer on the new branch in a new tab:
   `/ple/artifact/explorer?branchId=<id>&branchType=working&panel=Artifacts` (query-param navigation
   — there is no `working/<id>` route segment). The URL is built with `router.serializeUrl(...)` then
   run through `Location.prepareExternalUrl(...)` before `window.open(url, '_blank', 'noopener')`.
   `prepareExternalUrl` applies the app's base href (`/osee/` in production) the same way `routerLink`
   does for the template's "Open branch in Artifact Explorer" anchor; a bare `serializeUrl` path omits
   the base href and 404s in production. Mirrors the `world.component.ts` open-in-new-tab pattern.

Server side, the branch is created through `orcsApi.getBranchOps().createBranch`, which fires the
`BranchChangeTopic` chokepoint, so other web clients and the desktop are notified `created` uniformly
(the endpoint no longer broadcasts per-call). See `docs/ai/web/sse-real-time.md`.

## Real-time behavior

AcTra is a full SSE/GET-on-notify consumer: ATS transitions are ordinary ORCS transaction commits, so
they flow through the standard `TransactionCommitTopic` → SSE path with no ATS-specific plumbing;
working-branch create/commit/etc. flow through the `BranchChangeTopic` path. All AcTra views merge
`resync$` into their refetch triggers to recover events missed during a disconnect. The
relevance/refresh and reconnect details live in `docs/ai/web/sse-real-time.md`.
