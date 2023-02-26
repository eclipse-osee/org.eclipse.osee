java_library(
        name = "orcs_db",
        resources = ["xml_file","xml_files"],
	srcs =
                glob(["**/*.java"]),
                deps = ["java_deps"],
                visibility = ["//visibility:public"]
              )

filegroup(
    name = "xml_file",
    srcs =
    	[
		"src/org/eclipse/osee/orcs/db/internal/search/language/pluralToSingularExceptions.xml",
	]
)

filegroup(
    name = "xml_files",
    srcs =  
         glob(["OSGI-INF/*.xml"]),
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
		"@framework_core_model//:framework_core_model",
                "@activity_api//:activity_api",
		"@framework_resource_management//:framework_resource_management",
		"@jdbc//:jdbc",
		"@jaxrs//:jaxrs",
		"@orcs//:orcs",
		"@orcs_core//:orcs_core",
		"@console_admin//:console_admin",
		"@framework_logging//:framework_logging",
		"@logger//:logger",
		"@ext_jars//:eclipse_equinox_common",
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:apache_commons_lang"
                ],
)
package(default_visibility = ["//visibility:public"])

java_import(
        name = "apache_commons",
        jars = [
                "org.apache.commons.lang_2.6.0.v201404270220.jar",
       ]
)

