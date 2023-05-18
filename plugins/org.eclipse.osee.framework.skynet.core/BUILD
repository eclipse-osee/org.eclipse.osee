java_library(
        name = "framework_skynet_core",
        resources = ["exsd_files","xml_files","xsd_files"],
        srcs = glob(["**/*.java"]),
        deps = ["java_deps",
		"//framework_core_packages",
		"//:define_api",
		"//:cache_admin",
		"//:jdbc",
		"//:jaxrs",
		"//:framework_messaging",
		"//:framework_messaging_event_res",
		"//:orcs_rest_model",
		"//:framework_core_model",
		"//:framework_core_client",
		"//:framework_plugin_core",
		"//:activity_api"
			],
                visibility = ["//visibility:public"]
              )

filegroup(
    name = "exsd_files",
    srcs = glob(["OSEE-INF/extensionPts/*.exsd"]),
)

filegroup(
    name = "xml_files",
    srcs =
            glob(["OSGI-INF/*.xml"]),
)

filegroup(
    name = "xsd_files",
    srcs =
  	[
    		"OSEE-INF/xsd/export.xsd",
		"OSEE-INF/xsd/publish.xsd"
  	],	
)

java_library(
        name = "java_deps",
        exports = [
                "@maven//:com_fasterxml_jackson_core_jackson_core",
                "@maven//:com_fasterxml_jackson_core_jackson_databind",
                "@maven//:com_fasterxml_jackson_core_jackson_annotations",
                "@maven//:commons_codec_commons_codec",
                "@maven//:org_eclipse_jdt_org_eclipse_jdt_annotation",
                "@maven//:javax_ws_rs_javax_ws_rs_api",
                "@maven//:javax_activation_activation",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                "@maven//:org_slf4j_slf4j_api",
                "@maven//:javax_mail_mail",
		"@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
		"@maven//:org_hsqldb_hsqldb",
		"@maven//:org_jsoup_jsoup",
		"@ext_jars//:eclipse_core",
		"@ext_jars//:eclipse_equinox_common",
		"@ext_jars//:eclipse_osgi_services"
               ],
)

