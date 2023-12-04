This guide will show you how to run the OTE client and server uwing a
runtime workbench. Runtime workbenches provide an excellent development
environment where you can write and run your code as quickly as
possible. It is highly recommended when making changes to the test
environment that you use a runtime workbench for testing first.

## Requirements

  - RAM: Highly recommend at least 8GB of RAM
  - HDD: At least 4 GBs
  - OS: Windows or Linux are natively built. If you use Mac you can
    update the Maven build to create a Mac distribution.
  - JVM: Currently our dev line still depends on Java 8. We are also
    working on a feature branch at mmasterso/java11 if you wish to use a
    newer version of java.

## Install the OTE IDE

It is recommended to use the OTE or OSEE IDE to develop the OTE product.
The latest dev build can be found in Jenkins here:

[| Simple
IDE](https://ci.eclipse.org/osee/view/OTE_DEV/job/org.eclipse.ote.simple.oteide.product__DEV/lastSuccessfulBuild/artifact/org.eclipse.ote/org.eclipse.ote.simple.oteide.product/target/products)

Simply download and unzip the appropriate product into a new folder.

## Clone OTE source

It is usually easiest to import both the OSEE and OTE repositories:

  - OSEE: <https://git.eclipse.org/r/admin/repos/osee/org.eclipse.osee>
  - OTE: <https://git.eclipse.org/r/admin/repos/osee/org.eclipse.ote>

In general, there will be a master branch in each repository
representing the last release baseline and a dev branch representing the
latest unreleased version of the code.

## Import into the OTE IDE

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

<li>

From the top menu select File-\>import...

</li>

<li>

Select General-\>"Existing Projects into Workspace"

</li>

<li>

Hit the Browse button and find the root folder where you cloned both
org.eclipse.ote and org.eclipse.osee

</li>

<li>

Once selected, there should be many projects automatically selected
under "Projects"

</li>

1.  ![Image:ImportProjects.png](ImportProjects.png
    "Image:ImportProjects.png")

<li>

If not selection, click the "Select All" button

</li>

<li>

Hit the Finish button and wait for the workspace to populate and compile
completely

</li>

</ol>

## Running the OTE Test Server using runtime workbench

1.  The launch product is located in the org.eclipse.ote.simple.feature
    plugin
2.  To launch the test server, right-click on the
    ote.simple.test.server.product.launch file and select "Debug
    As"-\>ote.simple.test.server.product
3.  The server will start printing to the eclipse console. You may see
    some errors regarding master rest client that are not impactful
4.  You should be able to connect and start using the test server as
    normal ([see instructions
    here](/docs/OSEE/OTE/Users_Guide/Getting_Started.md#Start_the_OTE_Test_Server "wikilink"))

When running the test server in the debugger you are still able to
change a lot of code and eclipse debugger will hot-swap that code. This
makes developmento of server side changes quick and easy to execute.

Some cases where you will get a swap error editing source are:

  - Changing any class level elements like fields, constants, method
    signatures, etc
  - Changing imports
  - Causing compile errors

If you are only testing server-side code updates, you can connect to
this server straight from this development workspace. If you also have
some client side changes you can continue to the next section to also
launch the OTE IDE.

## Running the OTE IDE Client using runtime workbench

If you are using a runtime workbench OTE IDE, you should be using a
runtime workbench OTE Test Server as well.

1.  The launch product is located in the
    org.eclipse.ote.simple.oteide.product plugin
2.  To launch the test server, right-click on the ote.ide.product.launch
    file and select "Debug As"-\>ote.ide.product
3.  The IDE may start may start in the background, underneath your dev
    workspace/li\>
4.  If this is your first time running, an empty workspace will be
    presented and you can [follow the directions
    here](/docs/OSEE/OTE/Users_Guide/Getting_Started.md "wikilink") to import the
    test script plugin, connect to the test server, and run the simple
    test

# Documentation