
java_library(
        name = "define",
	resources = ["xml_files","html_files"],
	srcs = glob(["**/*.java"]),
	deps = ["java_deps","//framework_core_packages","//:jdbc","//:orcs",
                "//:jaxrs","//:orcs_core",
                "//:orcs_rest_model","//:template_engine",
                "//:framework_core_server","//:define_api",
	        "//:ats_api","//:activity_api","//:app"],
        visibility = ["//visibility:public"]

)

filegroup(
    name = "xml_files",
    srcs =
          [
	    "OSGI-INF/define.application.impl.xml",
	    "OSGI-INF/define.operations.impl.xml"
           ]
  ,
    visibility = ["//visibility:public"]
)

filegroup(
    name = "html_files",
    srcs =
          [
            "src/org/eclipse/osee/define/rest/internal/html/dataRightsReport.html",
            "src/org/eclipse/osee/define/rest/internal/html/publishLowHighReport.html",
            "src/org/eclipse/osee/define/rest/internal/html/systemSafetyReport.html",
	    "src/ApplicabilityGrammar.tokens"
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
		"@maven//:org_jsoup_jsoup",
		"@maven//:com_jcraft_jsch",
		"@maven//:org_eclipse_jgit_org_eclipse_jgit",
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:nebula",
		"@ext_jars//:apache_commons_lang",
		"@ext_jars//:rmf",
		"@ext_jars//:emf",
		"@ext_jars//:jgit"
                ],
)
