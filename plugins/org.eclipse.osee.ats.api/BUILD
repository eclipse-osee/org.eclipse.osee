
java_library(
        name = "ats_api",
	resources = ["x_file"],
	srcs = glob(["**/*.java"]),
        deps = ["java_deps", "//framework_core_packages","//org.eclipse.osee.jdbc:jdbc",
                "//org.eclipse.osee.jaxrs:jaxrs","//org.eclipse.osee.orcs:orcs", 
                "//org.eclipse.osee.orcs.core:orcs_core","//org.eclipse.osee.activity.api:activity_api",
                "//org.eclipse.osee.framework.resource.management:framework_resource_management",
                "//org.eclipse.osee.define.api:define_api","//org.eclipse.osee.orcs.rest.model:orcs_rest_model",
                "//org.eclipse.osee.framework.core.model:framework_core_model"],
        visibility = ["//visibility:public"]
)

filegroup(
        name = "x_file",
        srcs =
          [
            "OSGI-INF/ats.type.provider.xml",
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
		"@maven//:io_swagger_core_v3_swagger_core",
		"@maven//:io_swagger_core_v3_swagger_jaxrs2",
                "@maven//:io_swagger_core_v3_swagger_models",
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:nebula",
		"@ext_jars//:apache_commons_lang"
                ],
)
