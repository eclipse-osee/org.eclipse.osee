

genrule(
  name = "osee_server_zip",
  srcs = ["//org.eclipse.osee.server.p2:server_plugins","//org.eclipse.osee.server.p2:server_files","//org.eclipse.osee.server.p2:json","//org.eclipse.osee.server.p2:configuration"],
  tools = ["@bazel_tools//tools/zip:zipper"],
  outs = ["org.eclipse.osee.server.p2.zip"],
  cmd = "cp -r $(locations server_plugins) plugins;rm -rf etc;mkdir etc;cp -r $(locations //org.eclipse.osee.server.p2:json) etc;rm -rf configuration; mkdir configuration;cp -r $(locations //org.eclipse.osee.server.p2:configuration) configuration;mkdir zip1;cp -r $(locations //org.eclipse.osee.server.p2:server_files) zip1;mv plugins zip1;mv configuration zip1;mv etc zip1;echo zip2 plugins;$(location @bazel_tools//tools/zip:zipper) c thezip.zip  zip1/* zip1/plugins/* zip1/etc/* zip1/configuration/*;cp -r thezip.zip $@",
)


filegroup(
   name = "osee_server_plugins",
   srcs = [ "//org.eclipse.osee.accessor:osgi_jar",
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
	    "//org.eclipse.osee.define.operations.api:osgi_jar",
            "//org.eclipse.osee.define.rest.api:osgi_jar",
            "//org.eclipse.osee.disposition.rest:osgi_jar",
            "//org.eclipse.osee.disposition.rest.model:osgi_jar",
	    "//org.eclipse.osee.framework.core.model:osgi_jar",
            "//org.eclipse.osee.framework.core.server:osgi_jar",
	    "//org.eclipse.osee.framework.jdk.core:osgi_jar",
            "//org.eclipse.osee.framework.core:osgi_jar",
            "//org.eclipse.osee.framework.logging:osgi_jar",
	    "//org.eclipse.osee.logger.slf4j:osgi_jar",
            "//org.eclipse.osee.logger:osgi_jar",
            "//org.eclipse.osee.framework.resource.management:osgi_jar",
            "//org.eclipse.osee.framework.server.ide:osgi_jar",
            "//org.eclipse.osee.framework.server.ide.api:osgi_jar",
            "//org.eclipse.osee.jaxrs:osgi_jar",
            "//org.eclipse.osee.jaxrs.server:osgi_jar",
            "//org.eclipse.osee.jdbc:osgi_jar",
            "//org.eclipse.osee.mim:osgi_jar",
	    "//org.eclipse.osee.orcs.account.admin:osgi_jar",
	    "//org.eclipse.osee.orcs.authorization:osgi_jar",
	    "//org.eclipse.osee.orcs.core:osgi_jar",
	    "//org.eclipse.osee.orcs.db:osgi_jar",
	    "//org.eclipse.osee.orcs.rest.model:osgi_jar",
	    "//org.eclipse.osee.orcs.rest:osgi_jar",
	    "//org.eclipse.osee.orcs:osgi_jar",
	    "//org.eclipse.osee.testscript:osgi_jar",
            "//org.eclipse.osee.template.engine:osgi_jar",
            "//org.eclipse.osee.vcast:osgi_jar"
           ],
visibility = ["//visibility:public"]   
)

genrule(
  name = "build_no",
  srcs = ["osee_server_plugins",],
  outs = ["serv_plus"],
  cmd = "export version=1.0.0.v$$(date  +%Y%m%d%H%M);echo version: $$version;mkdir osee_plugins;for FILE in $(SRCS); do chmod 777 *; cp $$FILE  $${FILE%.*}_$$version.jar;mv $${FILE%.*}_$$version.jar osee_plugins;pwd;done;echo after move;chmod -R 777 osee_plugins; cp -r osee_plugins $@"
)

genrule(
  name = "server_plugins",
  srcs = ["build_no","third_party_deps"],
  outs = ["plugins"],
  cmd = "mkdir s1p;cp -r $(locations build_no)/* s1p; cp -r $(locations //org.eclipse.osee.server.p2:third_party_deps) s1p;chmod -R 777 s1p; cp -r s1p $@;"
)

filegroup(
   name = "server_files",
   srcs = [ "runDemo.sh",
            "runPostgreSqlLocal.sh",
            "runHsql.sh",
            "runServer.sh",
            "runDemo.bat",
          ],
)

filegroup(
   name = "json",
   srcs = [ "etc/osee.postgresql.json",
            "etc/osee.hsql.json",
          ],
)

filegroup(
   name = "configuration",
   srcs = ["configuration/config.ini"],
)

filegroup(
    name = "third_party_deps",
    srcs = [
	    "@maven//:commons_codec_commons_codec",
	    "@maven//:com_fasterxml_jackson_core_jackson_annotations",
            "@maven//:com_fasterxml_jackson_core_jackson_core",
            "@maven//:com_fasterxml_jackson_core_jackson_databind",
	    "@maven//:com_fasterxml_jackson_jaxrs_jackson_jaxrs_base",
	    "@maven//:com_fasterxml_jackson_jaxrs_jackson_jaxrs_json_provider",
	    "@maven//:com_fasterxml_jackson_dataformat_jackson_dataformat_yaml",
	    "@maven//:com_fasterxml_jackson_datatype_jackson_datatype_jsr310",
            "@maven//:com_vladsch_flexmark_flexmark_ext_gfm_tasklist",
	    "@maven//:com_vladsch_flexmark_flexmark_ext_tables",
	    "@maven//:com_vladsch_flexmark_flexmark_util_ast",
	    "@maven//:com_vladsch_flexmark_flexmark_util_data",
	    "@maven//:com_vladsch_flexmark_flexmark",
            "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
	    "@maven//:jakarta_activation_jakarta_activation_api",
            "@maven//:javax_activation_activation",
            "@maven//:javax_mail_mail",
            "@maven//:javax_servlet_javax_servlet_api",
	    "@maven//:com_googlecode_javaewah_JavaEWAH",
            "@maven//:org_antlr_antlr_runtime",
	    "@maven//:org_apache_felix_org_apache_felix_gogo_runtime",
	    "@maven//:org_apache_felix_org_apache_felix_gogo_command",
	    "@maven//:org_apache_felix_org_apache_felix_gogo_shell",
	    "@maven//:org_apache_felix_org_apache_felix_scr",
	    "@maven//:org_apache_cxf_cxf_core",
	    "@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
	    "@maven//:org_apache_cxf_cxf_rt_rs_client",
	    "@maven//:org_apache_cxf_cxf_rt_rs_security_oauth",
	    "@maven//:org_apache_cxf_cxf_rt_rs_security_oauth2",
	    "@maven//:org_apache_cxf_cxf_rt_security",
	    "@maven//:org_apache_cxf_cxf_rt_transports_http",
	    "@maven//:org_apache_cxf_cxf_rt_rs_service_description",
	    "@maven//:org_apache_cxf_cxf_rt_rs_json_basic",
	    "@maven//:org_apache_cxf_cxf_rt_rs_security_jose",
	    "@maven//:org_apache_cxf_cxf_rt_rs_security_jose_jaxrs",
            "@maven//:org_eclipse_jgit_org_eclipse_jgit",
            "@maven//:org_eclipse_jdt_org_eclipse_jdt_annotation",
            "@maven//:org_eclipse_jdt_org_eclipse_jdt_core",
	    "@maven//:org_hsqldb_hsqldb",
	    "@maven//:org_yaml_snakeyaml",
	    "@maven//:io_github_classgraph_classgraph",
	    "@maven//:org_apache_commons_commons_lang3",
	    "@maven//:org_jsoup_jsoup",
	    "@maven//:net_oauth_core_oauth",
	    "@ext_jars//:eclipse_equinox_common",
            "@ext_jars//:eclipse_core",
	    "@ext_jars//:emf",
	    "@ext_jars//:apache_commons_lang",
	    "@ext_jars//:javax",
            "@ext_jars//:nebula",
	    "@ext_jars//:rmf",
	    "@ext_jars//:eclipse_osgi_services",
	    "@ext_jars//:misc",
	    "@ext_jars//:poi",
	    "@ext_jars//:swagger",
	    "@ext_jars//:jgit",
	    "@eclipse_core_net//jar",
            "@equinox_security//jar",
	    "@jcraft//jar"

         ],
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

