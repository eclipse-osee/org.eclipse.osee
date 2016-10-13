This test bundle contains both JUnit and Integration test suites. Please read before running.

OrcsDb_JUnit_TestSuites and all suites in /internal can be run as JUnit with right-click > 
Debug As > JUnit

OrcsDb_All_TestSuite

integration/OrcsDb_Integration_TestSuites can be run through OrcsDbTestSuite
 
This suite uses canned database from org.eclpise.osee.orcs.db.mock.  
Database files will be copied out to users home directory for tests that require a db.
If schema or breaking db changes are made, update database from instructions in that plugin.
It does not require running an separate application server.

