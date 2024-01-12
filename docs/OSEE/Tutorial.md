This page is being adapted from [this
presentation](http://www.eclipse.org/osee/OSEE_eclipsecon2009_tutorial_slides.pdf).

It is recommended that this tutorial be viewed in a roughly half-width
browser window.

## Requirements

### System Requirements

OSEE requires a system with at least 1GB of RAM running JRE 1.6 or
higher. Microsoft Office is useful for the demo.

### Eclipse Dependencies

The following Eclipse dependencies are required, although it is easiest
to simply use the [Ganymede
Eclipse](http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/ganymede/SR2/eclipse-reporting-ganymede-SR2-win32.zip)
install.

  - Eclipse 3.4.2 SDK
  - org.eclipse.gef
  - org.eclipse.draw2d
  - org.eclipse.birt
  - org.eclipse.datatools

## Installation

### Database

Install [PostgreSQL](http://www.postgresql.org/download/) using the
following settings:

  - Default install path and data directory
  - Password "Postgre1"
  - Port 5432
  - Default locale
  - On the final screen, ensure that "Launch Stack Builder" is not
    selected

After saving the database password using pgadmin, run the
osee_db_setup script, which is located under the "PostgreSql" folder.

[More on PostgreSQL
installation](http://www.eclipse.org/osee/documentation/installation/postgresql_install.php)

### Java Runtime Environment (JRE)

Ensure that the directory in which the JRE is installed is in the path
by typing "java --version" at a command prompt.

### OSEE Client

Extract and launch Eclipse. From the update manager, install the update
sites located under "OseeClient":

  - org.eclipse.osee_integration_build_incubation.zip
  - osee.add.ons.updatesite.zip osee.add.ons.updatesite.zip

## Initialization

### Launch Application Server

Execute the osee_app_server script located under
"OseeApplicationServer" and wait until the server finishes the start up
procedure before closing the console.

### Database Initialization

In a command prompt, change to the Eclipse install directory and execute
the following command:

    eclipsec -application org.eclipse.osee.framework.database.configClient -vmargs -Xmx512m
    -Dosee.log.default=INFO -Dosee.application.server=http://localhost:8089
    -Dosee.authentication.protocol=trustAll -Dosee.prompt.on.db.init=false
    -Dosee.choice.on.db.init="OSEE Demo Database"

Once db init closes, type "exit" in the server console.

### Populate Demo Data

Launch the Application Server and then the OSEE client with the
following command:

    eclipse -vmargs -Xmx512m
    -Dosee.application.server=http://localhost:8089
    -Dosee.authentication.protocol=demo

Switch to the ATS perspective (**Window \> Open Perspective \> ATS**);
in the ATS Navigator window, enter "Populate" as the filter and press
Enter, then double click on the "Populate Demo Actions" item.

# Case Study

## Scenario

User Joe Smith finds a problem in a requirement impacting multiple
teams, namely Code, Test, Website, and IT. Multiple reviews will be
needed:

  - Decision Review (off Code Team Workflow)
  - Peer Review (off Test Team Workflow)

In addition, certain tasks will need to be performed off Code Team
Workflow.

## Dramatis Personae

  - Joe Smith
    Project Engineer (Requirements and Code)
  - Alex Kay
    Project Manager
  - Kay Jones
    Test Engineer

## Requirements Search

![selectbranch.png](/docs/images/selectbranch.png "selectbranch.png")
![quicksearch.png](/docs/images/quicksearch.png "quicksearch.png")

  - Switch to the Define perspective (**Window \> Open Perspective \>
    Define**).
  - In the Artifact Explorer, click **Select Branch...**
      - Select "SAW_Bld_2"
      - Click "OK"
  - Search for Item:
      - Click on the **Quick Search** view
      - Select the "SAW_Bld_2" branch
      - Enter "robot object" in the search string text box
      - Check the **Match Word Order** option
      - Check the **All Match Locations** option
      - Click the search button

## Exploring the requirement

The search results will be listed in a tree or list view with match
locations. We want to explore the robot object requirement. From the
search result *list* (not tree),

  - Right click on the "Robot Object" software requirement
  - Select each of the following from the pop-up menu individually
      - Reveal Artifact in Explorer
      - Resource History
      - Open With \> Artifact Editor
          - Open the **Attribute** tab
          - Open the **Relation** tab
      - Open with \> MS Word Preview
          - Convert file from XML Document
      - Sky Walker
          - Here, we can see how the "Robot Object" software requirement
            relates to other artifacts.

## Requirements Team Workflow

We want to create an action against the "Robot Object Requirement."
![3-createatsaction.png](/docs/images/3-createatsaction.png "3-createatsaction.png")

  - Switch to the ATS Perspective (**Window \> Open Perspective \>
    ATS**).
  - Click on the New Action icon (![Image:NewAction.gif](NewAction.gif
    "Image:NewAction.gif")) in the **ATS Navigator**.
      - Fill in the **Create ATS Action Dialog**
          - Enter "Robot Object requirement needs more detail" as the
            **Title**
          - Select "SAW Requirements" (under "SAW CSCI") as the
            **Actionable Item**
          - Click **Next**
      - Fill in the fields as follows:
          - **Description**: "See title"
          - **Change Type**: "Problem"
          - **Priority**: "3"
          - **User Community**: "Program_1"
      - Click **Finish**. This will generate a Requirements Team
        Workflow for the necessary requirements changes. Joe Smith will
        automatically be assigned the position of Team Lead.

### Endorse

The Requirements Team Lead (Joe) has approved the Requirement Team
Workflow and wants to transition to analysis.

  - Set the **Target Version** to SAW_Bld_2.
  - Change **Priority** to 2.
  - Transition to the "Analyze" state.
      - This is where the lead would normally assign another user to
        complete the work; however, we will not change the assigned user
        for the demo.

### Analyze

![7-actionableitemslink.png](/docs/images/7-actionableitemslink.png "7-actionableitemslink.png")
![6-impactedactionableitems.png](/docs/images/6-impactedactionableitems.png "6-impactedactionableitems.png") Here, Joe (as the Requirements
Developer) wants to analyze the action.

  - Input "Fix It" as the **Proposed Resolution**
  - Set **Estimated Hours** to "2.5".
  - As this change will impact Code as well as Test, we will add
    corresponding workflows for each.
      - Click on the **Actionable Items** hyperlink
      - Select "SAW Code"
      - Select "SAW Test"
      - Click **OK**. The **Action View** will now show new workflows.
        Email notifications have been sent to the team leads.
  - Transition to "Authorize".

### Authorize

Here, the Requirements Team Lead (Joe again) authorizes the Action.

  - Input "A324324A" as the **Work Package**.
  - Since the Team Lead needs concurrence from Kay (the Manager), a
    decision review is needed.
      - Click the **Add Decision Review** hyperlink.
      - Fill-in the **Create Decision Review** dialog:
          - **Review Title**: "Any Problems with authorizing this?"
          - **Associated state**: "Authorize"
          - Click **OK**

#### Decision Review

##### Prepare

Here, the Requirements Lead prepares the review:

  - Set **Review Blocks** to "Transition"
  - Set **Estimated Hours** to 3
  - Click **Assignee(s)**
      - Select "Alex Kay"
  - Transition the review to "Decision"

##### Complete

First, Alex Kay has to check his assigned work.

  - In the **ATS Navigator**, double click on **My World** and select
    Alex Kay as the user.
  - From Alex Kay's **User's World**, select the "Decision Review"
  - Alex Kay decides "yes;" we will simulate this with a **Privileged
    Edit**.
      - Click the Privileged Edit icon
        (![Image:5-PrivilegedEdit.png](5-PrivilegedEdit.png
        "Image:5-PrivilegedEdit.png")).
      - Click **Override and Edit**.
      - Select "Yes" as the **Decision**.
      - Transition the Review to "Completed".

### Authorize (cont'd)

Note that before Alex Kay had completed the decision review, Joe was not
able to transition to the next state.

  - Transition to "Implement".

## Implementing a Change

![10-selectworkingbranch.png](/docs/images/10-selectworkingbranch.png "10-selectworkingbranch.png") ![11-mswordedit.png](/docs/images/11-mswordedit.png "11-mswordedit.png")
![13-viewwordchangereport.png](/docs/images/13-viewwordchangereport.png "13-viewwordchangereport.png")

  - From the Implement State, select the **Create Working Branch** icon
    (![Image:9-CreateWorkingBranchIcon.Png](9-CreateWorkingBranchIcon.Png
    "Image:9-CreateWorkingBranchIcon.Png")), then click **OK**.
  - In the **Artifact Explorer**, click **Select Branch**.
      - Select the working branch just created. Its author is "Joe
        Smith" and its comment field reads "New Branch from SAW_Bld_2"
        (see image).
      - Navigate to and right click the "Robot Object" software
        requirement.
          - Alternatively, you can perform a **Quick Search**, but you
            must remember to specify the working branch again or the
            parent branch will be searched instead.
      - From the context menu, select **Open With \> Artifact Editor**.
  - Under the **Attributes** tab, change **Qualification Method** to
    "Inspection" and save the file (**File \> Save**).
  - From the **Artifact Editor**'s toolbar, select **Open With \> MS
    Word Edit** (see image), and, if prompted, specify that Word is to
    open the file as an XML document.
      - Insert into the document the next "Need more information here."
      - Save and close the document. The **Artifact Editor**'s Word
        Template Content Attribute should update accordingly.
  - Switch to the **Workflow Editor** and select the "Show Change
    Report" icon
    (![Image:12-ShowChangeReportIcon.png](12-ShowChangeReportIcon.png
    "Image:12-ShowChangeReportIcon.png")) from the toolbar immediately
    above the **Commit Manager**.
  - From the **Change Report View**, right click on the "Robot Object"
    Software Requirement and select **View Word Change Report**. If
    prompted, specify that the document should be opened as XML. A
    document similar to the one shown on the right should appear,
    highlighting the differences between the versions of the Artifact
    found in the parent branch and the working branch.
  - When finished, close MS Word.
  - Double click the working branch in the **Commit Manager** to apply
    changes to the parent branch.
  - Transition to "Complete".

## Code Team Workflow

Begin by opening the Code Workflow. This can be found using the **Show
all Team Workflows** view found in the **ATS Navigator**. The Workflow
in question has the title "Robot Object requirement needs more detail"
and is in the Endorse state with priority 2.

### Endorse

  - Enter "A234532" as the **Work Package**.
  - Transition to "Analyze".

### Analyze

  - For **Estimated Hours**, enter "10".
  - Click on the **Tasks** tab at the bottom of the View.
      - Add a Task by clicking on the **New Task** icon
        (![Image:15-NewTaskIcon.Png](15-NewTaskIcon.Png
        "Image:15-NewTaskIcon.Png")) on the toolbar.
      - In the **Create New Task** dialog, enter "Do the first thing".
      - Click **OK** to close the dialog.
  - Double click on the new task to open the **Task Editor**.
      - Click on **Assignee(s)** to assign a different user (it doesn't
        matter which user). Enter the **Estimated Hours**.
      - Close the **Task Editor**.
  - The above steps can be repeated to add additional tasks as needed.
  - Transition each task to the "Completed" state.
      - Note that the workflow cannot be advanced until all tasks
        associated with the Analyze state have been Completed.
  - Transition the code team workflow to Authorize.

## Test Team Workflow

Here, the Test Lead (Joe) estimates the work required for the test team
workflow.

  - Open the Test Team Workflow. If necessary, you can search for it in
    the same manner as the Code Workflow.
  - Since Kay Jones, the assignee for this workflow, is not in today,
    Joe will need to get edit privileges to transition the workflow.
      - Click on the Privileged Edit icon (second from the right) and
        click on **Override and Edit** in the resulting dialog box.
  - Transition to the "Analyze" state.
  - Set **Estimated Hours** to "25".

## Metrics

There are a number of different ways to extract / view metrics through
OSEE ATS.

### ATS Metrics Attributes

ATS workflows have metrics attributes available

  - ats.Estimated Completion Date
  - ats.Estimated Hours
  - ats.Estimated Release Date
  - ats.Created Date
  - ats.Completed Date
  - ats.Cancelled Date
  - ats.Start Date
  - ats.End Date
  - ats.Percent Complete

Created, Completed and Cancelled dates are set automatically as the
workflow moves through it's lifecycle. Other attributes can be set
through the Workflow Editor, World Editor or REST calls

### ATS Workflow Editor

The Workflow editor has Percent Complete (editable), Estimated Hours
(editable), Total Hours Spent (computed) and Remaining Hours (computed).
The Team Workflow Percent Complete is broken into 2 values xx/yy (if
applicable). The xx will show the Percent Complete for the viewed
workflow. The yy value will show a roll-up computed percent complete of
Team Workflow, Tasks and Reviews.

### ATS Log

Each workflow has an ats.Log that automatically stores events like
transitions, assignees and state hours spent. This log can be accessed
via the ATS State Manager and used to generate/export historical events
in the lifecycle of the workflows.

### ATS Metrics Tab

"What this place needs is some metrics\!" Alex Kay, as Manager, needs a
status report.
![16-redisplayasworkflows.png](/docs/images/16-redisplayasworkflows.png "16-redisplayasworkflows.png") ![17-metrics.png](/docs/images/17-metrics.png "17-metrics.png")

  - From the **ATS Navigator**, select "My World".
  - Click the rightmost icon on the toolbar to bring up a context menu.
    Select **Re-display as WorkFlows**. \* Click on the **Metrics** tab
    (at the bottom) to open the Metrics Page.
  - Set **Estimated Release Date** to two days from now.

Joe Smith will finish on time, as the estimate of remaining hours in his
workflows is well within the remaining time to release; Kay Jones, on
the other hand, won't make it. </br>

### Peer Review Metrics

Out of the box, OSEE Review provides the "Generate Participation Report"
which can be run for any OSEE user. This will show what peer reviews a
user has done.

### Other Attributes

As with all of OSEE, other attributes can be added to workflows to store
any data necessary for other metrics and added to the Workflow Editor
and World Editor.

## Peer Review

Joe, a Code Developer, has realized that he needs a peer review for
Analysis.

  - Add a Peer-To-Peer Review
      - Click on the **Add Peer to Peer Review** hyperlink, which can be
        found near the bottom of the page by **"Analyze" State
        Reviews**.
      - In the **Add Peer to Peer Review** dialog, select the "Analyze"
        state and click **OK**.
  - Add Reviewers
      - Click on the New Role icon (in the toolbar in the upper right of
        the **Role** pane, it is the leftmost icon) twice to add two new
        reviewers.
      - Set one of the roles to "Author" and the other to "Reviewer".
          - Edit the **Role** field by clicking on the field while
            pressing the ALT key.
  - Set **Location** to "That.java; This.java"
  - Set **Blocking** to "Transition"
  - Set **Estimated Hours** to "2"
  - Transition to "Review"

## Tool Team Workflow

Joe realizes that the change will affect the Tools Team.

  - Select the "Actionable Items" hyperlink from the top of the SAW Code
    Workflow.
      - Select "Website" (under "Tools") from the **Add Impacted
        Actionable Items** dialog.
  - Workflows are created for the Website Team.