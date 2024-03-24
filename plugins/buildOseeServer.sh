bazel build org.eclipse.osee.server.p2:osee_server_zip;rm -rf osee_server_zip;unzip bazel-bin/org.eclipse.osee.server.p2/org.eclipse.osee.server.p2.zip -d osee_server_zip;
cd osee_server_zip/osee_server

