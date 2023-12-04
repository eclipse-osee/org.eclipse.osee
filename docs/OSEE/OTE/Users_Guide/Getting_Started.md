# Overview

The OSEE Test Environment (OTE) provides a framework for the testing of
complex software systems. The test environment manages models, messages,
remote communication, and logging.

Environments provide the core configuration of the tests, there is
generally a 1 to 1 mapping between environments and labs. Generally,
tests are written which set input and check output against the unit
under test. These tests are sent to the Test Environment which then will
coordinate with the unit under test to run the submitted test, or set
the requested input.

These outputs can be imported into the OSEE Disposition tool for
multi-run disposition of the test failures. And the OSEE Continuous
Integration tool can accept changes, launch builds, run test scripts and
report to common location for analysis.

The OSEE Coverage tool can take instrumented test output and determine
Level A, B and C coverage throughout multiple runs.

# Requirements

  - OS: OTE can run anywhere Java can run but for this excercise we will
    focus on Windows
  - RAM: recommend at least 8 GB of RAM
  - HDD: At least 4 gigs of free disk space
  - Java: JDK for java version 11 or newer.

# Steps to running the OTE Simple Automated Test

### Clone org.eclipse.ote

1.  Create a folder for containing your Git repository/ies.
2.  Navigate to Gerrit link here:
    <https://git.eclipse.org/r/admin/repos/osee/org.eclipse.ote,general>
3.  Use one of the clone commands to clone the OTE source repository.
4.  Once cloned, navigate into that repository folder on your machine
    and checkout the branch feature/Java11

### Install OTE Client and Server

  - Download the latest client for your OS here:
    <https://ci.eclipse.org/osee/view/OTE_DEV/job/org.eclipse.ote.simple.oteide.product__DEV/lastSuccessfulBuild/artifact/org.eclipse.ote/org.eclipse.ote.simple.oteide.product/target/products/>
  - Download the latest server for your OS here:
    <https://ci.eclipse.org/osee/view/OTE_DEV/job/org.eclipse.ote.test.server__DEV/lastSuccessfulBuild/artifact/org.eclipse.ote/org.eclipse.ote.test.server.p2/target/ote.server.runtime.zip>

Unzip each zip into unique destination folders.

### Set up the client

The OTE client is where you will connect to the OTE server, develop and
run the automated tests, as well as develop OTE itself.

1.  Launch the OTE client by running ote.exe from the folder that the
    client was unzipped.
2.  When asked, select a workspace to contain the automated tests. The
    default workspace is fine.

<!-- end list -->

1.  ![Image:OteWorkspaceSelection.png](OteWorkspaceSelection.png
    "Image:OteWorkspaceSelection.png")

<li>

In the new workspace that comes up, ensure you are in the OSEE Test
Perspective:

</li>

1.  In the upper right of the workspace, click on the "Open Perspective"
    button and select "OSEE Test"
2.  ![Image:PerspectiveSelection.png](PerspectiveSelection.png
    "Image:PerspectiveSelection.png")

</ol>

### Import the Simple Automated Tests:

1.  From the top menu select File-\>import...
2.  Select General-\>"Existing Projects into Workspace"
3.  Hit the Browse button and find the root folder where you cloned
    org.eclipse.ote
4.  Once selected, there should be many projects automatically selected
    under "Projects"

<!-- end list -->

1.  ![Image:ImportProjects.png](ImportProjects.png
    "Image:ImportProjects.png")

<li>

To run tests, you only need to import a single project from this list so
hit the "Deselect all" button.

</li>

<li>

Scroll through the list put a checkbox on the
"org.eclipse.ote.simple.test.script"

</li>

<li>

Hit the Finish button and wait for the workspace to populate and compile
completely

</li>

</ol>

### Start the OTE Test Server

The test server is where tests are actually going to be executed and
allows for remote testing on machines anywhere in the world. For this
excercise you will be launching a test server locally.

1.  Navigate to the directory you unzipped the ote server
2.  Execute the runWinSimpleServer.bat
3.  A new console will launch and start printing out log information.
    When ready to connect you will see something like the following:
4.  ![Image:OteServerConsole.png](OteServerConsole.png
    "Image:OteServerConsole.png")
5.  The SERVER CONNECTION URI is key. Highlight and copy the text
    "tcp://11.22.33.44:18501"

### Connect to the test server

The Test Manager is where you will be managing which server you connect
to as well as which tests to run. There are also some controls for HOW a
script batch is to be run such as result file location, logging level,
etc.

1.  Back in the client, open the OTE Navigator:
    1.  If you are in the OSEE Test perspective as set up above, this
        should appear in the left side of the workspce next to the
        Package Explorer
    2.  If you do not see the navigator, select the magnifying glass in
        the upper right corner and search for "OTE Navigator"
    <!-- end list -->
    1.  ![Image:OteNavigator.png](OteNavigator.png
        "Image:OteNavigator.png")
2.  In the navigator, double-click on the "Open OTE Test Manager"
    selection
3.  In the dialog, select a previous configuration if it exists or
    select "Create New configuration file"
4.  In the Test Manager, notice there are some subviews in the lower
    left of the frame. By default it will launch in the "Hosts" tab.
5.  In the "Hosts" tab you will find a list of available servers that
    the Client knows about. At the moment it should be empty.
6.  In the host table, right-click somewhere in the table and select
    1.  ![Image:HostRightClick.png](HostRightClick.png
        "Image:HostRightClick.png")
7.  In the dialog, paste the TCP connection string your copied from the
    OTE test server before.
    1.  ![image:hostdialog.png](/docs/images/hostdialog.png "image:hostdialog.png")
8.  If successful, there should be a connection icon attached to the
    entry in the Host Table

### Run the Simple Test

Now you will use the OTE Test Manager to load and run the Simple Test

1.  In the OTE Test Manager, switch to the "Scripts" tab
2.  Now open the Package Explorer view on the left frame and find the
    SimpleTestScript.java file
    1.  You can find it by expanding the
        org.eclipse.ote.simple.test.script pacakge and searching around
        or you can do a Java type search with CTRL+SHIFT+T and type in
        "SimpleTestScript"
    2.  Once you find the SimpleTestScript java file, drag that file
        from the package explorer into the scripts table in the OTE Test
        Manager
    3.  Optionally you can right-click on the file and select OTE-\>Add
        to Test Manager
    <!-- end list -->
    1.  ![Image:RightClickOte.png](RightClickOte.png
        "Image:RightClickOte.png")
3.  Once added, the test should be automatically selected to run in the
    Scripts table
4.  To run the selected test, click on the Green play button at the top
    of the Test Manager
    1.  ![Image:TestManagerPlayButton.png](TestManagerPlayButton.png
        "Image:TestManagerPlayButton.png")
5.  The Test Manager will show periodic updates of the running test as
    it goes through its automation
    1.  Note that this test will never pass completely as there are
        intentional failures as well as some test that require setup to
        pass
    2.  There will be some examples of manual prompts as the test runs
        that you must click through to advance the test

# Related Documentation

For overviews, tutorials, examples, guides, and tool documentation,
please see:

Refer to the OSEE Team generated information included in the eclipse
help subsystem.

[White Paper - The Java Language
Enviornment](https://www.oracle.com/technetwork/java/langenv-140151.html)

[Trail - Learning The Java
Language](https://docs.oracle.com/javase/tutorial/java/index.html)

[How to Write Doc
Comments](https://www.oracle.com/technetwork/java/javase/tech/index-137868.html)

