# Messaging System Design Notes

## 10/20/09

### Concepts

  - Message:
  - Topic:
  - Component:
  - Queue:
  - Transport:
  - Camel:
  - ActiveMQ:
  - Broker:
  - Event:
  - Message Transaction:

### Requirements

1.  Register Listener
2.  Message Types
    1.  Status messages
    2.  Commands
    3.  Operation in progress
    4.  Operation complete
    5.  Cancel Operation messages
    6.  Control Messages - communication between app servers?
3.  Provide interim progress information
4.  Web Service Interaction

## Use Cases

1.  Web page sends a message with JSON object. Broker converts HTTP to
    JMS message

sends to all subscribers.

1.  Application kicks an operation and is able to sync on message
    receives.
2.  Broker comes alive, clients connect to broker and broker redirects
    client to appropriate application server

## TODO

1.  Document App Server Messages/Interactions
2.  Add Sequence Diagram for Messages

# Events versus Messages

Events: inter-process communication Messages: enterprise wide
communication

### What is the broker topology

network of brokers, host1 + host2 + host3, we need to make sure ote can
function w/o the DB layer available... so i want to make sure we can run
with a network of brokers, and it has to be easily configurable so that
platforms not using the persistence layer can function. Potentially in
some cases we launch an embedded broker.

### What are the topics/queue names, what are their functions, and how do we use them

#### CONSIDERATIONS:

1.  queue - used for app server commands - it gets removed by the first
    consumer to get it, so if there are multiple subscribers only one
    will act upon the message.
2.  topic - used for status - broadcast - pubsub

### Messages

#### CORE

##### queue:OSEE.CORE.createBranch

  - subscribers: appServers
  - publishers: oseeClients

##### topic:OSEE.CORE.newBranch

  - subscribers: oseeClients
  - publishers: appServers

#### OTE

##### topic:OSEE.OTE.station.global.statusRequest

  - subscribers: oteTestServers
  - publishers: oteTestClients
  - purpose: sent by test clients to find out information about
    available test servers, i.e. who exists, in what configuration,
    current state (connected clients, etc..)

##### topic:OSEE.OTE.station.global.statusResponse

  - subscribers: oteTestClients
  - publishers: oteTestServers
  - purpose: the response message to a
    'OSEE.OTE.station.global.statusRequest', contains configuration and
    state information of a test server

##### topic:OSEE.OTE.station.{station_name}.{station_id}.status

  - subscribers: connected-oteTestClients
  - publishers: specific-oteTestServers
  - purpose: update of current state, errors, test point, current
    command, etc...

##### topic:OSEE.OTE.station.{station_name}.{station_id}.command

  - subscribers: specific-oteTestServers
  - publishers: connected-oteTestClients
  - purpose: command the ote environment to do something

##### topic:OSEE.OTE.station.{station_name}.{station_id}.{model}.command

  - subscribers: specific-oteTestServers
  - publishers: connected-oteTestClients
  - purpose: command a model to do something

##### topic:OSEE.OTE.station.{station_name}.{station_id}.{model}.status

  - subscribers: connected-oteTestClients
  - publishers: specific-oteTestServers
  - purpose: update of current state, errors, test point, current
    command, etc...