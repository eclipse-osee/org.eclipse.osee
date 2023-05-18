java_test(
        name = "AllSkynetCoreJunitTestSuite",
        resources = ["htm_files","xml_files"],
        srcs = glob(["**/*.java"]),
        deps = ["java_deps",
		"//framework_core_packages",
		"//:define_api",
        "//:framework_skynet_core",
		"//:cache_admin",
		"//:jdbc",
		"//:jaxrs",
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
    name = "htm_files",
    srcs =
          [
	    "src/org/eclipse/osee/framework/skynet/core/utility/support/NormalizeHtml_converted.htm",
	    "src/org/eclipse/osee/framework/skynet/core/utility/support/NormalizeHtml_test_doc.htm",
           ],
)


filegroup(
    name = "xml_files",
    srcs =
          [
	    "src/org/eclipse/osee/framework/skynet/core/importing/parsers/outlineResolutionCausingInput_no_toc_formatted.xml",
	    "src/org/eclipse/osee/framework/skynet/core/importing/parsers/tableOfContentsHyperlinkTest.xml",
        "src/org/eclipse/osee/framework/skynet/core/importing/parsers/testNumbering.xml",
        "src/org/eclipse/osee/framework/skynet/core/importing/ExcelArtifactExtractorTest.xml",
        "src/org/eclipse/osee/framework/skynet/core/importing/NativeDocumentExtractorTest.xml"
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
		        "@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
		        "@maven//:org_hsqldb_hsqldb",
		        "@maven//:org_jsoup_jsoup",
                "@maven//:org_mockito_mockito_core",
		        "@ext_jars//:eclipse_core",
		        "@ext_jars//:eclipse_equinox_common",
		        "@ext_jars//:eclipse_osgi_services",
                "@ext_jars//:apache_commons_lang"
               ],
)

