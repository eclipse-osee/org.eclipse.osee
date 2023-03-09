java_library(
    name = "ote_define",
	resources = ["xml_files", "exsd_files"],
	srcs =
                glob(["**/*.java"]),
                deps = ["java_deps"],
                visibility = ["//visibility:public"]
              )

filegroup(
    name = "xml_files",
    srcs =
          [
	    "plugin.xml"
	  ]
	  ,
	visibility = ["//visibility:public"]
)

filegroup(
    name = "exsd_files",
    srcs =
          [
            "OSEE-INF/extensionPts/OutfileParser.exsd"
	  ]
	  ,
	visibility = ["//visibility:public"]
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
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
		"@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
		"@maven//:org_apache_cxf_cxf_rt_rs_client",
		"@maven//:org_eclipse_jgit_org_eclipse_jgit",
		"@maven//:org_jsoup_jsoup",
		"@maven//:com_jcraft_jsch",
		"@maven//:org_eclipse_jdt_org_eclipse_jdt_core",
		"@maven//:org_apache_geronimo_specs_geronimo_jms_1_1_spec",
                "@maven//:org_apache_activemq_activemq_all",
		"@maven//:org_mockito_mockito_core",
		"@maven//:org_hamcrest_hamcrest_core",
                "@framework_jdk_core//:framework_jdk_core",
		"@framework_core//:framework_core",
		"@framework_core_client//:framework_core_client",
		"@framework_plugin_core//:framework_plugin_core",
		"@framework_ui_swt//:framework_ui_swt",
		"@framework_logging//:framework_logging",
		"@logger//:logger",
		"@activity_api//:activity_api",
		"@framework_messaging_event_res//:framework_messaging_event_res",
		"@framework_server_ide_api//:framework_server_ide_api",
		"@framework_core_server//:framework_core_server",
		"@framework_messaging//:framework_messaging",
		"@cache_admin//:cache_admin",
		"@framework_core_model//:framework_core_model",
		"@jaxrs//:jaxrs",
		"@orcs//:orcs",
		"@orcs_rest_model//:orcs_rest_model",
		"@jdbc//:jdbc",
		"@framework_server_ide//:framework_server_ide",
		"@console_admin//:console_admin",
		"@orcs_core//:orcs_core",
		"@orcs_rest//:orcs_rest",
		"@activity//:activity",
		"@define_api//:define_api",
		"@framework_resource_management//:framework_resource_management",
		"@account_rest_client//:account_rest_client",
		"@account_rest_model//:account_rest_model",
		"@framework_help_ui//:framework_help_ui",
		"@framework_skynet_core//:framework_skynet_core",
		"@framework_ui_plugin//:framework_ui_plugin",
		"@ats_api//:ats_api",
		"@framework_ui_ws//:framework_ui_ws",
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:eclipse_ui",
		"@ext_jars//:swt",
		"@ext_jars//:jface",
		"@ext_jars//:eclipse_core",
		"@ext_jars//:eclipse_equinox_common",
		"@ext_jars//:javax",
		"@ext_jars//:apache_commons_lang",
		"@ext_jars//:eclipse_jdt",
		"@ext_jars//:nebula"
                ],
)
package(default_visibility = ["//visibility:public"])




