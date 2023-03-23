java_library(
        name = "ats_core",
	resources = ["xml_files","osee_inf_files"],
	srcs = glob(["**/*.java"]),
        deps = ["java_deps","//framework_core_packages", "//org.eclipse.osee.jdbc:jdbc",
                "//org.eclipse.osee.orcs:orcs", "//org.eclipse.osee.jaxrs:jaxrs",
                "//org.eclipse.osee.ats.api:ats_api", "//org.eclipse.osee.framework.core.model:framework_core_model",
                "//org.eclipse.osee.orcs.core:orcs_core", "//org.eclipse.osee.orcs.rest.model:orcs_rest_model"],
        visibility = ["//visibility:public"]

)

filegroup(
    name = "xml_files",
    srcs = glob(["OSGI-INF/*.xml"])
  ,
    visibility = ["//visibility:public"]
)

filegroup(
    name = "osee_inf_files",
    srcs =
          [
            "OSEE-INF/atsConfig/convert/StateDef.txt",
	    "OSEE-INF/atsConfig/convert/Widget.txt",
	    "OSEE-INF/images/workflow.gif",
	    "OSEE-INF/web/agilebl/LineChart.html",
	    "OSEE-INF/web/agilebl/LineChart.json",
	    "OSEE-INF/web/agilebl/LineDataset.json"
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
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:nebula",
		"@ext_jars//:apache_commons_lang"
                ],
)
