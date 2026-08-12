# OSEE Event System Architecture

## Overview

The OSEE Event System provides real-time synchronization of data changes across multiple IDE clients connected to the same OSEE server. When any client persists an artifact change, branch operation, or access control modification, all other connected clients receive notifications and update their local caches accordingly.

The system uses **Apache ActiveMQ** as the message broker, with JMS topics for publish/subscribe communication between clients.

---

## High-Level Event Flow

```mermaid
flowchart LR
    subgraph Client_A ["IDE Client A (Event Source)"]
        A1[User Action<br/>e.g. Save Artifact]
        A2[OseeEventManager]
        A3[EventTransport]
        A4[Local Listeners]
    end

    subgraph ActiveMQ ["ActiveMQ Broker"]
        MQ[JMS Topics<br/>RemotePersistEvent1<br/>RemoteBranchEvent1<br/>RemoteTopicEvent1<br/>etc.]
    end

    subgraph Client_B ["IDE Client B (Event Receiver)"]
        B1[FrameworkRelayMessagingListener]
        B2[EventTransport.onEvent]
        B3[Remote Event Handler<br/>Updates Cache]
        B4[Local Listeners]
    end

    A1 --> A2
    A2 --> A3
    A3 --> A4
    A3 -->|sendRemote| MQ
    MQ -->|JMS Subscription| B1
    B1 --> B2
    B2 --> B3
    B3 --> B4
```

---

## Component Architecture

```mermaid
classDiagram
    class OseeEventManager {
        <<static facade>>
        +addListener(IEventListener)
        +removeListener(IEventListener)
        +kickPersistEvent(Object, ArtifactEvent)
        +kickBranchEvent(Object, BranchEvent)
        +kickTopicEvent(Object, TopicEvent)
        +kickAccessTopicEvent(Object, payload, topic)
        +kickArtifactTopicEvent(Object, ArtifactTopicEvent)
    }

    class OseeEventService {
        <<interface>>
        +send(Object, FrameworkEvent)
        +receive(RemoteEvent)
        +isConnected() boolean
    }

    class EventTransport {
        +send(Object, FrameworkEvent)
        +sendLocal(Sender, FrameworkEvent)
        +sendRemote(RemoteEvent)
        +onEvent(RemoteEvent)
    }

    class IOseeCoreModelEventService {
        <<interface>>
        +sendRemoteEvent(RemoteEvent)
        +addFrameworkListener(IFrameworkEventListener)
        +addConnectionListener(ConnectionListener)
    }

    class EventListenerRegistry {
        -qosToListeners: Map
        +addListener(EventQosType, IEventListener)
        +getListeners(EventQosType, FrameworkEvent)
    }

    class EventHandlers {
        +getLocalHandler(FrameworkEvent)
        +getRemoteHandler(RemoteEvent)
    }

    OseeEventManager --> OseeEventService
    OseeEventService <|.. OseeEventServiceImpl
    OseeEventServiceImpl --> EventTransport
    EventTransport --> EventListenerRegistry
    EventTransport --> EventHandlers
    EventTransport --> IOseeCoreModelEventService
    EventTransport ..|> IFrameworkEventListener
```

---

## Event Types

| Event Class | Purpose | Remote Counterpart | JMS Topic |
|---|---|---|---|
| `ArtifactEvent` | Legacy artifact/relation persist | `RemotePersistEvent1` | `topic:...RemotePersistEvent1` |
| `ArtifactTopicEvent` | New JSON-based artifact persist | `RemoteArtifactTopicEvent` | `topic:artifact.topic.event` |
| `BranchEvent` | Branch create/delete/commit/purge | `RemoteBranchEvent1` | `topic:...RemoteBranchEvent1` |
| `TransactionEvent` | Transaction deleted/purged | `RemoteTransactionEvent1` | `topic:...RemoteTransactionEvent1` |
| `TopicEvent` | Generic topic with properties | `RemoteTopicEvent1` | `topic:...RemoteTopicEvent1` |
| `AccessTopicEvent` | Access control changes | (via TopicEvent) | (via RemoteTopicEvent1) |

---

## Detailed Sequence: Local Artifact Persist → Remote Notification

```mermaid
sequenceDiagram
    participant User as User Action
    participant OEM as OseeEventManager
    participant ET as EventTransport
    participant AEH as ArtifactEventHandler
    participant FEU as FrameworkEventUtil
    participant LR as EventListenerRegistry
    participant MQ as IOseeCoreModelEventService
    participant AMQ as ActiveMQ Broker

    User->>OEM: kickPersistEvent(source, artifactEvent)
    OEM->>ET: send(source, artifactEvent)
    ET->>ET: createSender(source) with session info
    ET->>AEH: send(transport, sender, event)
    
    Note over AEH: Step 1: Local Dispatch
    AEH->>ET: sendLocal(sender, event)
    ET->>LR: getListeners(PRIORITY, ArtifactEvent)
    LR-->>ET: priority listeners
    ET->>ET: handle() each priority listener
    ET->>LR: getListeners(NORMAL, ArtifactEvent)
    LR-->>ET: normal listeners
    ET->>ET: handle() each normal listener
    
    Note over AEH: Step 2: Remote Send
    AEH->>FEU: getRemotePersistEvent(event)
    FEU-->>AEH: RemotePersistEvent1
    AEH->>ET: sendRemote(remotePersistEvent)
    ET->>MQ: sendRemoteEvent(remotePersistEvent)
    MQ->>AMQ: publish to JMS topic
```

---

## Detailed Sequence: Remote Event Reception

```mermaid
sequenceDiagram
    participant AMQ as ActiveMQ Broker
    participant FRL as FrameworkRelayMessagingListener
    participant ET as EventTransport
    participant AREH as ArtifactRemoteEventHandler
    participant FEU as FrameworkEventUtil
    participant Cache as ArtifactCache
    participant LR as EventListenerRegistry

    AMQ->>FRL: JMS message arrives
    FRL->>FRL: cast to RemotePersistEvent1
    FRL->>ET: onEvent(remotePersistEvent)
    ET->>ET: Sender.createSender(networkSender)
    ET->>ET: Check sender.isLocal()?
    Note over ET: Skip if sessionId matches local session
    ET->>AREH: handle(transport, sender, remoteEvent)
    
    Note over AREH: Update local artifact cache
    AREH->>FEU: getPersistEvent(remoteEvent)
    FEU-->>AREH: ArtifactEvent
    AREH->>Cache: updateArtifacts (Modified/Deleted/Added)
    AREH->>Cache: updateRelations
    
    Note over AREH: Re-dispatch locally
    AREH->>ET: send(sender, artifactEvent)
    ET->>LR: dispatch to all local listeners
```

---

## Sender Identity & Local vs Remote Detection

The `Sender` class is critical for preventing event echo (a client receiving its own event back). It wraps an `IdeClientSession` containing:

```mermaid
classDiagram
    class Sender {
        -sourceObject: String
        -oseeSession: IdeClientSession
        +isRemote() boolean
        +isLocal() boolean
        +getNetworkSender() NetworkSender
        +createSender(Object) Sender
        +createSender(NetworkSender) Sender
    }

    class NetworkSender {
        +sourceObject: Object
        +sessionId: String
        +machineName: String
        +userId: String
        +machineIp: String
        +port: int
        +clientVersion: String
    }

    class IdeClientSession {
        +id: String
        +clientName: String
        +clientAddress: String
        +userId: String
        +clientPort: String
        +clientVersion: String
    }

    Sender --> IdeClientSession
    Sender --> NetworkSender
```

**`isRemote()` logic:** Compares the sender's `sessionId` against `ClientSessionManager.getSession().getId()`. If they differ, the event originated from another client.

---

## Event Handler Architecture

```mermaid
classDiagram
    class EventHandlerLocal~L,E~ {
        <<interface>>
        +handle(L listener, Sender, E event)
        +send(Transport, Sender, E event)
    }

    class EventHandlerRemote~R~ {
        <<interface>>
        +handle(Transport, Sender, R remoteEvent)
    }

    class ArtifactEventHandler {
        +handle(): filters then dispatches
        +send(): sendLocal + sendRemote
    }

    class ArtifactRemoteEventHandler {
        +handle(): deserialize, update cache, re-send locally
    }

    class BranchEventHandler {
        +send(): sendLocal + sendRemote
    }

    class BranchRemoteEventHandler {
        +handle(): deserialize, re-send locally
    }

    class TopicLocalEventHandler {
        +send(): sendLocal + conditionally sendRemote
    }

    class TopicRemoteEventHandler {
        +handle(): deserialize, re-send locally
    }

    EventHandlerLocal <|.. ArtifactEventHandler
    EventHandlerLocal <|.. BranchEventHandler
    EventHandlerLocal <|.. TopicLocalEventHandler
    EventHandlerRemote <|.. ArtifactRemoteEventHandler
    EventHandlerRemote <|.. BranchRemoteEventHandler
    EventHandlerRemote <|.. TopicRemoteEventHandler
```

Each `EventHandlerLocal.send()` follows the same pattern:
1. If dispatch-to-local is allowed → `sendLocal()` (iterate listeners)
2. If sender is local AND event is not reload-only → `sendRemote()` (serialize + JMS publish)

---

## Event Listener Interfaces

```mermaid
classDiagram
    class IEventListener {
        <<marker interface>>
    }

    class IEventFilteredListener {
        +getEventFilters() List~IEventFilter~
    }

    class ITopicEventFilteredListener {
        +getTopicEventFilters() List~ITopicEventFilter~
    }

    class IArtifactEventListener {
        +handleArtifactEvent(ArtifactEvent, Sender)
    }

    class IArtifactTopicEventListener {
        +handleArtifactTopicEvent(ArtifactTopicEvent, Sender)
    }

    class IBranchEventListener {
        +handleBranchEvent(Sender, BranchEvent)
    }

    IEventListener <|-- IEventFilteredListener
    IEventListener <|-- ITopicEventFilteredListener
    IEventFilteredListener <|-- IArtifactEventListener
    IEventFilteredListener <|-- IBranchEventListener
    ITopicEventFilteredListener <|-- IArtifactTopicEventListener
```

### Event Filters

Listeners provide filters via `getEventFilters()`:
- **`BranchIdEventFilter`** — only passes events matching a specific branch
- **`ArtifactTypeEventFilter`** — only passes events for specific artifact types
- Filters check: branch match → artifact match → relation artifact match

---

## Messaging Layer (JMS/ActiveMQ Integration)

```mermaid
flowchart TB
    subgraph Client ["Each IDE Client"]
        ET[EventTransport<br/>implements IFrameworkEventListener]
        PROXY[OseeCoreModelEventServiceProxy]
        IMPL[OseeCoreModelEventServiceImpl]
        MS[MessageService]
        CN[ConnectionNode]
    end

    subgraph Broker ["ActiveMQ Broker (RunActiveMq)"]
        T1[topic:...RemotePersistEvent1]
        T2[topic:...RemoteBranchEvent1]
        T3[topic:...RemoteTransactionEvent1]
        T4[topic:...RemoteTopicEvent1]
        T5[topic:artifact.topic.event]
    end

    ET -->|sendRemote| PROXY
    PROXY --> IMPL
    IMPL -->|send| CN
    CN -->|JMS publish| Broker

    Broker -->|JMS subscribe| CN
    CN -->|receive| FRL[FrameworkRelayMessagingListener]
    FRL -->|onEvent| ET
```

### ResMessages Enum (JMS Topic Registry)

```java
RemoteBranchEvent1       → "topic:org.eclipse.osee.coverage.msgs.RemoteBranchEvent1"
RemoteBroadcastEvent1    → "topic:org.eclipse.osee.coverage.msgs.RemoteBroadcastEvent1"
RemotePersistEvent1      → "topic:org.eclipse.osee.coverage.msgs.RemotePersistEvent1"
RemoteTopicEvent1        → "topic:org.eclipse.osee.coverage.msgs.RemoteTopicEvent1"
RemoteTransactionEvent1  → "topic:org.eclipse.osee.coverage.msgs.RemoteTransactionEvent1"
RemoteTopicArtifactEvent → "topic:artifact.topic.event"
```

---

## Serialization: FrameworkEventUtil

`FrameworkEventUtil` converts between local event objects and their remote (wire-format) counterparts:

| Local Event | → Remote (send) | ← Remote (receive) |
|---|---|---|
| `ArtifactEvent` | `getRemotePersistEvent()` → `RemotePersistEvent1` | `getPersistEvent()` ← `RemotePersistEvent1` |
| `ArtifactTopicEvent` | `getRemotePersistTopicEvent()` → `RemoteArtifactTopicEvent` | `getPersistTopicEvent()` ← `RemoteArtifactTopicEvent` |
| `BranchEvent` | `getRemoteBranchEvent()` → `RemoteBranchEvent1` | `getBranchEvent()` ← `RemoteBranchEvent1` |
| `TransactionEvent` | `getRemoteTransactionEvent()` → `RemoteTransactionEvent1` | `getTransactionEvent()` ← `RemoteTransactionEvent1` |
| `TopicEvent` | `getRemoteTopicEvent()` → `RemoteTopicEvent1` | `getTopicEvent()` ← `RemoteTopicEvent1` |

### Legacy vs New Event System

OSEE maintains two parallel event paths:
- **Legacy**: `ArtifactEvent` → JAXB-serialized `RemotePersistEvent1` (individual artifact/relation/attribute records)
- **New (Topic)**: `ArtifactTopicEvent` → JSON-serialized `RemoteArtifactTopicEvent` (properties map with JSON payloads)

The `FrameworkEventUtil.USE_NEW_EVENTS` flag controls which path is used.

---

## Access Control Events

Access events use the generic `TopicEvent` mechanism with predefined topics:

```java
AccessTopicEvent.ACCESS_ARTIFACT_MODIFIED  → "framework/access/artifact/modified"  (LocalAndRemote)
AccessTopicEvent.ACCESS_ARTIFACT_LOCK_MODIFIED → "framework/access/artifact/lock/modified" (LocalAndRemote)
AccessTopicEvent.ACCESS_BRANCH_MODIFIED    → "framework/access/branch/modified"    (LocalAndRemote)
AccessTopicEvent.USER_AUTHENTICATED        → "framework/access/user/authenticated" (LocalOnly)
```

These flow through `OseeEventManager.kickAccessTopicEvent()` → `TopicEvent` → `TopicLocalEventHandler` → OSGi `EventAdmin.postEvent()` for local OSGi subscribers, and optionally via `RemoteTopicEvent1` to other clients.

---

## OSGi EventAdmin Bridge

The `TopicEventAdmin` class bridges OSEE's event system to OSGi's `EventAdmin`:

```mermaid
sequenceDiagram
    participant ET as EventTransport
    participant TEA as TopicEventAdmin
    participant EA as OSGi EventAdmin
    participant Sub as OSGi Event Subscribers

    ET->>TEA: handleTopicEvent(topicEvent, sender)
    TEA->>EA: postEvent(new Event(topic, properties))
    EA->>Sub: deliver to topic subscribers
```

This allows any OSGi component to subscribe to OSEE events via standard OSGi event topics without depending on the OSEE event listener interfaces.

---

## EventQosType (Quality of Service)

```java
enum EventQosType {
    PRIORITY,  // Delivered first — used by caches that must update before UI listeners
    NORMAL     // Standard delivery order
}
```

Priority listeners (e.g., `ArtifactCache`) are registered via `OseeEventManager.addPriorityListener()` to ensure caches are current before UI components query them.

---

## Remote Event Loopback (Testing)

The `EventSystemPreferences.isEnableRemoteEventLoopback()` flag enables a testing mode where:
1. Events sent to `sendRemote()` are also fed back into `onEvent()` with a new session ID
2. This simulates multi-client scenarios in a single JVM
3. Normal operation: local events skip `onEvent()` processing (detected via `sender.isLocal()`)

---

## Cache Update on Remote Events

When a remote `ArtifactEvent` arrives, `ArtifactRemoteEventHandler` updates the local in-memory artifact model:

| Modification Type | Cache Action |
|---|---|
| **Added** | Nothing (not in cache; will be loaded on demand) |
| **Modified** | Update attributes: load new data, reset dirty flags, update gamma IDs |
| **Deleted/Purged** | Mark artifact as deleted (`internalSetDeletedFromRemoteEvent()`) |
| **ChangeType** | Update artifact type reference |
| **Relation Added** | Create new `RelationLink` in the artifact's relation set |
| **Relation Deleted** | Mark `RelationLink` as deleted |

---

## Plugin Structure

| Plugin | Responsibility |
|---|---|
| `org.eclipse.osee.framework.skynet.core` | Event manager, transport, handlers, listeners, filters |
| `org.eclipse.osee.framework.messaging.event.res` | Remote event message types, JMS integration, relay listeners |
| `org.eclipse.osee.framework.messaging` | Abstract messaging layer (ConnectionNode, MessageService) |
| `org.eclipse.osee.framework.core` | Core event types (NetworkSender, FrameworkEvent, TopicEvent) |
| `org.eclipse.osee.framework.core.client` | Access topic events, client session management |
| `jms.activemq.launch` | ActiveMQ broker embedded launcher |

---

## Summary

The OSEE event system is a publish/subscribe architecture where:
1. **Local changes** are dispatched synchronously to in-process listeners (filtered by branch/type)
2. **Remote notification** serializes the event and publishes it to an ActiveMQ JMS topic
3. **Remote reception** deserializes, updates the local artifact cache, then re-dispatches to local listeners as a "remote" event
4. **Echo prevention** uses session ID comparison to ignore self-originated events
5. **Two parallel systems** (legacy JAXB + new JSON) coexist for backward compatibility
