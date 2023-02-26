java_library(
        name = "ats_api",
	resources = ["x_file"],
	srcs =
                glob(["**/*.java"]),
                deps = ["java_deps"],
                visibility = ["//visibility:public"]
              )

filegroup(
    name = "x_file",
    srcs =
          [
            "OSGI-INF/ats.type.provider.xml",
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
                "@framework_jdk_core//:framework_jdk_core",
		"@framework_core//:framework_core",
		"@framework_core_model//:framework_core_model",
                "@activity_api//:activity_api",
		"@framework_resource_management//:framework_resource_management",
		"@jdbc//:jdbc",
		"@jaxrs//:jaxrs",
		"@orcs//:orcs",
		"@orcs_core//:orcs_core",
		"@orcs_rest_model//:orcs_rest_model",
		"@console_admin//:console_admin",
		"@framework_logging//:framework_logging",
		"@logger//:logger",
		"@define_api//:define_api",
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:nebula",
		"@ext_jars//:apache_commons_lang"
                ],
)
package(default_visibility = ["//visibility:public"])
