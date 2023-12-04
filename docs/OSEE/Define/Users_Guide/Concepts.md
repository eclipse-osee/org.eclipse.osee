# Define Navigator

## Purpose

Central location to launch frequently used define operations.

## How to do it

Double-click any navigation item to kickoff the corresponding operation.

## Filter

Filter out all navigation items that conains the entered text. Select
the clear action (![image:clear.gif](/docs/images/clear.gif "image:clear.gif")) to
clear out the text and restore all navigation items.

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

# Relations

Each relation link connects two distinct artifacts and is of a known
relation type. Each artifact type specifies the allowable relation link
types that may be attached to an instance (artifact) of its type and in
what multiplicity. What about sides?

# Verification and Validation relations

Verification and Validation relations should be defined for requirements
at every level of the requirements decomposition.

