java_library(
        name = "framework_skynet_core",
        resources = ["exsd_files","xml_files","xsd_files"],
        srcs =
                glob(["**/*.java"]),
                deps = ["java_deps",
				#"eclipse_debug",
			],
                visibility = ["//visibility:public"]
              )

filegroup(
    name = "exsd_files",
    srcs = 
	    glob(["OSEE-INF/extensionPts/*.exsd"]),
   
    visibility = ["//visibility:public"]
)

filegroup(
    name = "xml_files",
    srcs =
            glob(["OSGI-INF/*.xml"]),
    visibility = ["//visibility:public"]
)

filegroup(
    name = "xsd_files",
    srcs =
  	[
    		"OSEE-INF/xsd/export.xsd",
		"OSEE-INF/xsd/publish.xsd"
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
		"@maven//:org_hsqldb_hsqldb",
		"@maven//:org_jsoup_jsoup",
                "@framework_core//:framework_core",
                "@framework_logging//:framework_logging",
		"@framework_jdk_core//:framework_jdk_core",
        	"@logger//:logger",
		"@define_api//:define_api",
		"@cache_admin//:cache_admin",
		"@jdbc//:jdbc",
		"@jaxrs//:jaxrs",
		"@framework_messaging//:framework_messaging",
		"@framework_messaging_event_res//:framework_messaging_event_res",
		"@orcs_rest_model//:orcs_rest_model",
		"@ext_jars//:eclipse_core",
		"@ext_jars//:eclipse_equinox_common",
		"@ext_jars//:eclipse_osgi_services", 
		"@framework_core_model//:framework_core_model",
		"@framework_plugin_core//:framework_plugin_core",
		"@activity_api//:activity_api",
		"@framework_core_client//:framework_core_client"
		],
)
package(default_visibility = ["//visibility:public"])
java_import(
        name = "eclipse_debug",
        jars = [
                "org.eclipse.debug.core_3.15.100.v20200514-0816.jar",

       ]
)

