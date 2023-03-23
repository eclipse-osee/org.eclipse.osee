java_test(
        name = "FrameworkCoreTestSuite",
        srcs = glob(["**/*.java"]),
        deps = ["java_deps", "//org.eclipse.osee.framework.jdk.core:framework_jdk_core",
                "//org.eclipse.osee.framework.core:framework_core","//org.eclipse.osee.logger:logger"],
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
		"@ext_jars//:eclipse_equinox_common", 
		"@ext_jars//:eclipse_osgi_services",
                ],
)
package(default_visibility = ["//visibility:public"])


