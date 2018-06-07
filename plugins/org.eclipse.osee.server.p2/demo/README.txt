When database schema, types or dbinit change, the files in this data directory may need to be updated.  
To do so:

1) Delete C:/Users/<account>/hsql and C:/Users/<account>/attr
2) Launch OSEE_Application_Server_[HSQLDB]
3) Run AtsClient_Integration_TestSuite, stop at end of DemoDbPopulateSuite
4) Terminate and relaunch OSEE_Application_Server_[HSQLDB]
5) Terminate OSEE_Application_Server_[HSQLDB] again
6) Zip up hsql dir to hsql.zip and copy to this directory
7) Zip up attr to binary_data.zip and copy to this directory


All canned-db test bundles should be updated with the same zip files, there are 
- search for hsql.zip for all locations
