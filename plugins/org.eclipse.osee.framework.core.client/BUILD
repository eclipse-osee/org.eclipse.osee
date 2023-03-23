
java_library(
        name = "framework_core_client",
        resources = ["xml_files"],
        srcs = glob(["**/*.java"]),
        deps = ["java_deps", "//framework_core_packages","//:jdbc","//:jaxrs","//:define_api",
                "//:cache_admin", "//:framework_messaging","//:orcs_rest_model","//:framework_core_model",
                "//:framework_plugin_core","//:activity_api","//:framework_server_ide_api"],
        visibility = ["//visibility:public"]
        
)

filegroup(
    name = "xml_files",
    srcs = glob(["OSGI-INF/*.xml"]),
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
		"@maven//:org_hsqldb_hsqldb",
		"@maven//:org_jsoup_jsoup",
		"@ext_jars//:eclipse_core",
		"@ext_jars//:eclipse_equinox_common",
		"@ext_jars//:eclipse_osgi_services", 
		],
)
