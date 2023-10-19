
java_library(
       name = "testscript",
       resources = ["xml_files"],
       srcs = glob(["**/*.java"]),
       deps = ["java_deps", "//framework_core_packages","//:framework_core_model","//:activity_api",
	       "//:framework_resource_management","//:jdbc","//:jaxrs","//:orcs","//:orcs_core",
	       "//:orcs_rest_model","//:console_admin","//:define_api","//:define","//:ats_api","//:ats_core",
       "//:framework_core_server","//:template_engine", "//:ats_rest","//:accessor"],
       visibility = ["//visibility:public"]

)

filegroup(
    name = "xml_files",
    srcs =
  [
	"OSGI-INF/script.api.xml",
	"OSGI-INF/script.application.xml",
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
                "@maven//:io_swagger_core_v3_swagger_core",
                "@maven//:io_swagger_core_v3_swagger_jaxrs2",
    		"@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
    		"@maven//:io_swagger_core_v3_swagger_models",		
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:nebula",
		"@ext_jars//:eclipse_core",
		"@ext_jars//:apache_commons_lang",
		"@ext_jars//:eclipse_equinox_common"
                ],
)
