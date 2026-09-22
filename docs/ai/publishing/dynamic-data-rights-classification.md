---
summary: "Data rights classification enum is a compiled seed plus a runtime refresh from the DataRightsFooters artifact; explains the valid-set vs dbLoadedEnumTokens distinction and the AttributeTypeEnum promotion contract"
tags: [publishing, data-rights, enum, attribute-type, required-indicator]
fileMatch: "**/AttributeTypeEnum.java,**/DataRightsClassificationNameParser.java,**/datarights/**,**/DataRightsClassificationClientRefresher.java"
---

# Dynamic Data Rights Classification

## Overview

The set of valid `DataRightsClassification` values is not fully compiled. It is a small compiled
**seed** (all current `RequiredIndicator` members, including `Unspecified`) plus values discovered
at runtime from the `DataRightsFooters` artifact on the `Common` branch. The seed keeps class-load
and the `CoreArtifactTypes` attribute default valid before any database exists; the runtime refresh
promotes footer-defined names into the valid selectable set once the DB is reachable.

Do not "fix" this by hard-coding footer names back into the enum. Adding a classification is a data
edit to the footers artifact, not a code change.

## AttributeTypeEnum: two buckets, one ordinal scheme

`AttributeTypeEnum` holds two lists, and the distinction is load-bearing:

- `enumTokens` — the **valid** selectable set (pick lists, `isValidEnum`, `getEnumStrValues`).
- `dbLoadedEnumTokens` — a **tolerance** bucket for arbitrary stored values seen by
  `valueFromStorageString`. These are intentionally **not** valid selectable values; they exist so
  any stored string round-trips to a stable token (`==`/`equals`) even if it was never a valid pick.

Both methods that mint tokens assign the next ordinal as `enumTokens.size() + dbLoadedEnumTokens.size()`.
Because the ordinal is derived from the combined size, the two paths never collide on an ordinal as
long as they share that scheme. Keep it shared.

### The promotion rule (easy to get wrong)

`addDbLoadedValidEnum(name)` promotes a name into the valid set. It must be identity-preserving:

1. If the name is already valid, return the existing valid token.
2. If the name was previously minted into `dbLoadedEnumTokens` by `valueFromStorageString`, **move
   that same token** into `enumTokens` (preserve its ordinal). Do NOT clone a new token — cloning
   creates two tokens with the same name and different ordinals, breaking the round-trip identity
   guarantee (a stored value and its promoted valid value would no longer be `==`/`equals`).
3. Otherwise mint a new token with the next ordinal.

Both mutators are `synchronized`; both token lists are `CopyOnWriteArrayList` so unsynchronized
readers (`isValidEnum`, `getEnumStrValues`, `getEnum`, `getEnumValues`) get stable snapshots and
never throw `ConcurrentModificationException`.

## Shared parsing rule

A `DataRightsFooters` `GeneralStringData` value encodes the classification name on line 1 and the
footer content on the remaining lines. `DataRightsClassificationNameParser` is the single source of
truth: `value.split("\n", 2)`; contribute a result only when the split yields two parts; the name is
`parts[0].trim()` (blank first line contributes nothing); the content is `parts[1].trim()`. Use
`parseFooter` when you need both name and content so they cannot come from different rules. Both the
server loader and `DataRightClassificationMap.create` must call this parser rather than re-splitting.

## Where the refresh runs

Server side (`org.eclipse.osee.define` datarights package, package-private impls; interfaces are
public only so tests can mock them across the OSGi classloader boundary):

- `DataRightsFootersIndicatorLoader` reads the footer names. A missing/unreadable artifact returns
  an empty set — never throws — so the seed set governs.
- `RequiredIndicatorRefresher` diffs footer names against the live valid set and promotes the new
  ones. Additive and idempotent.
- Triggers, and only these two: server startup (`DefineOperationsImpl.start`) and data-rights cache
  clear (`DataRightsOperationsImpl.deleteCache`). Not per attribute access, not per publish.

Client side: the client type registry is compiled and independent of the server's, so
`DataRightsClassificationClientRefresher.ensureRefreshed()` promotes footer names into the client's
own valid set (once per session, reset on cache clear) before building a `DataRightsClassification`
pick-list.

## Invariants worth preserving

- `isValidEnum("Unspecified")` is always true, DB or not (seed guarantee; matches the attribute
  default and the footer default classification).
- Refresh never removes a value; a second refresh with unchanged footers adds zero.
- `valueFromStorageString(v)` always returns a token named `v`, whether or not `v` is valid.
