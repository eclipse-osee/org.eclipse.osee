When database schema, types or dbinit change, the files in this data directory may need to be updated.  
To do so:

1) Delete C:/Users/<account>/hsql and C:/Users/<account>/attr
2) Do a normal AtsClient_Integration_TestSuite, stop before ClientResourceTest
3) Zip up hsql dir to hsql.zip and copy to this directory
4) Zip up attr to binary_data.zip and copy to this directory
5) Follow instructions in org.eclpise.osee.x.server.integration.test/README

All canned-db test bundles should be updated with the same zip files, there are 
- search for hsql.zip for all locations
