java_test(
        name = "JdkCoreTestSuite",
       	resources = ["xml_files"],
	srcs =
                 glob(["**/*.java"]),
                 deps = ["java_deps",],
                 visibility = ["//visibility:public"]
                        )

filegroup(
    name = "xml_files",
    srcs = 
        ["src/org/eclipse/osee/framework/jdk/core/text/utf8_input.xml",
	 "src/org/eclipse/osee/framework/jdk/core/rules/sample_icd_formatted.xml"
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
		"@maven//:org_mockito_mockito_core",
		"@maven//:org_hamcrest_hamcrest_core",
		"@maven//:org_junit_junit5_api",
                "@framework_jdk_core//:framework_jdk_core",
                ],
)

