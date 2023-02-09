java_library(
        name = "framework_resource_management",
        resources = ["xml_files"],
	srcs =
                        glob(["**/*.java"]),
                        deps = ["java_deps",
								"eclipse_core",
								"eclipse_osgi_services",

			],
    visibility = ["//visibility:public"]
)

filegroup(
    name = "xml_files",
    srcs =
        [
        "OSGI-INF/resource.manager.xml"
        ],
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
                "@framework_jdk_core//:framework_jdk_core",
		"@framework_core//:framework_core",
                "@logger//:logger",
                "@framework_logging//:framework_logging"
                ],
)
package(default_visibility = ["//visibility:public"])
java_import(
        name = "eclipse_core",
        jars = [
                "org.eclipse.core.runtime_3.18.0.v20200506-2143.jar",
       ]
)


java_import(
        name = "eclipse_osgi_services",
        jars = [
                "org.eclipse.osgi.services_3.8.0.v20190206-2147.jar",
               "org.eclipse.osgi_3.15.300.v20200520-1959.jar"
       ]
)

