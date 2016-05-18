When database schema, types or dbinit change, the files in this data directory may need to be updated.  
To do so:

1) Delete C:/Users/<account>/hsql and C:/Users/<account>/attr
2) Do a normal AtsClient_Integration_TestSuite
3) Zip up hsql dir to hsql.zip and copy to this directory
4) Zip up attr to binary_data.zip and copy to this directory
5) Re-run AtsServer_Integration_TestSuite and fix any errors