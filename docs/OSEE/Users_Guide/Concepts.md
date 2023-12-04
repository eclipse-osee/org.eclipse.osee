# Artifacts

The **artifact** is the fundamental object in OSEE. All data objects
stored within OSEE are artifacts. Artifacts are strongly typed and can
store any data throughout the systems engineering lifecycle. Any type of
data can be stored in OSEE as an artifact; not only systems engineering
data (such as processes and requirements), but also anything from
meeting minutes to architecture diagrams.

# Attributes

An **attribute** is data attached to an artifact. A `User` artifact
might have `Email`, `Name`, and `Phone Number` attributes. A `Software
Requirement` artifact might have attributes such as `Qualification
Method`, `Safety Criticality`, or `Subsystem`.

The default attribute `Name` is required for all artifacts. Other
attribute types can be created and associated with any artifact in the
system.

# Relations

A **relation** is a link between two artifacts. Like artifacts, they are
strongly typed; an `attend` relation `attend` might relate a `User`
artifact to a `Meeting` artifact. Similarly, a `Customer Requirement`
might be linked to the low-level `Software Requirement` that satisfies
it.

# Branches

A fundamental feature provided by OSEE is the concurrent management of
multiple variants or lines of a product. After a set of requirements is
developed, it may become the baseline for variant sets of requirements
for similar products. In other words, you may develop the same product
for another customer, but have slight changes to the requirements, code,
and test for features specific to that customer.

Historically, this would mean maintaining completely separate "copies"
of all the requirements and other artifacts. This is costly to maintain
when changes from the baseline artifacts must be propagated to the other
product line. The expense of this undertaking increases dramatically as
more customers are added, each with their own set of requirements
changes.

For this reason, OSEE provides full **branching** functionality. Using
OSEE, it is possible to create these variant branches, record where they
originated, and to apply changes made to a baseline branch to its
variants.

By default, OSEE has two system branches. The `System Root Branch` is
the parent of all other branches in the system. The `Common` branch is
used to store OSEE configuration information, such as users. `Common` is
a child of `System Root Branch`.

## Working Branches

On complex projects, artifacts can be subject to modification by any one
of hundreds of engineers. To have requirements "locked" while they are
being modified by one user can cause significant delays in schedule. The
need for parallel development (multiple users working on the same
requirements) is a necessity to keeping a project moving forward. In
addition, users making mistakes need the ability to revert or throw away
their changes and start over without polluting the baseline branch. This
is done using **working branches**. A working branch is a sandbox area
used to prepare a commit to a baseline branch.

# BLAM

**BLAM Lightweight Artifact Manipulation** (BLAM) allows non-programmers
to graphically construct workflows to automate repetitive tasks. A given
workflow can be used for variety of similar tasks by using customizable
controls to specify workflow parameters. BLAM also provides programmers
the ability to interact with the OSEE Artifact Framework API to build
and execute tasks.

# Requirements traceability for a system

Every requirement for a system is defined at a distinct level of detail,
and these levels are ordered from the highest level down to the lowest
level. A trace relation connects two requirements from adjacent levels.
Every requirement that is not a top level (highest level) requirement,
must trace to one or more requirement at the next higher level. All
requirements, except those at the lowest level, must trace to one or
more requirement at the next lower level.

# Functional decomposition of a system

The functional decomposition of a system produces a proper tree (i.e.
every node in the tree has exactly one parent except the root which has
no parent). The tree's root represents the system in its entirety. The
root is decomposed into some number of components that can be further
decomposed to any desired level. Neither the root nor any of its
components are themselves requirements.

# Allocation of Requirements

Requirements are allocated using allocation relation links to components
(of the functional decomposition). A requirement at a given level is
allocated to a component at the corresponding next lower level in the
functional decomposition.

# Verification and Validation relations

Verification and Validation relations should be defined for requirements
at every level of the requirements decomposition.