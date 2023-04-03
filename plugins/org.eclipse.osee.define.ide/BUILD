java_library(
        name = "define_ide",
	resources = ["xml_files","img"],
	srcs = glob(["**/*.java"]),
	deps = ["java_deps",
	 	"//:framework_core_model",
	 	"//:framework_core_client",
	 	"//:framework_plugin_core",
	 	"//:framework_ui_swt",
	 	"//:ote_define",
	 	"//:define_api",
	 	"//:ats_api",
	 	"//:app",
	 	"//:framework_server_ide",
	 	"//:orcs_rest_model",
	 	"//:framework_messaging_event_res",
	 	"//:orcs",
	 	"//:framework_core_server",
	 	"//:cache_admin",
	 	"//:console_admin",
	 	"//:jdbc",
	 	"//:orcs_core",
	 	"//:framework_server_ide_api",
	 	"//:template_engine",
	 	"//:jaxrs",
	 	"//:framework_messaging",
	 	"//:framework_resource_management",
	 	"//:orcs_rest",
	 	"//:activity_api",
	 	"//:activity",
	 	"//:framework_ui_plugin",
	 	"//:framework_ui_skynet",
	 	"//:account_rest_model",
	 	"//:framework_help_ui",
	 	"//:framework_ui_ws",
	 	"//:framework_skynet_core",
	 	"//:account_rest_client",
	 	"//framework_core_packages"
	],
	visibility = ["//visibility:public"]
              )

filegroup(
    name = "xml_files",
    srcs =
          [
	    "plugin.xml",
	    "OSEE-INF/extensionPts/TraceUnitHandler.exsd",
	    "OSEE-INF/templates/Work_Instruction_Template.xml"
           ]
  ,
)

filegroup(
    name = "img",
    srcs =
          [
            "OSEE-INF/images/artifact_search.gif",
	    "OSEE-INF/images/chkbox_disabled.gif",
	    "OSEE-INF/images/chkbox_enabled.gif",
	    "OSEE-INF/images/compass.xcf",
	    "OSEE-INF/images/defineNav.gif",
	    "OSEE-INF/images/delete_button.gif",
	    "OSEE-INF/images/gears.gif",
	    "OSEE-INF/images/importTrace.gif",
	    "OSEE-INF/images/laser_16_16.gif",
	    "OSEE-INF/images/refresh.gif",
	    "OSEE-INF/images/relate.gif",
	    "OSEE-INF/images/relateWizard.gif",
	    "OSEE-INF/images/remove.gif",
	    "OSEE-INF/images/up.gif",
	    "OSEE-INF/images/wizard.bmp"
	  ]
  ,
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
		"@maven//:org_eclipse_jgit_org_eclipse_jgit",
		"@maven//:org_jsoup_jsoup",
		"@maven//:com_jcraft_jsch",
		"@maven//:org_eclipse_jdt_org_eclipse_jdt_core",
		"@maven//:org_apache_geronimo_specs_geronimo_jms_1_1_spec",
                "@maven//:org_apache_activemq_activemq_all",
		"@maven//:org_mockito_mockito_core",
		"@maven//:org_hamcrest_hamcrest_core",
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:nebula",
		"@ext_jars//:apache_commons_lang",
		"@ext_jars//:eclipse_ui",
		"@ext_jars//:swt",
		"@ext_jars//:jface",
		"@ext_jars//:eclipse_core",
		"@ext_jars//:eclipse_equinox_common"
	],
)

