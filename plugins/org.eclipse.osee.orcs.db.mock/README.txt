This bundle provides mocks for use in testing and also supports the OrcsIntegrationbyClassRule and 
OrcsIntegrationByMethodRule.

These rules will expand a canned ATS DbInit-ed database from the files located in the /data directory
and provide a local database connection for integration type tests.

When database schema, types or dbinit change, the files in the /data directory may need to be updated.  
To do so:

1) Delete C:/Users/<account>/hsql and C:/Users/<account>/attr
2) Do a normal AtsClient_Integration_TestSuite, stop at AtsTest_AllAts_Suite.setup
3) Zip up hsql dir to hsql.zip and copy to this directory
4) Zip up attr to binary_data.zip and copy to this directory
5) Re-run the local Test Suite and fix any errors

Note: These database files are written out to users home directory in a dir named after test

All canned-db test bundles should be updated with the same zip files and tests updated accordingly. 
- search for hsql.zip for all locations
