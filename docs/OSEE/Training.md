# OSEE Application - How do I...

## open, close and customize perspectives

<http://www.tutorialspoint.com/eclipse/eclipse_perspectives.htm>

## reset perspectives

If you close/lose views and editors, you can right-click on the
perspective icon in tool bar or use the Window menu \> Perspective \>
Reset Perspective. This will reset the perspective back to the default.

## maximize / minimize editors and views

To easily see an editor or view in full screen mode, you can
double-click the tab for that editor/view to maximize and again to
restore to normal size.

## share views and editors on multiple monitors

You can drag views and editors off the workbench by grabbing the tab of
that editor/view and dragging off the workbench to the desktop or
another monitor. You can the drag back on to restore into workbench. In
addition, you can duplicate your current workbench by choosing Window
pulldown \> New Window. This creates a duplicate workbench that can be
used independently of the existing one. Selecting the Windows X will
close that duplicate workbench.

## learn more about Workbench operation

We highly recommend going through the Workbench User Guide in Help menu
\> Help Contents. This will give a overview of how to use the workbench
to it's full potential. You can also view the Help Contents on the web
and go through the Eclipse Platform User Guide at
<https://help.eclipse.org/latest/index.jsp?nav=%2F0>.

## navigate the OSEE specific perspectives

OSEE has a number of perspectives based on the included applications.
All perspectives can be accessed through Window pulldown \> Open
Perspective. To easily navigate through OSEE developed perspectives,
select the OSEE pulldown and select your desired perspective.

## use eclipse shortcuts

<http://www.tutorialspoint.com/eclipse/eclipse_shortcuts.htm>

## activate the menu bar from keyboard

Press F10 to activate the Menu bar

## make a view or editor active

Press Ctrl+F7 to see a list of open views and editor area and switch to
one of them.

## change OSEE icon in toolbar based on which workspace is open

Once you have gotten used to running one OSEE, you'll realize that you
can run 2 or more. Or, if your a developer of OSEE applications, then
you could have one for development, another for run-time testing and
another for production use. It can get confusing to figure out which to
switch to in a windowing environment.

OSEE provides the ability to override the icon shown in the Windows
toolbar.

OSEE \> ATS or Define Perspective \> Enter "icon" in the navigator \>
Select "Set Workbench Override Icon". \> This opens an editor where you
can select a 32x32 image to use as the application icon. \> Select Run.

You'll now see that icon in the top left of your workbench and in the
windowing toolbar.

Here are a few that you can use

![osee_32.png](/docs/images/osee_32.png "osee_32.png")
![osee_32_dev.gif](/docs/images/osee_32_dev.gif "osee_32_dev.gif")
![osee_32_dev_run.gif](/docs/images/osee_32_dev_run.gif "osee_32_dev_run.gif")
![osee_32_rc.gif](/docs/images/osee_32_rc.gif "osee_32_rc.gif")
![osee_32_rc_run.gif](/docs/images/osee_32_rc_run.gif "osee_32_rc_run.gif")
![osee_32_rel.gif](/docs/images/osee_32_rel.gif "osee_32_rel.gif")
![osee_32_rel_run.gif](/docs/images/osee_32_rel_run.gif "osee_32_rel_run.gif")
![osee_32_src.gif](/docs/images/osee_32_src.gif "osee_32_src.gif")
![osee_32_src_run.gif](/docs/images/osee_32_src_run.gif "osee_32_src_run.gif")

## learn about the OSEE Architecture

[Osee Architecture](https://wiki.eclipse.org/OSEE/Architecture)

# Branch Manager - How do I...

## show artifact / action related to selected branch

Branch Manager \> Select Branch \> Open Associated Artifact \> This will
either open the Action or show the User that created the branch.

## quickly open an Artifact Explorer from selected branch

Branch Manager \> Double-click branch \> This opens an Artifact Explorer
to the branch selected

## show baseline branches first

When OSEE starts collecting many Baseline and Working branches, it may
be nice to see the Baseline branches first before all the Working
branches. By creating a Table Customization from the branch manager,
this view and any branch selection dialog will sort/filter based on your
saved and defaulted selection.

Sort and filter Branch Manager as you wish \> Select Table Customization
\> Select Save Customization \> Enter in customization name eg "Baseline
First" \> Select saved customization on left \> select "Set as Default"
\> Select Ok in dialog.

![branchmanager_baselinebranchfirst.png](/docs/images/branchmanager_baselinebranchfirst.png "branchmanager_baselinebranchfirst.png")

# Artifact Explorer - How do I...

## show more information, like attribute values, on each artifact?

You can show artifact types, versions and even valid attributes next to
the name of the artifacts on a branch in Artifact Explorer. Simply use
the white pulldown to select items you want to show. If you want to save
these selections for future Artifact Explorers, just select "Store Label
Settings".

![artifactexplorer_showmoreinfo.png](/docs/images/artifactexplorer_showmoreinfo.png "artifactexplorer_showmoreinfo.png")

## quickly open a new Artifact Explorer?

You can open a new Artifact Explorer using the "Open Artifact Explorer"
button on the top right of the Artifact Explorer. It will open the new
Explorer to the same branch as the one open.

![artifactexplorer_opennewexplorer.png](/docs/images/artifactexplorer_opennewexplorer.png "artifactexplorer_opennewexplorer.png")

## open the Action associated to the selected branch?

For branches that were created from an ATS Action, the "Open Associated
Action" button will open those Actions in ATS. If a branch was created
manually by a user, it will show a dialog of the user that created the
branch.

![artifactexplorer_openassociatedaction.png](/docs/images/artifactexplorer_openassociatedaction.png "artifactexplorer_openassociatedaction.png")

## open the selected artifact in another branch?

There is an easy way to open the selected artifact on another branch.
Select that artifact \> right-click \> Reveal on Another Branch. This
will open another Branch Manager and reveal the artifact on that branch.

![artifactexplorer_revealonanotherbranch.png](/docs/images/artifactexplorer_revealonanotherbranch.png "artifactexplorer_revealonanotherbranch.png")

## quickly search the currently selected branch?

Select the "Open Quick Search View" button. This will open the Quick
Search view, set the selected branch to the same as the Artifact
Explorer and place your cursor in the Search text box.

![artifactexplorer_quick_search.png](/docs/images/artifactexplorer_quick_search.png "artifactexplorer_quick_search.png")

## show other relations

OSEE creates a web of artifacts and relations. The Artifact Explorer
allows the user to easily navigate the Default Hierarchical relations.
At times, it is nice to be able to easily navigate other relations. You
can do this anytime by selecting the white triangle pulldown on the
Artifact Explorer \> select "Show Relations". You will now see all
relations that the artifact has.

![image:artifactexplorer_show_relations_menu2016.png](/docs/images/artifactexplorer_show_relations_menu2016.png "image:artifactexplorer_show_relations_menu2016.png")

![image:artifactexplorer_show_relations2016.png](/docs/images/artifactexplorer_show_relations2016.png "image:artifactexplorer_show_relations2016.png")

## create other related artifacts

You can easily create new related artifacts from the Artifact Explorer.
Select the white triangle pulldown \> select "Show Relations" \> Select
the artifact to add a related artifact to \> right-click \> select New
Related. This will pop-up a dialog showing valid relations for this
artifact. Follow the prompts to add a newly related artifact.

![image:artifactexplorer_new_related_fromartifact2016.png](/docs/images/artifactexplorer_new_related_fromartifact2016.png "image:artifactexplorer_new_related_fromartifact2016.png")

If you want to add a new artifact to an existing relation. Select the
relation to add the artifact to \> right-click \> select New Related.
This will pop-up a dialog showing valid relations for this artifact.
Follow the prompts to add a newly related artifact.

![image:artifactexplorer_new_related_fromrelation2016.png](/docs/images/artifactexplorer_new_related_fromrelation2016.png "image:artifactexplorer_new_related_fromrelation2016.png")

# Change Report - How do I...

## see the last modified date and author in Change Report

Open Change Report via Branch Manager or ATS \> Click the Customize icon
in the top right \> Search on "Last" in the 'Hidden Columns' list \>
Select 'Last Modified Date' and 'Last Modified by' and move to 'Visible
Columns' \> Select Ok

This will show the user that last modified the artifact and the time
they did it. Refreshing the Change Report will refresh these values.

# ATS - How do I Author Configuration Managed Changes

## ATS - How do I Author Configuration Managed Changes in GitLab Markdown (Preferred Option)

This section gives 2 videos on using the OSEE to author requirement
changes using GitLab Markdown. The first is an explanation of our demo
database that we use for these videos and demonstrations but can be
skipped. The second gives a 6 minute walkthrough of using OSEE GitLab
Markdown for requirements and other engineering artifact CM.

1.  [Introduction to OSEE Demo
    Database](https://www.youtube.com/watch?v=hN5NiYcw0XI#)
2.  [Authoring changes from Creating Action, Editing, Previewing and
    Committing](https://www.youtube.com/watch?v=b5sbRT2kUF4)

## ATS - How do I Author Configuration Managed Changes in Word

This section gives step by step videos on using the OSEE to author
requirement changes. If you watch the videos in the following order, you
will get a good overview of how to use OSEE to configuration manage
requirements or other artifacts.

1.  [Introduction to OSEE Demo
    Database](https://www.youtube.com/watch?v=hN5NiYcw0XI#)
2.  [Create a new Action](https://www.youtube.com/watch?v=nPZqgyqPI4k#)
3.  [Set the Targeted
    Version](https://www.youtube.com/watch?v=fa4gENXzgG4#)
4.  [Create a new Working
    Branch](https://www.youtube.com/watch?v=xG2K87iCnk8#)
5.  [Making Requirement
    Changes](https://www.youtube.com/watch?v=EuLgUR4Mdwc#)
6.  [Show Changes on Branch (Change
    Report)](https://www.youtube.com/watch?v=CWllitFgz1E#)
7.  [Commit a Working
    Branch](https://www.youtube.com/watch?v=M300NbOK6cs#)
8.  [Complete my Action](https://www.youtube.com/watch?v=OAW4PR_T_TI#)

# How do I open the Artifact Explorer / Branch

The Artifact Explorer allows a user to explore, read, edit and perform
other operations on artifacts, like requirements, on a branch. This
video shows a number of different ways to open this view.

[Watch Now](https://www.youtube.com/watch?v=jBRNnpJes-w#)

# ATS - How do I delete a working branch

If a set of changes are not needed, you can delete your working branch
in order to cancel the work or create another working branch.

[Watch Now](https://youtu.be/gq_TFxERkyE#)

# ATS - How to See Assigned Work Through My World

OSEE Actions (Problem Change Requests) can be assigned to one or more
users to work. In addition, multiple teams can track their work in OSEE
at the same time. Even things like Processes or even Facilities requests
can be tracked. My World gives the easy ability to view all the items
your assigned to in one place.

[Watch Now](https://www.youtube.com/watch?v=E4T8fCfxltM#)

# ATS - How do I...

## understand the difference between an action and workflow

ATS was designed to be 100+ PCRs systems in one. You can write an action
against one thing, and then add new workflows as you learn all the teams
and products that are impacted for that action.

The "Action" is the top object that groups all the "Team Workflows
(workflow)" that have work to be done.

A good example is a bug found in a software product. You can write an
action against the "code" and you will get the "action" object and the
"team workflow" object created. The "Team Workflow" is where the work is
done by the Code Team. Now, let's say the coder determines that the
problem isn't a code issue, but a requirements one. You can add a new
"Team Workflow" to the existing action against the "requirements". You
now have one "action" object and 2 "Team Workflows". You can then cancel
the Code workflow with a note that it's a requirements issue. Now, the
requirements are changing which requires a test fix. You can add a Test
"Team Workflow". And, you realize that some tool changes will be needed,
so you can add a Tool "Team Workflow". You now have 4 teams doing work
to fix the single "bug" that was found. Each team workflow is going
through it's own flow of work to complete the task.

[Read more about ATS
Objects](http://wiki.eclipse.org/OSEE/ATS/Users_Guide/Intro#Terms)

## configure to automatically add new Actions to a Goal

Goal artifacts are used as an ordered list of Actions, Reviews and
Tasks. If you want Actions written against an Actionable Item (AI) or
Team Definition to be automatically be added to your Goal: Open an
Artifact Explorer \> Select to Common Branch \> Open "Action Tracking
System" \> Expand down to find your AI or Team Definition \> Open your
Goal artifact in the ATS Editor \> Select the "Workflow" tab \> Expand
the "Relations" section \> Drag your AI or Team Definition to the
"AutoAddActionToGoal" relation. \> Save

![ats_auto_add_action_to_goal.png](/docs/images/ats_auto_add_action_to_goal.png "ats_auto_add_action_to_goal.png")

## visit recently opened ATS Actions

Write or open an Action, Review or other ATS object and can't find it?
Select "My Recently Visited" in the ATS Navigator to view all ATS
objects viewed in the current OSEE session. This list gets cleared after
restart.

# ATS Actions, Team Workflows, Tasks, Reviews - How do I...

## [**change an Action or Workflow title**](/docs/OSEE/HowTo/ChangeActionOrWorkflowTitle.md "wikilink")

## import Actions by spreadsheet

Actions can be created in bulk by importing from a spreadsheet. Select
"Import Actions Via Spreadsheet" in the ATS Navigator \> push the "Open
Excel Import Example Spreadsheet" button \> Save this sheet off \> Using
the examples, add appropriate items to the existing columns and add new
rows as desired \> In OSEE, push the "Select File" button \> select your
spreadsheet \> Select "Email POCs" if emails to assignees is desired \>
Select Goal to add to, if desired \> Select Run

## import Tasks by Simple List

Open Workflow to add tasks to \> Select "Tasks" tab \> Select gear icon
in upper right \> Select "Import Tasks from Simple list" \> Enter titles
of tasks, one per line \> Select Workflow state if tasks must be
completed in a specific state \> Select Assignees, if desired \> Select
Run

## add new Team Workflows to an existing Action

Actions are the top level ATS object that groups all Team Workflows that
are need to be completed. As more information becomes available to the
user, more Team Workflows may need to be created.

Open any Team Workflow in the Workflow Editor \> Select "Actionable
Items" hyperlink \> using the right window, select any number of
Actionable Items \> Select Run

New workflows will be created in the same Action.

## duplicate currently opened workflow

And advanced feature exists in ATS that will duplicate an existing
workflow in the same action. This is good when the work being done by a
team needs to tracked in separate Team Workflows.

Open Team Workflow to duplicate in Workflow Editor \> Open "Operations"
section at bottom of editor \> Select "Duplicate Workflows" \> Select
approprate options depending on how you wish the new workflow to be
created \> Enter title for new workflow \> Select Run

# ATS Searching - How do I...

## search ATS for Workflows, Tasks, Reviews and Goals

There are a number of ways to search ATS for what your looking for.
Searching by Id includes ATS Id, GUID (system generated unique id) or
Legacy PCR Id. Using specific searches in Navigator gives more detailed
search options.

![ats_searching.png](/docs/images/ats_searching.png "ats_searching.png")

## quickly search open ATS actions

The ATS Navigator has a quick search field to quickly find open actions.
This field will not only work for text, but also ATS ids (eg ATS10001)
and system ids, like guids (A61fqvlZOGa83OyWbCQA) or artifact ids
(12453).

The IC checkbox stands for "Include Completed/Cancelled" (cursor over to
see tooltip notation). This will include completed and cancelled actions
in the search.

Remember, as with any search you want to try to limit the result set
that comes back.

![ats_quicksearch.png](/docs/images/ats_quicksearch.png "ats_quicksearch.png")

## find Actions that I originated

The ATS User Search provides more searching criteria that would allow
you to easily find an Action that was written by a user. Once common
case is finding an Action that you originated.

OSEE \> ATS Perspective \> ATS Navigator \> Select "User Search" \>
Select User \> Select "Originator" checkbox and un-select "Assigned" \>
Check objects you want to search for, Workflows, Reviews or Tasks \>
Select Team that Action was written against, if desired \> Select Search

As with any search, the more fined grained the criteria, the faster it
will return and the closer to the answer you'll get.

You can also widen your search and use the advanced filtering, searching
that ATS World provides to narrow in on what you're looking for.

![ats_user_search.png](/docs/images/ats_user_search.png "ats_user_search.png")

## find Actions targeted for a version

There is an easy way to show all Actions that were targeted for a
version associated with a team.

OSEE \> ATS Perspective \> ATS Navigator \> Expand "Versions" \> Select
"Workflows Targeted-For Version \> Select your Team \> Select your
version.

This will open an ATS World to show all the Workflows in that version.

# ATS Navigator - How do I...

## show open ATS objects you are assigned to

Select the world icon or select "My World" in the list to show all
workflows, tasks and reviews you are assigned to.

## show ATS objects that you have marked as your favorites

In the ATS Editor, you can select the yellow star in the menu bar and in
ATS World, you can select items \> right-click \> Add to Favorites. This
will mark these items as your favorites.

In the ATS Navigator, you can select the yellow star in the toolbar or
"My Favorites" in the list. This will open an ATS World view of all
items you have marked.

## show a Change Report managed by an ATS workflow

If you have created a branch or committed changes via ATS, you can
easily open a Change Report by selecting the "Open Change Report by ID"
\> enter in ATS Id. Change Report for that ATS Workflow will open.

You can also select the "Open Change Report(s) by ID(s)" in the list to
perform the same operation.

## open all ATS objects related to the entered ATS Id

Select the "ID" toolbar icon \> Enter in an ATS Id or a Legacy PCR Id or
a guid. All items matching that id will be opened in ATS World. You can
also open multiple items by entering in a comma delimited list of Ids.

# ATS Emailing - How do I...

## email team leads / members

ATS Team Definitions are configured with Team Leads, who automatically
get assigned an Action when it's created, and Team Members, who have
have extra configurations and permissions to operate on Team Workflows.

In addition, ATS has a feature that enables you to easily email Team
Members, Team Leads or both.

Open OSEE \> ATS Perspective \> ATS Navigator \> type "email" in filter
box \> You will see "Email Team Leads/Members", "Email Team Leads" and
"Email Team Members". Select one of these \> Select Team \> Select
"Include all children Team Definition Actions" if you wish \> select
"ok" \> OSEE will open your configured mailtool with the appropriate
email addresses based on your selection.

## email groups of users

OSEE has a User Group artifact type with a Users relation. Create this
artifact on the common branch and drag in User artifacts to this
relation. Then, from ATS, Select "Email User Groups" and select the
appropriate group. Osee will open your configured mail tool with the
related User's email addresses.

## email message to a set of workflow assignees or originator

Select "Email Message to Action(s) Assigness or Originator from the ATS
Navigator \> Drag in the Team Workflows, Tasks or Reviews into the "ATS
Workflows" box \> enter a subject \> select the recipient (Assignees or
Originator) \> Enter the body of the message \> Select Run.

The appropriate users will receive an email with your subject and
message and the list of workflows that you selected which the user is an
assignee or originator.

# ATS Action View - How do I...

## show a graphical representation of Action, Workflows, Tasks and Reviews

In the ATS Workflow Editor, select the "Action View" icon at the top of
the editor. This will open a graphical representation of the "Action",
"Team Workflows", "Reviews" and "Tasks"

![ats_actionviewicon.png](/docs/images/ats_actionviewicon.png "ats_actionviewicon.png") ![ats_actionview.png](/docs/images/ats_actionview.png "ats_actionview.png")

# ATS Workflow Editor - How do I...

## add a new impact/Team Workflow to an action

To add a new Team Workflow to an action, select the "Actionable Items"
hyperlink at the top of the ATS Editor. On the right pane, select the
other Actionable Items that are impacted. New Team Workflows will be
created as needed.

![ats_actionableitemshyperlink.png](/docs/images/ats_actionableitemshyperlink.png "ats_actionableitemshyperlink.png")

## show a hierarchical representation of Action, Workflows, Tasks and Reviews

In the ATS Editor, select the "Open in World Editor". This will open the
object starting at the "Action". Expand-all will show all items in the
hierarchy.

![ats_openworldbutton.png](/docs/images/ats_openworldbutton.png "ats_openworldbutton.png")![ats_showhierarchical.png](/docs/images/ats_showhierarchical.png "ats_showhierarchical.png")

# ATS World and Task Editors - How do I...

## filter out completed/cancelled items

Select the pulldown-menu next to the gear and select "Filter Out
Completed/Cancelled". You can also use the Ctrl-F hotkey.

![ats_filtercanceledcompletedassignee.png](/docs/images/ats_filtercanceledcompletedassignee.png "ats_filtercanceledcompletedassignee.png")

## only show items assigned to you

Select the pulldown-menu next to the gear and select "Filter My
Assignee". You can also use the Ctrl-G hotkey.

![ats_filterassignee.png](/docs/images/ats_filterassignee.png "ats_filterassignee.png")

## open selected ATS items in a new ATS World or Task View

You can easily open selected items in a World or Task View by selecting
the World or Task icons in the top right of the editor. You can also
right-click \> Open With \> and select the desired editor for the
selected items.

|                                                                                                                                   |                                                                                                                                |
| --------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------ |
| ![image:ats_openselectedinnewworldeditor.png](ats_openselectedinnewworldeditor.png "image:ats_openselectedinnewworldeditor.png") | ![image:ats_openselectedinnewtaskeditor.png](/docs/images/ats_openselectedinnewtaskeditor.png "image:ats_openselectedinnewtaskeditor.png") |
|                                                                                                                                   |                                                                                                                                |

# Review - How do I...

## show all review defects in readable format

The Defect table of a Peer Review only shows 5 lines by default which
makes reading the defects difficult. In addition, the Description and
Location columns are too narrow to see all the text. There are a few
things you can do to help view this data easier.

1.  Make any column wider. Select the line between columns in header \>
    drag the columns out to make it wider
2.  Make the table larger. Select the gray line under the table and drag
    down to show more rows.

![peer_make_table_larger.jpg](/docs/images/peer_make_table_larger.jpg "peer_make_table_larger.jpg")

1.  View the entire table with wrapped columns. Right-click anywhere in
    the table \> select "View Table Report". This shows in an HTML
    format that wraps all the columns.

![peer_show_defects.jpg](/docs/images/peer_show_defects.jpg "peer_show_defects.jpg")

# Java Editor - How do I...

## incrementally search a Java file

Open Java File \> Ctrl-F (search) \> Select "Incremental" checkbox \>
Enter text to search. This will highlight the first occurrence of what
you type as you type.

![javaeditor_incremental_search.jpg](/docs/images/javaeditor_incremental_search.jpg "javaeditor_incremental_search.jpg")

# XViewer - Advanced Tree/Table Widget - How do I

The [XViewer](http://www.eclipse.org/nebula/xviewer) is an advanced and
dynamic Tree/Table Viewer that has the filtering and sorting the
capabilities of a spreadsheet while providing the users the ability to
customize their table to suit their current needs and save/restore these
customizations for future use by individual or group.

OSEE has used this widget in many of it's views and editors including:
Branch Manager, Results Editor, ATS World Editor, ATS Task Editor,
Change Report Viewer, Commit Manager, Merge Manager etc...

There are many advanced features of the XViewer that will help organize
and manipulate your view of whatever you're looking at.

## email, export to csv and print the contents

Select any item inside the table \> right-click \> View Table Report.
This will open an HTML view of the data. The top left options allow for
emailing, printing and exporting to csv. The export to csv will write
the file to disk and open whatever application is configure to view
comma separated value files, like Excel or Open Office.

[center](/docs/image:XViewer_viewTableReport.PNG.md "wikilink")

# Editors - How do I...

## easily maximize and minimize the current editor or view

Within the current editor or view, select Ctrl-M to maximize. Select
again to minimize.

## switch to different open editor

Ctrl-Shift-E shows a dialog of all open editors with editor name and
file path, if applicable. Options to select editor to open, close all
editors and save selected editors.

## restore changes or deleted files from local history

OSEE stores versions of most text files as changes are made. You can
access these versions to restore these changes. This includes a file or
directory that was deleted. Select the file or folder in the Package
Explorer \> right-click \> Restore from local history.

![editor_restore_from_local_history.jpg](/docs/images/editor_restore_from_local_history.jpg "editor_restore_from_local_history.jpg")

## prevent multiple editors from cluttering up your workbench

OSEE will show the '\>\>' indicator after 4 to 8 files are opened. You
can configure your workbench to automatically close editors
automatically. Select Window pulldown menu \> Preferences \> General \>
Editors \> select "Close editors automatically" \> enter number of
editors open before auto-close.

![editor_close_editors_automatically.jpg](/docs/images/editor_close_editors_automatically.jpg "editor_close_editors_automatically.jpg")

# Quick Search- How do I...

An OSEE Quick Search will open a search results view to show the
results. A history of all searches is available by default. You can view
previous searches by selecting the search history pulldown in the top
right corner of the Search View.

## get multiple search results tabs

If you want to show multiple search results views, you can \> perform
the first search \> select the Pin icon in the top right \> perform the
second search. This will open a new results view.

# History - How do I...

OSEE stores every change made to every artifact. This is very usefull in
determining who did what and when. In addition, these "transactions" can
be purged, thus un-doing an operation that was made in error.

## view history of a single artifact

From any artifact \> right-click \> open-with \> Resource History. This
will display the [Resource History
View](/docs/OSEE/Users_Guide/Features.md#Resource_History_View "wikilink")

## view history on a branch

Another way to show the changes made in OSEE, is too look at the Branch
Transactions. This will show all the changes for that branch. There are
two main ways to do this. First is thorugh the [Branch Manager
View](/docs/OSEE/Users_Guide/Features.md#Branch_Manager_View "wikilink").

The other is to open the [Change Report
Editor](/docs/OSEE/Users_Guide/Features.md#Change_Report_Editor "wikilink"). On
the Transactions tab, you'll see all the transactions listed.

You can also easily open the Transaction tab of the Change Report Editor
by selecing a branch off the Branch Manager View \> right-click \>
Branch Transaction Report

![image:branchtransactionmenu2016.png](/docs/images/branchtransactionmenu2016.png "image:branchtransactionmenu2016.png")

![image:branchtransactioneditor2016.png](/docs/images/branchtransactioneditor2016.png "image:branchtransactioneditor2016.png")

# OSEE and Earned Value Managment (EVM)

OSEE has a built in Problem Change Request and Configuration Management
system called the [Action Tracking System
(ATS)](/docs/OSEE/ATS/Users_Guide/Intro.md "wikilink"). This tracking system has
some powerful features that supports [Value
Management](http://en.wikipedia.org/wiki/Earned_value_management%7CEarned)

## OSEE Earned Value User Stories

These are the intial requirements (user stories) for the inclusion of EV
Work Package management in OSEE.

  - As a <b>Product Owner</b>, I want to <b>select from a list of active
    Work Packages for my team</b> to assign to actions
  - As a <b>Product Owner / Manager</b>, I want to be able to <b>create
    Work Packages</b> with
      - Work Package Id, Work Package Name, Program
      - Work Package Type: LOE, Discrete
      - Activity Id, Activity Name
      - Start Date, End Date
      - Percent Complete
      - Active
  - As a <b>Action consumer</b>, I want to be able to <b>see what
    Activity Id and Activity Name to charge</b> to on each action
  - As a <b>Cost Account Manager (CAM)</b>, I want to enter a <b>single
    percent complete</b> for each Work Package
  - As a <b>CAM</b>, I want to be able to <b>search for Work Package by
    Id</b>
  - As a <b>CAM</b>, I want to <b>see all related Team Workflows, Tasks,
    Reviews and Goals</b> for each Work Package and their <b>percent
    complete</b>
  - As a <b>Manager</b>, I want to be able to <b>import and re-import
    Work Packages</b> from excel spreadsheet with valid attributes
  - As a <b>Manager</b>, I want to <b>see the Start and End</b> date for
    each Work Package
  - As a <b>Manager</b>, I want to <b>see if Work Package is Discrete or
    LOE</b>

## Definitions

**Work Package** = Active account that has calculated and reported
earned value.
**Planning Package** = Planned work, not yet active.
**Activity Id** = End user account to charge.

## Product Owner / Manager - Create Work Packages

1.  Open Artifact Explorer
2.  Select Common Branch
3.  Find Team Definition (SAW Code)
4.  Create new child
5.  Artifact Type: Work Package
6.  Name: Earned Value task name (Work Pkg 01..03)
7.  Open Team Definition (SAW Code)
8.  Drag new Work Package to Team Definition -\> Work Package relation
9.  Add and set Activity Id and Activity Name (ASDHFA43 and HUF 210 )
10. Set other desired attributes

## Product Owner / Manager - Create Work Packages

1.  Search your objects in ATS
2.  Add the "Activity Id" column
3.  Select one or more items
4.  Right-click \> Edit \> Edit Activity Id OR Alt-Left-Click in
    Activity Id column

Note: Activity Id can be set on Team Workflow, Task, Goal and Reviews

## Action Consumer - View Activity Ids

1.  Search your objects in ATS
2.  Add the “Activity Id” column

## Account Manager - Search for Work Packages

1.  Search your objects in ATS
2.  Add the "Activity Id" column
3.  Select one or more items
4.  Right-click \> Edit \> Edit Activity Id OR Alt-Left-Click in
    Activity Id column

Note: Activity Id can be set on Team Workflow, Task, Goal and Reviews

## Account Manager - View all related items

1.  Select "Earned Value Work Package Report"
2.  Drag in Work Packages \> Run
3.  Report displayed

## Account Manager - Set Percent Complete

1.  Edit all available attributes of your Work Package through Artifact
    Editor

# OSEE and Agile / SCRUM - How do I...

OSEE has a built in Problem Change Request and Configuration Management
system called the [Action Tracking System
(ATS)](/docs/OSEE/ATS/Users_Guide/Intro.md "wikilink"). This tracking system has
some powerful features that supports [Agile Software
Development](http://en.wikipedia.org/wiki/Agile_software_development)
and [SCRUM Software
Development](http://en.wikipedia.org/wiki/Scrum_\(software_development\))

## use Goals to track backlog

The "Action" object in ATS exists to relate all the Team Workflows,
Reviews and Tasks that are necessary to complete a single "action" such
as "Make these changes to that widget". The "Goal" object, in contrast,
is used to group any ATS objects for any other purpose, such as an Agile
backlog.

Goals can contain any Team Workflows, Reviews, Tasks and even other
Goals. In addition ATS objects can belong to multiple Goals.

Items can be added to goals by one of the following methods

  - Opening Goal and draging items into the members tab
  - Adding the "Goal" column to any world view and selecting the Goal to
    add to
      - Alt-Left-Click the Goal cell
      - Selecting items \> right-click \> Edit \> Goal
  - Configuring a Team Definition or Actionable Item to auto-add created
    Team Workflows to any goals.
    \[OSEE/TipsTricks\#configure_to_automatically_add_new_Actions_to_a_Goal|More
    Information\]\]

Goals are ordered which supports the need for a ordered backlog.

Items in the Goal members tab can be re-ordered by one of the following
methods

  - Selecting item(s) and drag them to a new location
  - Selecting item(s) \> right-click \> Set Goal Order \> Select new
    location
  - Alt-left-click the Goal Order column and entering new location

## use Points column to set points

Add the "Points" column and select points by

  - Selecting items \> right-click \> Edit \> Points
  - Alt-left-click Points column cell

## sum entered Points for a Sprint or selected items

It is easy to sum the points column for the selected items when using
the [Points
Column](/docs/OSEE/TipsTricks.md#use_Points_column_to_set_points "wikilink"). Add
the "Points" column \> Select items to sum \> Right-click the "Points"
column header \> Select "Sum Selected for Column" \> a dialog will popup
with the sum of points for the selected items.

## use Targeted Version, Groups, Notes or Category to track Sprints

There are a number of different ways to track Sprints in OSEE. Targeted
Versions are normally used to track builds and releases, but could also
be used to track Sprints. If Targeted Versions are already used for
builds or releases, other text-based columns can be used. Among these
are the "Notes" column or any of the 3 open "Category" columns.

## use Goal Members tab to support daily stand-ups/scrum meetings

A common characteristic in Agile is a daily stand-up or scrum meeting.
These meetings are commonly held using a post-it-note task board.
Another option is by showing the backlog Goal with current spring items
at the top or by having another Goal containing only the current sprint
items. The team members can call out the current item order number they
finished and which they will work on next.

## automatically add new Team Workflows to a backlog Goal

Since an Agile backlog is supposed to contain all items, ATS has a
configuration that will automatically add new Team Workflows to any
number of Goals. [How to
configure](/docs/OSEE/TipsTricks.md#configure_to_automatically_add_new_Actions_to_a_Goal "wikilink")

## use import features to automatically create actions, workflows and tasks for backlog

OSEE has a number of existing features to easily create ATS objects for
use in backlogs.

Among these are:

1.  [Importing Actions via
    spreadsheet](/docs/OSEE/TipsTricks.md#import_Actions_by_spreadsheet "wikilink")
2.  [Importing tasks from simple
    list](/docs/OSEE/TipsTricks.md#import_Tasks_by_Simple_List "wikilink")
3.  [Add Team Workflow to existing
    Action](/docs/OSEE/TipsTricks.md#add_new_Team_Workflows_to_an_existing_Action "wikilink")
4.  [Duplicate existing Team Workflow
    (advanced)](/docs/OSEE/TipsTricks.md#duplicate_currently_opened_workflow "wikilink")

## use OSEE to support Agile/Scrum in the future

As OSEE is used more an more for Agile/Scrum, new features will be
incorporated.

Some ideas for improvements are:

1.  Automated generation of Sprint Burndown charts
2.  Web interface of backlog / sprint goals to support daily stand-ups
3.  Web view of "Scrum Task Board"
4.  Workflow Definitions tailored to Scrum states such as "to-do",
    "in-work", "testing", "done"
5.  Easier ways to import items into OSEE/backlog
6.  Automatic generation of items performed frequently by teams, some
    ideas are
    1.  Configure by Team or Actionable item to automatically create a
        set of tasks on Team Workflow creation
    2.  Configure a set of Workflows, Tasks an Reviews to be created for
        each Sprint
    3.  Configure a selectable list of items to create anytime they
        choose

NOTE: These are ideas only and are not yet scheduled for implementation.
As always, contributions to OSEE in this or any area are welcome.

## [**sort and filter multi-column (tabular) views**](/docs/OSEE/HowTo/Tables.md "wikilink")

## [**see all I want without having to keep changing perspective**](/docs/OSEE/HowTo/Perspectives.md "wikilink")

## deal with the data

1.  [**Undo a change**](/docs/OSEE/HowTo/Undo.md "wikilink")
2.  [**Change an attribute value in lots of artifacts at
    once**](/docs/OSEE/HowTo/MassEdit.md "wikilink")
3.  [**Commit changes**](/docs/OSEE/HowTo/Commit.md "wikilink")
4.  [**Import Artifacts**](/docs/OSEE/HowTo/ImportArtifacts.md "wikilink")
5.  [**Moderate a review**](/docs/OSEE/HowTo/ModerateReview.md "wikilink")

# All OSEE Training Videos

There are a lot of features and capabilities available in the OSEE
application. We've created a number of short videos to show these. We
try to keep each video under two minutes and focus on a specific feature
or task. These videos can also be shared for training purposes or as an
answer to user support questions.

1.  OSEE for Configuration Management
    1.  [Introduction to OSEE Demo
        Database](https://www.youtube.com/watch?v=hN5NiYcw0XI#)
          - One of OSEE's main use case is to configuration manage
            requirements and other artifacts throughout the lifecycle of
            an Engineering project. These videos, in order, show the the
            process of making changes to requirement artifacts using
            ATS.
    2.  Editing Requirements and Engineering Artifacts in GitLab
        Markdown
        1.  [Full Edit Scenario; From Creating Action, Edit and Commit
            using Markdown](https://www.youtube.com/watch?v=b5sbRT2kUF4)
    3.  Editing Requirements and Engineering Artifacts in Word
        1.  [Create a new
            Action](https://www.youtube.com/watch?v=nPZqgyqPI4k#)
        2.  [Set the Targeted
            Version](https://www.youtube.com/watch?v=fa4gENXzgG4#)
        3.  [Create a new Working
            Branch](https://www.youtube.com/watch?v=xG2K87iCnk8#)
        4.  [Making Requirement
            Changes](https://www.youtube.com/watch?v=EuLgUR4Mdwc#)
        5.  [Show Changes on Branch (Change
            Report)](https://www.youtube.com/watch?v=CWllitFgz1E#)
        6.  [Commit a Working
            Branch](https://www.youtube.com/watch?v=M300NbOK6cs#)
        7.  [Complete my
            Action](https://www.youtube.com/watch?v=OAW4PR_T_TI#)
2.  Common Views
    1.  Artifact Explorer
        1.  [How to Open the Artifact
            Explorer](https://www.youtube.com/watch?v=jBRNnpJes-w#)
        2.  [Artifact Explorer Preview and Preview with
            children](https://www.youtube.com/watch?v=XdQDbX-SDZs#)
        3.  [Artifact Explorer show
            options](https://www.youtube.com/watch?v=d1MgWgqDNwU#)
        4.  [Adding a file to a branch in
            OSEE](https://www.youtube.com/watch?v=d8OcwsJXi9M#)
    2.  Branch Manager
        1.  [Branch
            Manager](https://www.youtube.com/watch?v=KrNHH9s7ihY#)
    3.  Workbench
        1.  [Customizing your
            Workbench](https://www.youtube.com/watch?v=F07aFvGd7N0#)
        2.  [OSEE Links](https://www.youtube.com/watch?v=ubJ-uvW0bCw#)
    4.  Quick Search / Search Results
        1.  [Quick Search](https://www.youtube.com/watch?v=_L3COIOma9Y#)
        2.  [Search Results and Multiple
            Views](https://www.youtube.com/watch?v=Uv8fYK2jAVo#)
    5.  Resource History
        1.  [Resource History Was-Is
            Comparison](https://www.youtube.com/watch?v=Ete4X1AlA9w#)
        2.  [Resource
            History](https://www.youtube.com/watch?v=r2FyQ1n_0j4#)
    6.  XViewer - Common Tree/Table Viewer in OSEE
        1.  [XViewer - View Table
            Report](https://www.youtube.com/watch?v=NuG6u3-0SYY#)
        2.  [XViewer](https://www.youtube.com/watch?v=Jz-TQbxCGeA#)
    7.  Action Tracking System
        1.  [How to See Assigned Work Through My
            World](https://www.youtube.com/watch?v=E4T8fCfxltM#)
        2.  [Create a new
            Action](https://www.youtube.com/watch?v=nPZqgyqPI4k#)
        3.  [Set the Targeted
            Version](https://www.youtube.com/watch?v=fa4gENXzgG4#)
        4.  [Create a new Working
            Branch](https://www.youtube.com/watch?v=xG2K87iCnk8#)
        5.  [Commit a Working
            Branch](https://www.youtube.com/watch?v=M300NbOK6cs#)
        6.  [Complete my
            Action](https://www.youtube.com/watch?v=OAW4PR_T_TI#)
        7.  [How to Delete a Working
            Branch](https://www.youtube.com/watch?v=gq_TFxERkyE#)