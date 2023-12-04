# Quick Start for Installing OSEE

## Prerequisites for installation

  - System with at least 16 GB of RAM
  - Java JDK 1.11 or greater
  - Recent version of Git

## OSEE Installation Steps

  - Download and install the latest JDK
      - Website for download: [Eclipse
        Temurin](https://projects.eclipse.org/projects/adoptium.temurin/downloads)
  - Download and install git
      - Website for download: [Git - Downloading
        Package](https://git-scm.com/download/win)

![image:1_-_git_windows_download.png](/docs/images/1_-_git_windows_download.png
"image:1_-_git_windows_download.png")

  - Run the git executable
      - Adjusting your PATH environment
          - Use Git and optional Unix tools from the Command Prompt
      - Choosing HTTPS transport backend
          - Use the OpenSSL library
      - Configuring the line ending conversions
          - Checkout as-is, commit as-is
      - Configuring the terminal emulator to use with Git Bash
          - MinTTY
      - Leave the rest of the settings as default
  - Set up the proxy for git
      - If you haven't registered with eclipse, register and log in to
        [eclipse.org](https://www.eclipse.org/)
      - Clone the repository (see the code block below for an example of
        cloning the repository)

`    $  cd /c/Code/`
`    $  mkdir git_main`
`    $  cd git_main`
`    $  git clone `<https://><username>`@git.eclipse.org/r/osee/org.eclipse.osee.git --branch dev --depth 1`

  - Note: Replace <username> with your eclipse username
  - Download and install the org.eclipse version of OSEE from the OSEE
    site
      - Website for download: [Eclipse Nightly Build
        Downloads](https://ci.eclipse.org/osee/job/osee_nightly/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.client.all.product/target/products/)
      - This will be the most recent nightly build. If you are running
        Windows OS, choose the download similar to the picture just
        below:

![image:updatedimageeclipsedownloads.png](/docs/images/updatedimageeclipsedownloads.png
"image:updatedimageeclipsedownloads.png")

  - Make an Eclipse folder
      - For example: /c/Code/Eclipse
  - Extract all of the files from the eclipse download into your Eclipse
    folder.
  - When you run eclipse, it will ask to make a workspace. Make a
    workspaces folder.
      - For example: /c/Code/Eclipse/workspaces
      - Tip: Placing all workspaces within a single folder makes it
        easier to locate each workspace.
  - Make a folder to use for your first workspace.
      - For example: /c/Code/Eclipse/workspaces/first_git_main

## OSEE Initialization

  - Run the eclipse instance by switching to its directory and double
    clicking ‘osee.exe’
      - For example: ‘osee.exe’ would be located in
        ‘/c/Code/Eclipse/workspaces/first_git_main’
  - Select the directory of your first workspace and launch

![image:3_-_directory_of_first_workspace.png](/docs/images/3_-_directory_of_first_workspace.png
"image:3_-_directory_of_first_workspace.png")

  - Close the welcome window

![image:4_-_welcome_window.png](/docs/images/4_-_welcome_window.png
"image:4_-_welcome_window.png")

  - Switch to the Git Perspective

![image:5_-_switch_to_git_perspective.png](/docs/images/5_-_switch_to_git_perspective.png
"image:5_-_switch_to_git_perspective.png")

  - Add an existing local Git repository

![image:6_-_add_an_existing_local_git_repo.png](/docs/images/6_-_add_an_existing_local_git_repo.png
"image:6_-_add_an_existing_local_git_repo.png")

  - Search, select, and add the org.eclipse.osee Git repository

![image:7_-_select_and_add_the_org.eclipse.osee_git_repo.png](/docs/images/7_-_select_and_add_the_org.eclipse.osee_git_repo.png
"image:7_-_select_and_add_the_org.eclipse.osee_git_repo.png")

  - Right click on the imported Git repository and select ‘Import
    Projects…

![image:8_-_import_projects.png](/docs/images/8_-_import_projects.png
"image:8_-_import_projects.png")

  - Uncheck these folders (click ‘Finish’ upon completion):
      - org.eclipse.osee
          - \\\*.mbse
          - \\\*.icteam
          - \\\*.doors

![image:9_-_deselect_folders.png](/docs/images/9_-_deselect_folders.png
"image:9_-_deselect_folders.png")

## Running the Application Server

  - Switch to the Debug Perspective
  - Click on the dropdown arrow next to the debug icon
  - Click 'Debug Configurations…'

![image:10_-_debug_config_dropdown.png](/docs/images/10_-_debug_config_dropdown.png
"image:10_-_debug_config_dropdown.png")

  - Double-click 'OSEE_Application_Server_\[HSQLDB\]'

![image:11_-_application_server.png](/docs/images/11_-_application_server.png
"image:11_-_application_server.png")

  - Wait until the console produces this output:

![image:12_-_application_server_output.png](/docs/images/12_-_application_server_output.png
"image:12_-_application_server_output.png")

  - Open 'Debug Configurations…' (again)
  - Double-click 'AtsIde_Integration_TestSuite'

![image:atstestschoice.png](/docs/images/atstestschoice.png
"image:atstestschoice.png")

  - Wait until the JUnit test completes
      - If the ‘Runs’ reach a stopping point, terminate the test by
        pressing the red square icon.

![image:14_-_test_suite_expected_output.png](/docs/images/14_-_test_suite_expected_output.png
"image:14_-_test_suite_expected_output.png")

  - Open 'Debug Configurations…' (again)
  - Double-click 'OSEE_IDE_\[localhost\]'

![image:15_-_eclipse_client.png](/docs/images/15_-_eclipse_client.png
"image:15_-_eclipse_client.png")

  - You should expect an application that appears as such:

![image:16_-_expected_client.png](/docs/images/16_-_expected_client.png
"image:16_-_expected_client.png")

## Quick Start Complete

The additional sections below describe how to download and run the OSEE
as a client server installation.

Below here 'there be dragons' e.g. the documentation was written a while
ago and needs to be updated - it may have bad links or incorrect
instructions

-----

# Installation and Initialization

## Requirements

  - System with at least 4GB of RAM
  - Java Runtime Environment (JRE) 1.8
  - [Eclipse
    Neon 2](https://www.eclipse.org/downloads/packages/release/Neon/2)
  - Relational Databases: OSEE comes bundled with H2. For additional
    database support, see [Supported
    Databases](/docs/OSEE/Users_Guide/Getting_Started.md#Supported_Databases "wikilink").

## Eclipse Installation

To install OSEE: Start by downloading the OSEE Application Server and
the OSEE Client Update Archive from the
[downloads](http://www.eclipse.org/osee/downloads/) page.

### Quick Server Installation

If you are interested in a quick start, and are not setting up a custom
database installation, setting up the OSEE server with the bundled
HSQLDB database is simple.

  - Create a directory to place your server in, and unzip the downloaded
    server zip file (e.g. org.eclipse.osee.x.server.runtime.zip) into
    that directory.

You should end up with the following files and directories:

  -
    configuration
    demo
    eclipse
    etc
    plugins
    runDemo.bat
    runDemo.sh
    runHsql.sh
    runPostgreSqlLocal.sh

<!-- end list -->

  - Edit the desired startup script file (e.g. "runDemo.bat") to provide
    the server the link to the binary data.

For example, if you were using Windows, and you installed to
C:/UserData/OseeDemo, you would change the line in the batch file to:

  -
    \-Dosee.application.server.data="C:/UserData/OseeDemo/demo/binary_data"

<!-- end list -->

  - change to the etc directory, and edit the osee.hsql.json file.

Set the jdbc.server.db.data.path to the location of the hsql db.
Following the example above, for instance, you would change the line in
the json file to:

  -
    "jdbc.server.db.data.path":
    "<file:c:/UserData/OseeDemo/demo/hsql/osee.hsql.db>",

That completes the simple server setup. Run the server by setting up a
command window, then running the script file to start the server. For
example, if you were running with Windows, you would start a windows
command shell, then run the "runDemo.bat" batch script. See [Launch
Application Server](#Launch_Application_Server "wikilink") for details
on running the server.

### Client Installation

The OSEE client can be installed from within Eclipse like any other
Eclipse plugin.

1.  Start Eclipse and select the menu item **Help \> Install New
    Software...**
2.  Select the *Available Software* tab group and click the *Add...*
    button.
    ![add_site.png](/docs/images/add_site.png "add_site.png")
![Image:New_update_site.png](/docs/images/New_update_site.png
    "Image:New_update_site.png")
3.  In the *Add Repository* dialog, choose the Archive button, then
    navigate the file browser to the Client Update file downloaded in
    step one, "org.eclipse.osee.client.all.p2.zip". **Please note that
    the use of the software you are about to access may be subject to
    third party terms and conditions and you are responsible for abiding
    by such terms and conditions.**
4.  Click on the *OK* button to store update site information, "Eclipse
    OSEE Client All - (Incubation)".
5.  Select the OSEE update site entry and all features listed under its
    category. Click the *Next* button.
![Image:AvailableSoftware.png](/docs/images/AvailableSoftware.png
    "Image:AvailableSoftware.png")
6.  If you receive a security popup Window regarding unsigned content,
    click OK.
7.  The update manager calculates dependencies and offers you a list of
    features to install. Select the needed ones and click the *Next*
    button.
8.  Accept terms of license agreement and click the *Finish* button in
    order to start the installation of selected
    features.![Image:AcceptTerms.png](AcceptTerms.png
    "Image:AcceptTerms.png")
9.  To apply installation changes click on the *No* button and shutdown
    Eclipse. It is important that you don't restart Eclipse until you
    have completed the database initialization steps below.

![image:restart_dialog.png](/docs/images/restart_dialog.png
"image:restart_dialog.png") If you are using the default demo database
and did the server quick start above, you just need to make one change
to the eclipse eclipse.ini file - add the line

  -
    \-Dosee.application.server=<http://localhost:8089> to the end of the
    file.

Your installation is complete.

  - If you chose the quick server installation above, you can check to
    make sure the server is running in the command window, then start
    the OSEE client.
  - If you are using a custom database, then configure the database and
    initialize it before running the client.

## Additional Configuration Options

1.  The following instructions apply if you are setting up a custom
    database, or if you are configuring OSEE for use by multiple users.
2.  Custom Data Base: Follow the instructions at [Supported
    Databases](#Supported_Databases "wikilink") to complete this step.
3.  Initialize the database with default OSEE data. See [Database
    Initialization](#Database_Initialization "wikilink")
4.  Setup config.ini and launch eclipse to start using OSEE [Launch and
    Configuration](#Launch_.26_Configuration "wikilink")
5.  You can find different OSEE perspectives, such as Define and ATS,
    and views in correspondent dialogs, activated by menu items *Window
    \> Open Perspective \> Other...* and *Window \> Show View \>
    Other...*.

<table border="0" cellpadding="5" cellspacing="0">

<tr>

<td valign="top">

![image:open_perspective.png](/docs/images/open_perspective.png
"image:open_perspective.png")

</td>

<td valign="top">

![image:show_view.png](/docs/images/show_view.png "image:show_view.png")

</td>

</tr>

</table>

## Server Installation

1.  Download the server zip file from
    [downloads](http://www.eclipse.org/osee/downloads/).
2.  Unzip the file
3.  Set up the database (as described below)

## Supported Databases

Data created and managed by OSEE is persisted into a data store divided
into two sections. A relational database to store type, configuration,
and small content (\< 4000 bytes) and a remote file system to store
larger binary content.

Before you can use OSEE, you will need to select and install a
relational database suited for your needs and identify a file system
path for binary content storage. OSEE provides support for the databases
listed below. For launch and configuration instructions visit [Launch
and Configuration](#Launch_.26_Configuration "wikilink").

### PostgreSQL Installation

**Prerequisites**

  - Ensure you have selected the best database for your needs

**Instructions**

1.  Download PostgreSQL from <http://www.postgresql.org/download>
2.  Follow PostgreSQL installation instructions
    1.  Unless required, do not change the default port number (5432)
3.  By default, the PostgreSQL database server is configured to allow
    only local connections. If remote connections are to be allowed,
    edit postgresql.conf and pg_hba.conf to set the necessary

permissions. (To setup an unsecured database instance set
**listen_addresses = '\***' in the postgresql.conf file and add the
following line to the pg_hba.conf file: **host all all 0.0.0.0/0
trust**. You may need to set all METHODs to trust). These two files are
located in the \\PostgreSQL\\<version>\\data directory.

1.  Configure PostgreSQL for OSEE
    1.  Launch pgAdmin (in windows Start-\>All
        Programs-\>PostgreSQL\*-\>pgAdmin \*)
    2.  Double click on PostgreSQL Database Server (listed under Servers
        on the left hand side)
        1.  If you are prompted for a password type the password
            selected during installation (user should be postgres by
            default)
    3.  Create an "osee" user
        1.  Right-click on Login/Group Roles (in the tree on the left
            hand side) and select "Create -\> Login/Group Role..."
        2.  Enter the following in the dialog:
            1.  General Tab
                1.  Name: osee
            2.  Definition Tab
                1.  Password: osee
            3.  Privileges Tab
                1.  Can login? Yes
                2.  Superuser: Yes
                3.  Create roles? Yes
                4.  Create databases: Yes
                5.  Update catalog? Yes
        3.  Click 'Save'
        4.  You should now have an "osee" user under Login Roles
    4.  Expand the "Databases" item in the tree
        1.  Create the "osee" database by right-clicking on "Databases"
            and selecting "Create -\> Database..."
        2.  Enter the following in the dialog:
            1.  General Tab
                1.  Database: osee
                2.  Owner: osee
            2.  Definition Tab
                1.  Encoding: UTF-8
        3.  Click 'Save'
        4.  You should now have an "osee" Database under Databases
    5.  Click on "osee" and then expand it, then expand "Schemas"
        1.  Create the "osee" schema:
            1.  Right click on "Schemas" and select "Create -\>
                Schema..."
            2.  Enter the following in the dialog:
                1.  General Tab
                    1.  Name: osee
                    2.  Owner: osee
            3.  Click 'Save'
            4.  You should now have an "osee" schema under schemas
    6.  The relational database is now configured. Proceed to OSEE
        Database Initialization

## Launch Application Server

**Prerequisites**

  - Database has been installed

**Instructions**

  - Execute the launch script for your database and OS (if running from
    microdoc: <http://osee.microdoc.com/node/2>) - typical launch script
    names are *runPostgresqlLocal.sh* for a local Postgres on Unix and
    *runH2.sh* for H2 on Unix. It is straightforward to convert the
    downloaded scripts to Windows batch files.
      - If running PostgreSQL, be sure to add the driver bundle
        ([downloads](http://www.eclipse.org/osee/downloads/)) to the
        server installation. Unzip the files into the server
        installation and add org.postgresql.driver@start to the bundles
        in config.ini. In addition, add the
        org.postgresql.driver_<version>.jar file to the plugins
        directory in the Eclipse installation.
  - Wait until server finishes the startup procedure - **do not close
    the console**
  - To check that the server has connected successfully to the database,
    enter the command osgi\> osee server_status
  - You should see results similar to:

![<file:serverstatus.jpg>](/docs/images/serverstatus.jpg "file:serverstatus.jpg")

## Database Initialization

**Prerequisites**

Visit the 'User's Guide' if you need more information about any of the
pre-requisites below.

  - Database has been installed
  - Database server is running
  - A file system path has been selected for binary data storage. The
    system default the user's home directory.
  - An application server is running. See 'Application Server Launch'
    for more info.


***Warning: This process will delete all data from OSEE Data Store. Make
sure you are certain before running this process.***


**Instructions**



  - Ensure database connection information matches database
    installation. OSEE is pre-configured to work with a PostgreSQL
    server running on port 5432. If you need a specialized database
    connection see the 'Configuring Database Connection' section.
  - If using PostgreSQL, make sure the driver bundle has been installed
    on the client eclipse (available at
    [downloads](http://www.eclipse.org/osee/downloads/)).
      - The zip file can be dragged and dropped on the install new
        software window.
      - Uncheck the "Group items by category" for it to show up.
  - In a console from the client installation, launch the database
    initialization application by entering the following:



|                                                                                                                                                                                                                                             |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| eclipsec -nosplash -application org.eclipse.osee.framework.database.init.configClient -vmargs -XX:MaxPermSize=256m -Xmx512m -Dosee.application.server=http://localhost:8089 -Dosee.default.log=INFO -Dosee.authentication.protocol=trustAll |


\*When prompted select "Y"

  - Wait for initialization to complete

## Messaging Service

OSEE utilizes the [Java Message Service (JMS)
API](http://en.wikipedia.org/wiki/Java_Message_Service) for loosely
coupled, reliable, and asynchronous communication with OSEE clients. You
will need an implementation of this API such as
[ActiveMQ](http://activemq.apache.org) in order for clients to receive
updates to cached artifacts that were modified by another client (remote
events). Download the latest version from
[here](http://activemq.apache.org/download.html).

#### Install

General installation
[instructions](http://activemq.apache.org/getting-started.html#GettingStarted-InstallationProcedureforUnix).

If you are downloading a release of ActiveMQ from the terminal and need
to use an http proxy, this command will set up your proxy:

    $ export http_proxy=http://proxy.host.com:1234

#### Start

General instructions on how to start ActiveMQ are
[here](http://activemq.apache.org/getting-started.html#GettingStarted-StartingActiveMQ).

To direct the OSEE client to use this service, in your launcher ini
file, include the following Java system property:

    -Dosee.default.broker.uri=tcp://<localhost_or_your_server>:61616

## Launch & Configuration

Before you can launch OSEE, you will need the address of an arbitration
server or an application server to access the OSEE data store. If you
have questions regarding client/server interactions, visit
'Client/Server Overview'. If everything is on the same machine, the
following examples will work.

**Prerequisites**

  - Database has been installed
  - Database server is running
  - A file system path has been selected for binary data storage. The
    system default the user's home directory.


**Application Server Launch Instructions**

|                                                                                                                                                                                                          |
| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| java -Dorg.osgi.service.http.port=8089 -Dosee.check.tag.queue.on.startup=true -Dosee.db.connection.id=\<ConnectionId\> -jar plugins/org.eclipse.equinox.launcher_\<VersionForYourEclipse\>.jar -console |


For PostgreSQL:
{| style="background:rgb(230,230,230);color:black; border:1px solid
gray; font-family: fixedsys;text-align: left" cellpadding=20px
cellspacing=0 width=80% | runPostgresqlLocal.sh |}


**OSEE Client Launch Instructions**

|                                                                                                                                          |
| ---------------------------------------------------------------------------------------------------------------------------------------- |
| eclipse -vmargs -Xmx512m -Dosee.log.default=INFO -Dosee.application.server=http://localhost:8089 -Dosee.authentication.protocol=trustAll |

## Configure a New Project in OSEE

1.  Create baseline branch
    1.  From the Branch Manager's select parent branch -\> open context
        menu -\> select "branch" and enter new branch name
    2.  From the Branch Manager's select new branch -\> open context
        menu -\> access control
        1.  Add system administrators with Full Access
        2.  Add Everyone with Read Access

-----

# Screencasts

The following screencasts provide an effective way to quickly learn what
OSEE is and how it can be applied to your engineering project.

\* The [OSEE Demo Introduction
Screencast](http://www.eclipse.org/downloads/download.php?file=/technology/osee/1.0%20OSEE%20Demo%20Introduction%20Screencast.zip)
presents preliminary material and introduces the other available
screencasts.

  - The [OSEE
    Overview](http://www.eclipse.org/downloads/download.php?file=/technology/osee/2.0%20OSEE%20Overview%20Screencast.zip)
    provides an overview of OSEE, including the driving forces that led
    to its creation, its architecture, the maturity of the application
    framework, and the status and maturity of the different applications
    that use the framework.

<!-- end list -->

  - The [OSEE Application
    Framework](http://www.eclipse.org/downloads/download.php?file=/technology/osee/3.0%20OSEE%20Skynet%20Introduction.zip)
    screencast introduces the application framework that OSEE
    applications use to persist their shared data. It also gives an
    introduction to the generic views and editors that are available to
    all OSEE applications.

<!-- end list -->

  - The [OSEE Demo Data
    Introduction](http://www.eclipse.org/downloads/download.php?file=/technology/osee/4.0%20OSEE%20Demo%20Data%20Introduction%20Screencast.zip)
    introduces the data that is loaded into the OSEE Demo database for
    use by these screencasts. This helps the user understand how the
    data for an engineering environment fits into Application Framework
    and will help clarify the OSEE Define and OSEE ATS screencast demos.

<!-- end list -->

  - The [OSEE
    Define](http://www.eclipse.org/downloads/download.php?file=/technology/osee/5.0%20OSEE%20Define%20Introduction%20Screencats.zip)
    screencast introduces OSEE Define, the requirements and document
    management application that is built into OSEE. It will discuss the
    difference between requirements and document management and how both
    types are imported into OSEE. It will also introduce editing
    artifacts directly on the main branch, using working branches and
    show the basics of relating artifacts to each other.

<!-- end list -->

  - The [OSEE
    ATS](http://www.eclipse.org/downloads/download.php?file=/technology/osee/6.0%20OSEE%20ATS%20Introduction%20Screencast.zip)
    screencast introduces OSEE ATS, the integrated configuration
    management/change request application built into OSEE. It will
    introduce the purpose of creating an integrated change management
    system, the terms and objects used in ATS, the benefits of
    integrated processes and configured workflows in an integrated
    environment and the scenarios of creating and transitioning an
    action to completion. It will also walk through a simple
    configuration of ATS for a new product and briefly introduce the
    peer review framework that is available.

