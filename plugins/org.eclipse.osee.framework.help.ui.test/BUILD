java_test(
        name = "HelpUiTestSuite",
 	resources = ["resources",],
	srcs = glob(["**/*.java"]),
	deps = ["java_deps","//org.eclipse.osee.framework.help.ui","//org.eclipse.osee.framework.core.packages"
	],
	
	visibility = ["//visibility:public"]
)
                       

filegroup(
    name = "resources",
    srcs = glob(["**/*"])
)

java_library(
        name = "java_deps",
        exports = [
                "@maven//:com_fasterxml_jackson_core_jackson_core",
                "@maven//:com_fasterxml_jackson_core_jackson_databind",
                "@maven//:com_fasterxml_jackson_core_jackson_annotations",
                "@maven//:commons_codec_commons_codec",
                "@maven//:org_eclipse_jdt_org_eclipse_jdt_annotation",
                "@maven//:jakarta_ws_rs_jakarta_ws_rs_api",
                "@maven//:javax_activation_activation",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                "@maven//:org_slf4j_slf4j_api",
                "@maven//:javax_mail_mail",
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
		"@maven//:org_mockito_mockito_core",
		"@maven//:org_hamcrest_hamcrest_core",
		"@eclipse_core_runtime//jar",
		"@eclipse_equinox_common//jar", "@eclipse_osgi//jar"
                ],
)

