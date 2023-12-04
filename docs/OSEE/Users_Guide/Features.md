# Component Overview

## Application Framework

**Key Capabilities**

  - Dynamic and strongly typed artifact model (persistence layer)
  - Bidirectional traceability through typed relations model
  - Advanced version control including multi-level branching
  - Subject-based and object-based Access Control
  - User management and authentication
  - Dynamic searching API
  - Indexing and tagging services
  - Views and editors for the creation, manipulation, and navigation of
    artifacts, attributes, and relations
  - Remote event service for communication and synchronization of OSEE
    instances
  - Rules framework for text processing
  - Utilities for plug-in developers
  - Scheduling framework
  - API for the extension and use of this framework to build tightly
    integrated applications
  - Encryption Utility
  - Database Utilities
  - Jini Utilities
  - Scheduling Service
  - Datastore Adapter

## Define (Requirements Management)

Define provides requirements and process management with tightly
integrated change management using the Action Tracking System (ATS).
OSEE provides publishing capabilities that enable the creation of
documents from smaller internal documents to the complex documents
needed to meet military requirements for contract deliverables such as
the Software Requirements Specification (SRS), System Performance
Specification (SPS), Prime Item Development Specification (PIDS), and
all the required traceability between them.

**Key Capabilities**

  - Enterprise support for concurrent, distributed requirements
    development
  - Integrated process and workflow
  - Programmatic, bidirectional traceability
  - End user navigation and search capabilities
  - Capture accurate, meaningful review metrics
  - Tight integration with lifecycle tools
  - Automated change detection capabilities

## Action Tracking System

ATS is a tightly integrated change tracking system that manages changes
throughout a product's lifecycle. ATS provides integrated change
management to all OSEE applications through user customizable workflows.

**Key Capabilities**

  - Built on same OSEE application framework as requirements, code, test
    development
  - Common Workflow Framework that provides for the creation of any
    number of simple to complex workflow state machine configurations
    that can work together during the engineering lifecycle
  - Workflows are configured through graphical diagrams that ATS uses at
    runtime
  - Configuration of ATS performed through OSEE?s common application
    framework enabling workflows to be created and modified without
    separate OSEE releases
  - Advanced project planning capabilities and release management
  - Duplication errors are minimized as items are automatically linked
    and data is shared
  - Menus, Views and Editors give access to ATS while working in any
    other aspect in OSEE
  - ATS is used to track changes and support issues for the development
    of OSEE itself
  - Bug Icon allows quick Action creation against any OSEE integrated
    tool

## Open Test Environment

OTE is a powerful test solution within OSEE that integrates with
existing Java, C, and C++ development environments to provide a seamless
flow between developing, debugging, executing, and dispositioning of
tests for complex hardware and software. The user is provided a common
interface to the simulated and real-time environments for both
functional and unit testing.

**Key Capabilities**

  - Supports the execution of multiple simultaneous batches within a
    single workspace
  - Built-in help system extended with test manger user guide
  - Message system supporting MIL-STD-1553 MUX, serial, wire, Ethernet,
    and Data Distribution Service (DDS)
  - Message GUIs provide monitoring, manipulation, and recording of
    messaging data
  - Utilizes OSEE application framework to provide traceability to
    software requirements
  - Automatic generation of tests and testing support classes, directly
    from requirements.
  - Leverages off Java Development Toolkit (JDT) and C/C++ Development
    Toolkit (CDT)
  - Provides remote execution of scripts against target hardware and
    operating system
  - Test results are logged in XML
  - Transforms test results via built-in or user supplied XSL
    Transformations
  - Built-in XSL Transformations produce interactive HTML result reports
  - A test environment service that provides both soft real-time and
    simulated capabilities, schedules the periodic execution of
    simulation components, and manages the I/O and testing resources.

## BLAM

BLAM Lightweight Artifact Manipulation (BLAM) allows non-programmers to
graphically construct workflows to automate repetitive tasks. A given
workflow can be used for variety of similar tasks by using customizable
controls to specify workflow parameters.

**Key Capabilities**

  - Integrated management of charge/cost accounting
  - Build planning and execution
  - Reporting services
  - Rules framework for requiring/alerting certain conditions
  - Scheduling services for automating reoccurring tasks

## Program/Project Management

Program and project management tightly integrated with the Action
Tracking System and other OSEE components to provide services necessary
for estimation, planning, execution, and delivery of products managed
within OSEE.

## Discovery and Learning

Services provided for allowing advanced learning and discovery using
OSEE's abundant and inherent lifecycle data and metrics.

**Key Capabilities**

  - Discovery of inefficiencies in lifecycle processes
  - Advanced data mining and data fusion
  - Advanced estimation
  - Advanced import/export of product capabilities between programs
    including applicable design, requirements, code, and test
  - Advanced data visualization
  - Prediction of future risks
  - Simulation of recommended process and lifecycle changes

## Application Development

Provide capabilities needed for external software application
development plug-ins, like JDT, to utilize the OSEE persistence layer
and integrate with other OSEE-based applications.

## Design and Modeling

Provide capabilities needed for external design and modeling plug-ins to
utilize the OSEE persistence layer and integrate with other OSEE-based
applications.

# Views and Editors

## Branch Manager View

The Branch Manager View, shown by default in the Define Perspective,
shows all branches and transactions on each branch managed by OSEE in a
hierarchical fashion. By default, OSEE is initialized with two branches.
The System Root Branch (visible to OSEE Administrators only) and the
Common Branch. The System Root Branch is the base branch for all other
branches in the system. The Common branch is used to store system-wide
artifacts such as configuration artifacts, users, and user preferences.

![branchmanager.gif](/docs/images/branchmanager.gif "branchmanager.gif")

### Toolbar buttons

| Icon                                                        | Description                                                                     |
| ----------------------------------------------------------- | ------------------------------------------------------------------------------- |
| ![image:refresh.gif](/docs/images/refresh.gif "image:refresh.gif")       | Refreshes the branch hierarchy tree with the latest branch information.         |
| ![image:customize.gif](/docs/images/customize.gif "image:customize.gif") | Opens the table customization dialog. This allows users to add or hide columns. |
| ![image:bug.gif](/docs/images/bug.gif "image:bug.gif")                   | Opens an action against the Branch Manager View.                                |

### Toolbar Drop-Down

To display, click on the inverted triangle located on the upper
right-hand side of the Branch Manager view.

| Command                     | Description                                                                                |
| --------------------------- | ------------------------------------------------------------------------------------------ |
| Open Branch Graph           | Display a diagram showing the relations among branches.                                    |
| Open OSEE Data Model Editor | Opens a graphical editor of all the artifact, attribute, and relation types in the system. |
| Branch Presentation         | Switch presentation between flat or hierarchical views.                                    |
| Show Archived Branches      | (Admins Only) Toggles archived branch visibility.                                          |
| Show Merge Branches         | (Admins Only) Toggles merge branch visibility.                                             |
| Show/Hide Transactions      | Toggles branch transaction visibility.                                                     |
| Show Favorites First        | Orders branches by showing favorite branches first.                                        |

### Pop-up Menu

To display, perform a right-click on any branch.

| Command                     | Description                                                                                                                                                                                            |
| --------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Explore                     | Opens the Artifact Explorer View for the selected branch. This operation can also be performed by double-clicking on a branch.                                                                         |
| Change Report               | Opens the Change Report View for the selected branch.                                                                                                                                                  |
| Merge Manager               | Opens the merge manager. This is used to resolve conflicts when committing a working branch back into its parent branch.                                                                               |
| Branch                      | Creates a child branch of the selected branch.                                                                                                                                                         |
| Update Branch               | Re-baselines a branch into its parent, leaving the selected branch in place. If there are conflicts, the merge manager will be opened to allow the user to resolve them.                               |
| Commit Into                 | Commits changes made to a branch into another branch. Unlike "Update Branch," this feature does not assume that the destination branch is the parent. This feature can only be used by administrators. |
| Delete Branch               | Deletes the selected branch from the system by setting the branch state to deleted. The data from this branch will still be recoverable.                                                               |
| Purge Branch                | Purges the selected branch from the system by removing all data from the data store. This action cannot be undone.                                                                                     |
| Purge Transaction           | Purges the currently selected transaction from its branch. Removes all changes made on the selected transaction. (Not recoverable)                                                                     |
| Open Associated Artifact    | Opens the artifact that was associated with the creation of the selected branch.                                                                                                                       |
| Set Associated Artifact     | Associates an artifact with the currently selected branch.                                                                                                                                             |
| Archive                     | Archives the branch so that it does not appear in a list of branches unless archived branches are specifically requested.                                                                              |
| Access Control              | Opens the access control dialog.                                                                                                                                                                       |
| Rename                      | Allows users to change the branch name.                                                                                                                                                                |
| Mark as Favorite            | Sets/Unsets a branch as a favorite branch.                                                                                                                                                             |
| Copy                        | Places branch information into the clipboard.                                                                                                                                                          |
| Open Osee Data Model Editor | Opens a graphical editor of all the artifact, attribute, and relation types in the system.                                                                                                             |
| Open Branch Graph           | Shows a graphical representation of branches using the currently selected branch as the base branch.                                                                                                   |

### Additional Operations

**Searching** - Can be performed by typing text in the Search text box.
Matching text will be highlighted. Click on the
![image:clear.jpg](/docs/images/clear.jpg "image:clear.jpg") icon to clear the search
box and clear all the search results.

**Filtering** - Can be performed by typing text in the Filter box. Click
on the ![image:clear.jpg](/docs/images/clear.jpg "image:clear.jpg") icon to clear the
filter box and display all items.

## Artifact Explorer View

The Artifact Explorer view, shown by default in the Define perspective,
shows the artifact hierarchy of the selected branch. The artifact
hierarchy is derived from the artifact's default hierarchy relation. By
default, all branches have a default hierarchy root artifact which is
the hierarchy tree's base element.

![artifactexplorer.jpg](/docs/images/artifactexplorer.jpg "artifactexplorer.jpg")

### Toolbar buttons

| Icon                                                                                 | Description                                                                                                                              |
| ------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------- |
| ![image:collapseall.gif](/docs/images/collapseall.gif "image:collapseall.gif")                    | Collapses all tree nodes.                                                                                                                |
| ![image:up.gif](/docs/images/up.gif "image:up.gif")                                               | Navigates to the parent container of the artifacts that are currently displayed at the top level in the view.                            |
| ![image:artifact_explorer.gif](/docs/images/artifact_explorer.gif "image:artifact_explorer.gif") | Opens a new instance of the Artifact Explorer view.                                                                                      |
| ![image:branch_change.gif](/docs/images/branch_change.gif "image:branch_change.gif")             | Opens the Change Report View for the Artifact Explorer's selected branch. This report will show all changes made to the selected branch. |
| ![image:artifact_search.gif](/docs/images/artifact_search.gif "image:artifact_search.gif")       | Opens the Quick Search View for the selected branch.                                                                                     |
| ![image:bug.gif](/docs/images/bug.gif "image:bug.gif")                                            | Opens an action against the Artifact Explorer view.                                                                                      |

### Pop-up Menu

To display, select one or more artifacts and perform a right-click.

| Command                  | Description                                                                                                                                   |
| ------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------- |
| Open                     | Opens the selected artifact using the default editor.                                                                                         |
| Open With                | Opens a sub-menu listing the available editor's for this artifact.                                                                            |
|                          |                                                                                                                                               |
| Reveal on Another Branch | Open's a new instance of Artifact Explorer for a user selected branch. Expands tree elements to display the selected artifact to be revealed. |
|                          |                                                                                                                                               |
| New Child                | Creates a new artifact and places it directly under the selected artifact.                                                                    |
| Go Into                  | Sets the selected artifact as the base of the artifact hierarchy tree hiding artifacts except child artifacts.                                |
| Mass Edit                | Opens the Artifact Mass Editor populated with the selected artifacts.                                                                         |
| Sky Walker               | Launches the Sky Walker View.                                                                                                                 |
|                          |                                                                                                                                               |
| Delete Artifact          | Deletes the selected artifacts.                                                                                                               |
| Purge Artifact(s)        | Purges the selected artifacts from the data store.                                                                                            |
| Rename Artifact          | Allows a user to quickly change the artifact's name attribute.                                                                                |
|                          |                                                                                                                                               |
| Show Resource History    | Opens the Resource History view for the selected artifact. This will display all transactions for this artifact.                              |
|                          |                                                                                                                                               |
| Import                   | Opens Eclipse's import dialog.                                                                                                                |
| Export                   | Opens Eclipse's export dialog.                                                                                                                |
|                          |                                                                                                                                               |
| Lock                     | Locks the artifact so the current user is the only one allowed to make changes to its attributes.                                             |
|                          |                                                                                                                                               |
| Copy                     | Copies the artifact.                                                                                                                          |
| Paste                    | Pastes the artifact.                                                                                                                          |
| Expand All               | Expands all tree nodes from the selected artifact down.                                                                                       |
| Select All               | Selects all open tree nodes.                                                                                                                  |
|                          |                                                                                                                                               |
| Access Control           | Opens the access control dialog.                                                                                                              |

### Operations

Operations that can be performed on an Artifact Explorer.

| Command                     | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| --------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Cross Branch Drag and Drop  | Artifacts can be referenced from other branches by dragging an artifact from one branch and dropping in onto another Artifact Explorer. The outcome will be one of two actions. If the artifact already existed on the branch it will be updated with the source artifacts state data. If the artifact did not exist on the destination branch it will be introduced to that branch. Meaning it will show up in the change report as an introduced artifact. |
| File Document Drag and Drop | Artifacts can be created by dragging and dropping files directly onto the parent artifact. After the drop is performed, the Artifact Import Wizard should open. Select the import method and artifact type to convert file into. This should create a child artifact directly under the artifact where the file was dropped.                                                                                                                                 |

Dragging An Artifact From The Artifact Explorer

  - If an artifact is dragged from the explorer into a Word document a
    hyper link will be created with the artifact name in text and when
    selected it will open the artifact in the Artifact Editor.
  - If an artifact is dragged into a Text File the name of the artifact
    will be written inside the document.
  - If an artifact is dragged into a relation in the Relational View of
    the Artifact Editor a new relation will be created with the source
    artifact and the target artifact in the editor.
  - If an artifact is dragged within the Artifact Explorer the original
    Default Hierarchal Relation will be deleted and a new one be created
    whith the target artifact.

Dropping Onto The Artifact Explorer

  - If a file is dropped onto the Artifact Explorer a new artifact will
    be created with the contents of the source file and a new Default
    Hierarchal Relationship.

## Artifact Editor

The Artifact editor provides specialized features for editing artifacts
(this is the default editor for editing attributes and relations). The
editor can be opened through Artifact Explorer, by double-clicking on
any artifact or right-clicking on an artifact and selecting to Open With
"Artifact Editor".

Associated with the editor is an Artifact-specific Outline view, which
shows the structure of the active artifact. It is updated as the user
edits the artifact.

![artifacteditor.jpg](/docs/images/artifacteditor.jpg "artifacteditor.jpg")

The Artifact Editor is divided into the following sections *(some
sections can be expanded and collapsed by clicking on the section's
title bar)*.

### Title Area

1.  **Artifact Name** - the artifact's name attribute
2.  **Message Area** - reports issues that need to be addressed by the
    user before saving is allowed. Click on the message to open a
    message summary window. From the message summary window, click on
    any message to jump to the item associated with the message.
3.  **Toolbar Area**

| Icon                                                                          | Description                                                                                                                                                             |
| ----------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![image:refresh.gif](/docs/images/refresh.gif "image:refresh.gif")                         | Refreshes the artifact editor. Displaying the artifact's current data. NOTE: Changes made to the form will be lost unless data is saved before clicking on this button. |
| ![image:bug.gif](/docs/images/bug.gif "image:bug.gif")                                     | Opens an action against the Artifact Editor.                                                                                                                            |
| ![image:open.gif](/docs/images/open.gif "image:open.gif")                                  | Opens the artifact or if clicking on the down-arrow, displays the open with sub-menu.                                                                                   |
| ![image:delete.gif](/docs/images/delete.gif "image:delete.gif")                            | Deletes the artifact and closes the editor.                                                                                                                             |
| ![image:outline_co.gif](/docs/images/outline_co.gif "image:outline_co.gif")               | Displays the outline view.                                                                                                                                              |
| ![image:dbiconblue.gif](/docs/images/dbiconblue.gif "image:dbiconblue.gif")                | Opens the artifact's resource history.                                                                                                                                  |
| ![image:magnify.gif](/docs/images/magnify.gif "image:magnify.gif")                         | Displays the artifact in an Artifact Explorer view.                                                                                                                     |
| ![image:branch.gif](/docs/images/branch.gif "image:branch.gif")                            | Open the Branch Manager View and highlights the artifact's branch.                                                                                                      |
| ![image:authenticated.gif](/docs/images/authenticated.gif "image:authenticated.gif")       | Locks or unlocks the artifact for editing. Locking an artifact prevents other users from making changes to it.                                                          |
| ![image:copytoclipboard.gif](/docs/images/copytoclipboard.gif "image:copytoclipboard.gif") | Copies a link to the latest version of the artifact to the clipboard.                                                                                                   |

### Artifact Information Area

Displays the artifact's branch, artifact type, and human readable id.

### Attributes Section

Displays attribute types to be edited. Attribute types can be added or
deleted by clicking on the appropriate toolbar button *located on the
upper-right of the attributes section title bar*. **Note: The following
operations follow min/max occurrence rules defined by the attribute's
type.**

| Icon                                                        | Description                                                                                                                                                                                                 |
| ----------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![image:back.gif](/docs/images/back.gif "image:back.gif")                | Opens an attribute type selection dialog; the selected attribute type instances will be reset to their default value.                                                                                       |
| ![image:greenplus.gif](/docs/images/greenplus.gif "image:greenplus.gif") | Opens a dialog displaying attribute types that can be added to the artifact.                                                                                                                                |
| ![image:delete.gif](/docs/images/delete.gif "image:delete.gif")          | Opens a dialog displaying attribute types that can be deleted from the artifact. '''Note: Data entered for the attribute type to be deleted will be lost as soon as the dialog's **OK** button is selected. |

### Relations Section

All relations that are defined as being valid for the artifact are
shown. Relations can be added by dragging any set of artifacts into the
appropriate relation group. Opening a relation group will show all the
artifacts that are currently related. Double-clicking a related artifact
will open it in its default editor (normally the Artifact Editor).

**Pop-up Menu** - To display, expand the **Relations** section, select
one or more artifacts, and perform a right-click.

<table>
<thead>
<tr class="header">
<th><p>Command</p></th>
<th><p>Description</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><p>Open</p></td>
<td><p>Opens the selected relation using the default editor.</p></td>
</tr>
<tr class="even">
<td><p>Edit</p></td>
<td><p>Opens the Artifact for editing.</p></td>
</tr>
<tr class="odd">
<td><p>Mass Edit</p></td>
<td><p>Opens the Artifact Mass Editor populated with the selected artifacts.</p></td>
</tr>
<tr class="even">
<td><p>View Relation Table Report</p></td>
<td><p>Opens an HTML report of the relation tree.</p></td>
</tr>
<tr class="odd">
<td><p>Order Relation...</p></td>
<td><p>Select an order type for the relations in the group. Once the user makes a selection, the artifacts will be ordered appropriately. Options include:</p>
<ol>
<li>Lexicographical Ascending</li>
<li>Lexicographical Descending</li>
<li>User Ordered</li>
<li>Unordered</li>
</ol></td>
</tr>
<tr class="even">
<td><p>Delete Relation</p></td>
<td><p>Deletes the selected relations.</p></td>
</tr>
<tr class="odd">
<td><p>Expand All</p></td>
<td><p>Expands all tree nodes containing relations to this artifact.</p></td>
</tr>
<tr class="even">
<td><p>Select All</p></td>
<td><p>Selects all tree nodes.</p></td>
</tr>
<tr class="odd">
<td><p>Delete Artifact</p></td>
<td><p>Deletes the selected artifact and its relation to this artifact.</p></td>
</tr>
</tbody>
</table>

#### Drag N' Drop Operation

Artifacts can be related by dragging and dropping artifacts from
Artifact Explorer onto a relation link group.

Relation links may be reordered within the same relation link group by
selecting the link and dropping it into the desired location.

### Details Section

Displays artifact meta-data.

## Blam Editor

BLAM Lightweight Artifact Manipulation (BLAM) allows non-programmers to
graphically construct workflows to automate repetitive tasks. A given
workflow can be used for variety of similar tasks by using customizable
controls to specify workflow parameters.

## Resource History View

The resource history view allows users to view the detailed changes to
an aritfact.

![image:resourcehistoryview.jpg](/docs/images/resourcehistoryview.jpg
"image:resourcehistoryview.jpg")

In addition, the view also provides the user with the ability to select
and compare artifacts.

![image:resourcehistoryviewmenu2.jpg](/docs/images/resourcehistoryviewmenu2.jpg
"image:resourcehistoryviewmenu2.jpg")

## Mass Artifact Editor

The Mass Artifact Editor allows for easy view and editing of multiple
artifacts and their attributes. To Open, right-click on the Artifact
Explorer, the Search Results page, to open the pop-up menu and select
the **Mass Edit** option. Single cells can be edited via
Alt-Left-Mouse-Click. After editing any number of artifacts, click on
the save button. All changes will be saved.

![masseditor.png](/docs/images/masseditor.png "masseditor.png")

### Toolbar buttons

| Icon                                                        | Description                                                                     |
| ----------------------------------------------------------- | ------------------------------------------------------------------------------- |
| ![image:refresh.gif](/docs/images/refresh.gif "image:refresh.gif")       | Refreshes the data with the latest information.                                 |
| ![image:customize.gif](/docs/images/customize.gif "image:customize.gif") | Opens the table customization dialog. This allows users to add or hide columns. |
| ![image:bug.gif](/docs/images/bug.gif "image:bug.gif")                   | Opens an action against the Mass Editor.                                        |

See
[Xviewer](http://wiki.eclipse.org/OSEE/Users_Guide#XViewer_-_Advanced_TreeViewer_Widget)
for more information

## Quick Search View

The Quick Search view allows users to perform searches for information
that is contained *inside* artifacts in a selected branch or search for
artifacts by their GUID. The view is opened by default in the Define
Perspective. It can also be opened by clicking on the Artifact Explorer
tool bar's ![image:artifact_search.jpg](/docs/images/artifact_search.jpg
"image:artifact_search.jpg") icon.

![image:quicksearchview.png](/docs/images/quicksearchview.png
"image:quicksearchview.png")

To find all artifacts that contain a particular set of keywords:

1.  Select **Window \> Show View \> Other... \> OSEE \> Quick Search**
    to open the view
2.  Type your search string in the **Enter Search String** combo box (or
    use the pull-down list to select a previously entered search
    expression).
      - Special characters such as `(' ', !, ", #, $, %, (, ), *, +, ,,
        -, ., /, :, ;, <, >, ?, @, [, \, ], ^, {, |, }, ~, _)` are
        assumed to be word separators.
      - In the GUID search, spaces and commas are treated as separators.
          -
            For example:
            :\*Under normal attribute search operations, `hello.world`
            will be translated to `hello` and `world`. The search will
            match attributes with `hello` and `world` keywords.
            :\*When the GUID search is used,
            (`A+ABG7jFm+0BKaVZIxfqOQ,AFABG7jFm+0BKaVZIxfqOQ`) will be
            interpreted as two GUIDs. The search will match artifacts
            containing `A+ABG7jFm+0BKaVZIxfqOQ` and
            `AFABG7jFm+0BKaVZIxfqOQ` as its GUID.
3.  Select search options
4.  Click **Search** or press **Enter** from the combo box to execute
    the search
5.  The Search view displays the results of your search
6.  Right-click on any item in the Search view to open a context menu
    that allows you perform various operations on the artifacts such as
    copy search results to the clipboard or reveal a selected artifact
    in Artifact Explorer.
7.  To open one of the listed artifacts, double-click it or select
    **Open** from the context menu.

### Quick Search Options

#### Attribute Type Filter

An option to search in a specific set of attribute types for artifacts
on the selected branch. Type in the desired words to search for. Make
sure the Attribute Type Filter option is selected under Options. By
default, the filter is set to filter by attributes of type Name. If you
wish to change the filter, select the button to the right of the
configuration text area. When this is performed, a dialog displaying all
the different tagged attribute types will be displayed. Check the items
to include in the filter and select Ok. Press the Search button to
execute the search.

Note: When Attribute Type Filter option is selected, By Id option is not
allowed. Therefore, selecting Attribute Type Filter option disables the
By Id option by setting its state to not selected.

#### By GUID

An option to search for artifacts with a particular GUID on the default
branch. Type in the desired GUID(s) separated by commas or spaces. Make
sure the By GUID option is selected under Options, then press the Search
button.

Note: When By GUID option is selected, Attribute Type Filter option is
not allowed. Therefore, selecting By GUID option disables the Attribute
Type Filter option by setting its state to not selected.

#### Include Deleted

An option to include artifacts that have been deleted as part of a quick
search on the default branch. Type in the desired words. Make sure the
Include Deleted option is selected under Options, then press the Search
button.

#### Match Word Order

An option to match a phrase against artifacts during a quick search
operation on the default branch. Type in the desired words. Make sure
the Match Word Order option is selected under Options, then press the
Search button.

#### Exact Match

An option to return exact matches to the input string. The case and
special characters that are part of the input must be matched.

#### Case Sensitive

Type in the desired words. Make sure the Match Word Order option is
selected under Options, select All Match Locations then press the Search
button.

## Sky Walker View

The Sky Walker View displays a graphical representation of artifacts and
their relations for easy navigation.

![image:skywalker.png](/docs/images/skywalker.png "image:skywalker.png")

## Change Report Editor

The change report view shows all changes made to a branch. A Change
Report can be performed for a branch two ways. The first way is by
right-clicking a branch from the Branch Manager then choosing the "Show
Change Report" menu item. The second way is to select "Show Change
Report" from the Aspect view of an ATS Action that is still in work;
this will do a Change Report for the working branch of the Aspect.

The Change Report will display all of the artifacts on the branch that
have had an attribute or relation link modified. It will also do
conflict detection on these artifacts against the parent branch.
Attributes and relation links with multiple changes will provide a
summarized node that shows the final effect of the changes and can be
expanded to view all of the minor changes that were made. If an
attribute or relation link was modified on both branches then the
summary will show a red conflict mark to signify that a commit will
cause an override to occur.

On the Transactions tab, OSEE will show all the transactions that were
made on the branch. Author and timestamp show who and when and admins
have the ability to purge a transaction.

![changereporteditor.png](/docs/images/changereporteditor.png
"changereporteditor.png")

### Toolbar buttons

| Command                                                     | Description                                                                  |
| ----------------------------------------------------------- | ---------------------------------------------------------------------------- |
| ![image:refresh.gif](/docs/images/refresh.gif "image:refresh.gif")       | Refreshes the data in the change report view with the latest information.    |
| ![image:customize.gif](/docs/images/customize.gif "image:customize.gif") | Opens the table customization dialog. This allows users add or hide columns. |
| ![image:bug.gif](/docs/images/bug.gif "image:bug.gif")                   | Opens an action against the Change Report View.                              |

### Toolbar Drop-Down

To display, click on the inverted triangle located on the upper
right-hand side of the Change Report View.

| Command             | Description                                                                        |
| ------------------- | ---------------------------------------------------------------------------------- |
| Show Document Order | Switch presentation to show artifacts ordered by their default hierarchy relation. |

### Pop-up Menu

To display, perform a right-click on any branch.

| Command                          | Description                                                                                                                                                                                                        |
| -------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Open                             | Opens the selected artifact using the default editor.                                                                                                                                                              |
| Open With                        | Opens a sub-menu listing the available editor's for this artifact.                                                                                                                                                 |
| Reveal in Artifact Explorer      | Displays an artifacts location in the Artifact Explorer.                                                                                                                                                           |
| Resource History                 | Opens the Resource History view for the selected artifact. This will display all transactions for this artifact.                                                                                                   |
| View Word Change Report          | Displays a Word document populated with the branch differences of the selected artifacts.                                                                                                                          |
| View Viewer Report               | Generates a report of the Change Report View content.                                                                                                                                                              |
| Copy                             | Copies the artifact.                                                                                                                                                                                               |
| Replace with Baseline Version... | Displays dialog to Replace a single Attribute or Artifact. Attribute - Will replace the current attribute with the baseline version. Artifact - Will replace the complete artifact (all attributes and relations). |




## Define Navigator

The Define Navigator, shown by default in the Define Perspective,
provides a central location to launch frequently used define operations.
To quickly find a define operation to execute, enter text into the
filter box. This will filter out all navigation items that contains the
entered text. Select the clear action (![image:clear.gif](/docs/images/clear.gif
"image:clear.gif")) to clear out the text and restore all navigation
items. To execute the operation, double-click on any of the navigation
item.

## Merge Manager

The Merge Manager is used to resolve conflicts that arise when doing
development on parallel branches. The Merge Manager makes conflicts that
arise easily identifiable and then provides the means for resolving the
conflicts, so that the working branch can be committed. A conflict
exists if the value of an attribute/artifact has changed on both the
Destination and Source Branches. For reference the Source Branch is the
users working branch. It is the branch that the user has been making
changes to and would like to then add back into the Destination Branch
or Baseline Branch. Both branches are identified by name in the header
of the Merge Manager.

Depending upon the conflict found, the user may have several choices for
resolution. These include:

  - Accept the value on the Source Branch and overwrite the value on the
    Destination Branch
  - Accept the value on the Destination Branch and do not add any of the
    Source Branch changes, (These will still show up as merged on Change
    Reports)
  - Create a solution that is a combination of the two changes
  - Revert the changes on the Source Branch (This is the only available
    solution when the Artifact/Attribute was deleted on the Destination
    Branch, will show up as no change on the Change Report)
  - Do nothing (only possible for informational conflicts)

**Committing of Branches is blocked until all conflicts are resolved.**

![mergemanager.png](/docs/images/mergemanager.png "mergemanager.png")

### Icons

| Icon                                                                                                          | Description                                                                                                                                                                            |
| ------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![image:chkbox_disabled.gif](/docs/images/chkbox_disabled.gif "image:chkbox_disabled.gif")                                | Resolution has been started for the conflict                                                                                                                                           |
| ![image:accept.gif](/docs/images/accept.gif "image:accept.gif")                                                            | Conflict has been resolved and is ready to be committed, In the Merge column it means that the Source and Destination Branches have the same value even though they were both changed. |
| ![image:chkbox_enabled_conflicted.gif](/docs/images/chkbox_enabled_conflicted.gif "image:chkbox_enabled_conflicted.gif") | After a conflict has been marked as resolved the value changed on the source or destination branch                                                                                     |
| ![image:issue.gif](/docs/images/issue.gif "image:issue.gif")                                                               | A conflict that provides the user special information but does not need to be resolved                                                                                                 |
| ![image:red_light.gif](/docs/images/red_light.gif "image:red_light.gif")                                                  | A conflict that can not be resolved except by reverting the Artifact or Attribute, because it was deleted on the Destination Branch                                                    |
| ![image:blue_d.gif](/docs/images/blue_d.gif "image:blue_d.gif")                                                           | Shows that the item defined by the column it is in has the Destination Branches value                                                                                                  |
| ![image:green_s.gif](/docs/images/green_s.gif "image:green_s.gif")                                                        | Shows that the item defined by the column it is in has the Source Branches value                                                                                                       |
| ![image:yellow_m.gif](/docs/images/yellow_m.gif "image:yellow_m.gif")                                                     | Shows that the item defined by the column it is in has a new value that is neither the Source Branch nor Destination Branch value.                                                     |
| ![image:conflict.gif](/docs/images/conflict.gif "image:conflict.gif")                                                      | Shows that the conflict has not been given an initial value                                                                                                                            |
| ![image:user.gif](/docs/images/user.gif "image:user.gif")                                                                  | Opens the Associated Artifact for the merge                                                                                                                                            |
| ![image:branch_change_source.gif](/docs/images/branch_change_source.gif "image:branch_change_source.gif")                | Opens up the Change Report for the Source Branch                                                                                                                                       |
| ![image:branch_change_dest.gif](/docs/images/branch_change_dest.gif "image:branch_change_dest.gif")                      | Opens up the Change Report for the Destination Branch                                                                                                                                  |
| ![image:refresh.gif](/docs/images/refresh.gif "image:refresh.gif")                                                         | Refreshes the Merge Manger view to find new conflicts                                                                                                                                  |
| ![image:customize.gif](/docs/images/customize.gif "image:customize.gif")                                                   | Allows the user to customize the Merge Manager tables                                                                                                                                  |
| ![image:bug.gif](/docs/images/bug.gif "image:bug.gif")                                                                     | Report a bug with the Merge Manager                                                                                                                                                    |

### GUI Overview

The GUI is organized to provide the user with an ability to quickly
identify conflicts.

  - **The Heading** - The Heading contains text to help identify what is
    being merged. It identifies the Source Branch, and the Destination
    Branch. It also provides the user with information about how many
    conflicts there are and if they have been resolved. The Heading also
    contains easy launch icons for additional tools in connection with
    the Merge Manager.
  - **The Conflict Resolution Column** - This column provides the user
    information about the state of the conflict. A blank entry in the
    column means that the conflict is new and has not had any actions
    performed on it.
      - A ![Image:Chkbox_disabled.gif](Chkbox_disabled.gif
        "Image:Chkbox_disabled.gif") indicates that conflict is in the
        modified state. This means the user has begun merging the
        conflict but has not marked it as resolved. The user may
        transition it into the resolved state by left clicking on the
        ![Image:Chkbox_disabled.gif](Chkbox_disabled.gif
        "Image:Chkbox_disabled.gif") icon.
      - The ![image:accept.gif](/docs/images/accept.gif "image:accept.gif") icon
        indicates the user has marked the conflict as resolved. This
        means they have selected a value for it and have verified the
        value going in is what they want. No additional changes are
        allowed on a conflict once it is in the resolved state. It can
        be placed back into the modified state by left clicking on the
        ![image:accept.gif](/docs/images/accept.gif "image:accept.gif") icon.
      - The
        ![Image:Chkbox_enabled_conflicted.gif](Chkbox_enabled_conflicted.gif
        "Image:Chkbox_enabled_conflicted.gif") means that a conflict was
        in the resolved state but a new change has occurred on either
        the Source or Destination Branch. It serves to notify the user
        that the conflict was not in the finalized state when they
        resolved the conflict. The user can return to resolved state by
        left clicking on the
        ![Image:Chkbox_enabled_conflicted.gif](Chkbox_enabled_conflicted.gif
        "Image:Chkbox_enabled_conflicted.gif") icon.
      - The ![image:red_light.gif](/docs/images/red_light.gif "image:red_light.gif")
        icon indicates that an Artifact or Attribute must be reverted on
        the Source Branch. This indicates that the Artifact/Attribute
        was deleted on the Destination Branch and can not have a change
        committed onto it. The user must abandon any change to that
        artifact attribute by using the revert command. Once the
        Artifact/Attribute has been reverted the Merge Manager will be
        refreshed and the conflict will be removed.
      - The ![image:issue.gif](/docs/images/issue.gif "image:issue.gif") icon
        indicates an informational conflict. The user does not have to
        take any action to resolve these conflicts. It just provides the
        information that the Source Branch deleted the
        Artifact/Attribute but the Destination Branch has been modified.
        The user is free to act as desired based on the provided
        information.
  - **The Artifact Name Column** - This column tells which artifact the
    conflict occurred on. If the name is different between the Source
    and Destination Branches, (this will show up as a conflict) it will
    at first showing use the Source Branch value and then use whatever
    the name is resolved to be after that has occurred.
  - **The Artifact Type Column** - Simply lists what type of Artifact is
    conflicted
  - **The Conflicting Item Column** - In the case of an attribute
    conflict it states what attribute type is conflicting. In the case
    of an artifact conflict it will always say "Artifact State"
  - **The Source Value Column** - When possible this column tells what
    value the Source Branch has for the conflict. It will always have a
    ![image:green_s.gif](/docs/images/green_s.gif "image:green_s.gif") icon. If the
    conflicting item is Word Formatted Content the words "Stream data"
    will be shown. For artifact conflicts it will either show "Modified"
    or "Deleted". Left clicking on the ![Image:Green_s.gif](Green_s.gif
    "Image:Green_s.gif") icon will populate the Merge Branch with value
    found on the Source Branch.
  - **The Destination Value Column** - When possible this column tells
    what value the Destination Branch has for the conflict. It will
    always have a ![image:blue_d.gif](/docs/images/blue_d.gif "image:blue_d.gif")
    icon. If the conflicting item is Word Formatted Content the words
    "Stream data" will be shown. For artifact conflicts it will either
    show "Modified" or "Deleted". Left clicking on the
    ![image:blue_d.gif](/docs/images/blue_d.gif "image:blue_d.gif") icon will
    populate the Merge Branch with value found on the Destination
    Branch.
  - **The Merge Value Column** - The Merged Value column serves to show
    the user the value that has been selected for use when the Branch is
    committed. The Merge value is actually kept on a new "Merge Branch"
    and so any changes made to it will not affect the value seen on the
    Source or Destination Branches. When the Merge Value column is blank
    with no icon, the conflict is informational and no actions are
    provided.
      - When the Merge Value column contains a
        ![image:conflict.gif](/docs/images/conflict.gif "image:conflict.gif") icon
        the value has not been set. This is the icon that should be
        shown for all conflicts (Except informational conflicts, or same
        value conflicts) the first time the user brings up the merge
        manager.
      - The ![image:green_s.gif](/docs/images/green_s.gif "image:green_s.gif") icon
        indicates that the Source Value was selected as the final value.
        The actual Source Value text will also be shown in this column
        if possible.
      - The ![image:blue_d.gif](/docs/images/blue_d.gif "image:blue_d.gif") icon
        indicates that the Destination Value was selected as the final
        value. The Destination Value text will also be shown in this
        column if possible.
      - The ![image:yellow_m.gif](/docs/images/yellow_m.gif "image:yellow_m.gif")
        icon will be shown when a new value has been selected for the
        final value. This indicates that the user has modified the final
        value so that it is no longer a copy of the Source or
        Destination, but some variation thereof.
      - A ![image:accept.gif](/docs/images/accept.gif "image:accept.gif") icon
        indicates that although both the Source Branch Value and
        Destination Branch Value have changed they were both changed to
        the same value and so there is not really a conflict. Left
        clicking on the icon in the Merge Value column will bring up the
        Merge Wizard or in the case of un-resolvable conflicts a dialog
        offering the ability to revert the conflicting item.

### Pop-up Menu

To display, perform a right click on any row. This will provide a menu
with options to resolve conflicts.

<table>
<thead>
<tr class="header">
<th><p>Item</p></th>
<th><p>Description</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><p>Set Source as Default Branch</p></td>
<td><p>This selection is a quick way to set the Source Branch as the default branch.<br />
If the Source Branch is already set as the Default Branch, the menu item will be grayed out and there will be a check mark next to the option.</p></td>
</tr>
<tr class="even">
<td><p>Set Destination as Default Branch</p></td>
<td><p>This selection is a quick way to set the Destination Branch as the default branch.<br />
If the Destination Branch is already set as the Default Branch, the menu item will be grayed out and there will be a check mark next to the option.</p></td>
</tr>
<tr class="odd">
<td><p>Edit Merge Artifact</p></td>
<td><p>This option is only enabled for Word Formatted Content conflicts and will bring up the Merge Artifact in Word. The Merge Artifact is a separate version of the artifact that will preserve the details of the Merge, and will be reviewable in the Merge Manager after an artifact is committed. <em>IMPORTANT: If the user makes the changes to their Source Branch instead of on the Merge Artifact the Merge Manager will incorrectly represent the merge in future reviews.</em></p></td>
</tr>
<tr class="even">
<td><p>Generate Three Way Merge</p></td>
<td><p>Will generate a Three Way Merge for the Word Formatted Content.</p></td>
</tr>
<tr class="odd">
<td><p>Preview Source Artifact</p></td>
<td><p>Show a preview in Word of the Artifact based on the version selected.</p></td>
</tr>
<tr class="even">
<td><p>Preview Destination Artifact</p></td>
<td></td>
</tr>
<tr class="odd">
<td><p>Preview Merge Artifact</p></td>
<td></td>
</tr>
<tr class="even">
<td><p>Show Source Branch Differences</p></td>
<td><p>Generates differences based upon which option is selected. Allows the user to see how different versions of the artifact differ</p></td>
</tr>
<tr class="odd">
<td><p>Show Destination Branch Differences</p></td>
<td></td>
</tr>
<tr class="even">
<td><p>Show Source/Destination Differences</p></td>
<td></td>
</tr>
<tr class="odd">
<td><p>Show Source/Merge Differences</p></td>
<td></td>
</tr>
<tr class="even">
<td><p>Show Destination/Merge Differences</p></td>
<td></td>
</tr>
<tr class="odd">
<td><p>Reveal Artifact in Explorer</p></td>
<td><p>This option is only available when either the Source or Destination Branch is set as the default branch. When such is the case this will reveal the artifact in the Artifact Explorer for the Branch that is the default branch.</p></td>
</tr>
<tr class="even">
<td><p>Resource History</p></td>
<td><p>This option is only available when either the Source or Destination Branch is set as the default branch. When such is the case this will reveal the resource history of the artifact on the Branch that is the default branch.</p></td>
</tr>
</tbody>
</table>

### General Resolution of Conflicts

<table>
<thead>
<tr class="header">
<th><p>Type</p></th>
<th><p>Description</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><p>Informational Conflicts</p></td>
<td><p>Informational conflicts are identified by the <img src="/docs/images/issue.gif" title="fig:Image:Issue.gif" alt="Image:Issue.gif" /> icon in the conflict resolution column in the GUI. Informational conflicts require no action by the user, and no actions are provided in the GUI other than the ability to use the right click menu to examine the artifact using the tools provided there. An informational conflict is generated when the Source branch deletes an Artifact or an Attribute and that same Artifact or Attribute was modified on the Destination Branch. This is to allow the user the opportunity to review a change that was made on the Destination Branch that might make them want to take some action in regards to their deletion.</p></td>
</tr>
<tr class="even">
<td><p>Un-resolvable Conflicts</p></td>
<td><p>Un-resolvable Conflicts are identified by the <img src="/docs/images/red_light.gif" title="fig:Image:Red_light.gif" alt="Image:Red_light.gif" /> icon in the conflict resolution column of the GUI. This conflicts require the user to revert the Artifact or Attribute that caused the conflict on the Source Branch. An Un-resolvable conflict is caused when the Destination Branch deletes an Artifact or Attribute while the Source Branch modifies that same Artifact, Attribute. The reason the user must revert their changes is that committing in their changes would essentially undo that deletion and bring that item back into existence. If the deletion should not have happened the user needs to talk with the committer of the deletion to resolve the issue.</p></td>
</tr>
<tr class="odd">
<td><p>Attribute Conflicts</p></td>
<td><p>Attribute Conflicts occur when both the Destination and Source branch modify an attribute. This section will cover all attributes except Word Formatted Content Attributes.</p>
<p>The resolution of these Attribute values provide three options:</p>
<ol>
<li><strong>Use the Source attribute value</strong> - Left click on the <img src="/docs/images/green_s.gif" title="fig:Image:Green_s.gif" alt="Image:Green_s.gif" /> icon in the Source Value column. This will copy the <img src="/docs/images/green_s.gif" title="fig:Image:Green_s.gif" alt="Image:Green_s.gif" /> icon and the value displayed in the Source Value column into the Merged Value Column. <em>Available from the Merge Wizard (Left click on the icon in the Merge Value column) with the "Load Source Data" and "Load Destination Value" buttons.</em></li>
<li><strong>Use the destination attribute value</strong> - In order to use the Destination Value the user may left click on the <img src="/docs/images/blue_d.gif" title="fig:Image:Blue_d.gif" alt="Image:Blue_d.gif" /> icon in the Destination Value column. This will copy the <img src="/docs/images/blue_d.gif" title="fig:Image:Blue_d.gif" alt="Image:Blue_d.gif" /> icon and the value displayed in the Source Value column into the Merged Value Column. <em>Available from the Merge Wizard (Left click on the icon in the Merge Value column) with the "Load Source Data" and "Load Destination Value" buttons.</em></li>
<li><strong>Use a modified value that is some combination of the source and destination values</strong> - In order to modify the value to some combination the user must bring up the Merge Wizard which has an embedded editor specific to the attribute that needs to be modified. Once the value is accurately entered in the editor the user may than select "Finish" This will place a <img src="/docs/images/yellow_m.gif" title="fig:Image:Yellow_m.gif" alt="Image:Yellow_m.gif" /> icon in the Merged Value column along with the new value. The user then right clicks on the <img src="/docs/images/chkbox_disabled.gif" title="fig:Image:Chkbox_disabled.gif" alt="Image:Chkbox_disabled.gif" /> in the Conflict Status Column so that the <img src="/docs/images/accept.gif" title="fig:Image:Accept.gif" alt="Image:Accept.gif" /> icon is displayed. The conflict is resolved and will allow the Source Branch to be committed.</li>
</ol></td>
</tr>
</tbody>
</table>

### Word Formatted Content Conflict Resolution

Resolution of conflicts is provided in two different ways. They can
either copy and paste the changes into their Merge Artifact document or
they can generate a Three Way Merge and accept the changes that show up
in the generated document. Both approaches have their advantages and
disadvantages and are best suited for different situations. They can
also be combined where the situation warrants it, however the three way
merge must always be done first if this is the case.

### Manual Merging

**Usage:**

  - When one version of the artifact has many changes and the other
    version has very few changes
  - When both files have formatting changes
  - When three way merging generates a complex document
  - When both versions edit the same text in multiple places

Manual Merging is the process of combining the Source Branch changes and
the destination branch changes manually by copying and pasting them into
the Merge Artifact document. The Merge Artifact is a separate version of
the artifact that will preserve the details of the Merge, and will be
reviewable in the Merge Manager after an artifact is committed.
IMPORTANT: If the user makes the changes to their Source Branch instead
of on the Merge Artifact the Merge Manager will incorrectly represent
the merge in future reviews.

The following procedure illustrates the functionality available to
facilitate a manual merge.

The user will first either launch the Merge Wizard by left clicking on
the icon in the Merge Value column of the GUI or they may select the
functionality from the right click menu for the conflict in question.
The first thing to do is to bring up a word document comparison of both
the Source Branch Version and the Destination Branch Version. These
documents will show all of the changes that have been made to these two
artifacts since the Source Branch was created. To launch these
difference's the user either select "Show Source Diff" and "Show
Destination Diff" from the wizard or "Differences"-\>"Show Source Branch
Differences" and "Differences"-\>"Show Destination Branch Differences"
from the right click menu. These will bring up the two difference's in
different Word instances with window labels to allow the user to
differentiate the files. The intention of bringing up these difference's
is twofold. Firstly, it allows the user to identify the file that has
the most changes. Secondly, it will come in use later when the user
copy's and paste's changes into the Merge document.

Upon identifying the branch that has the most changes the user should
then set the Merge Artifact to contain that branches value. This is done
by either selecting "Populate with Source Data" or "Populate with
Destination Data" from the Merge Wizard or left clicking on the icon or
the icon in the Source and Destination Value columns in the Merge
Manager GUI. The user can then bring up the Merge Artifact for editing
by clicking on "Edit Merge Artifact" in the Merge Wizard or in the right
click menu. The Document that comes up contains the Merge Artifact and
any changes made to it will be reflected when the Source Branch is
committed. The user can than begin to copy the changes from the diff
report that showed the fewest changes (opposite of the one chosen as the
baseline). After all changes have been migrated into the Merge Artifact
document the user than saves the document, which will preserve the Merge
Artifact value. The user should be aware that any changes they do not
wish to preserve from either the Source or Destination version of the
Artifact need to be omitted on the Merge Artifact.

The user then right clicks on the in the Conflict Status Column so that
the icon is displayed. The conflict is resolved and will allow the
Source Branch to be committed.

#### Three Way Merge

**Usage:**

  - When both versions have many changes or both versions have few
    changes.
  - When only one file has formatting changes (Must be combined with
    Manual Merging in this case)
  - When three way merging generates an understandable document

Three Way Merging leverages Microsoft Words ability to merge documents.
At the beginning of any Word Formatted Content merge it is recommended
that user generate a Three Way Merge and check the complexity of the
document. In most cases Three Way Merging is a quicker way to merge two
documents, however in some cases the Three Way Merge will generate a
document that is difficult to use and understand. This usually arises
when the Source and Destination branches have edited the same text or if
one of the branches has touched a large percentage of the file. As it
runs fairly quickly it is always a good idea to run it at the beginning
of a Merge to check if it is useful. Three Way Merging only allows the
user to maintain format changes from one of the documents. If format
changes are made on both documents the Three Way Merge will prompt the
user as to which format changes they would like to maintain, the user
will then need to copy the format changes from the other document into
the Merge Artifact document manually.

A Three Way Merge is generated by selecting Generate Three Way Merge
from either the Merge Wizard or the right click menu. IMPORTANT:
Generating a Three Way Merge will discard any changes made to the Merge
Artifact, therefore a prompt will make sure this is the intended
operation. If a user had started a Three Way Merge previously but had
not completed the Merge the user is also given the option of continuing
the previous Merge in the prompt (Selecting Edit Merge Artifact will
also have this effect). The following is an example of a Three Way Merge
in Word.

![wordthreewaymerge.png](/docs/images/wordthreewaymerge.png "wordthreewaymerge.png")

The changes made by the Source Branch and Destination Branch are shown
in different colors in the Word Document. In this particular case the
changes made in Red were done by the Source Branch and the changes made
in Blue were done on the Destination Branch. The color scheme is not
consistent and the user needs to verify which color equates to which
changes by hovering there mouse over one of the changes. A popup will be
shown which will identify the author. The following Guide will explain
how to resolve the changes in the document. IMPORTANT: All changes must
be either accepted or rejected before the conflict can be marked as
resolved. After the user has resolved all the changes it is a good idea
to do generate a difference document between the Source Artifact and the
Merge Artifact, and the Destination Artifact and the Merge Artifact by
selecting "Show Source/Merge Diff" and "Show Destination/Merge Diff"
from the merge Wizard or "Differences"-\>"Show Source/Merge Differences"
and "Differences"-\>"Show Destination/Merge Differences" from the right
click menu. These views will show the differences between the branch
artifact and the merge artifact. For the Source/Merge difference this
will show everything that is different between the source document and
the Merge document. In the case where the user accepts all changes from
the source and destination branches this diff will highlight all of the
changes that occurred on the destination branch. In the
Destination/Merge diff it will highlight all of the changes that
happened on the source branch. It is always possible to use Manual
Merging techniques in conjunction with Three Way Merging.

The user then right clicks on the in the Conflict Status Column so that
the icon is displayed. The conflict is resolved and will allow the
Source Branch to be committed.

![word_formatted_content_merge_wizard.jpg](/docs/images/word_formatted_content_merge_wizard.jpg
"word_formatted_content_merge_wizard.jpg")

### Additional Features

The Merge Wizard contains a "Clear the Merge Artifact" that is not
available from the right click menu and only available for Word
Formatted Content. This will empty out the Merge artifact and allow the
user to start with an empty document for editing. It will also place a
![image:conflict.gif](/docs/images/conflict.gif "image:conflict.gif")icon in the
merge value column for that conflict.

### Step-By-Step Recipe

Upon selecting to commit a working branch, OSEE will prompt the user to
perform a merge if conflicts are detected between the changes made on
the child branch and any changes made to the parent branch since the
child branch was created.

![stoppeddialog.jpg](/docs/images/stoppeddialog.jpg "stoppeddialog.jpg")

The Merge Manager in OSEE will be used to reconcile these differences.
From the *Merge Manager* tab, select the Merged Value icon which will
cause the *Edit the attribute* window to appear.

Perform the following steps for each artifact listed on the *Merge
Manager* tab:

1.  Determine which change would be the easiest to re-implement
    (typically the smaller and simpler of the two). This can be done by
    comparing all of the changes made to this UI.
    1.  **Show Source Diff** displays the changes made on this working
        branch.
    2.  **Show Destination Diff** displays the changes made on the
        parent branch.
    3.  **Show Source/Destination Diff** displays the effect on the
        parent branch prior to any merge management. This view will show
        how the changes made on this working branch will be overwritten
        by the changes made on the parent branch.
2.  Select the *more complicated* of the two changes to populate the
    Merge Artifact: **Populate with Source Data** or **Populate with
    Destination Data**. The Merged Value column on the *Merge Manager*
    tab and the top-most icon in the *Edit the attribute* window will
    update to display "S" or "D" based upon this selection.
3.  Select **Edit Merge Artifact** to open the merge document for
    editing.
4.  Re-implement the changes from the *simpler* change report.
5.  If at any time the merge effort needs to be cleared or re-started,
    select **Clear the Merge Artifact**.
6.  The following selections may be used to review and confirm the
    changes made during the merge. Prior to updating the Merge Artifact,
    these selections will not provide accurate information.
    1.  **Show Source/Merge Diff** displays the additional changes
        beyond those made on the working branch.
    2.  **Show Destination/Merge Diff** displays the additional changes
        beyond those made on the parent branch.
7.  Select **Finish**
8.  Under the Conflict Resolution column on the *Merge Manager* tab,
    check the box so that the resolution status updates from "Modified"
    to "Resolved".
9.  Once all artifact conflicts have been addressed, the *Merge Manager*
    tab will report "All Conflicts Are Resolved." At this point, the
    user can return to the Workflow tab and re-initiate committing the
    branch.
10. OSEE will display the *Commit Branch* window to confirm the
    conflicts have been resolved via the Merge Manager.
11. Select **Ok** to finish committing the branch.

![complete2.jpg](/docs/images/complete2.jpg "complete2.jpg")

## Test Run View

The Test Run View provides an integration point with OTE (OSEE Test
Environment). The test run view is used for viewing test run results. It
can view a summary of output files that exist on a file system and it
will upload those output files to the OSEE data store. It can also be
used to view previous test runs that have been uploaded to the OSEE data
store.

![testrunview.jpg](/docs/images/testrunview.jpg "testrunview.jpg")

# Search

## Advanced Artifact Search

![image:artifact_search_page.gif](/docs/images/artifact_search_page.gif
"image:artifact_search_page.gif")

What it is The search page in the Eclipse Search window for finding
artifacts on the default branch. How to use it

The Artifact Search page works by building a list of filters which that
describe the desired artifacts. Filter types are selected from the drop
down at the top of the page. After completing the options for the
filter, pressing the Add Filter button will add the filter to the list
of filters. If the Not Equal option is checked, then it will be added
with the image signifying that the complement of the filter will be
used.

Filters can be removed from the list at anytime by selecting the next to
the filter.

The radio buttons in the Artifacts that match frame are used to control
whether artifacts are returned that match every filter listed or at
least one filter listed.

Once all of the options have been filled out, the Search button can be
pressed to start the search against the default branch. For convenience
the default branch is stated at the top of the search page. If a large
number of artifacts will be returned then a confirmation will be
displayed with a count of the artifacts that are about to be loaded.

The Search button will not be enabled until there is at least one filter
in the list.

# Wizards

## Artifact Import Wizard

|  |
|  |
|  |
|  |
|  |

# Services

## Event Service

OSEE is a client side application that runs off an Oracle database. As
with many applications, OSEE caches some of the data it provides to the
user. These caches need to be notified that there are updates available.
These updates are triggered by an event service that "connects" all OSEE
instances and notifies them that changes have been made to cached data.

When your application focus is on any OSEE View, you will notice a
double-arrow icon at the bottom of your workbench. When NOT connected to
the event service, this icon will show a red slash. When this happens,
you should shutdown, restart and accept all OSEE updates. If this does
not solve the problem, contact an OSEE Team member for help in resolving
the problem.

## Attribute Tagging for Quick Search

When an artifact is saved, each attribute contained in the artifact is
analyzed to produce a list of tags that are then associated with the
artifact. Quick search is a form of <i>keyword</i> based searching which
uses tags to perform contextual artifact searches.

For an attribute to be tagged by the system, it must meet the following
criteria:

  - The attribute's type must specify an <b>Attribute Tagger</b> to be
    used by the tagging system.
  - The attribute must contain valid data.
  - The attribute revision must be saved in the database before sending
    to tagging system.

To produce tags, modified attributes are sent to the OSEE application
server where the tagging system processes each attribute using an
<b>Attribute Tagger</b> specified by the attribute's type. The
<b>Attribute Tagger</b> knows how to interpret the attribute's data and
how to extract words from the content. At this point, a word is defined
as a sequential set of alphanumeric characters delimited by one or more
spaces. As words are parsed, they are sent to the tagging system's word
encoder where the following processing takes place:

  - The characters in the original word are converted to lower case.
  - The lowercase version of the word is encoded and stored in the
    tagging system.
  - The lowercase version of the word is split using `(' ', !, ", #, $,
    %, (, ), *, +, ,, -, ., /, :, ;, <, >, ?, @, [, \, ], ^, {, |, }, ~,
    _)` as delimiters.
  - Words given in inflected form (possessive, plural, etc) are
    converted into [citation
    form](http://en.wikipedia.org/wiki/Dictionary_form).
  - Each word is encoded and stored in the tag system.

When encoding words into tags, the tag encoder uses an algorithm which
transforms the word's characters into a bit-packed tag that will fit in
a 64-bit integer. The tag will represent up to 12 characters (all that
can fit into 64-bits). Longer words will be turned into consecutive
tags.

### Tag Encoding Examples

| Original   | Keywords               | Encoding  |
| ---------- | ---------------------- | --------- |
| appendices | appendix |-220858502   |           |
| batteries  | battery                | 529513131 |
| alternate  | alternate |-1420231874 |           |
| backup     | backup                 | 24902827  |

# Custom Widgets

## XViewer - Advanced TreeViewer Widget

[EclipseCon 2009 Presentation
Slides](http://www.eclipse.org/osee/xviewer/XViewer.pdf)

[Download Zip of Code and
Example](http://www.eclipse.org/osee/xviewer/org_eclipse_nebula_widgets_xviewer.zip)

[Download Instructions](http://www.eclipse.org/osee/xviewer/README.txt)

The purpose of the XViewer is to give the application developer a more
advanced and dynamic TreeViewer that has the filtering and sorting the
capabilities of a spreadsheet while providing the users the ability to
customize their table to suit their current needs and save/restore these
customizations for future use by individual or group.

The XViewer has been incorporated into the Nebula project.

![image:xviewer_main.png](/docs/images/xviewer_main.png "image:xviewer_main.png")

### Table Customizations

![image:xviewer_customize.png](/docs/images/xviewer_customize.png
"image:xviewer_customize.png")

  - Provides **Table Customization** dialog to allow selection of
    visible columns, as well as the specification of their widths and
    names
  - **Easily reorder columns** through drag/drop or table customization
  - Provides mechanism to **save and load table customizations** so
    users can easily switch between customizations
  - Provides mechanism to '''mark table customizations as individual or
    global '''to provide sharing of customizations by users and teams
    (needs to be backed with shared file system or database)
  - Provides mechanism to **mark table customization as default
    customization** to display whenever this table is shown
  - Provides ability for **multiple XViewer tables to be used within the
    same application**, but provide their own columns and customizations

### Sorting and Filtering

![image:xviewer_sortfilter.png](/docs/images/xviewer_sortfilter.png
"image:xviewer_sortfilter.png")

#### Sorting

  - Sort individual columns (forward or reverse) using **data-specific
    configured sorter** for that columns data type
  - Perform unlimited **multi-column sorting** by holding Ctrl key down
    and selecting other columns. Re-selecting a column while Ctrl is
    held down will reverse the sort for that column

#### Filtering

  - **Quick filter (bottom left)** provides for filtering of all visible
    data by entered keywords
  - Alt-left click on column header (or right-click menu) allows for
    **filtering by column**. As many column filters can be added and
    work together.
  - Provides **simple metrics (bottom status label)** to show number of
    objects loaded, number shown and number selected.
  - **Status label shows filters and sorters** that are currently
    applied to viewer (bottom status label)

### Other Capabilities

  - Provides ability to **copy rows, columns or individual cells into
    buffer** for pasting into other applications
  - Allows for **multi-column editing of selected rows/columns**
  - Provides **html rendering** of currently visible table data
  - Provides **comma separated value export** to csv file that can be
    opened in any spreadsheet application
  - All the normal capabilities of the existing SWT Tree/TreeViewer have
    been retained as XViewer is an extension to TreeViewer

### Future

  - **Submitted to Nebula** as alternative to existing SWT TreeViewer
    (already part of eclipse.org/osee project)
  - Allow for **advanced column filtering** by complex expression and
    "canned list" of already entered items
  - Add **regular expressions** to all filtering capabilities
  - Provide easy ability to **sum selected rows for selected column**
  - Provide simple **summing/counting of rows/column** data
  - Provide **formulas and functions** for advanced summing/counting of
    data
  - Provide **graphing and charting** of data
  - Collaborate with other table/tree solutions to integrate
    functionality...

### HTML Report and CSV Export

![image:xviewer_reportexport.png](/docs/images/xviewer_reportexport.png
"image:xviewer_reportexport.png")

