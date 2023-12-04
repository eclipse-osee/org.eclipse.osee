  -
    The primary purpose of architecture is to support the life cycle of
    the system. Good architecture makes the system easy to understand,
    easy to develop, easy to maintain, and easy to deploy. The ultimate
    goal is to minimize the lifetime cost of the system and to maximize
    programmer productivity.

[Clean Architecture: A Craftsman's Guide to Software Structure and
Design](https://learning.oreilly.com/library/view/clean-architecture-a/9780134494272/)
by Robert C. Martin

## System Overview

The Open System Engineering Environment (OSEE) is an integrated,
extensible tool environment for large engineering projects. OSEE is more
than an integrated development environment (IDE), but is an integrated
product life-cycle development environment. The system captures project
data into a common user-defined data model providing bidirectional
traceability, project health reporting, status, and metrics which
seamlessly combine to form a coherent, accurate view of a project in
real-time. By building on top of a central data model, OSEE provides an
integrated configuration management, requirements management,
implementation, testing, validation, and project management system. All
of the components work together to help an organization achieve lean
objectives by reducing management activities, eliminating data
duplication, reducing cycle-time through streamlined processes, and
improving overall product quality through work flow standardization and
early defect detection. ![oseesystemcontext.png](/docs/images/oseesystemcontext.png
"oseesystemcontext.png") OSEE is customizable to meet the needs of the
project. The teams working on the project, the roles they perform, and
the processes they follow are all configurable within OSEE. Traceability
is maintained from requirements through acceptance testing. The OSEE
High Level System Overview diagram illustrates the range of users and
the roles they may perform, which encompasses the entire product
life-cycle

  - Requirements Engineer can create, manage, track requirements
  - Implementation Engineer may design, implement, integrate, unit test
    the product from software to hardware.
  - Test Engineer may perform different levels of integration or
    acceptance testing, and provide comprehensive test reporting.
  - Project Manager can track the progress of the product and gather
    customized metrics in order to best gain insight into the project
    status.
  - System Administrator manages the user accounts and IT needs.
  - Support Engineer can trace progress on customer issues
  - Maintenance Engineer can run validation, verification, or diagnostic
    tests on the target environment.

## Java Software Structures

Levels of code organization

  - bundles contain packages
      - packages contain types (classes and interfaces)
          - classes contain methods
              - methods contain blocks
                  - blocks contain statements
                      - statements contain expressions

[Expressions, Statements, and
Blocks](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/expressions.html)

  - bundle
    explicitly declare the packages they use (their external
    dependencies) and the packages they export for use by other bundles
  - packages
    are organized hierarchically and provide a namespace for the types
    they contain
  - classes
    define objects and how they are created
  - methods
    defines the parameters it accepts and the type of value it returns
    (or void)
  - statements
    forms a complete unit of execution
      - expression statements - Assignment, method invocation, object
        creation
      - declaration statements - type variableName = value; i.e. int
        daysInWeek= 7;
      - control flow statements - decision making, looping, and
        branching, enabling conditional execution of blocks of code
          - decision-making statements (if-then, if-then-else, switch)
          - looping statements (for, while, do-while)
          - branching statements (break, continue, return)

[Control Flow
Statements](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/flow.html)

### Responsibilities and Constraints

At various levels of the software structure (bundles, packages, types,
and methods) the responsibilities and constraints of that structure is
documented using
[Javadoc](https://docs.oracle.com/javase/1.5.0/docs/tooldocs/solaris/javadoc.html#javadoctags).
For bundles, this information is in the package-info.java file in the
top level package of the bundle. For packages see the package-info.java
file in the corresponding package. For classes and methods their Javadoc
is at the top of the class or method.

## Quality Attributes in Software Architecture

  - Availability - part of reliability and is expressed as the ratio of
    the available system time to the total working time
  - Extensibility - The services/components provided with OSEE will be
    reusable and/or extendable.
  - Flexibility - The tailoring of OSEE for a specific project can be
    accomplished through dynamic configuration.
  - Interoperability - facilitates integration with third-party systems
  - Maintainability - easily change to meet new business requirements;
    easily change to meet non-functional requirements
  - Openness - OSEE uses open standards to enable quality, standardized,
    and well thought-through interfaces.
  - Performance - low latency for individual requests
  - Reliability - system's ability to continue to operate in the face of
    various adverse conditions
  - Reusability -
  - Scalability - The system will scale to handle large numbers of
    simultaneous events and managed projects.
  - Security - prevent inappropriate access to information and other
    malicious actions
  - Supportability - support diagnosis of issues health checking
  - Testability - the system allows performing automated tests with high
    percentage of code coverage
  - Usability - intuitive interface that is discoverable and minimizes
    the overall effort required by the user

[ISO 25010 Software Quality
Diagram](https://iso25000.com/index.php/en/iso-25000-standards/iso-25010)
[Quality attributes in Software
Architecture](https://medium.com/@nvashanin/quality-attributes-in-software-architecture-3844ea482732)

## OSEE Bundle Architecture

  - ats.api - Models and Interfaces (ide/server)
  - ats.core - Business logic, implementation of interfaces (ide/server)
  - ats.core.test - Junit (does not require server and db)
  - ats.ide - Eclipse / Windows user interface; Implementation of client
    specific interfaces
  - ats.ide.demo - Demon Database business logic
  - ats.ide.demo.feature - List of bundles that are needed for demo
  - ats.ide.help.ui - Help User Interface - Generated from wiki
  - ats.ide.integration.tests - AtsIde_Integration_TestSuite - Demo Db
    Init and Integration tests (requires server/db)
  - ats.rest - Integration of Interfaces specific to server; REST
    implementations (should be renamed to ats.server)
  - ats.rest.test - Tests for Server specific implementations (should be
    renamed to ats.server.test)
  - Anything with "rest" == server
  - Anything with "integration" == integration tests require server/db;
    run with launch configs
  - Anything with ".test" (not integration) == Pure Java JUnit test
  - Anything with "ide" == Eclipse bundles w/ OS User Interfaces
  - Anything with "api" or "core" = shared client and server

## OSEE Bundle Structure/Layering

  - ATS - Configuration Management

`     Def: Process/Workflow control of managing data`
`     - Depends on Define`

  - Define - Content Management

`     Def: Generic capabilities/editors of managing content`
`     - Does NOT depend on ATS`
`     - Traceability`
`     - Publishing`
`     - Reporting`
`     - Generic Editors`
`     - Rendering`
`     - Templates`
`     - Word everything`

  - Orcs - Revision Control System including all things Data Model

`     - Server only`
`     - Art, Attr, Rel, Appl, Branch`

  - Framework.skynet

`     - Client only`
`     - Art, Attr, Rel, Appl, Branch`

  - Next Steps

`     - framework.ui.skynet should be rolled into define.ide`

## OSEE is built upon Open Standards and Open Source

  - [OSGi Service Platform Core Specification Release 4,
    Version 4.3](https://osgi.org/download/r4v43/osgi.core-4.3.0.pdf)
  - [SQL:2003
    ISO/IEC 9075-2:2003](https://en.wikipedia.org/wiki/SQL:2003)
  - [The Java Language Specification, Java SE 11
    Edition](https://docs.oracle.com/javase/specs/jls/se11/html/index.html)
  - [Angular](https://angular.io)
  - [Angular Material](https://material.angular.io)
  - [JSON](https://www.json.org)
  - [Java API for RESTful Web Services
    (JAX-RS)](https://github.com/eclipse-ee4j/jaxrs-api)

## OSGi Declarative Services

OSGi is the Dynamic Module System for Java. Its services model enables
application and infrastructure modules to communicate locally and
distributed across the network.

OSGi Declarative Services are explained well here [Getting Started with
OSGi Declarative
Services](http://blog.vogella.com/2016/06/21/getting-started-with-osgi-declarative-services)
which is summarized below:

` OSGi services use a publish-find-bind mechanism.`
` A bundle can provide/publish a service implementation of a given interface (type) for other bundles to consume.`
` With declarative services, services are not registered or consumed programmatically.`
` Instead, a Service Component is declared via a Component Description in an XML file in the OSGI-INF folder.`
` The Component Description is processed by a Service Component Runtime (SCR), e.g. Equinox DS or Felix SCR) when a bundle is activated.`

  - Service API
  - Service Provider
  - Service Consumer

` OSGi services are dynamic so, the service consumer must react to life cycle events.`
` Service Components have their own lifecycle, which is contained in the life cycle of a bundle.`
` OSGi service retrieval is type-safe since it is done based on an interface (type)`

### OSGi Bundle Diagnostics Console Commands

  - lb or ss - to list bundles and their states
  - bundle <bundleId> -
  - diag (try with no arguments)
  - help

### OSGi Component Diagnostics Console Commands

  - list | grep unsatisfied

`[bundle.id]   org.eclipse.osee.framework.resource.management.ResourceManager  enabled`
`[component.id] [unsatisfied reference]`
`info <component.id>`

  - list \<bundle.id\>
  - list | grep \<component.name\>
  - info \<component.id\>
  - inspect capability service
  - services

### OSGi best practices

  - use declarative services and no Activators.
  - Bundle-ActivationPolicy lazy
  - Don't specify a Component name. Per [OSGi Compendium
    Release 7 112.13.4.1](https://osgi.org/specification/osgi.cmpn/7.0.0/service.component.html#org.osgi.service.component.annotations.Component),
    if not specified, the name of a Component is the fully qualified
    type name of the class being annotated.

## OSEE Components

OSEE follows a client server architecture with a thin client and one to
N servers. The client can exist in two different forms: One, a web
browser client; Two, an [Eclipse](http://wiki.eclipse.org/Main_Page)
based IDE. The OSEE Server is built utilizing the [Eclipse
Equinox](http://www.eclipse.org/equinox/) [OSGi
framework](http://www.osgi.org/Technology/WhatIsOSGi). All instances of
the server attach to a single centralized data repository.
![osee_clientserver.png](/docs/images/osee_clientserver.png "osee_clientserver.png")

At the core of OSEE Application Server is the Object Revision Control
System Framework. On top of the framework sits four core components:
Action Tracking System (ATS), Define, Coverage, and Open System
Engineering Test Environment (OTE). The User Management component, which
allows for user authentication, verification and role based access
control (RBAC), is used by all of the OSEE components.
![oseecomponentdiagram.png](/docs/images/oseecomponentdiagram.png
"oseecomponentdiagram.png") OSEE is built on top of
[Eclipse](http://wiki.eclipse.org/Main_Page), and utilizes the [OSGi
framework](http://www.osgi.org/Technology/WhatIsOSGi) to manage the
component bundles. Capabilities provided by 3rd party libraries and
exposed in the Base Level API include:

  - Logging - Provide a consistent logging mechanism
  - Console - User ability to issue command line operations on the
    server
  - [JAX-RS](http://en.wikipedia.org/wiki/Java_API_for_RESTful_Web_Services)
    - Java REST API for web services
  - Event Management - [ActiveMQ](http://activemq.apache.org/) is used
    for event messaging

### OSEE Framework

#### OSGi Framework

The OSGi Framework Linked above provides the underlying support for the
OSEE Framework. The Eclipse implementation of OSGi is called Equinox.
The Equinox framework supports Extensions, Services, Declarative
Services and Spring-OSGi. Since OSEE is built on top of this extendable
foundation, it inherits these capabilities. OSEE developers have
primarily taken advantage of Declarative Services.

#### Extensibility

The layered approach provides extensibility to OSEE. The IDE Client is
extended through the use of Eclipse Extension Points. The OSEE
Application Server is extended through Declarative Services. As
described in the following sections, the data model is abstracted one
level up, so that a broad variety of specific data can be expressed in
the data model. The database interface complies with standard SQL-2003
("with" clause and row_number() is used), so relational databases
providing SQL-2003 compliance can be substituted in through the use of
JSON configuration files provided in OSEE.

#### Object Revision Control System

The heart of the OSEE Framework is the Object Revision Control System
(ORCS). ORCS provides the foundation the rest of the components are
built on top of. The key capabilities provided by ORCS are:

  - Object Management - The definition and persistence of any object of
    any simple or complex type can be managed, and type safety is
    ensured on all operations.
  - Data Model - the underlying data model is configurable per project
  - Version management - baselines and branching for a project are
    managed
  - Generic UI editor framework - customized editors can be created
  - Searching/Indexing
  - Transaction Management
  - Administration
  - Data Import/Export - external systems can supply or consume data

#### Data Model

The object management provides the core building blocks for the
centralized data model. The core building block for the data model
consists of:

  - **Artifact** - The fundamental object in OSEE. All data objects
    stored within OSEE are artifacts. Artifacts are strongly typed and
    can store any data throughout the systems engineering lifecycle.
    Artifacts have a Description, Type, and set of 1..n Attributes. Any
    type of data can be stored as an artifact; not only systems
    engineering data (such as processes and requirements), but also
    anything from meeting minutes to architecture diagrams.
  - **Attribute** - A specific piece of data attached to an Artifact.
    Attribute consists of Description, Type, default value (optional),
    Min occurrences in an artifact, Max occurrences in an artifact. An
    Attribute can be a basic type or a more complex type like another
    Artifact.
  - **Relation** - Defines the relationship (link) between two
    artifacts. The relation is strongly typed, which means it can only
    be used to link the specified artifact types. The relation allows
    the multiplicity between the two types to be specified as: 1..1,
    1..\*, \*..1, \*..\*

![osee_artifactattribute.png](/docs/images/osee_artifactattribute.png
"osee_artifactattribute.png")

#### Version Management

Version management allows for the parallel development of different
variations of a product, as well as the sharing of common information
across similar products. Changes made to one version baseline can be
merged to another version baseline in order to maintain commonality as
desired. ![osee_versions.png](/docs/images/osee_versions.png "osee_versions.png")

### Action Tracking System ([Details](/docs/OSEE/Architecture/ATS_Details.md "wikilink"))

The Action Tracking System (ATS) is a tightly integrated tracking system
that manages changes throughout the different aspects of a product's
lifecycle. ATS provides integrated change management to all OSEE
applications through customizable work processes (workflows) and ensures
traceability from start to completion. ATS utilizes the core
capabilities provided by the [ORCS](#OSEE_Framework "wikilink") layer.

ATS is highly configurable and can be configured to meet any project's
work tracking needs. The level of detail of work items, team
organization, and process to complete work item types are all
configurable for a project. The configuration is realized through the
use of the [data model](#Data_Model "wikilink") to create the core
building blocks of ATS: Action, Actionable Item, Team Definition,
Workflow Definition, Task, and Version.

At the highest level, an item of work to be completed is referred to as
an Action. Actions are created as work is needed for a project.

A project can specify a work hierarchy for the different kinds of work
tasks that need to be performed and tracked. Each defined work category
is referred to as an Actionable Item (AI). An Action can be composed of
a single or multiple AIs.
![osee_ats_aihierarchy.png](/docs/images/osee_ats_aihierarchy.png
"osee_ats_aihierarchy.png")

A team can be assigned to work on an AI. The team definition is similar
to an organization chart, or a logical grouping of teams that perform
certain types of work.
![osee_ats_teamdefinition.png](/docs/images/osee_ats_teamdefinition.png
"osee_ats_teamdefinition.png")

Each Team Definition has a Workflow Definition (or state machine) that
defines the process that team uses to track and complete the work. Each
state of the workflow can have configured conditions or fields that are
required to transition. A Review can be attached to a state and can
block the transition until successfully completed.
![osee_ats_workflowdefinition.png](/docs/images/osee_ats_workflowdefinition.png
"osee_ats_workflowdefinition.png") A Task is the lowest level of work,
and is used to allocate the work to individuals. A Task can be
associated with a particular state or a state can have multiple tasks
that need completing before the workflow can advance to the next state.
![osee_ats_taskdefinition.png](/docs/images/osee_ats_taskdefinition.png
"osee_ats_taskdefinition.png")

A Version is used to group a set of Actions together into a "build",
"release", "edition", etc. A common set of actions can apply to more
than one version enabling data sharing across similar or variant
projects. The version capability relies on the [version
management](#Version_Management "wikilink") framework.

Status data associated with tasks can be used to create metrics that
roll-up to the Workflow, which can roll up to the Team, which can
roll-up to the Action, which roll-up to the project. Metrics can be
obtained for any specified grouping within the project (e.g. Team,
Version, etc.)

[ATS Architecture Details](/docs/OSEE/Architecture/ATS_Details.md "wikilink")

### Define

Define is the requirements management component of OSEE. Define provides
support for concurrent and distributed requirements development.
Requirements can be imported from other sources to provide comprehensive
and coherent requirements management across the product life-cycle.

Since Define uses the OSEE [data
model](/docs/OSEE/Architecture.md#Data_Model "wikilink") and [version
management](/docs/OSEE/Architecture.md#Version_Management "wikilink"), the
following list of properties applies. Requirements in OSEE:

  - can be hierarchical
  - can be integrated into the processes and workflows
  - will have bidirectional traceability from beginning to end of the
    product life-cycle
  - will have meaningful review metrics
  - can support parallel project development

### Coverage

Coverage provides for the configuration management and tracking of
coverage disposition efforts throughout a project. OSEE allows for the
configuration of what is tracked for verification and validation on the
project. For example, in a software project the lines of code in the
software can be exercised through software tests, and tools can
determine how many of the software lines of code where executed during
the tests. A report is generated and can be imported by Coverage and a
complete coverage report generated.
![osee_coverage.png](/docs/images/osee_coverage.png "osee_coverage.png") The
Coverage is configurable through the creation of a Coverage Package. The
Coverage Package configuration includes all the inputs (unit tests, test
scripts, coverage test report imports), traceability (results to tests,
tests to requirements) and outputs (reports, metrics) desired. The
Coverage Package will also allow for checking the differences between
test runs by comparing different instances of test result imports.

### Open System Engineering Test Environment

The Open System Engineering Test Environment (OTE) is an integrated
approach to product testing, and follows a client/server architecture.
The OTE client is part of the OSEE client, while the OTE Server is
separate from the OSEE Application Server.

The OTE Client provides:

  - Test Manager for configuration of tests and test environment
  - Automated test execution
  - An API to create simulated test components (models)
  - Managing of tests (unit tests, scripts, etc.)
  - Real-time test result monitoring, recording, and playback
  - Test result reporting and
    [coverage](/docs/OSEE/Architecture.md#Coverage "wikilink")

The OTE Test Server provides:

  - A test execution manager
  - A simulated test environment that manages/executes test models
  - Managing I/O connections to the target test environment
  - Managing the test environment resources
  - Relay real-time test data to client

![osee_ote.png](/docs/images/osee_ote.png "osee_ote.png")

## OSEE Functional Use Cases

## OSEE Deployment

OSEE is designed to work in a collaborative environment, supporting many
developers working on the same data. One of the many considerations to
help accomplish this is the use of a multi-tier architecture to maintain
a common set of underlying data for all. Specifically, OSEE utilizes a
[three tier
paradigm](https://en.wikipedia.org/wiki/Multitier_architecture) to
separate the user interface from the component capabilities. There are
three cases that are important to understand.

### Single User Deployment

This is the default deployment if you download OSEE from the web. The
embedded HSQL database will be automatically created when the server is
run in a command shell. The client is configured to connect to the local
server by default. ![oseesingleuser.png](/docs/images/oseesingleuser.png
"oseesingleuser.png")

### Multi-User, Single Application Server Deployment

The multi-user deployment can be achieved by installing OSEE clients on
separate machines that have network access to the server machine. The
client .ini file needs to be configured to connect to the Application
Server machine. A more robust database can be configured for the
Application Server via the database [JOSN configuration
file](/docs/OSEE/Developers_Guide.md#Server_OSGi_properties "wikilink").

![oseemultiuser.png](/docs/images/oseemultiuser.png "oseemultiuser.png")

### Multi-User, Multi-Application Server Deployment

Multi-Application Server deployment should be considered if:

  - higher availability for the application server is required
  - the Application Server becomes over burdened, impacting server
    response time

In these cases, load balancing across multiple application servers is
encouraged. OSEE Application servers are designed to support load
balancing, and do not require the client to connect to a specific
application server, and do not maintain lists of the clients they
service. The load balancer is allowed to distribute client requests
according to its configured distribution scheme.
![oseenetworkdiagram.png](/docs/images/oseenetworkdiagram.png
"oseenetworkdiagram.png")

## Links

Images were created using [Inkscape](http://inkscape.org/) \<\!--and
saved in a [Scalable Vector
Graphics](http://en.wikipedia.org/wiki/Scalable_Vector_Graphics) format
\>