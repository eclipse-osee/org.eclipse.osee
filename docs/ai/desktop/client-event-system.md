---
summary: "Desktop client event system architecture: caching, event dispatch, remote events, and UI refresh"
tags: [desktop, events, cache, activemq, artifact, ui]
fileMatch: "**/skynet/core/internal/event/**,**/skynet/core/event/**,**/skynet/core/artifact/ArtifactCache*,**/skynet/core/artifact/cache/**,**/messaging/event/res/**"
---

# Desktop Client Event System & Artifact Caching

## Overview

The OSEE desktop (Eclipse RCP) client has an in-process event system that:
1. Notifies local UI components when artifacts/branches/transactions change
2. Sends change events to other clients via ActiveMQ
3. Receives remote events from ActiveMQ and updates the local cache + UI

## Key Components

| Class | Package | Role |
|-------|---------|------|
| `OseeEventManager` | `skynet.core.event` | Static public API. Delegates to `OseeEventService`. Entry points: `kickPersistEvent`, `kickBranchEvent`, `kickTopicEvent`, etc. |
| `OseeEventServiceImpl` | `skynet.core.internal` | OSGi DS component. Creates `EventTransport`, registers handlers, tracks `IEventListener` services. |
| `EventTransport` | `skynet.core.internal.event` | The engine. Handles send (local+remote), onEvent (remote→local), executor pool, sender identity. Implements `IFrameworkEventListener` for ActiveMQ. |
| `EventHandlers` | `skynet.core.internal.event` | Two `HashMap` registries: local handlers (event class → `EventHandlerLocal`) and remote handlers (remote event class → `EventHandlerRemote`). |
| `EventListenerRegistry` | `skynet.core.internal.event` | Stores listeners by QoS tier and event class. Uses reflection on listener interfaces to auto-map to compatible event types. |
| `ArtifactCache` | `skynet.core.artifact` | Static facade for artifact caching. Prevents duplicate Artifact instances. |
| `ArtifactIdCache` | `skynet.core.artifact.cache` | Backing store with 3 maps: `idCache`, `guidCache`, `artIdBranchCache`. Manages strong/weak references based on dirty state. |

## Event Handler Registration

At `OseeEventServiceImpl.start()`:

```
LOCAL HANDLERS (event class → handler):
  ArtifactEvent          → ArtifactEventHandler
  ArtifactTopicEvent     → ArtifactTopicEventHandler
  BranchEvent            → BranchEventHandler
  RemoteEventServiceType → RemoteServiceEventHandler
  TransactionEvent       → TransactionEventHandler
  TopicEvent             → TopicLocalEventHandler

REMOTE HANDLERS (remote event class → handler):
  RemotePersistEvent1       → ArtifactRemoteEventHandler
  RemoteArtifactTopicEvent  → RemoteArtifactTopicEventHandler
  RemoteBranchEvent1        → BranchRemoteEventHandler
  RemoteTransactionEvent1   → TransactionRemoteEventHandler
  RemoteTopicEvent1         → TopicRemoteEventHandler
```

## EventQosType

```java
enum EventQosType { PRIORITY, NORMAL }
```

- **PRIORITY** listeners fire first. Used for caches that must update before UI listeners react.
- **NORMAL** listeners fire after PRIORITY. UI components register here.

Only `TopicEventAdmin` is registered as PRIORITY (bridges TopicEvents to OSGi EventAdmin).

## Event Dispatch Flow

### Local persist (user saves an artifact)

```
1. User edits attribute → attribute.isDirty = true → ArtifactCache.updateCachedArtifact() → strong ref
2. Persist → DB write → OseeEventManager.kickPersistEvent(source, artifactEvent)
3. EventTransport.send(source, event):
   - createSender(source) → Sender with local session ID
   - handlers.getLocalHandler(ArtifactEvent.class) → ArtifactEventHandler
   - Submit to executor thread:
     → ArtifactEventHandler.send(transport, sender, event):
       a. transport.sendLocal(sender, event)  [dispatches to all registered listeners]
       b. transport.sendRemote(FrameworkEventUtil.getRemotePersistEvent(event))  [→ ActiveMQ]
```

### Remote event received (from another client via ActiveMQ)

```
1. ActiveMQ message arrives → FrameworkRelayMessagingListener.process()
   → IFrameworkEventListener.onEvent(remoteEvent)  [= EventTransport.onEvent()]
2. EventTransport.onEvent():
   - Create Sender from remoteEvent.getNetworkSender()
   - Check sender.isLocal() → skip if same sessionId (own event returning)
   - handlers.getRemoteHandler(event.getClass()):
     RemotePersistEvent1 → ArtifactRemoteEventHandler.handle()
3. ArtifactRemoteEventHandler.handle():
   a. Convert: FrameworkEventUtil.getPersistEvent(remotePersistEvent) → ArtifactEvent
   b. Update cache: updateModifiedArtifact() — apply attribute data to cached artifacts
   c. Update cache: updateRelations() — update relation links
   d. transport.send(sender, artifactEvent) → dispatches locally (same as step 3 above, minus sendRemote)
4. ArtifactEventHandler.send():
   - transport.isDispatchToLocalAllowed(sender) → true (sender is remote)
   - transport.sendLocal() → iterates PRIORITY then NORMAL listeners
   - sender.isLocal() is false → does NOT call sendRemote (no re-broadcast)
```

### Self-event filtering

When a local event is sent to ActiveMQ and returns via the subscription:
- `EventTransport.onEvent()` creates a `Sender` from the network sender's `sessionId`
- `Sender.isRemote()` compares the event's sessionId against `ClientSessionManager.getSession().getId()`
- If they match → `isLocal() = true` → event is skipped
- This prevents processing your own event twice

## ArtifactEventHandler — Local Dispatch to UI

```java
handle(listener, sender, event):
  1. Get listener's event filters: ((IEventFilteredListener) listener).getEventFilters()
  2. For each filter:
     - isMatch(event.getBranch()) — branch filter
     - isMatchArtifacts(event.getArtifacts()) — artifact type filter
     - isMatchRelationArtifacts(event.getRelations()) — relation type filter
  3. If all filters pass → listener.handleArtifactEvent(event, sender)
```

**Filter types:**
- `BranchIdEventFilter` — checks artifact.isOnBranch(branch)
- `ArtifactTypeEventFilter` — checks artifactType.inheritsFrom(filterType)
- `ArtifactEventFilter` — checks art.equals(specificArtifact)

## ArtifactEvent vs ArtifactTopicEvent

| | ArtifactEvent (legacy) | ArtifactTopicEvent (new) |
|-|------------------------|--------------------------|
| Wire format | JAXB XML via `RemotePersistEvent1` | JSON via `RemoteArtifactTopicEvent` |
| Artifact identity | `DefaultBasicGuidArtifact` (GUID string + artId) | `EventTopicArtifactTransfer` (ArtifactToken with numeric ID) |
| Attribute changes | `AttributeChange` → `RemoteAttributeChange1` | `EventTopicAttributeChangeTransfer` |
| Handler (remote) | `ArtifactRemoteEventHandler` | `RemoteArtifactTopicEventHandler` |
| Handler (local) | `ArtifactEventHandler` → `IArtifactEventListener` | `ArtifactTopicEventHandler` → `IArtifactTopicEventListener` |
| Controlled by | Always active | `FrameworkEventUtil.USE_NEW_EVENTS` flag |

Both handlers follow the same pattern: convert remote → local event, update cache, dispatch to listeners.

## TopicEventAdmin

A PRIORITY listener registered for `TopicEvent`. Its `handleTopicEvent()` re-posts the OSEE TopicEvent as an OSGi EventAdmin event. This bridges OSEE's internal topic events to the standard OSGi pub/sub mechanism.

## Artifact Cache Architecture

### Three maps in `ArtifactIdCache`

| Map | Key Type | Lookup | Purpose |
|-----|----------|--------|---------|
| `idCache` | `ArtifactToken` (ID+branch+viewId) | `getById(ArtifactToken)` | Legacy. Full token equality including viewId. Used by `updateReferenceType()`. |
| `guidCache` | `(String guid, BranchId)` | `getByGuid(guid, branch)` | Legacy GUID-based lookup. Used by old event paths. |
| `artIdBranchCache` | `(Long artId, Long branchId)` | `getByArtId(artId, branchId)` | **New.** Numeric ID lookup ignoring viewId. Used by web→desktop events. |

### Cache lifecycle

```
Artifact loaded from server (ArtifactQuery/ArtifactLoader)
  → ArtifactCache.cache(artifact)
    → idCache.put(artifact, cacheObject)
    → guidCache.put(guid, branch, cacheObject)
    → artIdBranchCache.put(artId, branchId, cacheObject)

cacheObject = strong reference if (eternal type OR dirty), else WeakReference<Artifact>
```

### Dirty state management

- Attribute/relation modified → `isDirty = true` → `ArtifactCache.updateCachedArtifact(artifact)` promotes to strong reference (prevents GC of edited artifacts)
- Persist/save → `isDirty = false` → `updateCachedArtifact()` demotes back to WeakReference (allows GC when no longer displayed)
- GC'd artifacts vanish from cache → next access triggers re-load from server

### Cache lookup priority (event processing)

`ArtifactCache.getActive(DefaultBasicGuidArtifact guidArt)`:
1. If `guidArt.getArtId() > 0` → `artIdBranchCache.get(artId, branchId)` (fast, viewId-independent)
2. Fallback → `guidCache.get(guid, branch)` (legacy string match)

`ArtifactCache.getActive(Collection<DefaultBasicGuidArtifact>)`:
- Same priority: try artId first, then GUID

## UI Refresh Chain

When a remote event arrives, the UI refresh happens through:

```
ArtifactRemoteEventHandler → updates cached artifact attributes in-memory
  → transport.send(sender, ArtifactEvent)
    → ArtifactEventHandler.send() → sendLocal()
      → for each IArtifactEventListener (after filter pass):
        listener.handleArtifactEvent(event, sender)

Example: ArtifactEditorEventManager.handleArtifactEvent():
  1. modifiedArts = artifactEvent.getCacheArtifacts(Modified, Reloaded)
     → ArtifactCache.getActive(collection) — resolves event artifacts to cached objects
  2. modifiedArts.contains(editor.getArtifact()) — checks if THIS editor's artifact was modified
  3. If yes → handler.refreshDirtyArtifact() → SWT widget redraw on UI thread
```

**Critical:** `getCacheArtifacts()` must resolve the event's artifact references to the SAME object instances that are in the cache (and displayed by the editor). This is why `ArtifactCache.getActive(Collection)` must use `artIdBranchCache` — the event's GUID field may not match the cached artifact's GUID when events come from the web server.

## DefaultBasicGuidArtifact.equals()/hashCode() — Dual-Path Identity

Used by event filtering, `ArtifactEvent.containsArtifact()`, and `HashSet`-based
matching such as `ArtifactEvent.isModified()` (which gates UI refresh).

```java
equals(Object obj):
  if (both have artId > 0):
    return artId == other.artId && sameBranch   // New: fast numeric comparison
  else:
    return guid.equals(other.guid) && sameType && sameBranch  // Legacy: GUID string

hashCode():
  return artId > 0 ? Long.hashCode(artId) : guid.hashCode()   // Consistent with equals
```

`hashCode()` **must** hash on `artId` when set, because `equals()` matches on `artId`.
Hashing on `artId` also makes `hashCode()` equal `Artifact.hashCode()` (id-based via
`BaseId`) for the same artifact — the invariant the event system relies on. Legacy
objects without an artId fall back to the GUID hash.

This ensures both new (web→desktop) and legacy (old desktop→desktop) events match
correctly, including in hash-based lookups where the web-originated event's GUID field
holds a numeric-id placeholder that differs from the cached artifact's real GUID.

## ActiveMQ Message Types

| Remote Event Class | Topic ID | Carried Data |
|-------------------|----------|--------------|
| `RemotePersistEvent1` | `AISIbRj0KGBv62x2pMAA` | Artifact changes with attribute data |
| `RemoteArtifactTopicEvent` | `ArqnvjHQVAGmszzgohwA` | Topic-based artifact changes (JSON) |
| `RemoteBranchEvent1` | `Aylfa1wlKXIbX2gOrVgA` | Branch state changes |
| `RemoteTransactionEvent1` | `AAn_QHkqUhz3vJKwp8QA` | Transaction purge events |
| `RemoteTopicEvent1` | `ARqNVjHQVAGmszjGOhwA` | Generic topic events |
| `RemoteBroadcastEvent1` | `Aylfa1y3ZBSIGbVU3JgA` | Broadcast messages |

## Potential Issues with Current Implementation

1. **`ActiveMqSseBridge` on desktop client** — The `org.eclipse.osee.orcs.rest` bundle runs on the desktop client unnecessarily, registering an extra `IFrameworkEventListener` that processes then discards web-originated events. Should be excluded from the client product.

2. **hashCode contract** — *Resolved.* `DefaultBasicGuidArtifact.hashCode()` now hashes on `artId` when set (falling back to the GUID hash otherwise), consistent with the artId-based `equals()`. The earlier assumption that these objects weren't hashed in the critical path was wrong: `ArtifactEvent.isModified()` uses `HashSet.contains()`, so the mismatched GUID-based hash was silently dropping web→desktop refreshes for artifacts whose event GUID (a numeric-id placeholder) differed from the cached artifact's real GUID.

3. **`idCache` viewId sensitivity** — The legacy `idCache` (ConcurrentHashMap<ArtifactToken>) requires exact branch equality including viewId. The new `artIdBranchCache` bypasses this limitation. The `idCache` is still used by `updateReferenceType()` which manages strong/weak reference promotion.
