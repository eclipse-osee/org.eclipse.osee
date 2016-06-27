DO NOT RUN THIS SUITE AS JUNIT PLUGIN TEST (it will connect to Production DB)

Integration test suite run with Application Server
Data gets written to target directory from org.eclipse.osee.x.server.p2/demo via the pom.xml

To run locally:
1) unzip org.eclipse.osee.x.server.p2/demo/hsql.zip to your home dir
2) unzip org.eclipse.osee.x.server.p2/demo/binary_data.zip to your home dir
3) Launch local hsql application server
4) Run Server Integration Test Suite as JUnit Test
