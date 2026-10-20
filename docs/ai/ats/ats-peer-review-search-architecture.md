---
summary: "Architecture of ATS Peer Review search/filter: Action Search form, My Reviews navigate item, saved searches, the AtsSearchData/IAtsQuery model, and how roles/defects are stored"
tags: [ats, peer-review, review, search, world-editor, navigate-view, saved-search, defects, roles]
fileMatch: "**/org.eclipse.osee.ats*/**/*review*"
---

# ATS Peer Review Search & Filter — Architecture

## Scope

ATS Peer-to-Peer Reviews are workflows, exactly like Team Workflows and Tasks
(see [ats-cm-architecture.md](./ats-cm-architecture.md) for the shared workflow /
state-machine model). A Peer Review (`IAtsPeerToPeerReview` -> `IAtsAbstractReview`
-> `IAtsWorkItem`) therefore has a state machine, assignees, an originator, and a
work definition, and it is searchable through the same machinery as any other work
item.

This doc describes **how review search is built** — the three mechanisms (Action
Search, My Reviews, Saving/Loading), the underlying `AtsSearchData`/`IAtsQuery` model,
and how roles and defects are stored (which determines what is and is not queryable).
It is grounded in the current source.

> **Feature-by-feature "is this supported?" answers** — including the step-by-step
> navigator paths for each reviewer need and the Supported / Partially Supported /
> On Request status of each — live in the audience-facing deliverable
> **`ats-peer-review-search-supported.html`** (in this same folder). Update that file
> when the supported behavior changes; keep this file for the architecture.

---

## The three mechanisms

### 1. Action Search / Review Search (parameterized form)

- `AtsSearchWorkflowSearchItem` (ide `.../ide/search/`) is the generic "Action
  Search" form. `AtsSearchReviewSearchItem` (ide `.../world/search/`) extends it as
  the dedicated **"Review Search"** form (`isReviewSearch()==true`, namespace
  `ATS_QUERY_REVIEW_NAMESPACE`). It exposes WorkItemType, Review Type
  (`ReviewFormalType`), Title, Actionable Item, Team Def, Version, StateType, Change
  Type, User, UserType, and State Name widgets.
- The form reads/writes an `AtsSearchData` DTO
  (`.../ats.api/query/AtsSearchData.java`), the single search model shared by the IDE
  and REST.
- Execution: `AtsSearchDataSearch.createAtsQuery()` (`.../ats.core/query/`) maps each
  `AtsSearchData` field onto an `IAtsQuery`, which runs against the OSEE query layer
  and renders results in the `WorldEditor` table.
- REST equivalent: `AtsWorldEndpointApi.searchNew(AtsSearchData)` (impl
  `AtsWorldEndpointImpl`). There is no separate "AtsSearchEndpoint".

### 2. My Reviews (navigate item)

- The ATS Navigate View is built by **`AtsNavigateViewItems`** (ide
  `.../ide/navigate/AtsNavigateViewItems.java`). Items are added under an `XNavItemCat`
  (e.g. `TOP`, `REVIEW`). Each search item is a `SearchNavigateItem` wrapping a
  `WorldSearchItem`; running it opens the World Editor.
- The **REVIEW** folder currently registers:
  - `MyReviewSearchItem` ("My Reviews")
  - `MyOpenDefectItemsNavigateItem` ("My Open Defect Items") and
    `UsersOpenDefectItemsNavigateItem` ("User's Open Defect Items")
  - `AtsSearchReviewSearchItem` ("Review Search" form)
  - `OpenWorkflowByIdAction` ("Open Review by ID")
  - `ShowOpenWorkflowsByReviewType` for Decision/Peer "Show Open" and "Show Workflows
    Waiting" variants
  - `NewPeerToPeerReviewItem`, `GenerateReviewParticipationReport`,
    `WorkingCompletePeerReviewReportSearchItem`, and (admin) `ValidatePeerDefectsAction`
- `MyReviewSearchItem` (ide `.../world/search/`) builds an `AtsSearchData` with
  `workItemTypes += Review`, `userId = currentUser`, `userType = Assignee`. So **"My
  Reviews" = reviews where I am a current-state assignee.** "Assigned to me" is the
  current workflow assignee set (same as `MyWorldSearchItem`, which calls
  `getQueryService().getAssigned(user)`).
- `MyOpenDefectItemsNavigateItem` finds open peer-review defects originated by the
  current user when they have a resolution but are not closed. Its user-selected
  counterpart uses the same endpoint for the chosen active user.

### 3. Saving & Loading searches

- Service `IAtsSearchDataService` -> `AtsSearchDataServiceImpl` (`.../ats.core/query/`).
- A saved search is the `AtsSearchData` serialized to JSON and stored as a
  **multi-valued attribute on the current user artifact**:
  `CoreAttributeTypes.AtsActionSearch`. `getSavedSearches(namespace)` reads the user's
  attributes and filters by namespace. **Saved searches are per-user**, scoped by
  namespace (the Review Search form uses the review namespace).
- UI actions on the search form toolbar: `SaveSearchAction`, `SaveAsSearchAction`,
  `LoadSearchAction`, `DeleteSearchAction`, `ClearSearchAction`, `ShowSearchItemAction`.
  Saved searches appear in the navigator via `SavedActionSearchNavigateItem`.
- Sharing is manual only: `ShowSearchItemAction` exposes the JSON; another user pastes
  it via `ImportSavedSearchNavigateItem` ("Import Saved Search"), which saves it onto
  *their own* user artifact. There is no shared/team-owned saved-search store.

---

## What a search can filter on

`AtsSearchData` fields (translated in `AtsSearchDataSearch.createAtsQuery`):
title, changeTypes, priorities, `userId` + `userType`, `stateTypes`
(`List<StateType>`), `workItemTypes`, teamDefIds, aiIds, versionId, `states` (state
names), programId, insertionId, insertionActivityId, workPackage, applicId,
`reviewType` (`ReviewFormalType`), releasedOption, holdState, `attrValues` (generic
attribute tag search), configuration, buildImpact.

Key semantics:

- **Open / in-work vs completed** is `StateType` (`Working`, `Completed`, `Cancelled`).
  "Open" = `Working`; "completed" = `Completed`.
- **Assignee** is `userId` + `AtsSearchUserType` (`Assignee`, `AssigneeWas`,
  `Originated`, `Subscribed`, `Favorites`, `None`).
- **Completed-assignee nuance**: when `userType == Assignee` AND stateTypes contains
  `Completed`/`Cancelled`, `createAtsQuery` runs the search twice (current `Assignee`
  plus `AssigneeWas`), because a completed workflow has no current assignee. This is
  how "my completed reviews" actually resolves.

### Why roles and defects are NOT queryable

Both the **role** model (`UserRole`: `ReviewRole`, `userId`, per-reviewer `completed`
flag — stored under attribute `AtsAttributeTypes.Role`, "ats.Role") and the **defect**
model (`ReviewDefectItem`: `userId` creator, `severity`, `disposition`,
`resolvedUserId`, `closed`, `closedUserId` — stored under
`AtsAttributeTypes.ReviewDefect`, "ats.Review Defect") are persisted as **opaque XML
inside a single string attribute** per review. The OSEE query layer cannot filter on
sub-fields of that XML. Any filter on review role, per-reviewer completion, or defect
state requires loading each review and parsing XML in code (via `UserRoleManager` /
`ReviewDefectManager`), not an attribute query.

Defect resolved-vs-closed semantics (from `DefectXViewerMenu`): the producer/author
setting a resolution sets `resolvedUserId`; the reviewer accepting it sets
`closed=true` + `closedUserId`. `TransitionManager` blocks review completion with
`REVIEW_DEFECTS_NOT_CLOSED` while any defect is not closed.

### Most-desirable "resolved but not closed by me" filter (requested spec)

The highest-priority request (see 4.2 in `ats-peer-review-search-supported.html`) is a
single filter that displays a review when **all** of the following hold:

1. The review is **open** (`StateType.Working`), **AND**
2. I created at least one defect on it — `ReviewDefectItem.userId == me`
   (the defect creator / "who"), **AND**
3. That defect's resolution is complete — `ReviewDefectItem.resolution` has text
   (equivalently `resolvedUserId` is set by the producer), **AND**
4. I have not addressed/closed it — `ReviewDefectItem.closed == false`.

If all four are true, the review is included in the results. Implementing this is the
work noted as "On Request": because `ReviewDefectItem` is opaque XML inside the single
`ats.Review Defect` attribute, it needs either a server-side pass that loads candidate
reviews and evaluates defects via `ReviewDefectManager`, or promoting `userId`,
`resolution`/`resolvedUserId`, and `closed` into queryable attributes.

---

## Mapping to supported features

The per-feature answers (open reviews, completed reviews, started-but-not-completed,
defect resolved-but-not-closed, custom/saved searches, sharing, additional
attributes/columns) are maintained in **`ats-peer-review-search-supported.html`**. Each
item there is backed by the mechanisms above:

- **Open / completed / started-not-completed by assignee** → the Action Search form and
  the My Reviews navigate item, resolved through `AtsSearchData` → `IAtsQuery`
  (`StateType` for open vs completed; `AtsSearchUserType.Assignee` for assignment, with
  the dual-pass `Assignee`+`AssigneeWas` behavior for completed items).
- **Started but not completed** → the role→assignee linkage: completing a role
  un-assigns the user, so the assignee searches already express it.
- **Defect "resolved but not closed by me", filter by defect fields** → not queryable,
  because roles and defects are opaque XML (see below); available on request.
- **Save / run saved searches** → the Saving & Loading mechanism (per-user).
- **Share with team** → no shared store today; available on request.

When the supported behavior changes, update the HTML deliverable; update this doc only
when the underlying architecture changes.

## Key sources

- `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/query/AtsSearchData.java`
- `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/query/AtsSearchUserType.java`
- `plugins/org.eclipse.osee.ats.core/src/org/eclipse/osee/ats/core/query/AtsSearchDataSearch.java`
- `plugins/org.eclipse.osee.ats.core/src/org/eclipse/osee/ats/core/query/AtsSearchDataServiceImpl.java`
- `plugins/org.eclipse.osee.ats.ide/src/org/eclipse/osee/ats/ide/navigate/AtsNavigateViewItems.java`
- `plugins/org.eclipse.osee.ats.ide/src/org/eclipse/osee/ats/ide/world/search/MyReviewSearchItem.java`
- `plugins/org.eclipse.osee.ats.ide/src/org/eclipse/osee/ats/ide/world/search/AtsSearchReviewSearchItem.java`
- `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/review/ReviewDefectItem.java`
- `plugins/org.eclipse.osee.ats.api/src/org/eclipse/osee/ats/api/review/UserRole.java`
