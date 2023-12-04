OSEE consists of an number of elements, which are described individually
at
[wiki.eclipse.org/OSEE\#Documentation](http://wiki.eclipse.org/OSEE#Documentation).


However, one interacts with OSEE using a number of Eclipse views.


This section of the OSEE Wiki will be developed to describe these views
and the common actions that are performed from them.


The assumption is made that OSEE's ATS (Action Tracking System) will be
used to provide lifecycle management. If you do not wish to use ATS then
only some of the views described are relevant.

# Essential concepts

This section effectively duplicates
[OSEE/Users_Guide/Concepts](/docs/OSEE/Users_Guide/Concepts.md "wikilink") and
therefore needs rationalising\!

### Artifacts

An Artifact is a "thing" that is stored in OSEE - a requirement, a
design document, a test plan, a schematic, a diagram, etc. OSEE is
configured by an organization to have known types of Artifact.

### Attributes

Each type of Artifact has a particular set of Attributes - the
information that is pertinent to that sort of thing. All Artifacts have
a Name attribute but other Attributes may vary, e.g. description, units,
safety criticality, and rationale are applicable to some Artifact types
but not others.

### Relations

The power of OSEE is in the fact that Relations can be used to specify
how Artifacts relate to one another. This allows, amongst other things,
traceability from requirements into design, implementation, and testing.
OSEE is configured by an organization to have known sorts of relations
between Artifact types. A warning is displayed if one attempts to create
a relation between types for which a relation has not been configured.

### Branches

All work in OSEE is carried out on a branch. Development can be thought
of as being like a tree. The trunk, called System Root branch in OSEE,
is not visible to most users and is just there for the major branches to
connect to.

Major branches are independent pieces of work, which may be separate
products, product families, projects, etc. These branches then have
their own sub-branches. At some level down the tree, users will have
development branches on which they do their day-to-day work. These
branches belong to the user(s) working on them and are not part of
released development until they have been "committed" back to their
parent.

e.g.

\- Product 1 |

|                   - Project x |

|                   |                 - My working branch

|                   |                 - Your working branch

|                   - Project y |

|                   |                 - Fred's working branch

\- Product 2 |

|                   - Project z |

|                                     - Test release branch |

|                                      
                                  -
Alf's working branch

|                                     - Final release branch

\- Hints and tips

|

\- Lessons learned



# Essential OSEE Views

  - ATS Navigator - the starting point for doing work
  - [**Artifact Explorer**](/docs/OSEE_Artifact_Explorer.md "wikilink") - a
    "branch aware" view from which Artifacts are edited
  - [**Artifact Editor**](/docs/OSEE_Artifact_Editor.md "wikilink")- the window
    in which Artifacts are edited
  - [**Change Report**](/docs/OSEE_Change_Report.md "wikilink") - how to see what
    you, or someone else, has changed on a branch
  - Skywalker - a graphical presentation of the Relations between
    Artifacts

# Useful OSEE Views

  - [**Branch
    manager**](/docs/OSEE/Users_Guide/Features.md#Branch_Manager_View "wikilink")
    - displays branches and allows operations between branches. Not
    usually needed to be used by most users because the ATS branch and
    commit widgets give the necessary capabilities and are less
    error-prone.