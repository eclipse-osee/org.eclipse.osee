java_library(
	name = "framework_logging",
	srcs =	
	     #	glob(["src/org/eclipse/osee/framework/jdk/core/*/*.java"]),
	     #  glob(["src/org/eclipse/osee/framework/jdk/core/persistence/*.java"]),	
	        glob(["**/*.java"]),	
			deps = ["java_deps","eclipse_osgi_services"],
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
		"@framework_jdk_core//:framework_jdk_core"
                ],
)

java_import(
        name = "eclipse_osgi_services",
        jars = [
                "org.eclipse.osgi.services_3.8.0.v20190206-2147.jar",
               "org.eclipse.osgi_3.15.300.v20200520-1959.jar"
       ]
)

