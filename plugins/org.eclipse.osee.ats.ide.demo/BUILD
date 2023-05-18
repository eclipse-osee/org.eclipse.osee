java_library(
        name = "ats_ide_demo",
        resources = ["files"],
        srcs = glob(["**/*.java"]),
        deps = ["java_deps",
		"//framework_core_packages",
		"//:define_api",
		"//:cache_admin",
		"//:jdbc",
		"//:jaxrs",
        "//:ats_core",
        "//:ats_api",
        "//:ats_ide",
        "//:define_ide",
        "//org.eclipse.osee.support.test.util:support_test_util",
        "//org.eclipse.osee.framework.ui.skynet:framework_ui_skynet",
        "//org.eclipse.osee.framework.skynet.core:framework_skynet_core",
        "//org.eclipse.osee.framework.ui.plugin:framework_ui_plugin",
        "//org.eclipse.osee.framework.ui.swt:framework_ui_swt",
        "//org.eclipse.osee.framework.database.init:framework_database_init",
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
    name = "files",
    srcs = glob(["**/*"]),
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
		        "@ext_jars//:eclipse_osgi_services",
                "@ext_jars//:jface"
               ],
)

