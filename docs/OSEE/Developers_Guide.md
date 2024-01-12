## Architecture

### Client Architecture

The central feature of OSEE is an extensible framework called the OSEE
Application Framework. Default applications distributed with the OSEE
framework are OSEE Define (for requirements management) and OSEE ATS
(the Action Tracking System, for configuration management).

![osee architecture](/docs/images/oseearchitecture.gif "osee architecture")The
Application Framework provides all the necessary services to allow the
applications to persist and share data in a common, version controlled
object database. Just as Eclipse provides the ability to add a plugin to
the existing Eclipse environment, so OSEE allows other applications to
add plugins and share the common data store.

And just like Eclipse RCP allows an application to be built and deployed
using the Eclipse framework but not include all the standard
applications like JDT, OSEE allows an application to be built and
deployed using the OSEE Application Framework without including such
applications as OSEE Define and OSEE ATS.

### Client/Server Architecture

In order to attain a greater degree of scalability, the Open System
Engineering Environment (OSEE) has been slowly migrating into a
distributed architecture where clients interact with an application
server, which is in charge of managing access to an OSEE data store.

Additionally, in an effort to provide load balancing, failure recovery,
and code compatibility, clients consult an arbitration server before
connecting to an application server. The arbitration server's
responsibility is to keep track of all the application servers
interacting with a common data store and direct clients to a healthy
application server compatible with the client's OSEE code version. In
this arrangement, arbitration servers act as the initial access points
into the OSEE server cloud where a collection of application servers
manage client requests to access and operate on a common OSEE data
store. Figure 1 shows an example of the OSEE Client/Server network.

![figure 1](/docs/images/client_server_view.png "figure 1")


In Figure 1, three application servers interact with a single OSEE data
store. The data store is comprised of a relational database and a remote
file system used to store binary data. It is not necessary for the
database and the binary data to exist on the same machine; the only
requirement is that the application servers have access to both
resources. Upon start-up, each application server registers himself on
the data store's server lookup table by entering its host address, port,
supported code versions, and its unique id. When the arbitration server
receives a request to find an application server to support a client
connection, the arbitration server reads the data store's server lookup
table and selects the best match for the client. The client requests
this information from the arbitration server upon start-up or whenever
it can't communicate with an application server. It is important to note
that the arbitration server does not have to be a different server than
an application server. All application servers are able to act as an
arbitration server. An application server is referred to as an
arbitration server when clients interact with it in this context. Figure
2 depicts the sequence of events involved in the arbitration process.

![figure 2](/docs/images/arbitration_sequence.png "figure 2")

Once a client receives an application server's address and port
information, the client must authenticate with the application server
before it can gain access to the OSEE data store. During the
authentication process, a client submits to the application server the
current user's credential information and the authentication protocol id
to use during the process. The application server verifies the user via
the selected protocol and grants access to the data store by creating a
session for the user. From this point forward, the application server
will be responsible for managing access to the data store by identifying
the user via the session id. Whenever a client wants to interact with
the application server, it will need to submit its session id in order
to gain access to the OSEE data store. Figure 3 shows the sequence of
events involved in the authentication process.

![figure 3](/docs/images/authentication_sequence.png "figure 3")

### Data Model

The OSEE framework is built around a user configurable and extensible
data model consisting of attributes, artifacts, and relations. An
**attribute** is a key-value pair representing a single data element
such as a description, a date, a number, or a file. These basic data
elements are grouped into artifacts. **Artifacts** can be configured to
have any number of attributes. By default, an artifact will always have
an attribute of type `name`. In addition, artifacts can be related to
one another via **relations**. By default, an artifact will always have
a default hierarchy relation type. This allows artifacts to be connected
together in a tree. In the example below, two instances of the basic
artifact type are shown. Artifact 1 has an attribute of type `name` set
to string data `"X"`. Artifact 2 has an attribute of type `name` set to
string data `"Y"`. These two artifact instances are related via the
default hierarchy relation type. Artifact 1 is Artifact 2's parent
artifact.

![image:basic artifact.png](/docs/images/basic_artifact.png "image:basic artifact.png")

Now that we have a basic understanding of the model, lets take a closer
look at attributes and how they are defined.

An attribute is defined through its attribute type. The attribute type
is a blue print for constructing attribute instances. It defines the
type of data the will be held by the attribute, the data source or who
provides it, how many instances can be created, default value to use
during creation, whether the attribute can be tagged for word searches,
and if the attribute holds file data, its file extension.

By default, data contained in the attribute can be represented through
OSEE's basic data types:

  - String Attribute
  - Boolean Attribute
  - Integer Attribute
  - Floating Point Attribute
  - Date Attribute
  - Enumerated Attribute

![image:attributetype.png](/docs/images/attributetype.png "image:attributetype.png")

OSEE provides three attribute data providers: the default attribute data
provider, URI attribute data provider, and the Clob attribute data
provider.

  - The **default attribute data provider** is used for data containing
    less than 4000 characters in length. Data is stored and retrieved
    from the OSEE relational database. Most attribute types will use
    this data provider to handle its data content.
  - The **URI attribute data provider** is used for large data. The
    provider communicates to the OSEE application server to store and
    retrieve data.
  - The **Clob attribute data provider** is a hybrid provider using both
    the OSEE relational database and the application server to retrieve
    and store data. When the data contained by the attribute has less
    than 4000 characters, the provider uses the relational database. If
    the data exceeds the 4000 character limit, then the application
    server is used.

## Configuration Properties

OSEE can be configured by setting certain Java system properties when
launching Eclipse and by setting various attribute values on the Global
Preferences artifact in the datastore. Java system properties are
key/value pairs and can be passed as launch arguments in the form of
`-D`{key}`=`{value} (*i.e. `-Dosee.authentication.protocol=trustAll`*).
These `-D` options can be specified directly in the command to launch
Eclipse or in the corresponding .ini file for the eclipse executable
used. Server-side OSGI properties are specified in an JSON file
referenced by the system property cm.config.uri.

### Server OSGi properties

See the file
org.eclipse.osee.support.config/launchConfig/osee.postgresql.json for an
example.

JdbcComponentFactory receives its OSGi properties from the JSON file
referenced by the system property cm.config.uri.
JdbcConnectionFactoryManager.getConnection() uses the JDK's
DriverManager.getConnection() which in turn uses the Java Standard
Edition Service Provider mechanism to load the JDBC driver referenced in
the JSON file. The JDBC driver must include the file
META-INF/services/java.sql.Driver which contains the name of the JDBC
driver implementation of java.sql.Driver.

### Datastore Preferences (via Global Preferences artifact)

Do a Quick Search on the Common branch for "Global Preferences" and open
the resulting artifact in the artifact editor. The available attribute
types for this artifact define what can be configured. Each attribute is
self-documenting, because the attribute tip text documents how to use
each one.

### Common Java System Properties

| System Property Name           | Values                        | Default                         | Description                                                                                                                                                                                                                                                      |
| ------------------------------ | ----------------------------- | ------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| osee.connection.info.uri       | `[FILE SYSTEM PATH]`          |                                 | File system path or uri containing custom database connection information.                                                                                                                                                                                       |
| osee.db.connection.id          | {db identifier}               | Default from db.connection file | Specifies which database OSEE should connect to. This id references connection information specified in the ...db.connection.xml file. Refer to the [Database Connection Information](#Database_Connection_Information "wikilink") section for more information. |
| osee.jini.forced.reggie.search | `true, false`                 | `false`                         | If true, adds the lookupList to the global lookup list such that a refresh will try to locate the service again                                                                                                                                                  |
| osee.jini.lookup.groups        | user defined group name       |                                 | the Jini Group that all OSEE provided Jini services will register with                                                                                                                                                                                           |
| osee.log.default               | `FINE, INFO, WARNING, SEVERE` | `WARNING`                       | the default logging level for all loggers                                                                                                                                                                                                                        |
| osee.port.scanner.start.port   | `1 - 65535`                   | `18000`                         | the first port number to test for availability when a new port is needed                                                                                                                                                                                         |

### Application Server Java System Properties

<table>
<thead>
<tr class="header">
<th><p>System Property Name</p></th>
<th><p>Values</p></th>
<th><p>Default</p></th>
<th><p>Description</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><p>org.osgi.service.http.port</p></td>
<td><p><code>1 - 65535</code></p></td>
<td><p><code>-1</code></p></td>
<td><p>Port the client will use to communicate with the OSEE Application Server</p></td>
</tr>
<tr class="even">
<td><p>osee.application.server.data</p></td>
<td><p><code>[FILE SYSTEM PATH]</code></p></td>
<td><p>User Home</p></td>
<td><p>A directory on the file system to be used by the application server to store and serve artifact binary data.</p></td>
</tr>
<tr class="odd">
<td><p>osee.db.embedded.server</p></td>
<td><p><code>[{address}:{port}]</code></p></td>
<td></td>
<td><p>When specified, this system property sets the URL used to launch an embedded database server.</p></td>
</tr>
<tr class="even">
<td><p>osee.version</p></td>
<td><p><code>[&lt;"version 1"&gt;;&lt;"version 2"&gt;]</code></p></td>
<td></td>
<td><p>When specified, this system property sets the application server's supported client versions. NOTE: version string can use regular expressions</p></td>
</tr>
<tr class="odd">
<td><p>osee.check.tag.queue.on.startup</p></td>
<td><p><code>true, false</code></p></td>
<td><p>false</p></td>
<td><p>When specified, this system property allows the application server to check the tag queue and begin tagging and pending tag jobs.</p></td>
</tr>
</tbody>
</table>

### Client Java System Properties

<table>
<thead>
<tr class="header">
<th><p>System Property Name</p></th>
<th><p>Values</p></th>
<th><p>Default</p></th>
<th><p>Description</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><p>osee.application.server</p></td>
<td><p><code>[<a href="http://">http://</a>{address}:{port}]</code></p></td>
<td></td>
<td><p>When specified, this system property sets the URL used to reference the application server and arbitration is bypassed.</p></td>
</tr>
<tr class="even">
<td><p>osee.arbitration.server</p></td>
<td><p><code>[<a href="http://">http://</a>{address}:{port}]</code></p></td>
<td></td>
<td><p>The arbitration server address and port to use. This system property must be specified for the system to gain access to OSEE data. If the application server property is set then that address takes precedence and arbitration is bypassed.</p></td>
</tr>
<tr class="odd">
<td><p>osee.authentication.protocol</p></td>
<td><p>protocol name</p></td>
<td></td>
<td><p>protocol to be used by the client to authenticate with the server</p></td>
</tr>
<tr class="even">
<td><p>osee.choice.on.db.init</p></td>
<td><p>choice name</p></td>
<td></td>
<td><p>the predefined database initialization choice</p></td>
</tr>
<tr class="odd">
<td><p>osee.file.specified.schema.names.on.db.init</p></td>
<td><p><code>true, false</code></p></td>
<td><p><code>false</code></p></td>
<td><p>Specifies whether OSEE database initialization should use the schema names specified in the schema.xml files instead of using the connection schema. Using the connection specified schema is the default behavior.</p></td>
</tr>
<tr class="even">
<td><p>osee.import.from.connection.id.on.db.init</p></td>
<td><p><code>[FILE SYSTEM PATH]</code></p></td>
<td></td>
<td><p>Specifies where table data should be imported from during OSEE database initialization. The default is to use the database connection id specified in the schema.xml files.</p></td>
</tr>
<tr class="odd">
<td><p>osee.import.on.db.init</p></td>
<td><p><code>true, false</code></p></td>
<td><p><code>false</code></p></td>
<td><p>Specifies whether OSEE database initialization should import database data as part of its tasks.</p></td>
</tr>
<tr class="even">
<td><p>osee.local.application.server</p></td>
<td><p><code>true, false</code></p></td>
<td><p><code>false</code></p></td>
<td><p>When set to <strong>true</strong> launches an application server upon start up. <em>Uses <strong>org.osgi.service.http.port</strong> arg to determine port to use.</em></p></td>
</tr>
<tr class="odd">
<td><p>osee.local.http.worker.port</p></td>
<td><p>{port}</p></td>
<td><p><code>Port Scan starting from 18000</code></p></td>
<td><p>Port to use for local worker server.</p></td>
</tr>
<tr class="even">
<td><p>osee.prompt.on.db.init</p></td>
<td><p><code>true, false</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Specifies whether to interactively prompt the user during database initialization for init choice</p></td>
</tr>
<tr class="odd">
<td><p>osee.record.activity</p></td>
<td><p><code>true, false</code></p></td>
<td><p><code>true</code></p></td>
<td><p>Specifies whether user activity should be logged</p></td>
</tr>
<tr class="even">
<td><p>osee.ote.benchmark</p></td>
<td><p><code>true, false</code></p></td>
<td></td>
<td></td>
</tr>
<tr class="odd">
<td><p>osee.ote.cmd.console</p></td>
<td><p><code>true, false</code></p></td>
<td></td>
<td><p>Specifies whether to enable the OTE command console</p></td>
</tr>
<tr class="even">
<td><p>osee.ote.server.title</p></td>
<td><p>free text name</p></td>
<td></td>
<td><p>name given to the OTE server which is displayed in the test manager</p></td>
</tr>
<tr class="odd">
<td><p>osee.ote.timing.log.path</p></td>
<td></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><p>osee.ats.ignore.config.upgrades</p></td>
<td><p><code>true, false</code></p></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>

### Setting Up Clients and Multiple Servers to work together

As described in the Architecture Section
[1](http://wiki.eclipse.org/OSEE/Developers_Guide#Client.2FServer_Architecture),
the Clients can be configured to choose a particular server or group of
servers. By specifying a server version, the arbitration server will
pick only the application servers that are configured to work with the
client. For instance, the configuration would make it possible to choose
only servers in the same location as the clients.
Steps:
1\. Configure each application server on the local server machine(s) to
support the local clients.
    a) Set the osee.version system property to a string that will
provide a common property to use with the OSEE Client.
        Example: In the VM Arguments for the server startup, add:
        –Dosee.version=”localSiteName”
    b) Set the osee.application.server.data to a location on the server
for the local copy of the application data
        Example: -Dosee.application.server.data=”path/to/local/data”
        Note: this local path could be rsync’d to another site to
improve local data performance
    c) Set the http port to the port number for the client to access the
server on
        Example: -Dorg.osgi.service.http.port=8092
2\. Configure the OSEE Client to connect to the one of the servers as an
arbitration server
    a) Set the osee.arbitration.server system property to the URL for
one of the application servers configured in step 1.
        Example:
-Dosee.arbitrations.server=<http://your.server.com:8092>
    b) Set the osee.version system property to match the application
server(s)
        Example: –Dosee.version=”localSiteName”


### Developer Workspace Configuration

#### Import Java Code Auto Formatting Preferences

1.  Start OSEE
2.  **File--\>Import...**
3.  Select **General/Preferences**
4.  Click **Next \>**
5.  Click **Browse** and navigate to (and select) the file
    *\[workspace_root\]\\org.eclipse.osee\\plugins\\org.eclipse.osee.support.config\\osee_team_preferences.epf*
6.  Click **Open**
7.  Check **Import all**
8.  Click **Finish**

#### Configure New Code Template Preferences

  - Window--\>Preferences
  - Java/Code Style/Code Templates
  - Code/New Java Files
  - Click Edit...
  - Paste the following (or some variant).  Be careful not to modify the
    template variables, i.e.: "${package_declaration}":

<!-- end list -->

    /*******************************************************************************
     * Copyright (c) 2012 Boeing.
     * All rights reserved. This program and the accompanying materials
     * are made available under the terms of the Eclipse Public License v1.0
     * which accompanies this distribution, and is available at
     * http://www.eclipse.org/legal/epl-v10.html
     *
     * Contributors:
     *     Boeing - initial API and implementation
     *******************************************************************************/
    ${package_declaration}
    /**
     * @author Joe P. Schmoe
     */
    ${typecomment}
    ${type_declaration}

  - Code/Catch block body
  - Click Edit...
  - Paste the following (or some variant):

<!-- end list -->

    OseeLog.log(Activator.class, Level.SEVERE, ${exception_var});



## Custom Data Model

The data model in OSEE is extensible and runtime user configurable
without modification to code or the database schema. Users can define
new artifact, attribute, and relation types and their constraints such
as multiplicity and applicability. Type inheritance allows similar types
to be defined and modified without tedious redundancy because the types
inherit what is common from their super type.

The OSEE data model is defined using a XText grammar designed by the
OSEE Team. This allows for editing of the types (object model)
configuration much the way you would edit source code. This includes
command completion and error notation when an incorrect syntax or
keyword is used.

**Example of the OSEE Types Editor**

![<file:osee_types_sheet_example.png>](/docs/images/osee_types_sheet_example.png "file:osee_types_sheet_example.png")

**Command Completion Example**

![<file:osee_command_completion_example.png>](/docs/images/osee_command_completion_example.png "file:osee_command_completion_example.png")

**Error Handling Example**

![<file:osee_editor_error_example.png>](/docs/images/osee_editor_error_example.png "file:osee_editor_error_example.png")

The OSEE types definitions are stored in artifacts and cached during
startup. They are edited in OSEE like any other artifact. Simply select
the artifact \> right-click \> open with \> OSEE DSL Editor. Convention
is to root them off the Common Branch default hierarchy.

![<file:osee_types_sheet_artifacts.png>](/docs/images/osee_types_sheet_artifacts.png "file:osee_types_sheet_artifacts.png")

### Artifact Definitions

All artifact types extend the type "Artifact". The snippet below of the
artifact type definition shows some types required by all OSEE
configurations.

    artifactType "Artifact" {       <-- Main Artifact type
        id 1                    <-- Unique long artifact id
        attribute "Name"        <-- List of Attributes that are valid for this Artifact Type
        attribute "Annotation"
        attribute "Content URL"
        attribute "Static Id"
        attribute "Relation Order"
    }

    artifactType "User" extends "Artifact" {   <-- User artifact extending Artifact
        id 5
        attribute "Active"    <-- Adding more attributes to those inherited from Artifact
        attribute "Phone"
        attribute "Email"
        attribute "Street Address"
        attribute "Dictionary"
        ...
    }

### Attribute Definitions

Attribute types define characteristics (fields) of an artifact. They are
strongly typed which supports data validation and editors and
applications can know how to handle the values returned.

Here's an example of the "Name" attribute that exists on every Artifact.

    attributeType "Name" extends StringAttribute {     <-- extends one of the base Attribute Types
        id 1152921504606847088                     <-- unique long id of attribute type
        dataProvider DefaultAttributeDataProvider  <-- different dataProviders exist to store data differently
        min 1                                      <-- minimum number of Attributes per artifact.  can be 0..n
        max 1                                      <-- maximum number of Attributes allowed.  can be 1..n
        taggerId DefaultAttributeTaggerProvider    <-- defines the tagger is used to split the value for searching
        description "Descriptive Name"             <-- description of what this Attribute stores
        defaultValue "unnamed"                     <-- default value to be used when min==1
        mediaType "text/plain"                     <-- media types for this Attribute
    }

    oseeEnumType "enum.req.subsystem" {              <-- Valid Enumerated values for the Subsystem attribute
        id 3458764513820541310
        entry "Robot_API"
        entry "Robot_Survivability_Equipment"
        entry "Robot_Systems_Management"
        entry "Chassis"
        ...
    }

    attributeType "Subsystem" extends EnumeratedAttribute {   <-- Enumerated Attribute definition
        id 1152921504606847112
        dataProvider DefaultAttributeDataProvider
        min 1
        max 1
        taggerId DefaultAttributeTaggerProvider
        enumType "enum.req.subsystem"         <-- Enumeration definition from above
        defaultValue "Unspecified"
        mediaType "text/plain"
    }

**Valid Attribute Base-Types are:**

  - org.eclipse.osee.framework.skynet.core.BooleanAttribute
  - org.eclipse.osee.framework.skynet.core.CompressedContentAttribute
  - org.eclipse.osee.framework.skynet.core.JavaObjectAttribute
  - org.eclipse.osee.framework.skynet.core.DateAttribute
  - org.eclipse.osee.framework.skynet.core.FloatingPointAttribute
  - org.eclipse.osee.framework.skynet.core.IntegerAttribute
  - org.eclipse.osee.framework.skynet.core.StringAttribute
  - org.eclipse.osee.framework.skynet.core.EnumeratedAttribute
  - org.eclipse.osee.framework.skynet.core.WordTemplateAttribute
  - org.eclipse.osee.framework.skynet.core.WordWholeDocumentAttribute

### Relation Definitions

Relations provide bi-directional links between artifacts on a branch.
Like Artifact and Attribute types, Relation types are strongly typed.
You can identify which artifact types are allowed to be on each side of
the relation. You can also specify the multiplicity.

    relationType "Code-Requirement" {   <-- Relation type name
        id 2305843009213694296      <-- Unique long id
        sideAName "code"                 <-- Name of the Artifacts on side A
        sideAArtifactType "Code Unit"    <-- Valid Artifact Type for side A
        sideBName "requirement"          <-- Name of the Artifacts on side B
        sideBArtifactType "Requirement"  <-- Valid Artifact Type for side B
        defaultOrderType Unordered       <-- Default Order Type
        multiplicity MANY_TO_MANY     <-- Multiplicity (any number of code can relate to any number of req)
    }

    relationType "Component-Requirement" {
        id 2305843009213694297
        sideAName "component"
        sideAArtifactType "Component"
        sideBName "requirement"
        sideBArtifactType "Requirement"
        defaultOrderType Unordered
        multiplicity ONE_TO_MANY     <-- Multiplicity (one component can relate to multiple requirements)
    }

As described above, you can use command completion to see the valid
values. An example is "multiplicity".

![<file:osee_multiplicity_options_example.png>](/docs/images/osee_multiplicity_options_example.png "file:osee_multiplicity_options_example.png")

### Making Types Changes in Production

1.  Search for OSEE Type Definition artifact types or find the types
    artifact on the Default Hierarchy of the Common Branch
2.  Select \> Right-Click \> Open With \> DSL Editor
3.  Edit the types
4.  Remove all errors
5.  Save the editor
6.  Refresh the server types caches, do one of the following
    1.  You should be prompted to update caches upon save. Select Yes
        for a single-server deployment.
    2.  POST to server urls
        {SERVER_ADDRESS}/orcs/types/invalidate-caches
    3.  curl -X POST {SERVER_ADDRESS}/orcs/types/invalidate-caches
    4.  A server restart will also reload the new types, but is not
        required
7.  If the code needs a new version of the types sheets (eg: Type sheet
    grammar has changed), See Types Versioning section below.

### Types and Access Control Versioning

As of the 24.0 line, the OSEE Types and Access Control Artifacts are
"Versioned". This allows the "Production" version of the code to use one
version of the types while another release line is being developed or
prepared for release.

#### OSEE Types Instructions

Prior to 0.24.0, types were loaded based on the Artifact Type. After
this, types are loaded by using the OrcsTypesData.OSEE_TYPE_VERSION to
index into the tuple table and grab the gamma_ids of the types
attributes to load. Thus this code variable needs to match up with the
tuple entries for the new version of the types sheet(s).

**To create a new types version**

  - Copy the existing types artifact and paste into OSEE Types folder.
    Types artifacts should be named for the last release they were used
    for (eg "Osee Type Config - 0.25.0") to reduce confusion.
  - Make appropriate grammar and types changes in the new types sheets
  - Update the code version number in
    org.eclipse.osee.framework.core.data.OrcsTypesData.OSEE_TYPE_VERSION
    to next number
  - Update the tuple table to point to new OSEE Types Sheets gamma ids
    using OSEE_TYPE_VERSION number from last step at the web page
    {SERVER_ADDRESS}/orcs/types/config/ui/main.html
      - NOTE: This MUST be done for each instance of OSEE database even
        if types didn't update
  - While both production and developmental types sheets are in play,
    type changes MUST be made in both types sheets or one version will
    not see the types changes and produce errors.
  - Types artifacts should be moved to a Retired OSEE Types folder when
    no longer used by any production releases. This helps reduce
    confusion for admins.

#### Access Control Instructions

  - Copy the "ATS CM Access Control" artifact on common and paste with
    new name
  - Replace the version at end of name
  - Update AtsArtifactToken.AtsCmAccessControl token with new ids
  - Make whatever access changes using DSL Editor on production to the
    newly created artifact

## OSEE Branches

[OSEE Branches](http://wiki.eclipse.org/OSEE/Branches)

## Workspace Setup

[Workspace Setup](http://wiki.eclipse.org/OSEE/Workspace_Setup)

## Downloading and Configuring Eclipse

[Downloading and Configuring
Eclipse](http://wiki.eclipse.org/OSEE/Workspace_Setup#Downloading_and_Configuring_Eclipse)

## Working with GIT

[Working with
Git](http://wiki.eclipse.org/OSEE/Workspace_Setup#Working_with_Git)

### Process for completing changes

1.  Before starting a change, for each repo: git pull --rebase
2.  Run the atside test suite that coincides with changes
    1.  If fails, run again
    2.  If fails twice, stop the server then delete db files
    3.  Also remove {osee_client}\\demo
3.  git status
    1.  In red, changes listed
    2.  Verify they are what is expected

<!-- end list -->

1.  gitk
    1.  review changes
2.  git commit --m "{feature/refinement/..}: Description" -a -s
    1.  e.g. git commit --m "feature: Web Config" -a -s
        1.  \-s only if you are not a committer
        2.  \-a means all (if you don't want all you omit the -a and
            replace with filename(s))

<!-- end list -->

1.  If committed - and need to reset
    1.  git lg -10
        1.  locate commit that you want to revert. Highlight/Write down
            version prior (and current version if you want to re-use)
        2.  e.g. $ git lg -10
        3.  git reset {paste version}
            1.  e.g. git reset 35bce46
2.  git status
    1.  should show previously committed changes in red
3.  Do git co of files you didn't intend to change (or remove)
4.  git commit -c {previous id} -a
    1.  Commits changes again, using previous id (don't use -m)

### When making multiple commits that need to look one

1.  git lg -10
    1.  be sure the latest commits are the ones you want to "squash"
2.  git rebase -i
    1.  Editor will pop up
    2.  Replace "pick" with "squash" for commits that need to be rolled
        up. Leave "pick" on the first commit. Click Save and Exit.
    3.  Another Editor will pop up
    4.  Keep only the first commit message
3.  git lg -10
    1.  One commit should be left, representing all changes and it
        should be the latest

### Before pushing to review

1.  In org.eclipse.osee repo do
    1.  git pull --rebase
2.  Run AtsIde and Suites to be sure they pass WITH YOUR
    changes

### Push to review

1.  git push review_dev
2.  Take link that results and send to two
    developers for review.

### If there are comments/changes needed based on reviews

1.  Open previously used workspace, make changes, pass the test and push
    it for review

### Merge changes

1.  Confirm changes have been reviewed by at least 2 people
2.  Confirm build has passed (Gerrit will give a +1)
3.  Ask committer to merge changes
4.  Create a clean repo/workspace
5.  In all repos: git pull --rebase
6.  Confirm it compiles

### After committer has merged changes and it is verified that they work

1.  Move task to Review in any external tracking tools
2.  Move task to Complete in OSEE

### When you want to reset repo to head (ok with losing changes)

1.  cherry pick101
    1.  ONCE a commit has been pushed; and further changes are needed
        based on review???
2.  on git web, find the change (e.g.
    <https://git.eclipse.org/r/#/c/148379/>)
3.  Click on the Download drop-down to the right and copy the link for
    CherryPick
4.  then do
    1.  . {paste command}
        1.  e.g. git fetch
            <https://git.eclipse.org/r/osee/org.eclipse.osee>
            refs/changes/79/148379/1 && git cherry-pick FETCH_HEAD

<!-- end list -->

  - IMPORTANT: if making changes to osee types (ie xxx).... must commit
    to database before pushing java to repo\!

### purging a branch- USE WITH CAUTION

  - powershell
  - telnet {server_url} {port}
  - oseehelp
  - DO NOT ENTER THE WORD: "exit" OR "quit"

### How to push only one commit of a stack of commits

  - Order the commits using rebase -i, making the commit you want the
    oldest

### git notes

1.  git pull --rebase
2.  git status
3.  git add {path}
4.  git commit
5.  git commit --append {path}
6.  git push review_dev
7.  git clean -fdx (before doing this be sure you have NO uncommitted
    changes)

<!-- end list -->

  - send path to two other developers to review changes

git reset --hard origin/dev \*\*only do when you have NO uncommitted
changes

## Testing

JUnit Method Rules:

<table>
<thead>
<tr class="header">
<th><p>Rule class</p></th>
<th><p>Description</p></th>
<th><p>Example</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><p><strong>OseeHousekeepingRule</strong></p></td>
<td><p>Checks if the Artifact Cache is clean</p></td>
<td><p><code> public class MyTest {</code><br />
<code>  @Rule</code><br />
<code>  public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();</code><br />
<br />
<code>   @Test</code><br />
<code>  public void testArtifactChangeName() throws Exception {</code><br />
<code>     //Arifact change name code</code><br />
<code>  }</code><br />
<code>}</code></p></td>
</tr>
<tr class="even">
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>



## Location of OSEE Bundles

[Location of OSEE
Bundles](http://wiki.eclipse.org/OSEE/Workspace_Setup#Locations_of_all_OSEE_bundles)

## Migrating Branches to Another OSEE Database (including from previous OSEE version)

1.  From the source application server's osgi console: export_branch
    myExportFolder
2.  The results will be placed in {user home}\\Exchange\\myExportFolder
3.  Zip the resulting folder and save it for possible reuse
4.  Run database initialization on destination database

|                                                                                                                                                                                                                                                                                                      |
| ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| eclipsec -application org.eclipse.osee.framework.database.init.configClient -vmargs -Xmx512m -Dosee.log.default=INFO -Dosee.application.server=http://localhost:8089 -Dosee.authentication.protocol=trustAll -Dosee.prompt.on.db.init=false -Dosee.choice.on.db.init="Base - for importing branches" |

1.  Import any custom types into the destination database
2.  From the destination application server's osgi console:
    import_branch {user home}\\Exchange\\myExportFolder
3.  From the application server run `tag_all`
4.  Restart destination application server
5.  Start corresponding OSEE client

## Fully Purge Branch and its backing data

### For situational awareness

#### Review the branch transactions

``` sql
SELECT * FROM osee_tx_details WHERE branch_id = ? AND tx_type = 0;
```

#### Review uniquely created versions on this branch

``` sql
SELECT UNIQUE(gamma_id) FROM osee_tx_details txd, osee_txs txs1 WHERE txd.branch_id = ? AND tx_type = 0 AND txd.branch_id = txs1.branch_id AND txd.transaction_id = txs1.transaction_id AND NOT EXISTS (SELECT 1 FROM osee_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs2.branch_id <> 6277884563228332544) ORDER BY gamma_id;
```

### Record URI of binary attributes to delete

``` sql
SELECT URI FROM osee_tx_details txd, osee_txs txs1, osee_attribute att WHERE txd.branch_id = ? AND tx_type = 0 AND txd.branch_id = txs1.branch_id AND txd.transaction_id = txs1.transaction_id AND NOT EXISTS (SELECT 1 FROM osee_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs2.branch_id <> 6277884563228332544) AND txs1.gamma_id = att.gamma_id AND URI IS NOT NULL;
```

### Delete gammas from OSEE item tables

``` sql
DELETE FROM osee_relation_link WHERE gamma_id IN (SELECT gamma_id FROM osee_tx_details txd, osee_txs txs1 WHERE txd.branch_id = ? AND tx_type = 0 AND txd.branch_id = txs1.branch_id AND txd.transaction_id = txs1.transaction_id AND NOT EXISTS (SELECT 1 FROM osee_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs2.branch_id <> 6277884563228332544));

DELETE FROM osee_artifact WHERE gamma_id IN (SELECT gamma_id FROM osee_tx_details txd, osee_txs txs1 WHERE txd.branch_id = ? AND tx_type = 0 AND txd.branch_id = txs1.branch_id AND txd.transaction_id = txs1.transaction_id AND NOT EXISTS (SELECT 1 FROM osee_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs2.branch_id <> 6277884563228332544));

DELETE FROM osee_attribute WHERE gamma_id IN (SELECT gamma_id FROM osee_tx_details txd, osee_txs txs1 WHERE txd.branch_id = ? AND tx_type = 0 AND txd.branch_id = txs1.branch_id AND txd.transaction_id = txs1.transaction_id AND NOT EXISTS (SELECT 1 FROM osee_txs txs2 WHERE txs1.gamma_id = txs2.gamma_id AND txs2.branch_id <> 6277884563228332544));
```

### Final Purge

1.  Purge the branch using the administrative operation "Purge Branch"
    from the Branch Manager
2.  If applicable, purge associated ATS workflow
3.  Delete binary attributes from file system

## Configuring ATS for Change Tracking

[Configuring ATS for Change
Tracking](/docs/OSEE/ATS/Users_Guide/Usage.md#Configure_ATS_for_Change_Tracking "wikilink")

## Custom OSEE Operations using BLAM

the extension point `org.eclipse.osee.framework.ui.skynet.BlamOperation`
can be used to contribute a custom OSEE operation that provides the
developer a very quick way to define the graphical interface that
supplies the operation with the user specified parameters.
`org.eclipse.osee.framework.ui.skynet.blam.operation.ChangeArtifactTypeBlam`
provides a simple example.

## Event Handling

### General Design Considerations

  - Event Handlers should run VERY quickly
  - A private implementation is more secure
  - Separate classes == maintainable but potentially costly
  - Ensure each listener is safeguarded from other listeners (wrap in
    event handling)

### Requirements

  - Need local client events and remote events to other clients
      - Local event model should be abstracted from remote model so app
        code doesn't change
      - Remote events should be versioned so event service can know how
        to handle
  - Need to handle events where artifact or relation is not in client's
    cache, but event may still matter
      - Client can't load every artifact to determine if it's desired
      - Registration needs to be at base level guid,artTypeId,branchId
        so these can be filtered out without loading all artifact data.
  - Need to provide lowest level registration (branch, artType and
    possibly even attrType) so event msgs reduced
  - Need to support versions of remote events so remote event model can
    change without causing exceptions
  - MUST have regression tests that are light-weight and easy to run so
    event service doesn't break
  - Since Artifact, Relation, Attribute types are dynamic, can't create
    static message model/brokers
  - Since artifact/relation/branch caches must be updated PRIOR to
    events propagate to GUI
      - Need priority listeners for caches to update prior to UI updates
  - Applications should be able to define their own higher level events
    while still listening to lower level events

### Event Handling Revelations

  - Reduce the necessity of events as much as possible - Restful
  - Data injection to keep caches up-to-date a complex/impossible
    solution for enterprise, multi-client, multi-server operations
  - All clients MUST be connected to event service if connected to
    database/app service
  - Applications shouldn't have to know what events to handle, but
    instead presented all the events to handle
      - Don't have separate purge, modified, change type events, but
        instead Artifact listener that has change types of purge,
        modified, deleted, change type, etc
  - Since artifacts/relations are cached, application to application
    events are prone to errors if cache not updated yet

### Event Handling Ideas / Goals

  - How could we have a version for each artifact so clients could know
    if they are up-to-date or missed an update and reload-all. This
    would allow for self-healing.
  - Reduce the items in the cache by improving our use of weak
    references. Anything not in the cache, doesn't have to get updated
  - Move configurations that have to be loaded by all clients to the
    server. Branch and type caches already there. Users and ATS Config
    could go there.
  - Is there a way to have events go straight to UIs (Editors, Views) to
    see if anyone cares that the cache is updated before updating the
    cache?

### OSEE Event Handling - Current

  - Design Issues
      - Communication mechanism - Jini
          - Clients are not connecting
          - Clients not staying connected
      - Event System
          - Events are at the data level; applications are required to
            turn high level operations (eg: save, transition) into low
            level data events (30 attributes modified, 10 relations
            added, 5 deleted) which are sent across the wire to another
            client to re-assemble into a high level operation.
          - Registration is for too high a level, each editor,view has
            to process through all events to find ones they care about
          - Have to filter between loaded and unloaded artifacts and
            handle them differently so as not to accidentally load
            everything
          - Have to register/handle separately for deleted versus purged
          - Two levels of registration for
            artifacts/attributes/relations, by transaction and by dirty.
            By dirty is too much information, but necessary sometimes.
            By transaction is ok, but have to filter through all
            un-necessary stuff to find what want. Dangerous and costly.
          - Access control is not handled in events
          - Not extensible, so applications can't create/propagate their
            own events
  - Registration
      - ArtifactPurgedEvent
      - FrameworkTransactionEvent
      - RelationModifiedEvent
      - BranchEvent

### OSEE Event Handling - New

  - Registration - Need lower level registration of framework events
      - By Artifact (branch, guid)
      - By Object Type (artifact, attribute, relation) - include
        inheritance
      - By Mod Type (added, modified, deleted, purged)
      - By Transaction (persist to DB of group of changes)
      - By Combinations (Artifact type and relation type or Artifact
        type and attribute type)
  - ArtifactEventManager
      - Sends BasicGuidArtifact for each mod type
      - Modification Types: Purged, Added, Modified, Deleted, Change
        Type, Access Control
      - Allow registration with filters
          - ArtifactGuidFilter - only sends event if artifact is
            affected
          - BranchGuidFilter - only send event if artifact on this
            branch is affected
          - ArtifactTypeGuidFilter - only sends event if artifact of one
            of these types is affected
  - BranchEventManager
      - Modification Types: Added, Deleted, Purged, Access Control,
        Renamed
      - Allow registration with same filters as Artifact
  - Remote events are different and versioned classes so abstract from
    framework events
  - Created via jaxb

### Event Model

#### Remote Events

1.  Remote events classes are auto-generated via jaxb which keeps toXml,
    fromXml implementation hidden and thus requires less junit tests for
    base clasess.
2.  Each class is versioned by name (1, 2, 3). When RemoteBranchEvent1
    changes, will create RemoteBranchEvent2 so there is no conflicts and
    implement backward compatibility that will
    1.  Create RemoteBranchEvent2 events when RemoteBranchEvent1 are
        received - for old clients
    2.  Create RemoteBranchEvent1 events when RemoteBranchEvent2 are
        received - for new clients
3.  Current classes:
    1.  RemoteAccessControlArtifactsEvent1.java
    2.  RemoteAttributeChange1.java
    3.  RemoteBasicGuidArtifact1.java
    4.  RemoteBasicModifiedGuidArtifact1.java
    5.  RemoteBranchEvent1.java
    6.  RemoteBranchRenameEvent1.java
    7.  RemoteBroadcastEvent1.java
    8.  RemoteChangeTypeArtifactsEvent1.java
    9.  RemoteNetworkSender1.java
    10. RemotePurgedArtifactsEvent1.java
    11. RemoteTransactionDeletedEvent1.java
    12. RemoteTransactionEvent1.java

#### Framework Events / Listeners

  - ArtifactEvent
  - BranchEvent
  - BroadcastEvent
  - AccessControl?? - Potentially roll into ArtifactEvent
  - TransactionDeleted?? - Potentially roll into ArtifactEvent

## Use Cases

  - Artifact Editor
      - Current Design
          - Registration = All Branches, Mod Types, Object Types
          - Handling
              - If not branch, throw away
              - If unloaded, ignore
              - If loaded refresh/update all
              - If relation, refresh relation tree
              - If attribute, refresh attribute tab
      - New Design
          - Registration = One guid and relations for that guid
          - Handling
              - If relation, refresh relation tree
              - If attribute, refresh attribute tab
  - Artifact Explorer
      - Current Design
          - Registration = All Branches, Mod Types, Object Types
          - Handling
              - If not branch, throw away
              - If unloaded, ignore
              - If loaded refresh/update all
              - If relation, get parent and call tree.refresh()
              - If attribute (name only), call tree.update(element)
      - New Design
          - Registration = One branch, One relation type, All Object
            Types
          - Handling = About the same, but don't have to filter through
            un-necessary stuff
  - SMA Editor - Edits single team workflow, but watches related tasks,
    reviews, user assignments, etc
      - Current Design
          - Registration = purged, relation modified, framework
            transaction, branch event. NOTE: Should register by branchId
            (Common), guids of workflows, artifact types (eg. don't care
            about general document artifact type changes on common)
          - Handling
              - If not Common branch or in transition, ignore all
                artifact/relation/attribute changes
              - If is purged/deleted, close editor
              - If is modified, deleted, relChanged, relDeleted,
                relAdded of main artifact, redraw pages
              - If related review is any of above, redraw pages
              - If relation modified, dirty editor
              - If branch event and working branch added, deleted,
                purged, committed, redraw pages
      - New Design
          - Registration - One branch, 12 artifact types
          - Handling = About the same, but don't have to filter through
            un-necessary stuff

References

  - <http://java.sun.com/docs/books/tutorial/uiswing/events/generalrules.html>

## Building OSEE using Tycho/Maven

### Build Requirements

  - Java Runtime Environment (JRE) 1.6 or higher
  - Maven 3.0 or higher

### Build Steps

1.  Download and install Maven. See <http://maven.apache.org> for
    installation details.
2.  Download the OSEE source code from [Git Web
    Client](http://git.eclipse.org/c/osee/org.eclipse.osee.git) (see
    clone section for more information\] or
    <https://github.com/eclipse/osee>
      -
        **Note:** *Tycho/Maven build support available for source code
        versions 0.9.9_SR6 and higher.*
3.  Download and unzip 3rd party dependency P2 archived site:
    <http://code.google.com/p/osee-external/downloads/detail?name=org.eclipse.ip.p2_1.0.0.v201203200040-DEV.zip&can=2&q=>
4.  Build commands:
    1.  **cd org.eclipse.osee/plugins/org.eclipse.osee.parent**
    2.  **mvn clean verify -Declipse-ip-site="file:\<PATH TO 3rd PARTY
        P2 SITE\>"**
    3.  If you run into any problems (to display debug and stack trace
        info)
          -
            **mvn -e -X verify -Declipse-ip-site="file:\<PATH TO 3rd
            PARTY P2 SITE\>"**

`Assuming the following layout:`
`/UserData/org.eclipse.osee`
`/UserData/org.eclipse.ip.p2_1.0.0.v201203200040-DEV`

`machine@user /UserData/org.eclipse.osee/plugins/org.eclipse.osee.parent:`
`$mvn clean verify -Declipse-ip-site="file:../../../org.eclipse.ip.p2_1.0.0.v201203200040-DEV"`

### Interactive Build

`☞ `**`Depends``   ``on``   ``org.eclipse.osee.ip.p2`**

  -
    To build all org.eclipse.osee artifacts
    1.  cd org.eclipse.osee/plugins/org.eclipse.osee.support.maven
    2.  Issue maven build command: **mvn compile
        -Declipse-ip-site="file:../../../org.eclipse.ip.p2_1.0.0.v201203200040-DEV"**
    3.  Select build options when prompted

### Build Module Hierarchy (from highest to lowest)

1.  org.eclipse.osee.parent
    1.  org.eclipse.osee.ide.parent
        1.  org.eclipse.osee.client.all.parent
        2.  org.eclipse.osee.client.parent
        3.  org.eclipse.osee.ote.parent
        4.  org.eclipse.osee.runtime.parent
    2.  org.eclipse.osee.x.parent
        1.  org.eclipse.osee.x.server.parent
        2.  org.eclipse.osee.orcs.parent
        3.  org.eclipse.osee.x.core.parent

### Build Artifacts

| Path                                                         | Artifact                                     | Description                     |
| ------------------------------------------------------------ | -------------------------------------------- | ------------------------------- |
| plugins/org.eclipse.osee.client.all.p2/target/               | repository/                                  | OSEE IDE Client P2 Site         |
| org.eclipse.osee.client.all.p2.zip                           | OSEE IDE Client P2 Archived Update Site      |                                 |
| plugins/org.eclipse.osee.client.all.product/target/products/ | build_label.txt                             | OSEE Build Information          |
| org.eclipse.osee.ide.id-linux.gtk.x86.tar.gz                 | OSEE Client IDE All-In-One Linux x86         |                                 |
| org.eclipse.osee.ide.id-linux.gtk.x86_64.tar.gz             | OSEE Client IDE All-In-One Linux x86 64-bit  |                                 |
| org.eclipse.osee.ide.id-win32.win32.x86.zip                  | OSEE Client IDE All-In-One Win32 x86         |                                 |
| org.eclipse.osee.ide.id-win32.win32.x86_64.zip              | OSEE Client IDE All-In-One Win32 x86 64-bit  |                                 |
| plugins/org.eclipse.osee.x.server.p2/target                  | repository/                                  | OSEE Application Server P2 Site |
| server/                                                      | OSEE Application Server                      |                                 |
| org.eclipse.osee.x.server.p2.zip                             | OSEE Application Server Archived Update Site |                                 |
| org.eclipse.osee.x.server.runtime.zip                        | OSEE Application Server Zipped Runtime       |                                 |
|                                                              |                                              |                                 |


\====OSEE System Requirements====

  - System with at least 1GB of RAM
  - Relational database. H2 is included by default. If you wish to use
    something else such as PostgreSQL or Oracle, please see instructions
    for installing [Supported
    Databases](/docs/OSEE/Users_Guide/Getting_Started.md#Supported_Databases "wikilink")
    to complete this step.

## How to Define Classes for Coverage Importing

The following steps walk a developer through defining the classes
necessary to begin importing coverage data into the OSEE application. 
Please keep in mind that these are meant to be simplified examples and
developers are encouraged to 'get creative' when adapting these examples
to their own particular context.
![image:testworkflow.png](/docs/images/testworkflow.png "image:testworkflow.png")

**1. Write a class that extends AbstractCoverageBlam**
```
    public class MyCoverageImportBlam extends AbstractCoverageBlam {
       public static String COVERAGE_IMPORT_DIR = "Coverage Import Directory";
       public static String NAMESPACE = "Code Namespace";

       @Override
       public String getName() {
          return "My Coverage Import";
       }

       @Override
       public Collection<String> getCategories() {
          return Arrays.asList("Blams");
       }

       @Override
       public String getDescriptionUsage() {
          return "Import coverage from coverage directory.";
       }

       @Override
       public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws Exception {
          try {
             final String coverageInputDir = variableMap.getString(COVERAGE_IMPORT_DIR);
             if (!Strings.isValid(coverageInputDir)) {
                throw new OseeArgumentException("Must enter valid filename.");
             }
             final String namespace = variableMap.getString(NAMESPACE);
             if (!Strings.isValid(namespace)) {
                throw new OseeArgumentException("Must enter valid namespace.");
             }

             File file = new File(coverageInputDir);
             if (!file.exists()) {
                throw new OseeArgumentException("Invalid filename.");
             }

             MyCoverageImporter myCoverageImporter = new MyCoverageImporter(coverageInputDir, namespace);
             CoverageImport coverageImport = myCoverageImporter.run(monitor);
             setCoverageImport(coverageImport);
          } catch (Exception ex) {
             OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
          }
       }

       @Override
       public String getXWidgetsXml() {
          StringBuffer buffer = new StringBuffer("<xWidgets>");
          buffer.append("<XWidget xwidgetType=\"XDirectorySelectionDialog\" " + getDefaultDirectory() + " displayName=\"" + COVERAGE_IMPORT_DIR + "\" />");
          buffer.append("<XWidget xwidgetType=\"XText\" displayName=\"" + NAMESPACE + "\" />");
          buffer.append("</xWidgets>");
          return buffer.toString();
       }

       private String getDefaultDirectory() {
          if (CoverageUtil.isAdmin()) {
             return " defaultValue=\"C:\\UserData\" ";
          }
          return "";
       }

    }
```

**2. Define a class that implements ICoverageImporter**

    public class MyCoverageImporter implements ICoverageImporter {
       private final String coverageInputDir;
       private final String namespace;
       private final CoverageImport coverageImport = new CoverageImport("My Coverage Import");

       public MyCoverageImporter(String coverageInputDir, String namespace) {
          this.coverageInputDir = coverageInputDir;
          this.namespace = namespace;
       }

       @Override
       public String getName() {
          return "My Coverage Importer";
       }

       @Override
       public CoverageImport run(IProgressMonitor progressMonitor) throws OseeCoreException {
          /*
           * Use any member variables to populate coverageImport
           */
          return coverageImport;
       }

    }

**3. Add extension point declaration to package's plugin.xml**
```
    <?xml version="1.0" encoding="UTF-8"?>
    <?eclipse version="3.4"?>
    <plugin>
       <extension
             point="org.eclipse.osee.framework.ui.skynet.BlamOperation">
          <Operation
                className="com.my.coverage.MyCoverageImportBlam">
          </Operation>
       </extension>
    </plugin>
```


## Using and Updating Swagger

Swagger is an in-depth web/UI-based API documentation and interactive
tool, which is now incorporated into the OSEE framework. Below are
instructions on using and updating the tool.

### Browsing/Utilizing The Swagger Web Application

The Swagger web application is located here:
**http://{host}/swagger/index.html**, where **{host}** is the
top-level domain of your organization, or **http://localhost:{port}**
when running a local server on a development machine.

### Multiple Definitions

The default "definition" API endpoints will be shown and are based on
the **Select a definition** dropdown field selection in the upper-right
hand corner of the page. APIs are grouped by definition, which are
pre-generated files we produce and continually update. The Swagger
application utilizes these custom definition files, currently in JSON
format, to display a list of API endpoints and their features defined in
its respective definition file.

### Servers

The **Servers** dropdown field contains only one selection for now, but
can be used to specify alternate, specific servers in the future.

### Searchable Tagging

Just below the **Servers** field, you'll find the **Filter by tag** text
field. Swagger tags are custom searchable keywords we provide for groups
of endpoints. They allow quick filtering of endpoints the user may be
interested in, as the complete list of endpoints per definition may be
very lengthy.

### Generating/Updating The Definition Files

The Swagger definition files are generated by package-specific Java
applications located under each targeted package. They can be **Run** or
**Debugged** individually or all in tandem by running or debugging the
parent SwaggerGenerator application located under
**org.eclipse.osee.ats.ide.integration.test.util** package.

1.  Right-click on the SwaggerGenerator.java file and select **Run As**
    or **Debug As -\> Java Application**
    1.  The SwaggerGenerator.java class subsequently calls each
        package-specific Generator application in under its respective
        package. Refer to the SwaggerGenerator class to determine which
        classes/applications are called.
2.  Observe the Console for any errors during generation.
3.  The newly generated or updated files will be located under
    **org.eclipse.osee.web.ui/src/swagger/definitions** in this format:
    **{reformatted package name}.json**

### Adding New Endpoints To An Existing Swagger Annotated Class

An existing Swagger-annotated class has the **@Swagger** annotation
placed at the class level. When adding a new endpoint to an existing and
properly set up Swagger-annotated class, the new endpoint will be picked
up automatically **as long as** the endpoint is annotated with a
**@Path** annotation, with these points in mind:

1.  The new endpoint may or may not have any searchable Swagger tagging
    applied to it automatically. This will depend on whether or not its
    associated package-level Generator class has ***generic tagging***
    implemented (see Generic versus Explicit Tagging below).
2.  The new endpoint will not have any documentation associated with it
    on the Swagger web application until the proper Swagger annotations
    are added to the endpoint.
3.  The new endpoint will otherwise have full functionality, depending
    on security settings, minus the useful documentation.

### Adding A New Swagger Annotated Class To A Swagger-Aware Package

A "Swagger-aware package" contains its own custom Swagger Generator
class/application, which scans for all Swagger-annotated classes under
that particular package. A Swagger-annotated class contains the
**@Swagger** annotation at the class level. When adding a new
Swagger-annotated class to a Swagger-aware package, add the **@Swagger**
annotation at the class level for this new class. For any containing
endpoints to be picked up by the Swagger web application, they must have
a **@Path** annotation, at minimum.

### Referencing An Existing Package To Be Included In Swagger Scanning

In order for all Swagger-annotated classes under a particular package to
be scanned, and subsequently added to the Swagger web application, a
**Generator Class** needs to be implemented within that package. See the
following code example below. This particular class has ***generic
tagging*** implemented, which provides out-of-the-box searchable tagging
support for Swagger-annotated classes ***with no Swagger endpoint
documentation tags implemented yet***.

1.  Ensure the **definitionFile** variable replaces periods in the
    package name with underscores, and only uses one period before the
    json file extension name.
2.  The **definitionPath** variable should not change.
3.  **infoTitle**, **infoDescription**, **serverUrl**, and
    **serverDescription** should all change based on the new definition.
4.  Endpoints in multiple packages need to have each respective package
    listed, as shown. Some bundles will have all endpoints in a single
    package, however.
5.  '''IMPORTANT: ''' *Implementing **both** generic tagging and Swagger
    documentation tagging under a particular package should be avoided,
    as the Swagger application will generate both tagging versions,
    which will have different naming conventions and will be confusing
    to the user. It needs to be one or the other; not a combination of
    both. For example, if the generator class has generic tagging
    implemented, then no endpoints should have any Swagger documentation
    annotation tags. It's best practice to provide all Swagger
    documentation tags for each endpoint under the particular package,
    and then remove the generic tagging implementation in the generator
    class **in a single commit.***

<!-- end list -->
```
    public class DefineApiSwaggerGenerator {
       private static final String definitionPath = "../org.eclipse.osee.web.ui/src/swagger/definitions/";
       // Only one period in the definition file name is supported
       private static final String definitionFile = "org_eclipse_osee_define_api.json";
       private static final String infoTitle = "Define API Endpoint Definitions";
       private static final String infoDescription = "Allows interactive support for Define API endpoints.";
       private static final String serverUrl = "/define";
       private static final String serverDescription = "Define";

       public static void main(String[] args) {

          Set<Class<?>> allClasses = Lib.getAllClassesUnderPackage("org.eclipse.osee.define.api");
          allClasses.addAll(Lib.getAllClassesUnderPackage("org.eclipse.osee.define.api.publishing"));
          allClasses.addAll(Lib.getAllClassesUnderPackage("org.eclipse.osee.define.api.publishing.datarights"));
          allClasses.addAll(Lib.getAllClassesUnderPackage("org.eclipse.osee.define.api.publishing.templatemanager"));
          allClasses.addAll(Lib.getAllClassesUnderPackage("org.eclipse.osee.define.api.synchronization"));
          allClasses.addAll(Lib.getAllClassesUnderPackage("org.eclipse.osee.define.api.toggles"));

          Set<Class<?>> swaggerClasses = new HashSet<Class<?>>();

          for (Class<?> clazz : allClasses) {
             if (clazz.isAnnotationPresent(Swagger.class)) {
                swaggerClasses.add(clazz);
             }
          }

          System.out.println("Creating Swagger " + definitionFile + " definitions file.  Please wait...");

          // Read in all applicable classes, creating initial Swagger openAPI definition object
          OpenAPI openAPI = new Reader(new OpenAPI()).read(swaggerClasses);

          Info info = new Info();
          info.setTitle(infoTitle);
          info.setDescription(infoDescription);
          openAPI.setInfo(info);

          Server server = new Server();
          server.setUrl(serverUrl);
          server.setDescription(serverDescription);
          openAPI.addServersItem(server);

          // Add searchable tagging support to groups of endpoints
          Map<String, PathItem> taggedPaths =
             openAPI.getPaths().entrySet().stream().map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(),
                addTagsToPathItem(entry.getKey(), entry.getValue()))).collect(
                   Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
          Paths paths = new Paths();
          paths.putAll(taggedPaths);
          openAPI.setPaths(paths);

          try (FileWriter fr = new FileWriter(definitionPath + definitionFile)) {
             fr.write(Json.mapper().writeValueAsString(openAPI));
          } catch (JsonProcessingException e) {
             e.printStackTrace();
          } catch (IOException e) {
             e.printStackTrace();
          }

          System.out.println("Swagger " + definitionFile + " definitions file created.");
          System.out.println("");
       }

       private static PathItem addTagsToPathItem(String path, PathItem pathItem) {
          String pathElements[] = path.split("/");

          if (pathItem.getGet() != null) {
             pathItem.getGet().addTagsItem(pathElements[1]);
          }
          if (pathItem.getDelete() != null) {
             pathItem.getDelete().addTagsItem(pathElements[1]);
          }
          if (pathItem.getHead() != null) {
             pathItem.getHead().addTagsItem(pathElements[1]);
          }
          if (pathItem.getPatch() != null) {
             pathItem.getPatch().addTagsItem(pathElements[1]);
          }
          if (pathItem.getPost() != null) {
             pathItem.getPost().addTagsItem(pathElements[1]);
          }
          if (pathItem.getPut() != null) {
             pathItem.getPut().addTagsItem(pathElements[1]);
          }
          if (pathItem.getTrace() != null) {
             pathItem.getTrace().addTagsItem(pathElements[1]);
          }
          if (pathItem.getOptions() != null) {
             pathItem.getOptions().addTagsItem(pathElements[1]);
          }
          return pathItem;
       }
    }
```
Here is an example of a class with ***no generic tagging implemented***.
All of the enpoint classes under this particular package, for which the
generator class is handling ***have Swagger documentation tagging
implemented for each endpoint instead***. This customized documentation
tagging takes better advantage of Swagger's capabilities, however takes
more work documenting each endpoint. For classes under packages that
have not been documented yet, generic tagging may be used in the
meantime.
```
    public class DispoSwaggerGenerator {

       private static final String definitionPath = "../org.eclipse.osee.web.ui/src/swagger/definitions/";
       // Only one period in the definition file name is supported
       private static final String definitionFile = "org_eclipse_osee_disposition_rest.json";
       private static final String infoTitle = "Dispo API Endpoint Definitions";
       private static final String infoDescription = "Allows interactive support for Dispo API endpoints.";
       private static final String serverUrl = "/dispo";
       private static final String serverDescription = "Dispo";

       public static void main(String[] args) {

          Set<Class<?>> allClasses = Lib.getAllClassesUnderPackage("org.eclipse.osee.disposition.rest.resources");
          Set<Class<?>> swaggerClasses = new HashSet<Class<?>>();

          for (Class<?> clazz : allClasses) {
             if (clazz.isAnnotationPresent(Swagger.class)) {
                swaggerClasses.add(clazz);
             }
          }

          System.out.println("Creating Swagger " + definitionFile + " definitions file.  Please wait...");

          // Read in all applicable classes, creating initial Swagger openAPI definition object
          OpenAPI openAPI = new Reader(new OpenAPI()).read(swaggerClasses);

          Info info = new Info();
          info.setTitle(infoTitle);
          info.setDescription(infoDescription);
          openAPI.setInfo(info);

          Server server = new Server();
          server.setUrl(serverUrl);
          server.setDescription(serverDescription);
          openAPI.addServersItem(server);

          System.out.println("Swagger " + definitionFile + " definitions file created.");
          System.out.println("");
       }
    }
```
**1. Add the new class to the parent SwaggerGenerator.java class:**
```
    public class SwaggerGenerator {

       public static void main(String[] args) {
          DefineApiSwaggerGenerator.main(args);
          MimSwaggerGenerator.main(args);
          OrcsSwaggerGenerator.main(args);
          AtsApiSwaggerGenerator.main(args);
          DispoSwaggerGenerator.main(args);
          // Add new Swagger Generator class here
       }
    }
```
**2. Add the new definition file reference URL to the
swagger-initializer.js file under
org.eclipse.osee.web.ui/src/swagger/node_modules/swagger-ui-dist/ Make
sure it is placed in alphabetical order based on its "name" attribute in
the "urls" array:**
```
    window.onload = function() {
      //<editor-fold desc="Changeable Configuration Block">

      // the following lines will be replaced by docker/configurator, when it runs in a docker-container
      window.ui = SwaggerUIBundle({
         // Alphabetical:
         urls: [
            {
                url: "/swagger/definitions/org_eclipse_osee_ats_api.json", name: "ATS API Endpoints"
            },
            {
                url: "/swagger/definitions/org_eclipse_osee_define_api.json", name: "Define API Endpoints"
            },
            {
                url: "/swagger/definitions/org_eclipse_osee_disposition_rest.json", name: "Dispo API Endpoints"
            },
            {
                url: "/swagger/definitions/org_eclipse_osee_mim.json", name: "MIM API Endpoints"
            },
                    {
                url: "/swagger/definitions/org_eclipse_osee_orcs_rest.json", name: "Orcs Endpoints"
            }
         ],
        dom_id: '#swagger-ui',
        filter: true,
         configUrl: '/swagger/swagger-config.json',
        deepLinking: true,
        presets: [
          SwaggerUIBundle.presets.apis,
          SwaggerUIStandalonePreset
        ],
        plugins: [
          SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: "StandaloneLayout",
         supportedSubmitMethods: [
            "get",
            "head"
            ]
      });

      //</editor-fold>
    };
```
**3. For any classes to picked up by the Swagger web application for
this new definition, the @Swagger annotation must be added to any
relevant classes at the class level, and any relevant endpoint needs to
be annotated with the @Path annotation, at minimum.**

### Generic Versus Explicit Tagging

As stated above, generic tagging may be utilized in the meantime until
all endpoints under a particular Swagger-aware package are fully
documented with Swagger documentation tagging. For a full description of
supported Swagger annotation tagging, refer to the **Swagger 2.X
Annotations documentation** here:

  - <https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations>

Below is an example of Swagger documentation annotation tagging on an
enpoint. In this case, the Swagger-specific documentation tags are:

  - **@Operation** (This particular tag includes a **summary**, which is
    shown as visible text on this endpoint's dropdown field within the
    Swagger web application.)
  - **@Tags** (An array container for multiple **@Tag** annotations. For
    a single **@Tag** annotation, the **@Tags** array annotation is not
    needed.)
  - **@Tag** (This is the searchable text for one or more endpoint
    "types," e.g. "branches". Two **@Tag** annotations on a single
    endpoint will appear in two locations. This number should be kept at
    a minimum; otherwise the page may end up with too many entries for
    the same endpoint.)
  - **@ApiResponses** (An array container for multiple **@ApiResponse**
    annotations. For a single **@ApiResponse** annotation, the
    **@ApiResponses** array annotation is not needed.)
  - **@ApiResponse** (In this case the response code and its
    description.)
  - **@Parameter** (Inline annotation used to describe the formal
    parameter, e.g. **@QueryParam**)

<!-- end list -->

```
   @Path("{name}")
   @POST
   @RolesAllowed(DispoRoles.ROLES_ADMINISTRATOR)
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.APPLICATION_JSON)
   @Operation(summary = "Create a new Disposition Set given a name, dispoType, and path")
   @Tags(value = {@Tag(name = "create"), @Tag(name = "set")})
   @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "OK. Created the Disposition Set"),
      @ApiResponse(responseCode = "409", description = "Conflict. Tried to create a Disposition Set with same name"),
      @ApiResponse(responseCode = "400", description = "Bad Request. Did not provide both a Name and a valid Import Path")})
   public Response postDispoSetByName(
      @Parameter(description = "String used to specify the directory to populate the set", required = true) @FormParam("path") String importPath,
      @Parameter(description = "String used to name the Set", required = true) @PathParam("name") String name,
      @Parameter(description = "String used to specify if using disposition vs coverage", required = true) @QueryParam("dispoType") String dispoType,
      @QueryParam("userName") String userName) {
      DispoSetDescriptorData descriptor = new DispoSetDescriptorData();
      descriptor.setName(name);
      descriptor.setImportPath(importPath);
      descriptor.setDispoType(dispoType);

      return postDispoSet(descriptor, userName);
   }
```

**Below is a link to a commit which removes generic tagging and adds
custom Swagger documentation tagging for all endpoints in an existing
Swagger-aware package:**

  - <https://git.eclipse.org/r/c/osee/org.eclipse.osee/+/202029>

### Parent/Child Annotated Path Limitation

For any endpoint **classes** with a class **@Path** annotation that are
called by another class with a **@Path** annotation, only the **parent**
class should be annotated with the **@Swagger** annotation. This is
because Swagger cannot decipher the parent/child relationship of the two
**@Path** annotations, and subsequently any calls to the child class
directly will fail. An example is the BranchesResource.java class calls
a number of other endpoint classes including TupleEndpoint,
ApplicabilityEndpoint, and others. Therefore, those classes do not
implement the **@Swagger** annotation, as it is already picked up by the
"parent" BranchesResources class.

### Common Swagger Annotations

In cases where a group of endpoints share the same annotations in whole
or in part, implementing a new annotation @interface containing these
common annotations will reduce redundancy and make for cleaner code.
Below is an example of a common Swagger annotation @interface containing
these common shared annotations. In this case, when adding the
**@SwaggerCommonOrcsAnnotations** annotation to a group of endpoints,
they will all implement the Swagger **@ApiResponse** annotations shown
below:

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @ApiResponses(value = {
       @ApiResponse(responseCode = "200", description = "Successful"),
       @ApiResponse(responseCode = "400", description = "Content not found")})
    public @interface SwaggerCommonOrcsAnnotations {
       //
    }

## OSEE Web Development

OSEE provides a simple mechanism to contribute static web resources in
the MANIFEST.MF. The header "Osee-JaxRs-Resource" allows specifying a
path in the bundle and its mapping to a URL. See
org.eclipse.osee.ats.rest/META-INF/MANIFEST.MF for an example. The
resource(s) at that path will be severed by the embedded web server in
the OSEE server at the specified URL.

## Helpful links

  - <http://rcpquickstart.com/2007/06/20/unit-testing-plug-ins-with-fragments/>
  - <http://junit.sourceforge.net/>
  - <http://junit.sourceforge.net/doc/cookbook/cookbook.htm>
  - [Build and Test Automation for plug-ins and
    features](http://www.eclipse.org/articles/Article-PDE-Automation/automation.html)
  - [Eclipse Documentation
    Online](http://www.eclipse.org/documentation/)
  - [Using the Eclipse GUI outside the Eclipse
    Workbench](http://www-128.ibm.com/developerworks/library/os-ecgui3/)
  - [Understanding Layouts in
    SWT](http://www.eclipse.org/articles/article.php?file=Article-Understanding-Layouts/index.html)
  - [Eclipse - a tale of two VMs (and many
    classloaders)](http://www.eclipsezone.com/articles/eclipse-vms)
  - [Strong Lesson About OSGI
    Modularity](http://www.osgi.org/blog/2006/04/strong-lesson-about-modularity.html)

## Design Questions

### I have strange eclipse/osee issues can I delete stuff from my ***configuration*** folder?

yes. Everything **except**:

`  config.ini`
` org.eclipse.equinox.simpleconfigurator`

### Is OSEE an application framework or an application?

The simple answer is BOTH. OSEE Application Framework is created to
allow applications to be built on top and share the common data model.
This can be used independently of any OSEE applications. In addition,
there are applications that are delivered with and use the OSEE
Application Framework. This includes a full featured Requirements and
Document Management System (OSEE Define), a powerful change tracking and
configuration management application (OSEE ATS - Action Tracking
System), a fully customizable peer-review module and other project,
reporting and metrics tools. These application can be used
out-of-the-box and new applications can be created or integrated on the
framework to share and contribute to the same data.

### Other products sound similar. Why OSEE?

  - Open Source Extensible Platform
  - Open Eclipse Project w/ Collaboration
  - Tight Integration Around A Common Data Model
  - Full Lifecycle Engineering Environment

### Is OSEE only for Avionics Engineering?

No. Although OSEE was created to handle the complexity of a large US Department of Defense program, it was architected to support any systems engineering project from a simple application built for a single customer to a large complex application. In addition, since OSEE is an independent application, the OSEE development team uses OSEE to develop, deploy and maintain OSEE.

### What is Skynet?

Skynet is a legacy term for the persistence portion of the OSEE
Application Framework.

### What are Artifact Framework types?

OSEE provides Artifacts, Attributes and Relations that are strongly
typed. This means that the user can create their own artifact type, for
example a "Software Requirement" to represent the requirements at a
software level and their own attribute types, for example a
"Qualification" attribute or a "Safety Criticality" attribute and event
their own relations, for example a "Software Requirement to Allocation"
relation. These types are defined in the Artifact Framework and can be
created dynamically in the system during database creation or while
running. This allows the end user to expand the data that is being
stored in OSEE without providing a new release.

### What is the Action Tracking System (ATS)?

The Action Tracking System is the tightly integrated configuration
management system built in OSEE and very tightly integrated with the
OSEE Application Framework. It uses a powerful workflow engine to
provides a fully customizable workflow to track improvements, problems
and support for any number of teams/tools/programs simultaneously. This
gives the user a single point view into all the work that they are
required to do.

### Why build yet another bug tracking tool like the Action Tracking System (ATS)?

Although there are a number of open source and commercial bug tracking
systems available, OSEE's goal to integrate workflow management and
provide a tight integration with the Application Framework, and the
applications built on top, required us to develop ATS. ATS is meant to
be more than simple bug tracking since it can be used to manage multiple
teams working on multiple products or support simultaneously. This means
that you can create a single "Action" to "Fix the XYZ capability" that
will create the necessary workflows for all the teams that need to
perform work. For example, a workflow may be created for not only the
Software Development team, but also the test team, documentation team,
integration team and even facilities like labs or conference rooms. Each
team then moves independently through its workflow to perform the work
necessary for the common "Action". In addition, ATS enables complete
customization of different workflows for each configured team. This
means that the documentation team can follow their own "process" which
may contain 5 different states while the application developers can
follow their own more complex "process" which may contain 30 different
states.

### How does OSEE handle traceability?

[Traceability](http://en.wikipedia.org/wiki/Requirements_Traceability)
is handled in OSEE through the use of Relations. These relations can be
defined in OSEE according to their need and the users can then add and
remove these relations throughout the lifecycle of the requirements or
other artifacts. Deliverable documents or any report generation would
also use this traceability.

### What is Define?

OSEE Define is OSEE's advanced Requirements and Document Management
System. OSEE Define can be used to track a simple application's
requirements, code and test or configured to support a large program
doing concurrent development with multiple parallel builds and manage
requirements for multiple product lines simultaneously. Although any
application file (document) can be stored and managed, OSEE Define is
tightly integrated with Microsoft Word(c) to store and manage individual
requirement objects (stored in XML) and provide advanced features like
index based searching and showing differences between historical
changes. Integrated tightly with the Action Tracking System, OSEE Define
can be configured to provide advanced configuration management for any
set of requirements object.

