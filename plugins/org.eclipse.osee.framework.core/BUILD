java_library(
        name = "framework_core",
        srcs =
                        glob(["**/*.java"]),
                        deps = ["java_deps",
								"eclipse_core",
								"eclipse_osgi_services",
								"eclipse_equinox_common"
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
                "@logger//:logger",
                "@framework_logging//:framework_logging"
                ],
)
package(default_visibility = ["//visibility:public"])
java_import(
        name = "eclipse_core",
        jars = [
                "org.eclipse.core.runtime_3.18.0.v20200506-2143.jar",
		"org.eclipse.core.resources_3.13.700.v20200209-1624.jar",
		"org.eclipse.core.jobs_3.10.800.v20200421-0950.jar"
      
       ]
)
java_import(
        name = "eclipse_equinox_common",
        jars = [
                "org.eclipse.equinox.common_3.12.0.v20200504-1602.jar",

       ]
)

java_import(
        name = "eclipse_osgi_services",
        jars = [
                "org.eclipse.osgi.services_3.8.0.v20190206-2147.jar",
               "org.eclipse.osgi_3.15.300.v20200520-1959.jar"
       ]
)

