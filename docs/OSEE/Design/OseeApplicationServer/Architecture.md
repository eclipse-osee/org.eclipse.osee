# OSEE Application Server Architecture

Currently, the OSEE application server is divided into the following
services:

  - Branch Management
  - Authentication
  - Session Management
  - Resource Management
  - Searching/Tagging Engine
  - Server Lookup / Arbitration
  - Task Service
  - Admin Operations

Communication between Client/Server is handled via servlets:

  - Session Management Servlet - interats with session manager and
    authentication
  - Resource Management Servlet - interacts with resource management
    service to store, acquire, and delete resource data
  - Branch Management Servlet - interacts with the Branch Management
    Service
  - Search Engine Servlet - interacts with search and tagging services
  - Server Lookup Servlet - performs server lookup / arbitration
  - Add ons (These are servlets that provide none-core functionality) :
  - Artifact Servlet - serves artifact's binary data (only supports
    whole word document artifact types)
  - Client Info Servlet - provides application server installation
    locations through a web pagee

# Design Limitations

  - Progress Information is not sent to clients
  - Unable to easily cancel operations once they are in progress
  - Unable to determine what is running on the server (communication
    between service)
  - Code duplication between admin ops and client requested ops
  - Database code is spread out among services

# Reasons For refactor

1.  Address design limitations
2.  Change from servlet communication to using Osee Messaging Service
    (Gateway)
3.  Easily migrate skynet core functionality to the server
4.  Be able to synchronize operations for data integrity
5.  Multiple application servers - distributed

# Architecture Goal

# Communication

# Blocks

# New Service Structure

1.  Need a controller to dispatch all operations - sends

<!-- end list -->

  - Services:
      - Status Service - who is running what - multi app server /
        internal - potential progress monitoring
      - Negotiation Service - synchronization of operations
      - Executor Service - manages thread pools/jobs -
          - only one allowed to create threads.
          - only one allowed to run the operation.

<!-- end list -->

  -   - All other services send/receive messages (dispatchers)
      - Operations send/receive messages

<!-- end list -->

  - Bundle:
      - Versioning Bundle
          - Commit
          - Create Branch
          - Change Report
          -
          - Branch Exchange

## Migration

  - Commit -
  - Change Report -
  - Transaction Manager -
  - Branch Archiving -
  - Status Service -
  - Conflict -
  - Executor Service -

