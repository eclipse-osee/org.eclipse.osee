# ATS Icons

## ATS Objects

![image:action.gif](/docs/images/action.gif "image:action.gif") denotes an Action,
the top level object in ATS

![image:workflow.gif](/docs/images/workflow.gif "image:workflow.gif") denotes a Team
Workflow; moves team through completion of change

![image:task.gif](/docs/images/task.gif "image:task.gif") denotes a Task; lightweight
workflow that is performed during Team Workflow states

![image:version.gif](/docs/images/version.gif "image:version.gif") denotes the ATS
targeted for release version

![image:r.gif](/docs/images/r.gif "image:r.gif") denotes the ATS review

## ATS Overlays

![image:warn.gif](/docs/images/warn.gif "image:warn.gif") warning that something
needs to be addressed; open object for more information

![image:oranger_8_8.gif](/docs/images/oranger_8_8.gif "image:oranger_8_8.gif")
released version

![image:yellown_8_8.gif](/docs/images/yellown_8_8.gif "image:yellown_8_8.gif") next
release version

![image:favorite.gif](/docs/images/favorite.gif "image:favorite.gif") ATS object
marked as user's favorite; select "My Favorites" to load all marked

![image:subscribed.gif](/docs/images/subscribed.gif "image:subscribed.gif") ATS
object user desires to receive email upon every state transition

![image:whitet_8_8.gif](/docs/images/whitet_8_8.gif "image:whitet_8_8.gif") ATS
object is obtaining estimated hours, percent complete, hours spent and
remaining hours from tasks

## ATS Operations

Select ![image:newaction.gif](/docs/images/newaction.gif "image:newaction.gif") to
create a new action

Select ![image:newtask.gif](/docs/images/newtask.gif "image:newtask.gif") to create a
new task

Select ![image:refresh.gif](/docs/images/refresh.gif "image:refresh.gif") to refresh
current view

Select ![image:print.gif](/docs/images/print.gif "image:print.gif") to print the ATS
Results

Select ![image:email.gif](/docs/images/email.gif "image:email.gif") to email the ATS
Results to an OSEE user

Select ![image:export_table.gif](/docs/images/export_table.gif "image:export_table.gif") to export the ATS Results to CSV file

Select ![image:bug.gif](/docs/images/bug.gif "image:bug.gif") to report a bug.

## ATS Configuration

![image:team.gif](/docs/images/team.gif "image:team.gif") denotes a team configured
to do work in ATS

![image:ai.gif](/docs/images/ai.gif "image:ai.gif") denotes an Actionable Item that a
user can create an Action against

# ATS Navigator

![image:atsnavigator2016.png](/docs/images/atsnavigator2016.png "image:atsnavigator2016.png")

The **ATS Navigator** is the central location for locating ATS objects.
Double clicking a navigation item will open it in the appropriate viewer
or editor. The **Filter** can be used to quickly locate ATS Navigator
objects containing the entered text. The **Search** box can be used to
search all ATS objects in the system. Checking the **IC** checkbox will
include completed and cancelled items in the search.

# ATS Action View

![image:ats_action_view.jpg](/docs/images/ats_action_view.jpg "image:ats_action_view.jpg")

The **ATS Action View** shows a graphical representation of the
currently open Action or Team Workflow. The Action View will show the
parent-child relationship between the Action and its children Team
workflows. The action currently open in the editor will have a cyan
outline.

Hovering over any object will reveal information about the current
state, its assignees, and work to be done.

# ATS World View

![image:ats_world_view.jpg](/docs/images/ats_world_view.jpg "image:ats_world_view.jpg")

## Purpose

Shows ATS workflow objects including Actions, Team Workflows, Tasks and
Reviews that were returned from a search normally performed by the ATS
Navigator.

## How to do it

Double-click search item from ATS Navigator. ATS World will show that it
is loading. Upon return, ATS World will contain all ATS objects from the
search. In addition, a plus is shown next to any object. Upon selection,
viewer will expand to show all children of the selected object.

## Open ATS Action, Team Workflow, Task or Review

Double-click to open any item open in ATS Workflow Editor.

## Sorting

Selecting column headers will sort that column. Holding down Ctrl and
selecting successive columns will enable multisort. Re-selecting a
column will reverse sort that column. The status label at the bottom
will show the columns being sorted and which direction the sort is being
performed. To remove all sorting, right-click and select "Remove All
Sorting".

## Bottom Status Line

The bottom status line will show the total number of objects loaded into
the table, the total shown and the total selected. It will also show all
the sorts and filters that are enabled.

## Top Status Line

The top status line will show the name of the search that populated the
ATS World View. A warning icon (![image:warn.gif](/docs/images/warn.gif "image:warn.gif")) will show if the search returned no objects to show.

## Filtering

Bottom right of the ATS World shows a filter box that is a quick way to
filter by one or two words. Simply type in a string, press enter and ATS
World will only show those loaded objects that where one of the visible
cells contains the typed text. This is a case insensitive search. Select
the clear action (![image:clear.gif](/docs/images/clear.gif "image:clear.gif")) to
clear out the text and restore all loaded actions.

## Actions

Select ![image:a.gif](/docs/images/a.gif "image:a.gif") to load ATS World with all
the actions that you have work to do on.

Select ![image:action.gif](/docs/images/action.gif "image:action.gif") to create a
new Action.

Select ![image:rank.gif](/docs/images/rank.gif "image:rank.gif") to rank actions by
deadline date then by priority and annual cost avoidance.

Select ![image:refresh.gif](/docs/images/refresh.gif "image:refresh.gif") to refresh
the current search.

Select ![image:customize.gif](/docs/images/customize.gif "image:customize.gif") to
Customize Table.

Select ![image:bug.gif](/docs/images/bug.gif "image:bug.gif") to Report a Bug.

# Result View

![image:result_view.jpg](/docs/images/result_view.jpg "image:result_view.jpg")

The **Report View** show reports, errors, metrics and other data in a
multi-paged view with print, email and exporting capabilities. This view
pops up automatically when OSEE needs to report larger amounts of data
to the user.

## Actions

Select ![image:bug.gif](/docs/images/bug.gif "image:bug.gif") to generate a bug
report against this view.

Select ![image:print.gif](/docs/images/print.gif "image:print.gif") to print the
current window.

Select ![image:email.gif](/docs/images/email.gif "image:email.gif") to email the
current results view to an OSEE user.

Select ![image:folder.gif](/docs/images/folder.gif "image:folder.gif") to import a
saved results report.

The two toolbar icons between the separators are used to export report
data, and vary from system to system. The icon on the left will export
the report data to a comma-separated value file (.csv), which on this
system is associated with Microsoft Excel. The icon on the right will
export to an HTML file, which on this system is associated with Firefox.

# ATS Workflow Editor

The ATS Workflow Editor is an editor for workflows configured for use in
ATS including Team Workflows, Tasks and Reviews. It can be opened by
double-clicking any Action or Team Workflow (e.g. from My World, Search
results, etc). The Workflow Editor includes the following tabs:
Workflow, Tasks, and Metrics.

## Workflow Tab

![image:ats_workflow_editor_workflow_tab.jpg](/docs/images/ats_workflow_editor_workflow_tab.jpg "image:ats_workflow_editor_workflow_tab.jpg")

The **Workflow Tab** is the default tab of the Workflow Editor. It shows
the states of the workflow and allows modification of information
associated with the current state, which is indicated in the top status
bar and is the only state open by default.

## Task Tab

![image:ats_workflow_editor_task_tab.jpg](/docs/images/ats_workflow_editor_task_tab.jpg "image:ats_workflow_editor_task_tab.jpg")

The **Task Tab** shows tasks associated with the current state.
Double-clicking a task will open it in the ATS Workflow Editor for
editing like any other workflow; however, the Task Tab view also allows
for quickly editing task information directly: simply hold down alt and
left-click the field to edit. An editor associated with the type of the
selected cell will pop up.

It is also possible to edit multiple tasks at once. Selecting the tasks
to be edited and right-clicking will display a context menu listing the
operations possible on the selected tasks.

### Actions

Select ![image:newtask.gif](/docs/images/newtask.gif "image:newtask.gif") to create a
new task.

Select ![image:redremove.gif](/docs/images/redremove.gif "image:redremove.gif") to
delete selected task.

Select ![image:customize.gif](/docs/images/customize.gif "image:customize.gif") to
Customize Table.

Select ![image:refresh.gif](/docs/images/refresh.gif "image:refresh.gif") to refresh
the current task list.

Select ![image:newaction.gif](/docs/images/newaction.gif "image:newaction.gif") to
create a new ATS action.

Select ![image:bug.gif](/docs/images/bug.gif "image:bug.gif") to create a bug report
against this view.

# Working Branch Widget

This widget allows creation and manipulation of the working branch
configured in a Team Workflow. The following options can be performed
using this widget.

![image:workingbranchwidget.gif](/docs/images/workingbranchwidget.gif "image:workingbranchwidget.gif")

1.  Working Branch to be created by selecting the "Create Working
    Branch" icon
2.  Working Branch artifacts to be edited by selecting the "Open in
    Artifact Explorer" icon
3.  Review of changes on Working Branch by selecting the "Open Change
    Report" icon
4.  Working Branch to be deleted by selecting the "Delete Working
    Branch" icon

# Commit Manager Widget

This widget allows commit for Working Branch configured in a Team
Workflow. The following options can be performed using this widget.

![image:commitmanagerwidget.gif](/docs/images/commitmanagerwidget.gif "image:commitmanagerwidget.gif")

Double-click to perform action listed. These include:

1.  Commit Branch allows working branch changes to be committed to the
    configured branch or branches
2.  Merge Conflicts and Commit allows changes that conflict to be merged
    and then committed
3.  Show Change Report shows the changes associated with the Working
    Branch and Merges

# ATS Workflow Configuration Editor

![image:configeditor.jpg](/docs/images/configeditor.jpg "image:configeditor.jpg")

## Purpose

Give a graphical method to creating / updating ATS Workflow
Configurations. This editor also gives easy access, through
double-click" to edit the "Work Page Definition" artifact that represent
the selected state.

## How to do it

  - Open editor for a specific workflow:
  - Existing Work Flow Definition Artifact: From Common branch in
    Artifact Explorer, expand Action Tracking System -\> Work Flows and
    double-click the workflow you wish to edit.
  - From ATS Workflow Editor: Select workflow icon at top right and
    associated workflow will be opened.
  - New Workflow Configuration: File -\> New -\> Other -\> OSEE ATS -\>
    Workflow Configuration; enter unique namespace for this workflow
    configuration. (eg: org.{company}.code). This creates a simple
    workflow to use as a starting place.
  - Create states:
      - Add necessary states to diagram to represent workflow. Note:
        Only one state is the entry point and the workflow must end at
        the "Completed" state. The "Cancelled" state is optional, but
        should be used in most cases.
  - Editing Rules, Widgets and other State attributes: Double-click on
    any state to open the Artifact in the Artifact Editor.
  - Create transitions:
      - For each state, a single "Default Transition" must be specified.
        This will be the default state specified as the "next" state in
        the workflow. Optional transitions to other states can be
        specified by the "Transition" arrow. The user will have the
        option of selecting one of these states instead.
      - The "Return Transition" can be specified for valid jumps "back"
        to previous states. (For example, the user may need to
        transition back to the "Analyze" state from the "Authorize"
        state if authorization failed and more analysis is needed.
  - Select a single state and set it's "Start Page" property to "Yes"
    for the state specified to start the workflow. Only one state can be
    the start state.
  - Save and test the new workflow configuration:
      - Select Save to persist the changes. Validation will be
        automatically run (see below)
      - Configure the Team Definition to use the new workflow
      - Create a new Action and test the created workflow

## Validation

Validation of a workflow is provided by selecting the check icon and
selecting a state, transition or the entire workflow (selecting the
white background). This will pop up whatever error occurs or a
"Validation Success" if all is ok. Note that this same validation will
occur during save and will fail if all problems are not resolved.

# Work Flow Definition Artifact

## Purpose

The Work Flow Definition artifact specifies the workflow that a team
moves through to complete an Action. This artifact specifies the states,
their transitions and the start state of the workflow. Creation/Editing
of this artifact can either be done through the Artifact Editor or using
the Workflow Configuration Editor (OSEE 0.6.0 Release and beyond).

## How to do it - Configuration Editor

## How to do it - Artifact Editor

On the Common branch in the Artifact Editor, Expand "Action Tracking
System" and right-click on "Work Flows", select New Child -\> Work Flow
Definition. Enter a unique namespace name for this workflow (eg:
org.{company}.code). In the Artifact Editor, add and set the attributes
and relations as below.

  - Attributes
      - Name:\[unique namespace, usually the same as the Work Id
        specified below\]
      - osee.wi.Work Id:\[unique workflow namespace\] - this will
        preceed each state name specified in the workflow.
      - osee.wi.Parent Work Id:\[Work Id of Parent Work Flow
        Definition\] - this will allow this workflow to inherit it's
        transitions and start state from another workflow. This can not
        be used with other attributes below
      - osee.wi.Start Page:\[namespace.state name\] - this specifies the
        name of the initial state in the workflow
      - osee.wi.Transition:\[\[from_state_name\];\[transition_type\];\[to_state_name|from
        state name\];\[transition type\];\[to state name\]\] - specifies
        the transition from state to state where
          - \[from state name\]/\[to state name\]- unique Work Page
            Definition Work Id comprised of \[namespace\].\[State Name\]
          - \[transition type\] - one of the following:
              - ToPageAsDefault - Transition is "Default Transition"
                state
              - ToPage - Transition is optional transition to state
              - ToPageAsReturn - Optional transition to a previously
                visited state
      - osee.wi.Description:Optional attribute to add description of
        workflow
  - Relations
      - Work Item.Parent: Relate to any Team Definition configured to
        use this workflow.

# Work Page Definition Artifact

## Purpose

The Work Page Definition artifact configures a single state of the
Workflow Configuration.

## How to do it - ATS Workflow Configuration Editor

The ATS Workflow Configuration Editor will allow the editing of the
three main fields of a Work Page Definition Artifact. The remaining
fields, and the relations to Work Rules and Work Widgets must be done
through the Artifact Editor.

## How to do it - Artifact Editor

Work Page Definitions are either stored as children of their Work Flow
Definition artifact or under the "Work Pages" folder.

On the Common branch in the Artifact Editor, Expand "Action Tracking
System" and right-click on "Work Page", select New Child -\> Work Page
Definition, or double-click an existing Work Page Definition artifact.
Edit the following attributes and relations accordingly.

  - Attributes
      - Name:\[unique name matching Work Id below\]
      - osee.wi.Work Id:\[unique workflow namespace\].\[Work Page Name\]
        as below.
      - osee.wi.Parent Work Id:\[Work Id of Parent Work Page
        Definition\] - this will allow this workflow to inherit it's
        widgets and rules from another workflow. This can not be used
        with other attributes below
      - osee.wi.Work Page Name:State Name that the user will see. This
        can not have any special characters including . in the name.
  - Relations
      - Work Item.Child: Relate to any Rules or Widgets that this state
        is made of.

# Work Rule Widget Artifact

## Purpose

The Work Widget Definition artifact specifies a single widget, via xml,
that will be displayed on the state page in the ATS Workflow Editor.

## How to do it - Artifact Editor

On the Common branch in the Artifact Editor, Expand "Action Tracking
System" expand "Work Widget" and double-click any existing Work Widget.
Edit the following attributes and relations accordingly.

  - Attributes
      - Name:\[unique name matching Work Id below\]
      - osee.wi.Work Id:\[unique id\]
      - osee.wi.Work Description:Simple description explaining what
        widget is.
      - osee.wi.Work Data: XWidget=\[XWidget xml specifying widget\]
  - Relations
      - Work Item.Parent: Relate to any Team Definition or Work Page
        Definition artifacts as appropriate.

# Work Rule Definition Artifact

## Purpose

The Work Rule Definition artifact specifies a single rule that can be
applied to workflow configurations or to Team Definition artifacts.
These rules are normally backed by Java code that performs certain tasks
like automatically creating new reviews, assigning workflows to specific
users or specifying states as allowing create/commit of branches.

## How to do it - Artifact Editor

On the Common branch in the Artifact Editor, Expand "Action Tracking
System" expand "Work Rule" and double-click any existing Work Rule. Edit
the following attributes and relations accordingly.

  - Attributes
      - Name:\[unique name matching Work Id below\]
      - osee.wi.Work Id:\[unique workflow namespace\].\[Work Page Name\]
        as below.
      - osee.wi.Work Description:Simple description explaining what rule
        does.
      - osee.wi.Work {Data:Key/Value} pares of information used by rule.
  - Relations
      - Work Item.Parent: Relate to any Team Definition or Work Page
        Definition artifacts as appropriate.

# Mass Artifact Editor

## Purpose

The Mass Artifact Editor allows the easy view of multiple artifact along
with their attributes. This editor can be viewed from right-click off
Artifact Explorer, Search Results page, or via ATS action. Single cells
can be edited via Alt-Left-Mouse-Click. After editing any number of
artifacts shown, the save button persists this data.

Note: This editor will close upon switch of default branch unless the
editor is tied to the common branch.

## Actions

Select ![image:refresh.gif](/docs/images/refresh.gif "image:refresh.gif") to refresh
the contents.

Select ![image:customize.gif](/docs/images/customize.gif "image:customize.gif") to
Customize Table.

Select ![image:bug.gif](/docs/images/bug.gif "image:bug.gif") to Report a Bug.

# Table Customization

## Purpose

Customize the table to show desired columns, widths with specified
sorting and filters. Enables loading of both personal and global
customizations and provides the ability to select a customization as the
default customization to be loaded upon startup.

## Select Customization

Lists current personal and global customizations to be selected from.
Double-click to automatically load selected customizationa and close
dialog.

  - "-- Table Default --" - Show the default customization for this
    table.
  - "-- Current --" - Show the current customization as set from table
    alterations.
  - "Other" - Shows a stored customization available for loading.

## Select Customization - Icons / Overlays

  - ![image:customize.gif](/docs/images/customize.gif "image:customize.gif")
    Customization.

<!-- end list -->

  - ![image:customized.gif](/docs/images/customized.gif "image:customized.gif")
    Default customization loaded up restart.

<!-- end list -->

  - ![image:customizeg.gif](/docs/images/customizeg.gif "image:customizeg.gif")
    Global customization available to all users.

## Select Customization - Buttons

  - Load - Loads the currently selected customization.
  - Load+Close - Loads the selected customization and closes the dialog.
  - Set as Default - Sets the currently selected customization as the
    default to load upon restart.
  - Delete - Deletes the currently selected customization.

## Configure Customization

Allows for the selected customization to be configured, loaded and
saved.

  - Hidden Columns - Shows the columns that are available to be displays
    but configured as hidden.
  - Visible Columns - Show the columns that are configured to be
    displayed for this customization.
  - (x) - shows the currently configured width of the column
  - Sorter - xml representation of the column order to sort by
  - Text Filter - shows the configured text filter to be applied for
    this customization. Enter as string.

## Configure Customization - Buttons

  - Load - Loads the configured customization.
  - Load+Close - Loads the configured customization and closes the
    dialog.
  - Rename - Allows the user to define an alternate name for the column.
  - Save - Saves the configured customization as personal or global (if
    permissions allow).

