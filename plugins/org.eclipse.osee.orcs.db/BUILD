
java_library(
        name = "orcs_db",
        resources = ["xml_file","xml_files"],
	srcs = glob(["**/*.java"]),
        deps = ["java_deps","//framework_core_packages","//:jdbc","//:jaxrs","//:orcs",
                "//:framework_core_model","//:framework_resource_management","//:orcs_core",
	        "//:console_admin","//:activity_api"],
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
    srcs =  glob(["OSGI-INF/*.xml"]),
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
		"@ext_jars//:eclipse_equinox_common",
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:apache_commons_lang"
                ],
)


