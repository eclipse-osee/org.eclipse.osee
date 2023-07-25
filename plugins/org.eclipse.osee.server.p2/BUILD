java_library(
    name = "org.eclipse.osee.server",
    resources = glob(["**/*"]),
    srcs = ["ServerMain.java"],
    deps = ["//org.eclipse.osee.orcs.p2:orcs_packages",
               "//:account_admin",
               "//:account_rest",
               "//:account_rest_client",
               "//:account_rest_model",
               "//:activity",
               "//:activity_api",
               "//:app",
               "//:ats_api",
               "//:ats_core",
               "//:ats_rest",
               "//:authentication_admin",
               "//:authentication_ldap",
               "//:authorization_admin",
               "//:cache_admin",
               "//:config_admin",
               "//:console_admin",
               "//:define",
               "//:define_api",
               "//:disposition_rest",
               "//:disposition_rest_model",
               "//framework_core_packages",
               "//:framework_core_model",
               "//:framework_core_server",
               "//:framework_resource_management",
               "//:framework_server_ide",
               "//:framework_server_ide_api",
               "//:jaxrs",
               "//:jaxrs_server",
               "//:jdbc",
               "//:mim",
               "//:template_engine",
               "//org.eclipse.osee.vcast:vcast"
               ]
)

filegroup(
   name = "equinox_jar",
   srcs = ["plugins/org.eclipse.equinox.launcher_1.5.700.v20200207-2156.jar"]


)


genrule(
   name = "runDemo",
   srcs = ["equinox_jar"],
   outs = ["out.txt"],
   cmd = "java -server \
-Xmx3G \
-Dorg.osgi.service.http.port=8089 \
-Dlogback.configurationFile=logback-dev.xml \
-Dorg.eclipse.equinox.http.jetty.context.sessioninactiveinterval=3600 \
-Dcm.config.uri=etc/osee.hsql.json \
-Dosee.authentication.protocol=demo \
-Dosee.application.server.data=demo/binary_data \
-jar $(location :equinox_jar) -console -consoleLog; pwd; ls > $@",
)


