# Frequently Asked Questions

## How do I open an ATS Task, Workflow or Review from the ID?

Open OSEE, switch to ATS perspective, select ID button in ATS Navigator,
type or copy/paste the 5 character ID into this window. This will show
the workflow in question.

## How do I add Actionable Items or Team Workflows to an Action?

The Action tracking system has the designed concept of keeping all work
being performed by multiple teams under a single "Action" object. As the
user learns more about a problem/enhancement, they can add additional
actionable items, and thus team workflows, by selecting the "Actionable
Items" hyperlink at the top of the editor and selecting/deselecting
items. New Team Workflows will be automatically created by ATS for AIs
not covered by existing Team Workflows.

For instance, suppose a Requirements workflow is being worked and the
requirements developer realizes that the change impacts tools and code
and test. They can select these actionable items and new Team Workflows
will be created and routed to the appropriate teams and team leads as
configured.

## How do I edit the fields of a state in a Team Workflow, Task or Review?

You must be an assignee and on the current state to edit the fields.

## How do I become an assignee for a Team Workflow, Task or Review?

In order to become the assignee of a state:

1.  Any current assignee can add new assignees
2.  If current assignee is "UnAssigned", you can add yourself as an
    assignee, save the workflow and then edit as normal. Note: You must
    save the workflow before being allowed to edit.
3.  Select the [Privileged
    Edit](http://wiki.eclipse.org/OSEE/ATS/FAQ#Priviledged_Edit) button
    and either assign yourself or find a user that has privilege to add
    you

## What is a Privileged Edit?

The "Privileged Edit" capability in ATS allows for certain users to be
assigned authority to edit any field in the workflow (Team, Task or
Review). This includes adding and removing assignees. Selecting the
"Privileged Edit" icon in the Workflow Editor will either show a list of
users assigned this capability or allow the current user to enter this
mode and make changes.

## What is the basic workflow of an Action in ATS?

  - **(End User) Generate Action for Enhancement/Issue**
    Select bug icon in top right corner of OTE window corresponding to
    Action OR select "A" icon in top right window of ATS Navigator or
    ATS World. If bug icon, enter title for Action and press OK; If "A"
    icon, Select OSEE Product, Configuration, version the problem
    was found against and Select OTE_SW, OTE_HW or both depending on
    whether the Action is against the Software or Hardware portions of
    OTE.
  - **(End User) Identify State**
    Edit Action to include required/optional fields and select
    "Complete"
  - **(Endorser \[configured\]) - Endorse State**
    Validate information provided in Identify State; Edit Action and to
    provide more information; Select impacted Product/Aspects
  - **(Product Leads/Dev Engineers) - Analyze State**
    For each product impacted, analyze Action and assign Aspects
    impacted and record estimated hours to complete
  - **(Management/Product Leads) - Authorize State**
    Approve Action for implementation
  - **(Product Leads/Dev Engineers) - Implementation State**
    For each product impacted, assign engineers to implement changes to
    Aspects impacted and continually record percent complete and hours
    spent until completion
  - **(End User) - Validation State**
    Validate that the resolution meets the users needs
  - **Completed State**
    Changes committed to repository and ready for release

## How do I create a Working Branch?

Team Workflows can be configured to allow changes to Artifacts (like
requirements) to be configuration managed. States that allow this will
show the "Working Branch" widget which allows for branches to be
created.

## How do I create Tasks for a Team Workflow?

There are a few ways to easily create tasks for a Team Workflow.

1.  From the "Task" tab off the Team Workflow Editor, select the "New
    Task"
2.  Right click on "Task" tab table off the Team Workflow Editor, select
    "New Task"
3.  Import tasks from Simple List
    1.  Select pulldown from toolbar off the Team Workflow Editor,
        select "Import Tasks via Simple List"
    2.  Enter one task title per line.
    3.  Select an assignee for the new Task(s), if desired.
4.  Import tasks from Spreadsheet
    1.  [Download sample excel
        spreadsheet](http://www.eclipse.org/osee/documentation/ats/support/ATS%20Task%20Import.xml)
    2.  Edit spreadsheet, providing one row per task to be created
    3.  Ensure that Originator and Assignee cells have name of existing
        user in your OSEE database
    4.  Select pulldown from toolbar off the Team Workflow Editor,
        select "Import Tasks via Spreadsheet".

## How do I configure ATS for multi-branch committing?

ATS can be configured to support committing artifact changes to multiple
variant branches. This is done through configuring ATS to use Versions
and configuring each version to the branch that it manages.

## How do I configure teams to share the same versions?

  - Configure a parent Team Definition as the "Team Uses Versions" by
    setting that attribute to "yes" and relate the versions to this Team
    Defintion
  - Create any number of children Team Definitions and relating them to
    the parent by the "Default Hierarchical" relation. You will also
    need to set their "Team Uses Versions" to true

These children will then use the parent's version artifacts as the valid
set.

## How do I save ATS Searches?

![image:atssearchsavetoolbar.png](/docs/images/atssearchsavetoolbar.png "image:atssearchsavetoolbar.png")

  - Open an ATS Search, Team Workflow Search, Task Search or any other
    search
  - Select search options
  - Select the Save button on the toolbar
  - Search will show in "Saved Searches" in the ATS Navigator
  - Other Options
      - Save will allow the search to be saved or re-saved
      - Save As will allow a saved search to be copied to another name
      - Open Save Searches
      - Delete this Saved Search
      - Clear search options

## Where did Actionable Item search go?

As of 0.24.0 release, the ATS Searching was consolidated into a single
search that provides saving. This also enabled all searches to have the
same fields as others.

Actionable Item search was integrated into the main ATS Search and also
the Team Workflow Search.

## How do I search by ID?

Each ATS Work Item is given a unique id. These ids can be configured by
team to have different prefixes. This ATS Id is the easiest way to find
and open work item in the Workflow Editor. The ID can be seen at the top
of the Workflow Editor or in the ATS Id column in the World Editor.

![workflow editor](/docs/images/workfloweditoratsid.png "workflow editor") ![World
Editor](WorldEditorAtsId.PNG "World Editor")

