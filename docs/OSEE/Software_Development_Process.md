## Planning

The OSEE team uses Agile methods to develop OSEE. While the Agile
Manifesto values individuals and interactions over tools, we have
identified elements of the Agile Process that can be enhanced by the
concepts embodied in OSEE.

The OSEE development team understands and uses the Agile Development
Process. The Agile process is continuous – the team has a Product Owner
who understands the goals for the product and keeps a backlog of items.
For the OSEE team, the most convenient way to understand what needs to
be done to improve or fix OSEE is to use the tool to manage its own
Actions. The Action provides enough information to determine what part
of OSEE the Action applies to, who initiated the Action, and the exit
criteria for completion of the Action.

Development of OSEE is divided into Sprints. The Product Owner reviews
the backlog Actions, and provides a groomed list of Actions to the team
to include in Sprint planning. The team chooses the most important
Actions and assigns story points to each candidate Action. The story
points provide a way to identify the level of effort required to
complete an Action. The process emphasizes team interaction, and the
team buys-in to all of the actions in the Sprint because they are
involved in the selection of Actions. The selected Actions are given a
Goal order to suggest the order they should be addressed. The Sprint
typically starts the day after the previous Sprint is completed.

Sprint progress is monitored using daily Scrum meetings. During the
meeting, each developer reports progress on the Actions they are
working. Anything blocking the developer from completing an Action is
raised in the Scrum meeting so help can be obtained. Completed Actions
are marked, and developers are assigned remaining Actions from the
Sprint plan. Project metrics are collected that show how many story
points are completed. The OSEE team uses a combination burn down/burn up
chart that shows how far along the plan the team is and how much
completed work the team has accomplished.

At the end of the Sprint, a Sprint Retrospective is used to review with
the team how well the Sprint went. The Retrospective covers what went
well and what didn’t with an eye to improving future Sprints. Team
Velocity, a measurement of the number of story points per day per
person, is collected and graphed to show how the Sprint compares to
other Sprints. The Sprint Planning process repeats and the next Sprint
starts.

` TODO: Add bugzilla relation to planning`

## Change Requests

The OSEE team utilizes its Action Tracking System
([ATS](http://wiki.eclipse.org/OSEE/Architecture#Action_Tracking_System_.28Details.29))
to capture and track OSEE software actions. OSEE users have the ability
to submit change requests (e.g.
[bugzilla](https://bugs.eclipse.org/bugs/)) describing something a user
wants (e.g. a new feature or a software bug fix). These actions reside
in an ordered backlog where they are evaluated and prioritized to be
resolved, tested, and released appropriately using the [agile software
process](http://wiki.eclipse.org/OSEE/Software_Development_Process#Planning).
The OSEE product backlog consists of features, bugs, technical work, and
knowledge acquisition items. Items in this backlog are sorted through a
collaborative approach with users/stakeholders and planned for work.

`TODO: Add reference links for bugzilla or other process references.`

## Software Requirements

Requirements for OSEE are an on-going process. User Stories are drawn up
for each key use case. Software requirements are derived from the User
stories. The intent is to identify **WHAT** OSEE needs to do in order to
fulfill customer acceptance for a particular use case. Requirements are
meant to be high level and not focused on how OSEE will accomplish each
task.

`"OSEE shall access an outside system that maintains requirements" is `**`WHAT`**` it will do.`
`"OSEE shall import requirements from DOORS" is `**`HOW`**`.`

`TODO: Add Functional decomposition information`

## Software Design

Design considerations:

  - What open source libraries already provide part or all of the
    solution?
  - Remove inclusion of unnecessarily specific restrictions (i.e.
    generalize the solution)
  - Carefully consider consistency with and impact to overall system
    architecture
  - Minimize the user support this feature will require.
  - Consider use of appropriate software patterns.
  - Consider the concurrency implications – including does the design
    take advantage of multiple cores
  - Interface design considerations
  - API compatibility - Binary vs. source vs. contract
  - Gracefully handle bad data, invalid states, etc.
  - Usability – work closely with customer on visual interface

`TODO: Add link to software architecture/design`
`TODO: How is the design documented?`
`TODO: What process is used to design?`
`TODO: How will the design impact Datastore/Data model compatibility?`
`TODO: Trace design to requirements`

## Software Implementation Process

The phase in the software life-cyle where the actual software is
implemented for the project. This process includes: updating production
software, updating test software, verifying changes through unit tests
and system level tests, committing changes into version control, making
local builds and submitting changes for review.

![image:software_implementation_workflow.png](/docs/images/software_implementation_workflow.png
"image:software_implementation_workflow.png")

`TODO: Add Implementation Process Diagram`
`TODO: Add customer review block as part of implementation cycle`
`TODO: Add documentation`
`TODO: Add Checklists and static analysis checks before submission`

### Test

#### Testing Strategy

  - Testing occurs concurrently with development. Test driven
    development, especially when resolving bugs, is encouraged.
  - We utilize a multiple level testing strategy to provide adequate
    test coverage while minimizing test maintenance
      - Client side integration JUnit tests provide 100% coverage of
        REST Endpoints paths
          - provides code coverage for JAX-RS annotations, providers,
            serialization, and deserialization
          - provides significant code coverage for server code with
            tests that are resilient in the face of refactoring of
            underlying services
      - Server side integration JUnit tests provide coverage of OSGi
        service interfaces methods missed by REST based tests
      - Pure unit tests test internals via a fragment bundle to provide
        coverage missed by REST and OSGi service level testing

#### REST Endpoint Testing

REST endpoints utilize OSGi services that are exposed through public API
interfaces such as OrcsApi, DefineApi and AtsApi. Endpoints refer to the
REST URLs that are available for clients such as Web, IDE and other
external tools needing access to data managed in OSEE. The REST
endpoints should be tested through he Integration tests such as the
OseeClient_Integration_TestSuitean and AtsIde_Integration_TestSuite.
These test suites use and OSEE desktop client and OSEE server and start
with database initialization and population with demo data. The goal is
100% coverage of REST Endpoints paths which will also provide
significant coverage of the OSGi services they utilize.

These Integration tests are very important to test the full path of an
operation or many operations together. Historically, these have been our
most productive in finding bugs. A good example of this is the
DemoBranchRegressionTest. This tests a major set of operations that are
used by anyone authoring requirements using OSEE. A requirements action
is created, a branch created, changes made, branch committed, code/test
workflows/tasks are generated and the workflow completed. All these
operations together test a large amount of code and are very useful in
finding bugs before our customers.

#### OSGi Service Testing

Server side integration JUnit test suites use an OSEE server and start
with database initialization and population with demo data. These tests
provide coverage of OSGi service interfaces methods missed by REST based
tests.

#### Unit Tests / JUnit

Unit tests provide coverage missed by REST and OSGi service level
testing. For unit tests, pure JUnit is preferred over JUnit plugin
testing. JUnit tests do not require server, database or other services
running so they can be created, run and debugged very quickly. JUnit
tests are best for testing complex logic and/or where it would take a
lot of work to setup all the test cases using a demo database. We do not
encourage all methods to be JUnit tested. Especially things like
getters/setters where it is only testing that a value can
stored/retrieved. Instead complex logic like looping and for statements
should be separated from UI and database constraints and tested
independently.

#### Using Fragments

Eclipse provides a concept of a fragment bundle. The fragment will have
the same name as the host bundle with ".test" appended to it. The
package structure will mirror that of the host bundle (i.e. it will not
contain ".test" in any package). The test class will have the same name
as the unit under test with "Test" appended. Each test method in the
test class will be named testMethodName() where methodName() is the
method being tested in the unit under test. Each package will contain a
package test suite file that lists which classes are run as a part of
that test. Within Eclipse, the Emma code coverage tool can be used for
unit tests.

#### Mockito

Note: In the past, Mockito (http://code.google.com/p/mockito/) was used.
<b>This is now discouraged due to the difficulty of debugging/fixing the
tests as refactors happen.</b> Mokito tests are used to create test mock
data types. Mockito mock types can be setup to return predetermined
values which can be used in assert statements to test functionality.
Tests are put into a fragment bundle where the host bundle contains the
code being tested.

`TODO: Discuss test bundle structure and test suites`
`TODO: Add test patterns such as: setup, exercise, verify`
`TODO: Client test framework - rules, monitorlog, etc..`
`TODO: Discuss how tests behave during build, what is accessible (accessing resources)`

### Review

Reviews are done using the Gerrit Code Review tool. After code has been
written, it is submitted to Gerrit which allows other developers a
chance to review the code prior to it being merged into a baseline
branch. Information on how to configure Git to use Gerrit can be found
here: [1](http://wiki.eclipse.org/Gerrit). Comments can be made in
Gerrit in-line of the changes. After the code has been reviewed, use the
“Review” button to complete the process. A **-1** should be used if
there are comments that need to be addressed prior to the code being
merged in. A **+1** should be used if the change looks good. Once two
developers have reviewed the code, a committer can then review the
change with **+2** to merge the change into a baseline branch.

The [Peer Review
Checklist](/docs/OSEE/Software_Development_Process/Peer_Review_Checklist.md "wikilink")
should be used by the developers during the code review.

**Note:** A review of **-2** from a committer will block the change from
being merged in. The IP portion of the review is for preventing
unauthorized 3rd party libraries from being packaged with OSEE. If no
unapproved external dependencies are included in the change, a **+1**
should be given by at least one reviewer. The Verified portion of the
review means that the change has been successfully built (i.e. the
change does not break the build). If the patch successfully builds, a
**+1** should be given or a **-1** if the build fails.

`TODO: Add link to Gerrit Workflow`

## Continuous Integration

For quality purposes, all tests are run prior to checking in code
changes. Gerrit can integrate with Hudson for the **Verified** portion
of the review, but that is currently disabled due to the time it takes
to run the build and tests. Multiple integration builds are created
prior to release and are used by developers to check out new
functionality as well as verify prior functionality. Once a build has
been used by developers, it is promoted to user acceptance. A subset of
users use the new build to verify more functionality. Prior to release,
all tests must pass and all changes that went into the new version are
verified by doing readiness reviews. These reviews are to double-check
that changes we say are in a build are actually in the build that is
marked for release.

`TODO: Add tag description`
`TODO: Describe builds (alpha, betas, release) - how to access`
`TODO: Repository tags ?`
`TODO: Add links to Eclipse Hudson`
`TODO: Add build diagram for alpha, betas, etc`

### Releases

The OSEE Development process is designed to enable a change to be
adequately reviewed and tested such that it could be quickly
incorporated into the a next release. The releases attempt to facilitate
this.

  - OSEE Release
      - Production Release
      - Most Stable Release
  - OSEE Release Candidate Beta
      - Next release to be released to Production
      - Used by smaller subset of user base
  - OSEE Release Candidate Alpha
      - Nightly snapshot of what has been released into the current RC
        branch
      - Can be promoted to Beta when changes are ready
  - OSEE Development Beta
      - Trusted changes from the development line
      - Normally only used by OSEE developers or users who need to test
        a single change
      - Unstable and can be updated / restarted anytime
  - OSEE Development Alpha
      - Nightly snapshot off development line
      - Only used by OSEE developers
      - Most unstable and can be updated / restarted anytime

### Building And Deploying

OSEE Builds and Deployment are done through Hudson. Snapshot builds are
kicked off nightly and available the next morning. At anytime, an OSEE
developer can select to promote an Alpha to Beta or Beta to Release by
activating the promotion through Hudson.

OSEE is currently being deployed automatically to shared location.

## Compatibility Considerations during Deployment

Depending on the type of action, care must be taken to deploy to an
production site.

  - Additions modifications of Artifact, Attribute and Relation types
      - If changes to the type model are additions or minor
        modifications, they can be made directly in the production
        unified type artifact without down-time
      - If the changes include deletions or renames of types, you will
        need to coordinate converting the database before making the
        changes to he types.
  - Code changes
      - If multiple releases of OSEE are deployed, code changes must be
        made in a compatible way such that the current release can
        continue to work while the alpha and beta releases are able to
        be tested and released to a smaller user audience. One way is to
        use the OSEE_INFO table to create a flag that allows old code
        to run until toggled. This allows the development and testing of
        the new code, but alpha and beta and full releases won't run the
        new code until the flag is toggled.

Followup changes during and after a release

  - If changes are necessary during a release, such as table conversion
    or converting database attributes, you will need to create a ATS
    action labeled "During <release id> work". Then add a task in the
    task tab that explains what work needs to be done and references the
    Action that it's being done for. Provide enough detail that any OSEE
    developer can perform the task.
  - If changes are necessary after a release, such as cleaning up
    deactivated or compatibility code, create a ATS action labeled "Post
    <release id> work". Then add a task in the task tab that explains
    what work needs to be done and references the Action that it's being
    done for. Provide enough detail that any OSEE developer can perform
    the task.

