## Before Committing Code

1.  Synchronize and Update
2.  Run Code Quality Checks
3.  Ensure all the tests are green. (Tests projects can be identified by
    their '\*.test' suffix. All tests have to be executed as plug-in
    unit tests.)
4.  Ensure the reference documentation is up to date (i.e. reflects your
    changes) (Documentation is maintained in project 'via the OSEE
    wiki').

## Code Quality Tools

Update your eclipse installation with the following tools:

  - [Find Bugs](http://findbugs.sourceforge.net/) - Used to perform
    static analysis on java code - [Find Bugs Update
    Site](http://findbugs.cs.umd.edu/eclipse)
  - [PMD](http://pmd.sourceforge.net/) - Additional static analysis and
    code duplication checks - [PMD Update
    Site](http://pmd.sourceforge.net/eclipse)
  - [Check Style](http://eclipse-cs.sf.net) - Code Style Checks - [Check
    Style Update Site](http://eclipse-cs.sf.net/update/)
  - [Eclipse Emma](http://www.eclemma.org/index.html) - Emma Coverage
    Tool Plugin For Eclipse - [Emma update
    site](http://update.eclemma.org/)

Installation for Find Bugs, PMD, Emma, and Check Style:

1.  Launch Eclipse and go to **Help-\>Software Updates-\>Available
    Software**
2.  Drag the Update Site URLs into the **Software Update and Add-ons**
    dialog
3.  Select code style tools to install - **for Find Bugs make sure you
    only select the Eclipse 3.4 or later entry**
4.  Click the **Install** button
5.  Once installation completes, restart eclipse

## Code Quality Configuration

### Import OSEE Team Preferences

1.  Right-click on the following link [**OSEE Team
    Preferences**](http://git.eclipse.org/c/osee/org.eclipse.osee.git/plain/plugins/org.eclipse.osee.support.config/osee_team_preferences.epf)
2.  Select **Save Link As**
3.  Enter **osee_team_preferences.epf** and click **Save**
4.  Launch Eclipse
5.  Select **File-\>Import**
6.  Open the **General** folder
7.  Select **Preferences**
8.  Click on the **Browse...** button, navigate to the location where
    you saved **osee_team_preferences.epf**
9.  Select **Import all**
10. Click **Finish** to import settings

### Check Style Configuration

1.  Select **Window-\>Preferences**
2.  Select **Checkstyle**
3.  Under the **General Settings** set **Rebuild projects if needed** to
    **always**
4.  Under the **Global Check Configurations**, click the **New** button
5.  Select **Remote Configuration** under the **Type** drop down
6.  Set name to **OSEE Checks (Eclipse)**
7.  Copy the following link into the **Location** entry [**OSEE Checks
    (Eclipse)**](http://git.eclipse.org/c/osee/org.eclipse.osee.git/plain/plugins/org.eclipse.osee.support.config/codeStyle/osee_check_style.xml)
8.  Set the **Cache configuration file** checkbox to true
9.  Click **OK**
10. Select the **OSEE Checks (Eclipse)** configuration and click on
    **Set as Default**
11. Click **OK** to accept settings

### Find Bugs Configuration

1.  Select **Window-\>Preferences-\>Java**
2.  Select **Find Bugs**
3.  Set **analysis effort** to **Default**
4.  Click **OK** to accept settings

### PMD Configuration

1.  Right-click on the following link [**OSEE PMD Rule
    Set**](http://git.eclipse.org/c/osee/org.eclipse.osee.git/plain/plugins/org.eclipse.osee.support.config/codeStyle/osee_pmd_rule_set.xml)
2.  Select **Save Link As**
3.  Enter **osee_pmd_rule_set.xml** and click **Save**
4.  In Eclipse, select **Window-\>Preferences**
5.  Select **PMD-\>Rules Configuration**
6.  Click on **Import rule set...**
7.  Click on **Browse**, navigate to the location where you saved
    **osee_pmd_rule_set.xml**
8.  Set the **Import by Copy** check box to true
9.  Select **OK** to import the rule set
10. Select **OK** to accept the change and close the **Preferences
    Dialog**

## Monitor OSEE Bugs using Mylyn

See [Integrating OSEE and
Bugzilla](/docs/Integrating_OSEE_and_Bugzilla.md "wikilink").

## Coding Standards

### Consistency

1.  Redundant modifiers on interface method declarations

[Chapter 9.4 of the Java Language
Specification](http://docs.oracle.com/javase/specs/jls/se7/html/jls-9.html#jls-9.4)
states: It is permitted, but discouraged as a matter of style, to
redundantly specify the public and/or abstract modifier for a method
declared in an interface.

### Utility Classes

In order to optimize reuse of code, OSEE developers have adopted a set
of standards.

Utility classes should:

1.  Be named xxxUtil. This allows for each searching and location by
    looking for \*Util. This excludes stand-alone utility classes like
    HashCollection or CountingMap.
2.  As much as possible, be located in a package postfix'd with .util.
    eg. org.eclipse.osee.ats.util
3.  Should contain static methods

#### Cleanup of existing utility methods

The following needs to be done:

1.  Create set of common utility class names
2.  Move utilities to their respective places
3.  Either deprecate or replace uses of old locations

### Comments

Most comments offer more clutter than information, especially
`non-Javadoc` comments which can be removed using the following regular
expression `\R[ \t]*/\*\s+[\* ]*`\(non-Javadoc\)`[^/]+/`.

### Regular Expression Find/Replace application of standards

  - `([^ ]+) != null && !(\1).equals`\(""\) replace with
    `Strings.isValid($1)`
  - `([^ ]+) == null \|\| (\1).equals`\(""\) replace with
    `!Strings.isValid($1)`

## REST API Documentation

Jersey and Javadoc supported tags in the code are processed during the
maven compile phase to create an enhanced application.wadl file. The
enhancements include extra documentation pulled from the source. The
following set of tags should be used so a consistant set of
documentation is available for all REST interfaces.

### JAX-RS Tags

1.  Every class implementing REST methods should use an @Path tag even
    if the paths is empty (e.g. @Path("") ).
2.  All parameters should be tagged with the appropriate @xxxParam tag
    (e.g. @PathParam, @QueryParam, etc.).
3.  The @Consumes tag should be used as appropriate for methods that
    take in data.
4.  The @Produces tag should be used as appropriate for methods
    returning information.

### Javadoc Tags

1.  Use the '@param name description' for every parameter in the method.
    The 'name' needs to match the method paramater name
2.  Use the '@return description' as appropriate

The Jersey wadl generator supports additional tags for documenting
request and response information.

1.  Use the '@response.representation.xxx.doc description' for all valid
    responses from the method
2.  Use the '@response.representation.xxx.mediaType type' when the media
    type is different than specified in @Produces

### Example

``` java
@Path("/TakeAREST")
public class TakeAREST {

    /**
     * Find a great RESTing place for your resources
     * @param place The ideal spot to look for RESTing
     * @return The best RESTing spot available
     * @response.representation.200.doc found the spot for you
         * @response.representation.503.doc RESTing places are currently unavailable.  Try again later.
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public String getPlaceToREST(@QueryParam("place") String place) {
        return "RESTing Place";
    }

    /**
     * Store a RESTing spot for others to find and use
     * @param spot Just a good spot for RESTing
     * @return Acknowledging the location of the RESTing spot
     * @response.representation.200.doc This is a good spot
     * @response.representation.404.doc Wasn't able to create the shared RESTing spot
     * @response.representation.404.mediaType plain/txt
     */
    @PUT
    @Consumes("plain/txt")
    @Produces("application/xml")
    public RestReturn createRESTingSpot(@PathParam("spot") String spot) {
        RestReturn myRet = new RestReturn();
        myRet.setName("A Great Name");
        myRet.setUuid("UniqueID");
        return myRet;
    }
```

## OSEE Master Test Suite

OSEE uses JUnit 4 for its test suites. Some links to get started:

  - [Jnit4 in 60
    seconds](http://www.cavdar.net/2008/07/21/junit-4-in-60-seconds/)
  - [Upgrading to
    JUnit4](http://stackoverflow.com/questions/264680/best-way-to-automagically-migrate-tests-from-junit-3-to-junit-4/677356)

### Use Cases

Requirements of the OSEE test suite:

1.  Single button press to run all tests
2.  Minimal number of launch configurations to maintain
3.  Ability for any user, internal or external, to easily run a suite of
    tests before commit
4.  Continuous integration (checkout, build, test, report)
5.  Use JUnit framework for all testing
6.  Enable health checks against production database to be part of test
    suite
7.  New test cases can be added easily

### Running the OSEE Test Suite

The OSEE test suite uses the `org.eclipse.osee.ats.config.demo` plugin
to initialize a demo database, populate it with demo data and run the
majority of the OSEE tests against this common data set.

These tests are contributed to the MasterTestSuite groups using
Eclipse's extension point framework. Any Test Suite can implement
IOseeTest and extend the OseeTest extension point to be contributed to
the appropriate test suite(s).

#### Steps to test

1.  Checkout org.eclipse.osee.ats.config.demo
2.  Checkout org.eclipse.osee.support.test
3.  Run the following tests in order and resolve any errors:
    1.  Run the Demo database tests:
        1.  Run the **OSEE Demo Application Server** launch config
        2.  Run the **MasterTestSuite_DemoDbInit** launch config. This
            initializes the postgres database for demo populate and
            tests
        3.  Run the **MasterTestSuite_DemoDbPopulate** launch config.
            This loads the database with branches, actions and sets
            conditions for populate tests.
        4.  Run the **MasterTestSuite_DemoDbTests** launch config. This
            runs tests against the DemoDb Populated database.
        5.  Stop the **OSEE Demo Application Server** if still running
    2.  Run the Production TestDb tests:
        1.  Run the **OSEE Application Server** launch config.
        2.  Run the **MasterTestSuite_TestDbInit** launch config. This
            initializes the postgres database for production testdb
            tests
        3.  Run the **MasterTestSuite_TestDbTests** launch config. -
            This runs production specific tests using a TestDb.
    3.  Run the Production tests and health checks against the current
        production release
        1.  Run the **MasterTestSuite_ProductionTests** launch config.
            This runs tests and health checks against the current
            production released database.

### Common test utility plugin

The plugin org.eclipse.osee.support.test.util is in support of our
testing framework. It has a number of common enums and a TestUtil class
that should be used by any junit tests. The intent is to keep this
plugin lightweight and without many dependencies cause all the testing
fragments "should" include it. In addition, it should not be included by
any production plugins, only test fragments.

### Adding new Tests to MasterTestSuite

### To create test fragment off plugin to be tested

All OSEE JUnit tests should live in a fragment of the plugin to be
tested.

1.  Select plugin to be tested
2.  Right-click -\> New Project -\> Plugin Project -\> Plugin Fragment
3.  Enter plugin to be tested as Host plugin
4.  Complete wizard
5.  In plugin to be tested (eg org.eclipse.osee.ats)
    1.  Add "Eclipse-ExtensibleAPI: true" to MANIFEST.MF of plugin to be
        tested. This allows test suites to see tests in this plugin
6.  In new fragment (eg org.eclipse.osee.ats.test)
    1.  Add org.junit4 (make sure junit4, not junit) to dependencies
    2.  Add the common test utility plugin
        org.eclipse.osee.support.test.util plugin to dependencies
    3.  Export packages containing TestCases and TestSuites
7.  In MasterTestSuite plugin (eg org.eclipse.osee.support.test)
    1.  Add dependency on plugin to be tested (ge org.eclipse.osee.ats)
    2.  Add test cases and suites from fragment to appropriate
        MasterTestSuite java Test Suites

### To add a new JUnit TestCase

1.  Write the TestCase to run against a database populated with
    DemoDbInit and DemoDbPopulate
2.  Add the TestCase to an existing MasterTestSuite_DemoDbTests test
    suite

### Things to consider

1.  Your tests must clean-up after themselves to ensure that the entire
    test suite can be run.
2.  Do not assume order of execution except within your own Test Suite

### Master Test Suite - Frequently Asked Questions

#### What do I need for every test case?

  - import static org.junit.Assert.\*;
  - In setup method (@Before), always assert that you are on correct
    database by adding one of these

<!-- end list -->

``` sql
assertTrue("Should be run on production datbase.", TestUtil.isProductionDb());
assertTrue("Should be run on test datbase.", TestUtil.isTestDb());
assertTrue("Should be run on demo datbase.", TestUtil.isDemoDb());
```

  - In setup method (@Before), always assert that you have correct app
    server running \*

<!-- end list -->

``` sql
assertTrue("Demo Application Server must be running.",
  ClientSessionManager.getAuthenticationProtocols().contains("demo"));
assertTrue("Client must authenticate using demo protocol",
   ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));

assertFalse("Application Server must be running.",
   ClientSessionManager.getAuthenticationProtocols().contains("demo"));
assertFalse("Client can't authenticate using demo protocol",
   ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
}
```

#### Why do I get an exception on synchronized-lock when I launch the tests

The launch configuration does not have all plugins necessary to run.

1.  Open Debug Configurations for that launch item
2.  Go to plugins tab and select "Validate Plugins". This will show if
    any plugins are missing from selected items.
3.  Select "Add Required Plugins" to add them.
4.  Re-launch.

#### Why do I get exceptions that cycle was detected when I add things to the org.eclipse.osee.test.util plugin

This plugin is meant to be extremely light-weight and only provide
simple statics and methods to all the test plugins. Because it has to be
included in all test fragments from jdk.core all the way up to
framework.ui (skynet.ui), it can not depend on any of the higher level
plugins or cycles will occur. Remove these dependencies and cycles will
be fixed.

#### How do I timeout a test?

Define a timeout period in miliseconds with “timeout” parameter. The
test fails when the timeout period exceeds. view

``` java
@Test(timeout = 1000)
public void infinity() {
    while (true);
}
```

#### How do I test exception handling

Exception Handling Use “expected” paramater with @Test annotation for
test cases that expect exception. Write the class name of the exception
that will be thrown. view plainprint?

``` java
@Test(expected = ArithmeticException.class)
public void divisionWithException() {
   // divide by zero
   simpleMath.divide(1, 0);
}
```

### To add a new JUnit TestSuite

1.  Create a fragment for the plugin (see above)
2.  Create new JUnit TestCases as above
3.  Create a new JUnit TestSuite
4.  Add new TestSuite to one of the MasterTestSuite java test suites

### Common test cases

``` java
/*
    COMPANY DISTRIBUTION STATEMENT
*/

package com.company.component.testcase;

import
...
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @link {link_to_class_under_test}
 * @author {you}
 */
public final class MySuperbTest {

   private static SevereLoggingMonitor monitorLog = null;
   private static Artifact myRootArtifact = null;

   @Test
   public void importSimpleArtifacts() throws Exception {
      Assert.assertTrue("Some message...", conditionToBeTrue);
   }

   @Before
   public void setUp() throws Exception {
      /* some pre init stuff before each case */
   }

   @After
   public void tearDown() throws Exception {
      /* useful to destroy any artifacts after your test */
      new PurgeArtifacts(myRootArtifact.getChildren()).execute();
      /* example how you would wrap one element to pass in */
      new PurgeArtifacts(Collections.singletonList(myRootArtifact)).execute();
   }

   @BeforeClass
   public static void setUpOnce() throws Exception {
      monitorLog = TestUtil.severeLoggingStart();
   }

   @AfterClass
   public static void tearDownOnce() throws Exception {
      TestUtil.severeLoggingEnd(monitorLog);
      /* any other final cleanup */
   }
}
```

## OSGi

  - OSGi is a set of specifications that define a dynamic component
    system for Java where an application is composed of several
    components which are packaged in bundles. Components communicate
    locally and across the network through services.
  - Singleton bundles

<!-- end list -->

  -
    A singleton bundle is used to prevent any other version of a bundle
    being loaded in memory, there can be only one resolved version in
    the run time at any point. The use of a singleton bundle can be
    desirable where access to a single system resource is required from
    a set of applications.
    <https://www.ibm.com/support/knowledgecenter/en/SSGMCP_5.5.0/applications/developing/java/dfhpj_osgiframework.html>.
    Use this option for test fragments.

## GUI using RAP

  - The term "RAP client" is used to identify the software that displays
    the actual UI of a RAP application and communicates with the RAP
    server using the RAP Protocol.
  - The RAP Protocol is JSON-based message format that fully decouples
    the RAP server and client with the intention of making the client
    exchangeable.
  - The default RAP client is written in JavaScript and is downloaded
    and started automatically when a browser accesses the URL of a RAP
    application.

[Developer's Guide for
RAP 2.0](http://eclipse.org/rap/developers-guide/)

## Java Development

### Learning Java

[Install Eclipse as your Development
Environment](https://www.eclipse.org/downloads/)

From the Creator of Java: Trails Covering the Basics

  - [Learning the Java Language — Lessons describing the essential
    concepts and features of the Java Programming
    Language](http://docs.oracle.com/javase/tutorial/java/)
  - [Essential Java Classes — Lessons on exceptions, basic input/output,
    concurrency, regular
    expressions](http://docs.oracle.com/javase/tutorial/essential/)
  - [Collections — Lessons on using and extending the Java Collections
    Framework](http://docs.oracle.com/javase/tutorial/collections/)

Excellent Sources

  - Head First Java, 2nd Edition (available in Safari and from Amazon)
  - [Stack Overflow for answers to specific
    questions](http://stackoverflow.com/)
  - [Simple v.s.
    Easy](http://www.infoq.com/presentations/Simple-Made-Easy)

Two alternative learning sources:

  - <http://www.greenteapress.com/thinkapjava/>
  - <http://math.hws.edu/javanotes/>

### Threading

  - Collections.synchronizedMap still requires manual synchronization on
    the returned map when iterating over any of its collection views.
  - Eclipse -\> Debug Perspective -\> Debug view (stacktrace view) -\>
    white downward arrow menu -\> Java -\> Show Monitors

<!-- end list -->

  -
    Eclipse can detect deadlocks and gives locking information needed to
    determine the root cause

<!-- end list -->

  - When using a HashMap with more than one thread, use
    java.util.concurrent.ConcurrentHashMap instead

## Unix Commands

``` bash
find {filename} -type f -exec chown {new_owner} {} \; -exec chmod 664 {} \;
du -hs *
tail -f /var/tmp/my.log
netstat -an
vi find and replace: s/pattern1/pattern2/g
find . -exec grep -l pattern {} \;
finger -wpsf {user}
```

## SQL Examples

### How to create a new table on OSEE production database

``` sql
GRANT DELETE ON "OSEE"."{table_name}" TO "OSEE_CLIENT";
GRANT INSERT ON "OSEE"."{table_name}" TO "OSEE_CLIENT";
GRANT SELECT ON "OSEE"."{table_name}" TO "OSEE_CLIENT";
GRANT SELECT ON "OSEE"."{table_name}" TO PUBLIC;
GRANT UPDATE ON "OSEE"."{table_name}" TO "OSEE_CLIENT";

create public synonym {synonym_name} for {table_name} ;
```

``` sql
-- create a trigger that runs each time rows are deleted from osee_server_lookup

create or replace trigger osee_server_lookup_brd
before delete on osee_server_lookup

for each row
   begin
      insert into find_user( user_name, chg_date) values (user, sysdate );
   end;

-- create a synonym in the osee_client scheme to the table osee_enum_type_def owned by scheme osee and then give osee_client privileges to actually use it
CREATE OR REPLACE SYNONYM osee_client.osee_enum_type_def FOR osee.osee_enum_type_def;
grant select, update, insert on osee_enum_type_def to osee_client;

-- an update statement that involves another table
UPDATE osee_attribute_type aty SET enum_type_id = (select et.enum_type_id from osee_enum_type et where aty.name = et.enum_type_name) where validity_xml is not null;

-- retrieve duplicate HRIDS, its GUID and Artifact Type name:
SELECT t1.guid,
  t1.human_readable_id,
  t3.name
FROM osee_artifact t1,
  osee_artifact_type t3
WHERE t1.human_readable_id IN
  (SELECT t2.human_readable_id
   FROM osee_artifact t2
   GROUP BY t2.human_readable_id HAVING COUNT(t2.human_readable_id) > 1)
AND t3.art_type_id = t1.art_type_id
ORDER BY t1.human_readable_id;

-- retrieve the number of attributes with the specified value:
-- (Note: COUNT function returns the number of rows in a query, COUNT(1) is for better performance;
-- In the below example, the COUNT function does not need to retrieve all fields from the osee attribute table
-- as it would if you used the COUNT(*) syntax. It will merely retrieve the numeric value of 1 for each record
-- that meets your criteria)
SELECT count(1) from osee_attribute where value like ?, where ? == '%{value}%'

-- retrieve the number of commit comments with the specified value:
SELECT count(1) from osee_tx_details where osee_comment like ?, where ? == '%value>%'

-- retrieve the number of branch names with the specified value:
SELECT count(1) from osee_branch where branch_name like ?, where ? == '%value>%'

-- retrieve all data from the specified tables on a specific artifact
SELECT *
FROM osee_artifact_version arv,
  osee_txs txs,
  osee_tx_details txd
WHERE art_id = {value}
 AND arv.gamma_id = txs.gamma_id
 AND txs.transaction_id = txd.transaction_id

-- retrieve all data from the osee artifact table for the specified artifact
SELECT * from osee_artifact where art_id={value}

-- list execution plans
select distinct 'explain plan set statement_id = ' || sql_id || ' for ' || sql_text || ';' as ex from v$sql where lower(sql_text) like 'select%osee_relation_link%' and sql_text not like '%DS_SVC%' and sql_text not like '%SYS.DUAL%' and parsing_schema_name = 'OSEE_CLIENT' order by ex;
select * from plan_table;
```

## Release Engineering

### Posting OSEE downloads to Eclipse.org

  - Download release binaries

:\* on Hudson goto the following job

:\*\* <https://hudson.eclipse.org/osee/job/osee_nr/105/parameters/>

:\*\* Enable the job

:\*\* Goto Configure and update the 'version' parameter (e.g. 0.25.1 or
0.26.0, etc)

:\*\* Save the configuration change

:\*\* Execute the job

:\*\* Do the same for the posting job
<https://hudson.eclipse.org/osee/job/osee_promote_latest_to_download_site/>

  - Update OSEE website to link to new version

:\* use the Git repository
<https://git.eclipse.org/r/www.eclipse.org/osee.git>

:\* update release version in sharedEnv.php

### Rebaselining a development branch onto master

` Note: Replace all references to /c/x/git/x with your git reop's parent folder`

1.  prerequisite: master is pointing to tip of most recent next release
    branch otherwise replace references to master with it below
      -
        git pull --rebase
        git push origin head:master
2.  Announce on mailing list commit freeze for source and destination
    branches

<!-- end list -->

1.  Start rebase using onto
    The following steps apply to org.eclipse.osee and any repositories
    that depend on it
      -
        To see the work layout before you using Git version 2.19.0 or
        newer
          -
            git range-diff origin/{previous_stable_version} origin/dev
            dev
        ensure any Eclipse workspace associated with the repository is
        closed
        cd /c/x/git/x/org.eclipse.osee; git fetch; git checkout dev
        git reset --hard origin/dev
        remove any untracked files
        git rebase -i --onto origin/master
        {one commit prior to first commit on dev}
        mark for edit the first commit "refactor: Update build numbers
        to 0.x.0

<!-- end list -->

1.  Resolve merge conflict of version numbers
      -
        git reset --hard
        Edit update_versions.sh to update versions
          -
            vi
            /c/x/git/x/org.eclipse.osee/plugins/org.eclipse.osee.support.dev/update_versions.sh
        Run update_versions.sh from repository root
          -
            /c/x/git/x/org.eclipse.osee/plugins/org.eclipse.osee.support.dev/update_versions.sh

<!-- end list -->

1.  methodically reapply commits
      -
        git rebase --edit-todo
        Select specific commits to edit (aligning with other
        repositories as needed)
        confirm no compile errors at each selected commit and every few
        commits run integration tests
        when a compile error or test failure is detected, determine root
        cause commit and create new FIXUP commit
        git status to check for uncommitted files (one possible cause is
        a deleted bundle)
        use Import Projects... from Git perspective in Eclipse to import
        newly added bundles

<!-- end list -->

1.  Cleanup fixup commits
      -
        note current hash of head
        use git rebase -i for second pass of rebase
        move each FIXUP commit to just after the commit it corrects and
        combine with fixup command
        git diff {previously_noted_hash} head to ensure no unintentional
        changes

<!-- end list -->

1.  Ensure development branch still builds
    1.  Ensure no compile errors in Eclipse workspace
          -
            Launch OSEE dev_alpha install with workspace
            E:\\workspace_nr
            refresh, rebuild, restart
    2.  Ensure full Jenkins build succeeds
          -
            git push origin head:user/rebase -f
            build on topic branch
            <https://hudson.eclipse.org/osee/job/osee_topic_2>
            Wipe Out Workspace first to avoid odd issues building
2.  git rebase --committer-date-is-author-date
3.  git fetch to ensure the commit freeze was not violated
4.  git push origin head:dev -f

### Managing Orbit Bundles

  - <http://wiki.eclipse.org/Orbit_Builds>
  - <http://wiki.eclipse.org/Orbit_Builds#Orbit_Builds_for_Orbit_Committers>
  - <http://wiki.eclipse.org/Adding_Bundles_to_Orbit>
  - <http://wiki.eclipse.org/Orbit_Bundle_Checklist>
  - <http://build.eclipse.org:9777/dashboard/tab/builds>

<!-- end list -->

  -
    builds -\> orbit-I -\> click the circular arrow (force build)

<!-- end list -->

  - <http://download.eclipse.org/tools/orbit/committers/>

<!-- end list -->

  - New -\> Other... -\> Plug-in Project

:\* check "Create a java project"

:\* "an osgi Framework": Equinox

:\* no activator

:\* Bundle Id: {my.plugin}

:\* Bundle-Version: 10.5.1.1_qualifier

:\* Name: %bundleName

:\* Provider: %bundleProvider

  - delete .settings folder
  - create folder {my.plugin}/source-bundle
  - create folder {my.plugin}/source-bundle/META-INF
  - create file {my.plugin}/source-bundle/META-INF/MANIFEST.MF

:\* Bundle Id: {my.plugin}.source

:\* Eclipse-SourceBundle: {my.plugin}

  - create file {my.plugin}/readme.txt
  - copy plugin.properties to {my.plugin}/plugin.properties

:\* update bundleName

  - copy {my.plugin}/plugin.properties to
    {my.plugin}/source-bundle/plugin.properties
  - copy about.html to {my.plugin}/about.html
  - create folder {my.plugin}/about_files

:\* copy licensing text files here

  - extract content of binary jar file into root of {my.plugin} (do
    not over-write MANIFEST.MF)

:\* select project in workspace -\> context menu -\> Refresh

  - Manifest editor -\> Build tab

:\* check {my.plugin}/about.html under both your Binary Build and
Source Build

:\* check {my.plugin}/about_files under both your Binary Build and
Source Build

:\* check {my.plugin}/plugin.properties

:\* check {my.plugin}/{class folder}

  - copy {my.plugin}/about.html to
    {my.plugin}/source-bundle/about.html
  - copy {my.plugin}/about_files to
    {my.plugin}/source-bundle/about_files

<!-- end list -->

  - context menu on {my.plugin} -\> Properties -\> Java Build Path

:\* Source Tab -\> remove all source folders

:\* Libraries -\> Add class Folder ... -\> select root project

::\* expand newly added class folder and select source attachment -\>
Edit... -\> Workspace... -\> /{my.plugin}/source-bundle

:\* Order and Export -\> ensure libraries and class folders are marked
exported

  - edit {my.plugin}/.project

:\* add a space and then the 3 part version number to the project name

  - edit {my.plugin}/build.properties

:\* ensure exists: output.. = . and remove source.. = .

  - edit {my.plugin}/META-INF/MANIFEST.MF:

:\* add line: Bundle-Localization: plugin

  - copy source to {my.plugin}/source-bundle/
  - copy {my.plugin}/build.properties to
    {my.plugin}/source-bundle/build.properties

:\* add to bin.includes the root directory of the source (i.e. org, com,
etc.)

:\* Note: the source bundle's build.properties entries must be relative
to {my.plugin}/source-bundle/ (rather than just {my.plugin}/)

  - context menu on {my.plugin} -\> Team -\> Share Project... -\> CVS
    -\> "dev.eclipse.org/cvsroot/tools"

:\* Use specified module name -\> org.eclipse.orbit/{my.plugin}

:\* uncheck Launch the Commit wizard

  - context menu on {my.plugin}/readme.txt and {my.plugin}/.project
    -\> Team -\> Commit... -\> "initial project creation"

<!-- end list -->

  - select project in workspace -\> context menu -\> Team \> Branch...

:\* "v{version number}" where {version number} original libraries
version with '.' s replaced with '_' . (i.e. version is 2.3 would go in
the v2_3 branch)

:\* check "Work with this branch"

  - delete {my.plugin}/readme.txt

<!-- end list -->

  - CVS check out org.eclipse.orbit/org.eclipse.orbit.build.feature.set1
  - edit /org.eclipse.orbit.build.feature.set1/feature.xml

:\* add plugin

:\* set download-size and install-size

  - CVS check out org.eclipse.orbit/org.eclipse.orbit.releng
  - edit /org.eclipse.orbit.releng/maps/bundles.map

:\*
plugin@org.apache.derby.net,10.5.1=CVS,tag=v200910011000,cvsRoot=:pserver:anonymous@dev.eclipse.org:/cvsroot/tools,path=org.eclipse.orbit/org.apache.derby.net

:\*
plugin@org.apache.derby.net.source,10.5.1=CVS,tag=v200910011000,cvsRoot=:pserver:anonymous@dev.eclipse.org:/cvsroot/tools,path=org.eclipse.orbit/org.apache.derby.net/source-bundle

:\* if you edited /org.eclipse.orbit.build.feature.set1/feature.xml,
then update the map file's tag also (so the updated version will be
used)

  - context menu on {my.plugin} -\> Team -\> Commit...
  - context menu on {my.plugin} -\> Team -\> Tag as Version... use the
    CVS tag you put in the map file
  - context menu on org.eclipse.orbit.build.feature.set1 -\> Team -\>
    Tag as Version... (if you changed it)
  - edit /org.eclipse.orbit.releng/psf/orbit.psf
  - Update the IP Log

### Windows Setup

    Win + E > File > Change folder and search options > View > Advanced Settings:
        Show hidden files, folders, and drives
        !Hide extensions for known file types
    Win > Taskbar settings > Taskbar -> Use small taskbar buttons
    Win > Power & sleep settings > Sleep
        On battery power, PC goes to sleep after > 1 hour
        When plugged in, PC goes to sleep after > Never
    Win > Edit Power Plan > Change Advanced power settings > Sleep > Hibernate after > On battery > Never

    Control Panel
        View by: Small icons
        Power Options
            Choose what closing the lid does > When I close the lid: > Do Nothing

    Win > Control Panel  > System and Security > System > Advanced system settings > Advanced -> Performance -> Settings -> Advanced -> Virtual Memory -> Change -> No Page File -> Set
    Win > Defragment and Optimize Drives > Optimize > Change settings > !Run on a schedule

    Win > Remote Desktop settings > Enable Remote Desktop

[**Integrating OSEE and
Bugzilla**](/docs/Integrating_OSEE_and_Bugzilla.md "wikilink")

