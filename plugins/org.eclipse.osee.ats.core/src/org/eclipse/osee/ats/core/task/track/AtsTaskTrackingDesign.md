# ATS Task Tracking Documentation/Design

* <a href="../workflow/AtsWorkflowLinks.md">ATS Workflow Links</a>

### Purpose
Task Tracking is a generic REST accessible feature that allows for the creation of an action/team workflow 
and tasks using json. This capability is useful when a user wants to track anything with a set or changing 
set of steps.  

Example: Generating readiness reviews for a release process where some tasks are 
dynamic and related to code commits and some reviews are static where you must do these steps/tasks on 
every release.

### Design

#### Creating a new Action/Team Workflow and Tasks using a REST call

* POST to <server>/ats/action/tasktrack with appropriate credentials and json payload
* A new Action/Team Workflow will be created if it does not already exist.  
** Action Title MUST be unique
** Use appropriate Actionable Item artifact id for action creation
* See TaskTrackingDataExampleCreate.json in org.eclipse.osee.ats.ide.integration.tests/OSEE-INF/taskTrack for an example of the json to send it.  
* The resulting XResultData object will contain errors if something went wrong

#### Using database configured tasks during REST call

* If json payload to REST call contains <b>"taskTrackArtId" : "485234857",</b> (see example), the artifact with the art id (eg: 485234857) will be read for other tasks to create
* This supports tasks that should always be created
* A General Data artifact should be created on the Common branch.  
* See TaskTrackStaticArtExample.json for example of what to add as a single General String Data attribute


#### Adding new tasks using successive REST calls

* Successive POSTs with json payload will create any new tasks if names don't match an existing task
* Search of action/workflow is by name.  If duplicate workflow names exist, operation will fail.  
* Tasks will NOT be duplicated/updated if they were already created

#### Relating tasks to "Supporting" Common branch Team Workflows

* If a task entry contains <b>"supportingAtsId" : "TW10"</b> (see example), then the task that is generated will be linked (using SupportingInfo relation) to that workflow.  This TW number MUST be valid if it is used.

#### Task Assignees

* If a task entry contains <b>"assigneesArtIds" : "277990", "comment": "Jason Michael",</b>, then the user art id (eg: 277990) will be used to set the assignee of that task.
* If multiple assignees are desired, you can use a comma delimited list of user art ids (eg: 277990,23244)
* Note: "comment" json tag is not read or used by this feature, in this example it is used to show the name of the configured user so it is easy for someone to read when configuring / updating the json payload.  This is because json does not provide a comment feature.

#### Example and Testing

This feature is tested by TaskTrackingOperationTest.java in the ATS Integration Test Suite.  Running those test and loading the Action/Workflow/Tasks will show the capability in action.  It also shows/tests the static db configured tasks in the created artifact TaskTrackItemsArt also in that test class.

#### Improvement of payload 

TaskTrackDataMain.java can be used to generate the example payloads if the fields of TaskItem or TaskTrackItems or TaskTrackingData changes.  This is a stand-alone java class that can just be run with right-click > Debug-As > Java Application
