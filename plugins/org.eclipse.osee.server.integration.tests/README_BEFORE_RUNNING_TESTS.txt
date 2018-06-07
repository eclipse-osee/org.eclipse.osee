DO NOT RUN THIS SUITE AS JUNIT PLUGIN TEST (it will connect to Production DB)

This is an integration test suite run with Application Server.  During the build, the pom.xml file calls to expand
the org.eclipse.osee.server.p2/demo database zips into target area, and then launches an OSEE Application Server at that path.
S
To run test locally:
1) unzip org.eclipse.osee.server.p2/demo/hsql.zip to your home dir
2) unzip org.eclipse.osee.server.p2/demo/binary_data.zip to your home dir
3) Launch local hsql application server
4) Run ServerIntegrationTestSuite launch config
