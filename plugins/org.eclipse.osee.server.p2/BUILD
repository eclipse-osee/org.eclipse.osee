load("//:osgi.bzl", "server_p2")


server_p2(
    name = "sp2",
    arg = "org.eclipse.osee.server.jars",
    srcs = ["//org.eclipse.osee.server.p2:p2",
            ],
    visibility = ["//visibility:public"]
)

java_library(
    name = "org.eclipse.osee.server",
    resources = glob(["**/*"]),
    srcs = ["ServerMain.java"],
    deps = [
            "//org.eclipse.osee.account.admin:osgi_jar",
	    "//org.eclipse.osee.account.rest:osgi_jar",
	    "//org.eclipse.osee.account.rest.client:osgi_jar",
	    "//org.eclipse.osee.account.rest.model:osgi_jar",
               "//org.eclipse.osee.activity",
               "//org.eclipse.osee.activity.api",
               "//org.eclipse.osee.app",
               "//org.eclipse.osee.ats.api",
               "//org.eclipse.osee.ats.core",
               "//org.eclipse.osee.ats.rest",
               "//org.eclipse.osee.authentication.admin",
               "//org.eclipse.osee.authentication.ldap",
               "//org.eclipse.osee.authorization.admin",
               "//org.eclipse.osee.cache.admin",
               "//org.eclipse.osee.config.admin",
               "//org.eclipse.osee.console.admin",
               "//org.eclipse.osee.define",
               "//org.eclipse.osee.define.rest.api",
               "//org.eclipse.osee.disposition.rest",
               "//org.eclipse.osee.disposition.rest.model",
               "//org.eclipse.osee.framework.core.packages",
               "//org.eclipse.osee.framework.core.model",
               "//org.eclipse.osee.framework.core.server",
               "//org.eclipse.osee.framework.resource.management",
               "//org.eclipse.osee.framework.server.ide",
               "//org.eclipse.osee.framework.server.ide.api",
               "//org.eclipse.osee.jaxrs",
               "//org.eclipse.osee.jaxrs.server",
               "//org.eclipse.osee.jdbc",
               "//org.eclipse.osee.mim",
               "//org.eclipse.osee.template.engine",
               "//org.eclipse.osee.vcast"
               ]
)

filegroup(
   name = "server_files",
   srcs = [ "runDemo.sh",
            "runPostgreSqlLocal.sh",
            "runHsql.sh",
            "runServer.sh",
            "runDemo.bat",
            "etc/osee.postgresql.json",
            "etc/osee.hsql.json",
            "Configuration/config.ini"
          ],
   visibility = ["//visibility:public"]
)


java_import(
    name = "p2",
    jars = [ 
	       "//org.eclipse.osee.accessor:osgi_jar",
               "//org.eclipse.osee.account.admin:osgi_jar",
	       "//org.eclipse.osee.account.rest:osgi_jar",
               "//org.eclipse.osee.account.rest.client:osgi_jar",
               "//org.eclipse.osee.account.rest.model:osgi_jar",
               "//org.eclipse.osee.activity:osgi_jar",
               "//org.eclipse.osee.activity.api:osgi_jar",
               "//org.eclipse.osee.app:osgi_jar",
               "//org.eclipse.osee.ats.api:osgi_jar",
               "//org.eclipse.osee.ats.core:osgi_jar",
               "//org.eclipse.osee.ats.rest:osgi_jar",
               "//org.eclipse.osee.authentication.admin:osgi_jar",
               "//org.eclipse.osee.authentication.ldap:osgi_jar",
               "//org.eclipse.osee.authorization.admin:osgi_jar",
               "//org.eclipse.osee.cache.admin:osgi_jar",
               "//org.eclipse.osee.config.admin:osgi_jar",
               "//org.eclipse.osee.console.admin:osgi_jar",
               "//org.eclipse.osee.define:osgi_jar",
               "//org.eclipse.osee.define.rest.api:osgi_jar",
	       "//org.eclipse.osee.define.operations.api:osgi_jar",
               "//org.eclipse.osee.disposition.rest:osgi_jar",
               "//org.eclipse.osee.disposition.rest.model:osgi_jar",
	       "//org.eclipse.osee.framework.core:osgi_jar",
	       "//org.eclipse.osee.framework.jdk.core:osgi_jar",
	       "//org.eclipse.osee.framework.logging:osgi_jar",
               "//org.eclipse.osee.logger:osgi_jar",
	       "//org.eclipse.osee.logger.slf4j:osgi_jar",
               "//org.eclipse.osee.framework.core.model:osgi_jar",
               "//org.eclipse.osee.framework.core.server:osgi_jar",
	       "//org.eclipse.osee.framework.resource.management:osgi_jar",
	       "//org.eclipse.osee.framework.server.ide:osgi_jar",
	       "//org.eclipse.osee.framework.server.ide.api:osgi_jar",
	       "//org.eclipse.osee.jaxrs:osgi_jar",
	       "//org.eclipse.osee.jaxrs.server:osgi_jar",
	       "//org.eclipse.osee.jdbc:osgi_jar",
	       "//org.eclipse.osee.mim:osgi_jar",
	       "//org.eclipse.osee.orcs:osgi_jar",
	       "//org.eclipse.osee.orcs.core:osgi_jar",
	       "//org.eclipse.osee.orcs.rest.model:osgi_jar",
	       "//org.eclipse.osee.template.engine:osgi_jar",
	       "//org.eclipse.osee.vcast:osgi_jar",
    ],
    visibility = ["//visibility:public"]
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

