# ATS Overview

The [Action Tracking System
(ATS)](http://www.eclipse.org/osee/documentation/overview/components.php#ats)
is a tightly integrated change tracking system that manages changes
throughout a product's lifecycle. ATS provides integrated change
management to all OSEE applications through user customizable workflows.

## Terms

  - **Actionable Item (AI)**
    Item that can be impacted by an Action. AIs are what the user has to
    select from when creating an Action. Examples: Flight Box, Lab
    Computer, Code Subsystem.
  - Team Workflow Definition
    Teams are the OSEE representation of the group of people expected to
    perform any work associated with the Action. They are related to the
    AIs that they are responsible for and are configured with Leads and
    Members to route the Actions and perform the work. A workflow
    represents and provides the steps the team will follow to perform
    the work. The workflow is configurable - the work steps and
    representation can be changed.
  - **Workflow Configuration**
    State machine that shows the path the Team will follow to perform
    the work associated with the Action.
  - **Action**
    Top level grouping object. An Action is written against any number
    of AIs. The Team Workflows are then created for each team configured
    to perform work for an AI.
  - **Team Workflow**
    Instantiation of a Workflow Diagram needed to perform the work. Each
    team independently moves through their workflow state machine
    however ATS can be configured such that certain gates must be met
    from other Teams or outside events before a workflow can continue.
  - **Task**
    Within states of a Team Workflow, smaller-light-weight Tasks can be
    created to further separate the work that needs to be completed for
    that state. Normally, the state can not continue until the Task is
    completed.
  - **Versions**
    ATS has built in project/release planning. Versions are created to
    group Team Workflows (Actions) into Builds and Releases.

# Configuration

## Configuring ATS for Change Tracking

The Action Tracking System (ATS) can be configured for tracking changes
made to such things as requirements, software, hardware and facilities.

The integrated nature of ATS also allows for the tight configuration
management of changes to the artifacts that are stored and managed in
OSEE, such as requirements, and provides advanced features that allow
for advanced workflow management of these items.

OSEE ATS provides a few levels of configuration, from the simple/dynamic
configuration that can be created/modified during runtime to a more
advanced configuration that requires a separate plugin and
release/update.

1.  Create a New configuration using the ATS Configuration Wizard
    1.  Select File -\> New -\> Other -\> OSEE ATS -\> ATS Configuration
    2.  Enter in a unique namespace for your configuration (e.g.:
        org.company.code)
    3.  Enter in a name for the Team that will be performing the work
        (e.g.: Code Team)
    4.  Enter in a list of Actionable Items that you want the users to
        write change requests against. (eg: Editor, Installer, Website)
    5.  If Actions are to be grouped and released in versions (or
        builds), enter a list of versions. (eg: 1.0.0, 1.1.0, 1.2.0)
        Otherwise, this field may be left blank.
    6.  If an existing workflow (eg: osee.ats.teamWorkflow) is to be
        used, enter this as the id. Otherwise a new workflow will be
        created.
    7.  Upon Selecting Finish, ATS will be configured with the entered
        information. You can then select to create a new Action and
        select one of the Actionable Items (named above). This will
        create a new workflow and assign it to the above Team for
        processing.
    8.  See [Configure ATS for Change
        Tracking](/docs/OSEE/ATS/Users_Guide/Usage.md#Configure_ATS_for_Change_Tracking "wikilink")
        for more information.
2.  Creating a New workflow configuration for existing Team / Actionable
    Items using the ATS Workflow Configuration Editor
    1.  Select File -\> New -\> Other -\> OSEE ATS -\> ATS Workflow
        Configuration
    2.  Enter in a unique namespace for your configuration (eg:
        org.company.code)
    3.  Upon Selecting Finish, ATS will create a simple workflow that
        can be expanded with new states, transitions and widgets.
    4.  This workflow will need to be related to the Team Definition
        that will use it. See [Configure ATS for Change
        Tracking](/docs/OSEE/ATS/Users_Guide/Usage.md#Configure_ATS_for_Change_Tracking "wikilink")
        for more information.
3.  Editing an existing workflow configuration using the ATS Workflow
    Configuration Editor
    1.  In the Branch Manager, set the Default Working Branch to the
        Common branch.
    2.  In the Artifact Explorer, expand Action Tracking System -\> Work
        Flows and select the workflow to edit. This will open the ATS
        Workflow Configuration Editor.
    3.  Workflow can be edited to include new states and transitions.
        Double-click state to relate widgets and rules.
    4.  Selecting Save will validate the workflow and save the changes
        to the database. These changes can be used immediately by Team
        Definitions configured to use this workflow.
    5.  See [Configure ATS for Change
        Tracking](/docs/OSEE/ATS/Users_Guide/Usage.md#Configure_ATS_for_Change_Tracking "wikilink")
        for more information.
4.  Advanced ATS Configuration via the org.eclipse.osee.ats.config.demo
    - Although ATS can be configured dynamically in a runtime
    environment, advanced configuration, like creating new widgets and
    rules, can be done through extension points. The ats.config.demo
    plugin is an example of these capabilities.

## Configuring ATS Workflows for Branch Configuration

ATS is designed to configuration manage the changes made to a branch
through configuration of a workflow for branch create/commit and the use
of versions.

Branch creation/commit widgets TBD

Branch Configuration Attributes via Version artifacts:

  - **ats.Allow Branch Create** - activates ability to create working
    branches from parent
  - **ats.Allow Branch Commit** - activates ability to commit working
    branches to parent
  - **ats.Parent Branch Id** - set to the branch id of the branch to
    create/commit

Branch configuration mapping If ATS - Team
Definition attribute "ats.Team Using
Versions" == false, then ATS uses branch configuration attributes from
Team Definition.

Else If ATS - Team Definition
attribute "ats.Team Using Versions" == true, then ATS uses branch
configuration attributes from targeted ATS - Version
Artifact. This means that the ATS -
Team Workflow must be targeted to a
version before branching can occur.

## Configuring ATS for Multi-Branch Committing

ATS provides the ability to configure a workflow/working branch for
commit to multiple baselines. This capability can only be used with the
use of Team Versions.

**Example:** Working Branch WB is created off baseline branch
Build1Branch and targeted for version Build1. Baseline branch
Build2Branch is created off Build1Branch in order to develop both builds
in parallel. Changes to WB are to be committed to Build1Branch and
Build2Branch.

**Configuration:**

  - In ATS Navigator, double-click "Versions - Team Versions"
  - Select team to be configured
  - Double-click version artifact, in this example "Build1"
  - In Artifact Editor, expand relations section of Build1 and drag
    "Build2" version into "ParallelVersion - Child" relation.

**Use:**

  - Open Action associated with WB
  - Create Working Branch
  - Make changes to Working Branch
  - Notice that Commit Manager widget will have both Build1 and Build2
    versions to commit into.
  - Double-clicking each version will allow for commit/merge into each
    build independently

**Note:** Once the first commit is performed, the Working Banch is no
longer available for modifications. Any merging changes can be performed
via the Merge Manager that will be available if conflicts exist.

