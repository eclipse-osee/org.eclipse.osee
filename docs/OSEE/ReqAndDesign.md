## Fast, highly relevant search

### Requirements

  - support regular expression searches
  - support case-insensitive and case-sensitive (use second pass)
  - history on name and url changes
  - history on search terms changes
  - access control

### Design

  - Query Parser Steps

<!-- end list -->

1.  make search text lowercase
2.  split search text into search terms by white space, allow quotes to
    enclose a search term that includes white space
3.  strip leading and trailing characters that are neither letters or
    digits
4.  remove trailing 's (possessive case)
5.  discard single char terms that are not digits (only implement if
    proven useful)
6.  transform words into singular form
7.  discard duplicate terms
8.  discard stop words. see
    <http://www.gobloggingtips.com/wp-content/uploads/2014/08/Google-stopwords.txt>
9.  truncate term if longer than 128 characters (max token length)
10. sort terms
11. compute query_hash with Arrays.deepHashCode() on the sorted terms
    from previous step

| source text    | options | matching query | non-matching query |
| -------------- | ------- | -------------- | ------------------ |
| C. S. Lewis    | tbd     | C S Lewis      | CS Lewis           |
| 75             | tbd     | 7              | 0.7                |
| 75%            | tbd     | 75             | %                  |
| nav_power_on | tbd     | power_on      | power on           |
| Books          | tbd     | booK           | bookes             |
| source text    | tbd     | matching query | non-matching query |

Test Cases

  - Query options

;

:\* Case sensitive

:\* Match word order (no effect with 1 search term)

:\* Include leading punctuation

:\* Include trailing punctuation

:\* Whole content match

:\* Whole word match

  - Regular expression match- a list of regular Expressions that are
    individually matched against terms in the first pass, and a single
    regular expression that is applied in the second pass or blank to
    skip second pass (no other option should be combined with this one)

#### Data Model

`osee_search_url(BIGINT url_id, Varchar(128) title, Varchar(2048) url, valid_test_time) PRIMARY KEY (url_id) ORGANIZATION INDEX`

:\* title - search terms will be extracted from the title using the
query parser steps and stored with rank name

:\* url uniqueness is enforced via atomic insert command

:: INSERT INTO osee_search_url m1 (match_id, name, url,
valid_test_time) SELECT ?,?,?,? FROM dual WHERE NOT EXISTS (SELECT 1
From osee_search_url m2 WHERE m2.url = ?)

:\* maintenance operation identifies dead links. valid_test_time is
updated with current date and time when the link is tested as valid

`osee_search_term(Varchar(128) text, SMALLINT match_type_id, BIGINT app_id, SMALLINT rank_type_id, BIGINT term_group_id, SMALLINT term_group_size, BIGINT match_id) PRIMARY KEY (text, match_type, term_group_id, match_id) ORGANIZATION INDEX COMPRESS 2`

:\* text is normalized search term (see query parser steps)

:\* match type

:: artifact, attribute, relation (for rationale), url type

:\* app_id

:\* match_id can be a osee_search_url.url_id or an attribute or
relation gamma_id (or possibly extended to any other id)

:\* the rank allows early exclusion of unwanted term types such as
content with the sql criteria that only includes rank \> 20

:\* Results are returned in ascending rank order of the sum of all ranks
for a match_id

:\* rank_type_id:

  -

      -
        Tuple3Type{RankTypeId, Long, String} RankType =
        Tuple3Type.valueOf(SearchFamily, 12L); // rank_type, rank
        (larger is higher), rank
          -
            1 \[220\] unique id - (employee id, charge number, USPS
            Tracking number, etc.)
            2 \[200\] keyword - terms that when searched together should
            represent a top result
            3 \[ 40\] tag (meta) - terms applied by human intelligence
            to classify and categorize
            4 \[ 20\] content - terms extracted from the content body
            using the query parser steps
            5 \[ 10\] team - relevant to a given team

type name

:\* term_group_id - 1 when the term_group_size is 1; otherwise
randomly generated id used to group search terms to apply a rank only
when all terms in the group match

`osee_search_query(BIGINT query_hash, Varchar(500) query_text, SMALLINT status, BIGINT top_match)`

:\* CREATE INDEX OSEE_SEARCH_QUERY_TEXT_IDX ON OSEE_SEARCH_QUERY
(QUERY_TEXT);

:\* query_hash is computed using the Query Parser Steps

:\* query_text exactly as entered

:\* status

::\* no results

::\* unverified results

::\* verified results

:\* top_match is the top match returned the last time this query was
run. Useful for sharing links/ searches. consider supporting more than 1
top matches

:\* search prediction based on query_text with past queries of user
sorted first

`osee_search_share(BIGINT to_user, BIGINT from_user, BIGINT query_hash, timestamp time) PRIMARY KEY (to_user, from_user, query_hash) ORGANIZATION INDEX COMPRESS 2`

-----

`osee_search_history(BIGINT item_id, BIGINT user_id, timestamp) PRIMARY KEY (item_id, user_id, timestamp) ORGANIZATION INDEX COMPRESS 2`

#### Rest Interface

:\* Allow request to specify max number of links or all (default is 25).
Top ranked n links are returned as json {id, name, url, search terms,
rank}

:\* in order to set access control on a link you have to have the access
to the group you are setting it to, you can add anyone else

#### User Interface

When entering a new URL the name is initially populated from the web
page title

`Search results will include a why link that provides the rank information and allows users to improve this information`

## Fast, versioned tuple service

### Requirements

  - Creation and deletion of tuples shall have transactional history
  - tuple operations shall be near constant time
  - shall efficiently support tuples of numbers and strings of length 2,
    3, and 4

#### Uses Cases

  - multiple tags per transaction with with history

` osee_tuple3(tx_key_type, branch_id, tx_id, attr_id, gamma_id)`

  - cross branch linking

` osee_tuple2(relation_type, a_art_id, b_art_id, gamma_id)`

  - user defined tables

` osee_tuple2(BIGINT my_table_type, BIGINT row_id, String row_data, gamma_id)`

:\* the row_id allows the rows to be returned in order, also allows
showing history of a given row

:\* the row_data stores the json representing the row data

### Design

#### Data Model

  - Tuples provide a high performance general purpose storage that
    leverages index-organized tables. Traditional database tables are
    unorganized heaps that require indexes for fast access. If the size
    of individual rows can be kept small, then index-organized tables
    can be used and remove the overhead and duplication of also having
    the unorganized heap. Additionally, these tables support key
    compression which further improves performance and reduces storage
    requirements.

`osee_tuple2 (tuple_type, element1, element2, gamma_id) PRIMARY KEY (tuple_type, element1, element2) ORGANIZATION INDEX COMPRESS 2;`
`osee_tuple3 (tuple_type, element1, element2, element3, gamma_id) PRIMARY KEY (tuple_type, element1, element2, element3) ORGANIZATION INDEX COMPRESS 2;`
`osee_tuple4 (tuple_type, element1, element2, element3, element4, gamma_id) PRIMARY KEY (tuple_type, element1, element2, element3, element4) ORGANIZATION INDEX COMPRESS 3;`
`unique index: gamma_id`

  - Each tuple has a type. See
    org.eclipse.osee.framework.core.enums.CoreTupleTypes
  - gamma_id provides for branching and history. Tuples can be created
    and deleted on a branch using transactions
  - elements can be of type number or string. A string is stored as a
    key to the osee_key_value table
  - In the table each tuple element is stored as a BIGINT and if it
    represents a String then this number is a key into the
    OSEE_KEY_VALUE table where the string is stored. This approach
    keeps each row in a tuple table to be of a small fixed size.

## Product Line Engineering

### Requirements

  - User can quickly select branch-view without requiring knowledge of
    corresponding version of product line. Select by branch-view name
    (i.e. Country X San) which is short hand for selecting My Product
    Line 2.0 branch with Country X view.
  - View applicability has history and can vary independently by branch.
  - External tools can join on view applicability in their sql queries
  - Need a list of branch-views to show in branch selection dialogs

### Definitions

  - Applicability_Id - just an id
      - part of osee_txs table, and thus versioned.
      - applicability id per artifact, attribute and relation.
      - applicability can be on sub attribute level using grammar within
        Word or other content
  - Feature - decomposed item of product or configuration
      - is an Applicability_Id
      - represented as unique NAME, ID
      - can be a single Feature or some mathematical combination of
        Features. eg: ROBOT_ARM_LIGHT, ROBOT_SPEAKER, ROBOT_ARM_LIGHT & ROBOT_SPEAKER
  - Variant - combination of Features that make up a version of a
    product
      - is an Applicability_Id
      - represented as unique NAME, ID
  - View - cross-section of a Branch that shows items applicable to that
    view
      - either a Configuration, Feature or grouping
      - read-only. Editing can only be done on parent Branch
      - what is shown is computed from selected Configs, Features

### Design

#### Data Model

  - item applicability is fully transactional on all branches

`osee_txs (branch_id, transaction_id, gamma_id, tx_current, mod_type, app_id)`

Default value for app_id in osee_txs is 1 which means in all views
(base). View artifacts are on the product line branches.

  - view applicability

`osee_tuple2 (CoreTupleTypes.ViewApplicability, ArtifactId view, String applicabilityText, gammaId)`

The view applicability tuples and view artifacts are on the product line
branches thus maintaining full transactional history. The applicability
text is the feature applicability text or the name of the of the view
for non product line engineering use of views. Each view will be mapped
to applicability id 1 (Base) so that base will be included for all
configs by simply joining on the view_id. The featureApplicabilityId is
the randomly generated key to applicabilityText in osee_key_value.

  - named branch-views

`osee_tuple3 (CoreTupleTypes.BranchView, BranchId branch, ArtifactId view, String name, BIGINT gammaId)`

Views are stored as artifacts of type "Branch View" on the branch their
are views of. Named branch-views are stored as tuples on the common
branch. These are used to populate the list of branches faster than
searching and loading every "Branch View" on every branch. The branch
view is accessed via BranchId.getView()

  - feature applicability definition

`osee_tuple4 (app_feature_tuple_type, app_id, feature_id, value_attr_id, and_or_flag, gamma_id)`

The Feature artifact has an attribute for each possible value. It also
has a name, description, abbreviation, and type (single or
multi-valued).

`osee_tuple3 (compound_app_tuple_type, app_id, app_id, and_or_flag, gamma_id)`

  - feature applicability involving more than one feature is stored as a
    list of feature applicability (recursive to any depth).

<!-- end list -->

``` sql
-- return all items (referenced by gamma_id) currently in a view of a branch
select * from osee_txs txs, osee_tuple2 app where branch_id = ? and tx_current = 1 and txs.app_id = app.e2 and e1 = ?;
-- applicability clause in SQL is only applied when using a branch view
-- select applicability for a given view (e1)
select e2, value from osee_txs txs, osee_tuple2 app, osee_key_value where tuple_type = 2 and e1 = ? and app.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1 and e2 = key;
```

## Activity Logging and Monitoring

### Requirements

  - shall handle creation/update of fine-grained log entries for at
    least 500 concurrent users
  - shall support logging by OSEE and other applications
  - the web of log entries related to an individual instance of a user
    request shall be able to be hierarcically related
  - log entries shall be quickly accessible based on any combination of
    source, user, timestamp, log type, duration, status
  - log entries shall be accessible (especially) when an application
    server is unresponsive
  - log entries shall be available until they are deleted by an admin or
    admin policy (applied by server automatically)
  - at run-time logging shall be enabled/disabled based on any
    combination of user, source, log level, and type
  - access control shall be applied at the log entry type basis

### Design

#### Data Model

  - osee_activity db table

:\* Log entry in Java: BIGINT entryId, BIGINT parentId, BIGINT typeId,
BIGINT startTime, BIGINT duration, BIGINT agentId, BIGINT status, String
msgArgs

:\* entry_id - random long returned for log method call

:\* parent_id - id of entry used for grouping this and related entries.
For root entries, it is the negative of session id of the client or the
server id. Ranges are used to group by client/server kind (IDE client,
app server, rest client).

:\* type_id - foreign key to type_id in osee_log_type table

:\* start_time - long with ms since epoch

:\* duration - starts at -1 and is never updated if duration does not
apply, otherwise updates when the associated job ends with duration in
ms

:\* account_id - long account id (the account_id returned from account
management services

:\* status:

` 0     initial value`
` 1-99  percent complete`
` 100   completed normally`
` 101   completed abnormally`

:\* msg_args newline separated list of strings used with
String.format(msg_format, msg_args);

  - Each new log entry's parent_id, agent_id is mapped to the thread
    that created it (only the most recent mapping per thread is
    maintained)
  - When an exception is thrown, it is logged as a child of the parent
    corresponding to the current thread. If no mapping is found in
    ConcurrentHashMap{Thread, Pair{Long, Long}}()

<!-- end list -->

  - Log entry type in DB: type_id, log_level, software_unit,
    message_format
      - type_id - a fine-grained application defined type, random id,
        defined as tokens and stored in the db for cross application
        support
      - log_level - as defined by java.util.logging.Level
      - module - application defined name of the software unit that uses
        this log entry type
      - msg_format - format defined by
        [java.util.Formatter](http://docs.oracle.com/javase/6/docs/api/java/util/Formatter.html)
        or if blank the raw message details are used directly

#### High Performance

  - 2 ConcurrentHashMap are allocated with an initial configurable size:
    newLogEntires, updatedEntries
  - newly created log entries are added to newLogEntires using the
    entry_id as the key and the array of sql insert parameters as the
    value
  - updated log entries are checked for in newLogEntires and updated if
    they exist, otherwise the update map is checked and updated if
    exists, else added to updatedEntries
  - A timer tasks runs at a configurable (short) periodic rate and batch
    inserts the log entries in the insert map and then runs the updates.
    This means that any update to a log entry that occurs in less than
    this configured time will not require a database update (i.e.
    writing the duration of a short operation). This also means only one
    thread writes to the log table per JVM.
  - new DrainingIterator(newLogEntires.values().iterator()) is used to
    iterate through the values and remove them one at a time during the
    batch insert
  - upon server shutdown must flush log
  - IDE client will directly use the same service that is used on the
    server
  - [data structure
    options](http://stackoverflow.com/questions/8203864/the-best-concurrency-list-in-java)
  - [Optimize JDBC
    Performance](http://www.precisejava.com/javaperf/j2ee/JDBC.htm)

#### Java API

``` java
 Long createThreadEntry(long userId, Long typeId);

 Long createThreadEntry(long userId, Long typeId, long parentId);

 Long createEntry(Long typeId, Object... messageArgs);

 Long createEntry(Long typeId, Long parentId, Object... messageArgs);

 void updateEntry(Long entryId, Long status);

 Long createExceptionEntry(Throwable throwable);
```

  - The first interface to the logging data can be the basic REST
    navigation

## Exception Handling

### Requirements

  - avoid unnecessary wrapping of exceptions

### Design

[Checked exceptions I love you, but you have to
go](http://misko.hevery.com/2009/09/16/checked-exceptions-i-love-you-but-you-have-to-go/)
[Why should you use Unchecked exceptions over Checked
exceptions](http://jyops.blogspot.com/2012/03/why-should-you-use-unchecked-exceptions.html)
[Clean Code by Example: Checked versus unchecked
exceptions](http://convales.blogspot.com/2012/09/clean-code-by-example-checked-versus.html)

  - Use application specific exceptions that extend RuntimeException -
    application specific allows for setting exception breakpoints in the
    debugger
  - Do not declare any run-time exceptions in any method signatures

## Artifact Query and Loading

### Requirements

  - As a developer and user, I would like quick searching/loading in as
    few sql statements provided from the server in a single call
      - Allow search rest call on server to load artifacts and just send
        data back necessary to construct artifacts/relations instead of
        client making more database calls to load
      - Consolidate the searching/loading from 8 queries to 1 or 2
  - As a developer, I would like to specify which relations and
    attributes and artifact data to load for the results of my query.
      - Provide selective loading
  - As a developer, I would like to search for artifacts and then load
    related artifact to a certain level. Example: ATS loads 3 Team
    Workflows, then needs to bulk load related actions, tasks and
    reviews.
      - Add ability to bulk load to search criteria. Include load level
        and list of relations to follow.
  - As a developer, I would like to be able to load artifacts with only
    artifact data (ids, types, etc) and a few attributes like Name.
    Example: Artifact Explorer shouldn't need to load the entire
    artifact when only the Name, Type and whether it has children is
    needed. Another example is Quick Search results, where all you need
    is the basic data from the results until the user is interested in
    selecting a resulting item.
      - Allow ability to specify artifact data and minimal attrs or
        relations.
      - Also provide case where want to "Show Relations" which would
        require knowing what relations exist and their types.
  - fast, scalable

### Design

## Dynamic Web App

### Requirements

  - As a developer and user, I would like to be able to specify the
    content of a web page by providing a JSON description.
      - Allow a group of controls to be displayed, and to specify REST
        calls for obtaining content and changing content.

### Design

We have adopted JSON Forms to supply this capability, but have written
our own Angular JS controls to support the capability to have each
control have its own REST calls. All of the controls in the box below
additionally provide links for getting and setting data. This allows the
page to update and check data one control at a time.

`Text box control: Similar to the JSON Forms text control.`
`Checkbox control: Similar to the JSON Forms checkbox.`
`Multi-line text box control: Provides a resizeable multi line text box.`
`Dropdown control: Similar to the JSON Forms dropdown control.`
`Multi-select Dropdown control: Wraps popular dotansimha Angular JS Multiselect Dropdown.`
`Program-version control: a special case of the multi select dropdown to support selection of OSEE ATS program and version.`
`Table control: wraps Angular UiGrid control to allow for table data.`

## Distributed OSEE Repositories

### Level 0 capability

Branches are private to their own OSEE repository, but distributed IDs
are used to avoid future ID collisions between OSEE repositories.
Implementation Note: IDs are randomly generated, except transactions
which are numbered sequentially on a per branch basis (avoids
transaction collisions since transaction ids can't be random due to max
transaction calculations)

### Level 1 capability

A branch may be cloned from the source OSEE repository into a
destination repository as a read-only branch that will automatically
update when network connectivity exists between the two repositories.
Note: any repository may request to clone a branch from another
repository. If the request is granted the source repository marks its
branch as source and then allows the clone.

### Level 2 capability

The branch updates for a cloned branch will also be able to be performed
via an export of the source updates to the file system followed by an
import in the OSEE that has the cloned branch.

### Level 3 capability

A branch may be bidirectionally shared between OSEE repositories. The
OSEE repository of the source branch tracks the branch lock. Other OSEE
repositories start with a clone of the shared branch. In order for any
OSEE to commit a transaction to the shared branch, it will first acquire
the branch lock from the source repository. All transactions from the
source are copied to the other repositories.

## Fast, Robust Attribute Storage

### Requirements

  - System can store and provide fast access to small attributes in bulk
  - System can store and provide fast access at least 1 MB attributes
  - System can store at least 1GB attributes

### Design

#### Data Model

  - each osee attribute type explicitly lists supported search types:
    exact match, case insensitive exact match, partial match, or none

`osee_search_hash (`
` match_type`
` app_id`
` term_hash`
` art_id`
` gamma_id)`

  - osee_attribute.value stores boolean, numeric, enumeration, date,
    strings, and binary

`osee_attribute (art_id, type_id, item_id, order, gamma_id, value)`

Binary data is rare and often large and is never searched directly
(instead it is tokenized and indexed) so Store it in an iot with the
gamma_id as key

``` sql
-- return all attributes for a given artifact currently in a view of a branch
select * from osee_txs txs, osee_tuple2 app where branch_id = ? and tx_current = 1 and txs.app_id = app.e2 and e1 = ?;
-- applicability clause in SQL is only applied when using a branch view
-- select applicability for a given view (e1)
select e2, value from osee_txs txs, osee_tuple2 app, osee_key_value where tuple_type = 2 and e1 = ? and app.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1 and e2 = key;
```

## REST

### Design

  - The path component contains hierarchical data that, along with data
    in the non-hierarchical query component serves to identify a
    resource [RFC3986 3.3 first
    paragraph](https://tools.ietf.org/html/rfc3986#section-3.3)
  - name of JSON keys should be camelCase just like Java
  - each segment in the REST URL has a singular name even the name of a
    collection of resources
  - GET/HEAD/PUT/DELETE shall not use a request message body according
    to originator of REST [Roy T.
    Fielding](https://lists.w3.org/Archives/Public/ietf-http-wg/2020JanMar/0123.html)
  - URL all lowercase with words separated with hyphens
  - MUST use POST when the action is not idempotent. GET, PUT and DELETE
    methods are required to be idempotent.
  - [REST APIs must be
    hypertext-driven](http://roy.gbiv.com/untangled/2008/rest-apis-must-be-hypertext-driven)