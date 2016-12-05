OrcsDb_Integration_TestSuites - Run from launch config with no Application Server running.

DO NOT RUN THIS TEST SUITE WITH Right-Click > Debug As > PDE JUnit Test 
or it will try to connect to the production database.

All integration tests should check to see if production before running.
See PurgeAttributeTest for an example on how to check if production database.

See this bundle's read-me file for more details.