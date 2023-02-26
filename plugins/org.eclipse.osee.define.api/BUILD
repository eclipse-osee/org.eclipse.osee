java_library(
        name = "define_api",
        srcs =                
		glob(["**/*.java"]),
                deps = ["java_deps"],
                visibility = ["//visibility:public"]
              )

java_library(
        name = "java_deps",
        exports = [
                "@maven//:com_fasterxml_jackson_core_jackson_core",
                "@maven//:com_fasterxml_jackson_core_jackson_databind",
                "@maven//:com_fasterxml_jackson_core_jackson_annotations",
                "@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",        
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
		"@maven//:javax_servlet_javax_servlet_api",
		"@maven//:org_eclipse_jgit_org_eclipse_jgit",
		"@maven//:io_swagger_core_v3_swagger_core",
		"@maven//:io_swagger_core_v3_swagger_jaxrs2",
                "@maven//:io_swagger_core_v3_swagger_models",
		"@framework_core//:framework_core",
                "@framework_logging//:framework_logging",
		"@framework_jdk_core//:framework_jdk_core",
		"@ext_jars//:eclipse_osgi_services",
		"@framework_core_model//:framework_core_model",
        	"@logger//:logger",
		"@jdbc//:jdbc",
		"@orcs//:orcs",
		"@console_admin//:console_admin",
		"@cache_admin//:cache_admin",
		"@activity_api//:activity_api"
		],
)
package(default_visibility = ["//visibility:public"])
