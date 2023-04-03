java_library(
    name = "framework_ui_plugin",
	resources = ["xml_files", "exsd_files", "img"],
	srcs = 
	glob(["**/*.java"]),
	deps = ["java_deps",
		"//framework_core_packages",
	 	"//:framework_core_client",
	 	"//:framework_plugin_core",
	 	"//:framework_ui_swt",
	 	"//:activity_api",
	 	"//:framework_messaging_event_res",
	 	"//:framework_server_ide_api",
	 	"//:framework_core_server",
	 	"//:framework_messaging",
	 	"//:cache_admin",
	 	"//:framework_core_model",
	 	"//:jaxrs",
	 	"//:orcs",
	 	"//:orcs_rest_model",
	 	"//:jdbc",
	 	"//:framework_server_ide",
	 	"//:console_admin",
	 	"//:orcs_core",
	 	"//:orcs_rest",
	 	"//:activity",
	 	"//:define_api",
	 	"//:framework_resource_management"
		],
	visibility = ["//visibility:public"]
              )

filegroup(
    name = "xml_files",
    srcs =
          [
	    "plugin.xml"
           ],
)

filegroup(
    name = "exsd_files",
    srcs =
          [
            "OSEE-INF/extensionPts/PropertyStoreControl.exsd",
            "OSEE-INF/extensionPts/WorkbenchTargetProvider.exsd",
	    "OSEE-INF/extensionPts/XNavigateItemProvider.exsd"
           ],
)

filegroup(
    name = "img",
    srcs =
          [
		"OSEE-INF/images/admin.gif",
		"OSEE-INF/images/browse.gif",
		"OSEE-INF/images/bug.gif",
		"OSEE-INF/images/chkbox_disabled.gif",
		"OSEE-INF/images/chkbox_enabled.gif",
		"OSEE-INF/images/edit.gif",
		"OSEE-INF/images/errorSm.gif",
		"OSEE-INF/images/faq.gif",
		"OSEE-INF/images/filter.gif",
		"OSEE-INF/images/folder.gif",
		"OSEE-INF/images/gear.gif",
		"OSEE-INF/images/lockkey.gif",
		"OSEE-INF/images/needSave.gif",
		"OSEE-INF/images/new.gif",
		"OSEE-INF/images/open.gif",
		"OSEE-INF/images/osee_16.png",
		"OSEE-INF/images/permission.bmp",
		"OSEE-INF/images/publish.gif",
		"OSEE-INF/images/refresh.gif",
		"OSEE-INF/images/remove.gif",
		"OSEE-INF/images/report.gif",
		"OSEE-INF/images/sample.gif",
		"OSEE-INF/images/saved.gif",
		"OSEE-INF/images/train.gif",
		"OSEE-INF/images/viewMenu.gif",
		"OSEE-INF/images/www.gif"
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
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
		"@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
		"@maven//:org_eclipse_jgit_org_eclipse_jgit",
		"@maven//:org_jsoup_jsoup",
		"@maven//:com_jcraft_jsch",
		"@maven//:org_eclipse_jdt_org_eclipse_jdt_core",
		"@maven//:org_apache_geronimo_specs_geronimo_jms_1_1_spec",
                "@maven//:org_apache_activemq_activemq_all",
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:eclipse_ui",
		"@ext_jars//:swt",
		"@ext_jars//:jface",
		"@ext_jars//:eclipse_core",
		"@ext_jars//:eclipse_equinox_common",
		"@ext_jars//:help",
		"@ext_jars//:junit"
	],
)
