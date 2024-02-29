bazel build org.eclipse.osee.server.p2:osee_server_zip;rm -rf osee_server_zip; unzip bazel-bin/org.eclipse.osee.server.p2/org.eclipse.osee.server.p2.zip -d osee_server_zip;
rm -rf org.eclipse.osee.server.p2/target/server/plugins;
cp -r osee_server_zip/zip1/plugins org.eclipse.osee.server.p2/target/server;
cd org.eclipse.osee.server.p2/target/server/
