This bundle provides mocks for use in testing and also supports the AtsIntegrationbyClassRule and 
AtsIntegrationByMethodRule.

These rules will expand a canned ATS DbInit-ed database from the files located in the /data directory
and provide a local database connection for integration type tests.

When database schema, types or dbinit change, the files in this data directory may need to be updated.  
To do so:

1) Delete C:/Users/<account>/hsql and C:/Users/<account>/attr
2) Do a normal AtsClient_Integration_TestSuite, stop before AtsTest_AllAts_Suite
3) Re-launch the application server and client to prime the database 
	- Close client/server, open hsql directory.  .log file should not be large, .script should
4) Zip up hsql dir to hsql.zip and copy to this directory
5) Zip up attr to binary_data.zip and copy to this directory
6) Re-run AtsServer_Integration_TestSuite and fix any errors

All canned-db test bundles should be updated with the same zip files

Run the following in a git bash
cd /c/UserData/git_fix/org.eclipse.osee/plugins/org.eclipse.osee.ats.mocks/data

Paste the following in git bash
cp binary_data.zip ../../../../lba.osee/plugins/lba.osee.server.p2/demo/
cp hsql.zip ../../../../lba.osee/plugins/lba.osee.server.p2/demo/
cp binary_data.zip ../../org.eclipse.osee.orcs.db.mock/data
cp hsql.zip ../../org.eclipse.osee.orcs.db.mock/data
cp binary_data.zip ../../org.eclipse.osee.server.p2/demo
cp hsql.zip ../../org.eclipse.osee.server.p2/demo
