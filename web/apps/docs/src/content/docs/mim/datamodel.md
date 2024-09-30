---
title: MIM Data Model
description: Overview of the MIM data model
---

MIM uses OSEE's Artifact data model, wich means all of MIM's data types inherit the traits of OSEE's Artifacts, including full lifecycle traceability to any other Artifact, full change history, and Product Line Engineering capabilities.

## Connection

Connections are the interface between systems and/or subsystems, represented by [Nodes](#node). Connections hold a set of [Messages](#message) that make up the ICD, and they speficy which Nodes are available for its messages to pushish and subscribe to.

## Transport Type

Transport types define the rules that connections must follow. Some rules include byte alignment expectations, minimum and maximum number of nodes a connection supports, how many publishers and subscribers messages can have, whether MIM will autogenerate message headers, and more.

## Node

Nodes represent a member of a connection that can publish or subscribe to messages.

## Message

Messages are a container for the data that is sent between Nodes. They hold information such as transmission rates, publishers and subscribers of the message, message type and periodicity.

## Submessage

Submessages are used to organize data within a message. Messages can contain multiple sctructures, which can be organized within submessages.

## Structure

Structures are collections of Elements. Structures hold information such as the simultaneity of that collection that can be sent in each message, and will show a rollup of the total size of all of the Elements in the structure.

## Element

Elements are where the low-level details of a message are defined. They are assigned a [Platform Type](#platform-type) that determines the size, logical type, and range of values for the Element. They also can be assigned a start and end index to represent an array of Elements in a single artifact. Elements can also be containers for other Elements, allowing for two-dimensional arrays which make maintaining large ICDs with repetitive data much more manageable.

## Platform Type

A Platform Type represents a specific instance of a logical type (eg. uInt, boolean, short, enum set) with a specific range and default value. Platform Types can be shared between elements, so a specific type with a specific range only needs to be defined once. If the logical type is set to "enumeration", there will be an [Enumeration Set](#enumeration-set) associated with the Platform Type as well.

## Enumeration Set

Enumeration Sets are named sets of individual Enuerations. They are always related to a Platform Type with an "enumaration" logical type.

## Enumeration

Enumerations hold the ordinal and value of an item in an Enumeration Set.
