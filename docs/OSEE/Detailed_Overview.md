OSEE was designed from the ground up to be a single tool that captures
all artifacts through-out the life-cycle of engineering. Since the
original OSEE project use was DoD, very strict guidelines are placed on
how we capture, trace and deliver our product. OSEE does this for us.

In addition to solving our specific engineering needs, it was decided
that a single engineering framework could be used for any software
development project. OSEE was designed to scale from a small team of 1
or 10 to a project of any size.

The team also wanted to address the issues of obsolesce. Too many times,
a tool is developed by one or more people to resolve a very specific
need. This tool is maintained and updated as needs change. Then the
engineers that developed that tool move to other positions. Or the
technologies that were used to develop that tool become obsolete. OSEE
was designed and developed under the Eclipse open source development
environment that is used world-wide. It was developed using one of the
leading software languages, Java, and it was made open-source so that
others could easily continue its development even if the existing
program moved to a different solution.

You can learn more about OSEE at <http://www.eclipse.org/osee>

# What is OSEE

The Open System Engineering Environment (OSEE) project provides a
tightly integrated environment supporting lean principles across a
product's full life-cycle in the context of an overall systems
engineering approach. The system captures project data into a common
user-defined data model providing bidirectional traceability, project
health reporting, status, and metrics which seamlessly combine to form a
coherent, accurate view of a project in real-time. By building on top of
this data model, OSEE has been architected to provide an all-in-one
solution to configuration management, requirements management, testing,
validation, and project management. All of these work together to help
an organization achieve lean objectives by reducing management
activities, eliminating data duplication, reducing cycle-time through
streamlined processes, and improving overall product quality through
work flow standardization and early defect detection.

# OSEE can be used for

`  - Configuration Managed Requirements Development`
`  - Multiple dynamically configured Problem-Change-Request (PCR) systems`
`  - Product Unit and Integration Testing`
`  - Publishing deliverable documents`
`  - Full traceability from high-level customer requirements to decomposed lower level requirements and even code, test and output files.`
`  - Full Level A, B and C coverage tracking and reporting`
`  - Agile Software Development`
`  - Peer Reviews`
`  - Process management`
`  - Linking in other tools / databases`
`  - Support for system safety tracking/reporting`
`  - And any other use cases that need to be developed`

# The major features of OSEE include

## OSEE Artifact Framework

The artifact is the fundamental object in OSEE. All data objects stored
within OSEE are artifacts. Artifacts are strongly typed and can store
any data throughout the systems engineering lifecycle. Any type of data
can be stored in OSEE as an artifact; not only systems engineering data
(such as processes and requirements), but also anything from meeting
minutes to architecture diagrams.

## Variant Management

Products developed in OSEE can take advantage of it's variant management
by using the waterfall method of build/release branching off each other
as children, or take advantage of our recent changes to a product-type
management where each product/feature is managed separately and parts
that are different for different customers/use-cases are tagged with
their applicability to that version.

## Full History

OSEE stores all changes to all artifacts in the database. This allows
for accidental changes to be backed-out and also facilitates research
into who did what and why.

## Dynamically Configured Model

Unlike relational databases where a new table is needed for each type of
object, OSEE Artifact Framework is an object database that can be run on
any SQL compliant database. Currently, Oracle, Postgres and HSQL are
used/tested. New objects can be created dynamically. This include meta
data on the artifacts and relations between artifacts. These models are
used by the features/applications that are built for different use cases
like problem tracking, configuration management, coverage, requirements
management and etc.

## REST and Web Interfaces

The need to easily access data stored within OSEE drove our architecture
to start providing many RESTful
(https://en.wikipedia.org/wiki/Representational_state_transfer)
services. And with those services available, the features of OSEE are
continually being provided through Web UI.

## Unit and Integration Testing

OSEE has a Test Environment that supports both unit and integration test
"scripts" that can be run individually or in a continuous integration
environment. Emulators can also easily be created to emulate different
pieces of hardware.

## Problem Change Request / Configuration Management

OSEE has a tightly integrated PCR and CM system built in. This allows
any number of teams to track their work within the same system. This
allows for a single user-interface for all PCRs, ability to link between
"actions", easily group different work for different teams within the
same "action" and dynamically configure each teams "work-flow" to be
different. Also integrated is a full-blown Peer Review system that
easily allows peer reviews to be created against a product, team or
action and be tracked similar to the PCRs.

# Usage

OSEE is currently used internal to Boeing. In addition, the OSEE Framework and some of it
applications are in use by the Bosch company for their world-wide
operations.

# Maturity

Different portions of OSEE are in different stages of maturity. In addition, all
mentioned programs/companies can and have provided new features/fixes to
the OSEE code-base. This is done through both the external eclipse.org
development processes and the internal OSEE-team development processes.

# Contact

More information about OSEE can be obtained by visiting our Eclipse
website <http://www.eclipse.org/osee> or by contacting one of the team
leads: Ryan Brooks (ryan.d.brooks AT boeing.com) or Don Dunne
(donald.g.dunne AT boeing.com)