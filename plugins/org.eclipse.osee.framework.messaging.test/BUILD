java_test(
        name = "AllMessagingIntegrationTestSuite",
        resources = ["test_messages"],
	srcs = glob(["**/*.java"]),
        deps = ["java_deps","//:framework_messaging","//org.eclipse.osee.framework.core.packages"],
)

filegroup(
    name = "test_messages",
    srcs = ["jaxb/testMessages.xsd",]   
  
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
		"@maven//:org_apache_geronimo_specs_geronimo_jms_1_1_spec",
                "@maven//:org_apache_activemq_activemq_all",
		"@maven//:org_glassfish_jaxb_jaxb_runtime"
                ],
)

