[Coverage Home](http://wiki.eclipse.org/OSEE/Coverage/Users_Guide)

![osee_coverage.png](/docs/images/osee_coverage.png "osee_coverage.png")

# Coverage Overview

The Coverage application allows provides for the configuration
management and tracking of coverage disposition efforts throughout a
project.

  - Allow importing of coverage results from user created or industry
    coverage tools
  - Track / disposition code coverage tasks at file level
  - Allow assignment / statusing of coverage dispositioning to be worked
  - Show which items are dispositioned
  - Allow customer to see file analysis, summary, disposition
  - Provide ability to generate metrics on coverage from current and
    previous runs
  - Provide ability to re-run reports to see what has changed
  - Provide ability for code coverage issue to be "ignored" in future
    runs
  - Document and report showing all coverage that was ran
  - Allow creating, relating and tracking of changes to work products
    based on coverage disposition results.

## Terms

### Coverage Package

  -
    Effort of coverage that includes multiple imports, reports, exports
    and metrics
  - **Requirements Traceability**
    Traceability between what Test Units are expected to cover each
    Coverage Item
  - **Coverage Traceability**
    Traceability between what Test Unit actually covered each Coverage
    Item
  - **Coverage Method**
    Coverage can be met by different methods: Deactivated Code,
    Exception Handling, Test Unit coverage, Test Procedure coverage, etc
  - **Coverage Import (CImp)**
    Single import of coverage information from Coverage Source. Contains
    all Coverage Units
  - **Coverage Source (CS)**
    Location of source coverage information to import from
  - **Coverage Unit (CU)**
    Single code unit (file/procedure/function) that can contain other
    Coverage Unit or Coverage Items
  - **Coverage Item (CI)**
    Single executable line of code whether it is covered or not
  - **Test Unit (TU)**
    Single test that can cover multiple Coverage Items
  - **Coverage Issue**
    Problem due to code/test script which causes Expected Test Units to
    not match actual coverage Test Units. Coverage Item should be marked
    as exempt from coverage due Deactivated Code, Exception Handling or
    any other User Defined Categories
  - **Work Product**
    Product that the coverage is being run against and will be changed
    based on the disposition results.
  - **Work Product Task**
    Task associated with modifying a Work Product based on findings from
    disposition. This is done through the OSEE ATS (Action Tracking
    System).