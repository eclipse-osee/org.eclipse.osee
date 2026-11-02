---
summary: "OSEE Navigator architecture: how XNavigateItems are contributed by providers, organized into a category hierarchy, gated per-user by user groups, rendered (with the (Admin) label), and how the build report works. Covers the post-XXWidget-merge fixes: removal of the name-based admin hack, the folder groups-drop bug, centralized (Admin) decoration, and a descriptive build report."
tags: [ide, navigator, xnavigate, user-groups, admin, blam, ui]
fileMatch: "**/xnavigate/**,**/*XNavigateItem*,**/*NavigateItemCollector*,**/*NavigateView*,**/*NavigateViewItems*"
---

# IDE Navigator (OSEE Navigator) Architecture

## Overview

The OSEE Navigator is the tree of actions, searches, BLAMs, and folders shown in the ATS/OSEE perspective. It is
built from `XNavigateItem`s contributed by providers, organized into a category hierarchy, filtered per-user by user
groups, and rendered in a `FilteredTreePlus`.

This document captures how items are contributed, gated, nested, rendered, and how the build report works. It reflects
fixes made after the XXWidget merge (removal of the name-based admin hack, the folder groups-drop bug, centralized
`(Admin)` decoration, and a descriptive build report).

## Key Classes

| Class | Package | Role |
|-------|---------|------|
| `XNavigateItem` | `framework.ui.plugin...xnavigate` | Base for all nav items: name, image, categories, user groups, children |
| `XNavigateItemFolder` | `framework.ui.plugin...xnavigate` | A category/folder item (no action; holds children) |
| `XNavigateItemAction` | `framework.ui.plugin...xnavigate` | Nav item that runs a JFace `Action` or overridden `run()` |
| `XNavigateBlamItem` | `framework.ui.skynet...xnavigate` | Nav item wrapping an `AbstractBlam` (opens the BLAM editor) |
| `XNavItemCat` | `framework.ui.plugin...xnavigate` | Category token (top-level or dotted sub-category like `ATS.Admin`) |
| `XNavigateItemProvider` | `framework.ui.plugin...xnavigate` | Contributes items (e.g. `AtsNavigateViewItems`, `DemoNavigateViewItems`, `BlamNavigateViewItems`) |
| `NavigateItemCollector` | `framework.ui.plugin...xnavigate` | Collects provider items, builds the category hierarchy, filters per-user |
| `XNavigateLabelProvider` | `framework.ui.plugin...xnavigate` | Renders each item's display text (adds the `- (Admin)` suffix) |
| `NavigateView` | `ats.ide...navigate` | The Eclipse view: kicks off the load job, shows the build report |
| `XNavigateComposite` | `framework.ui.plugin...xnavigate` | Hosts the filtered tree; double-click runs the item |

## Contribution → Hierarchy → Filter → Render (the flow)

1. **Contribute.** `NavigateView.refreshData()` creates a fresh `XResultData rd` and a `NavigateItemCollector` over
   all `XNavigateItemProviders.getProviders()`, then runs `AtsNavigateViewItemsOperation` as a job. Each applicable
   provider's `getNavigateItems(items)` adds `XNavigateItem`s to the flat `items` list.

2. **Build hierarchy** — `NavigateItemCollector.getComputedNavItems(currUserUserGroups)`:
   - `createCategoryHierarchy()`:
     - **Top folders**: for each `XNavItemCat.orderedValues()` top category, any item whose categories contain it is
       registered into `catStrToItem` keyed by the item's **name** and added to the computed list.
     - **Sub-category folders** (items with `XNavItemCat.SUBCAT`): the dotted category (e.g. `ATS.Admin.Testing`) is
       reduced one segment at a time (`getReducedCat`) until a registered parent is found in `catStrToItem`, then the
       folder is nested under it and itself registered.
   - `createChildren()`: every remaining (leaf) item is attached to the parent folder registered under its category
     name.

3. **Filter per-user** — `currentUserInUserGroups(item)`: an item is visible if its `getUserGroups()` is empty
   (everyone) OR the current user's groups intersect the item's groups. Applied in BOTH hierarchy phases and in
   `createChildren()`.

4. **Render** — `XNavigateLabelProvider.getText()` returns `item.getName()`, appending `" - (Admin)"` when
   `item.isAdminOnly()`.

## Access / Visibility Gating (single mechanism: user groups)

Gating is **visibility-only** (there is no run-time access check on nav items). An item is admin-only iff its
`getUserGroups()` returns an admin group.

- `XNavigateItem.getUserGroups()` returns the explicit `groups` passed to the constructor (or empty = everyone).
- `AbstractBlam.getUserGroups()` likewise; `XNavigateBlamItem` delegates to the wrapped BLAM's `getUserGroups()`.
- `isAdminOnly()` (on `XNavigateItem`) is true when the resolved groups contain `CoreUserGroups.OseeAdmin` or
  `CoreUserGroups.AtsAdmin`.

### Token identity matters
`AtsAdmin` exists in BOTH `CoreUserGroups.AtsAdmin` and `AtsUserGroups.AtsAdmin` with the SAME token id (`136750L`).
`UserGroupArtifactToken` equality is id-based, so the two are `.equals()`/`.contains()` interchangeable. This lets the
framework layer (`framework.ui.plugin`, which cannot depend on the ATS `AtsUserGroups`) reference
`CoreUserGroups.AtsAdmin` and still correctly match items gated with `AtsUserGroups.AtsAdmin`.

### Membership check is cheap (cached)
Filtering uses `userService().getMyUserGroups()` → `UserServiceImpl.getUserGrps()`, a lazily-populated cached field
(cleared only by `clearCaches()`, pre-warmed by `NavigateView`). It is in-memory token `contains` comparisons — NO
per-item server round trip. Do NOT switch filtering to `IUserGroup.isCurrentUserMember()` (that path can hit ORCS).

### REMOVED: the name-contains-"Admin" hack
Previously `XNavigateItem.getUserGroups()` and `AbstractBlam.getUserGroups()` auto-added `OseeAdmin` when the item's
**name** contained the substring `"Admin"`. This was removed. Admin gating is now explicit only.

Why it was bad:
- It gated by display label, not intent. A folder named "JHU Admin" or "Admin" got auto-gated to OseeAdmin.
- It caused intermittent hierarchy orphan errors: a PARENT folder whose name contained "Admin" was filtered out for a
  non-admin, while its CHILDREN (names without "Admin") survived and had no parent → "could not be placed" errors. It
  was intermittent because it depended on the current user's cached admin membership at build time.

When removing it, every item that relied SOLELY on the hack for gating had to get an explicit `getUserGroups()`/`groups`
added (see "Items migrated off the hack").

## `XNavItemCat`: placement only, not access

`XNavItemCat` is for WHERE an item lives in the tree, not WHO can see it. `OSEE_ADMIN` (name "ADMIN") is being retired
as an access signal in favor of `getUserGroups()`. Categories can be dotted (`ATS.Admin`, `ATS.Admin.Testing`) to
express nesting; the collector reduces the dotted path to find the parent folder.

## The critical rule: parent-folder gating must match child gating

`catStrToItem` is keyed by folder **name**, and a leaf/sub-folder only attaches if its parent folder was registered.
If a parent folder is filtered out (user not in its group) but a child is not, the child orphans ("could not be
placed"). Therefore:

- **A folder and its children must be gated with the SAME user group**, OR
- the folder (and its children) are created inside a shared guard (e.g. `if (isAtsAdmin())`) so they appear/disappear
  together. The top-level ATS "Admin" folder uses this pattern: the folder and all its children are added only inside
  `addAdminItems()`'s `isAtsAdmin()` block.

`getName()` is the item's stable identity used for category matching — do NOT decorate `getName()` (see rendering).

## Rendering the `(Admin)` label — in the label provider, not the name

The `- (Admin)` suffix is applied ONLY in `XNavigateLabelProvider.getText()`, keyed on `item.isAdminOnly()`. It is a
display decoration; `getName()` stays clean because it is used as the `catStrToItem` key and for parent matching.

Do NOT decorate `getName()` and do NOT re-implement the suffix per item type (it used to live only in
`XNavigateBlamItem`, so non-BLAM admin items like `CreateAtsConfiguration` never showed it). Centralizing it in the
label provider makes it universal (BLAMs and non-BLAMs alike).

## BLAMs in the Navigator

- `BlamNavigateViewItems.getNavigateItems()` wraps each applicable `AbstractBlam` (skipping `XNavItemCat.NO_NAV`) in an
  `XNavigateBlamItem`, filtering by `blam.getUserGroups()` intersected with the current user's groups.
- `XNavigateBlamItem.getUserGroups()` delegates to the wrapped BLAM (no category translation).
- Admin BLAMs express admin-only via an explicit `getUserGroups()` override returning `AtsUserGroups.AtsAdmin` or
  `CoreUserGroups.OseeAdmin` (migrated off the `OSEE_ADMIN` category). None rely on the name hack.
- `XNavigateBlamItem.run()` opens the BLAM editor with a fresh BLAM copy so widgets don't collide.

## The `groups`-drop pitfall (fixed)

`XNavigateItemFolder(String, OseeImage, Collection<IUserGroupArtifactToken> groups, XNavItemCat...)` used to call
`super(name, image, xNavItemCat)` — **dropping `groups`**. Any folder built through that overload silently had no
explicit groups and fell back to the (now-removed) name hack. Fixed to forward `groups` to super. When adding a
constructor overload that accepts `groups`, ALWAYS forward it to super. (`XNavigateItemAction`'s groups overloads set
`this.groups` directly and are fine.)

## The build report (`XResultData rd`)

`NavigateView` shows `XResultDataUI.report(rd, "OSEE Navigator Results/Errors", ...)` only when `rd.isErrors()`. The
collector logs a readable trace plus descriptive, actionable errors:

- Intro line explaining the build phases.
- `Registered top-level folder [X]` and `Nested folder [X] under [Y]` for successful placement (context).
- Errors state WHAT failed, the LIKELY CAUSE (missing folder, or gating mismatch where the parent folder was filtered
  out but the child was not), and the FIX (add the folder / gate child and parent with the same user group).

Keep error messages descriptive and actionable — this report is the primary diagnostic for hierarchy problems.

## Items migrated off the name hack (must keep explicit gating)

When the name hack was removed, these got explicit groups so they stay admin-only:
- ATS: the `ATS.Admin` and `Define.Admin` "Admin" folders → `AtsUserGroups.AtsAdmin`.
- Demo: the "JHU Admin" folder AND its five `SearchNavigateItem` children (Show all Actions/Decision Review/PeerToPeer
  Review/Team Workflows/Tasks) → `AtsUserGroups.AtsAdmin` (folder and children gated the same so no orphan).
- `ToggleAtsAdmin` → `AtsUserGroups.AtsTempAdmin` (temp-admins who aren't currently admin must see it to toggle on; it
  also self-guards in `run()`).
- The two demo "- Admin" actions (`CreateActionUsingAllActionableItems`, `CreateXWidgetExampleAction`) →
  `AtsUserGroups.AtsAdmin`.

Folders that already passed explicit groups (`User Management.Admin`, `ATS.Versions.Admin`,
`ATS.Work Definition.Admin`, top-level ATS `Admin`) were unaffected — but note they only actually carried groups after
the `XNavigateItemFolder` groups-drop fix above.

## Rules of Thumb

- Gate by **user group** (`getUserGroups()`/constructor `groups`), never by name substring.
- A folder and everything under it must be gated identically (same group) or created under one shared guard.
- `getName()` is identity for hierarchy; never decorate it. Put display decorations in `XNavigateLabelProvider`.
- Any constructor overload that takes `groups` must forward it to super.
- Filtering must use the cached `getMyUserGroups()`; do not add per-item membership queries.
- Keep the build report's errors descriptive: what failed, likely cause, and fix.
