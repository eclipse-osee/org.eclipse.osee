java_library(
        name = "disposition_rest",
        resources = ["outfile","xml_files"],
	srcs =
                glob(["**/*.java"]),
                deps = ["java_deps"],
                visibility = ["//visibility:public"]
             )

filegroup(
    name = "outfile",
    srcs =
          [
            "OSEE-INF/outFiles/sampleTmo.tmo",
           ]
  ,
    visibility = ["//visibility:public"]
)
filegroup(
    name = "xml_files",
    srcs =
           glob(["OSGI-INF/*.xml"]),
  
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
                "@framework_logging//:framework_logging",
		"@disposition_rest_model//:disposition_rest_model",
		"@vcast//:vcast",
		"@jdbc//:jdbc",
		"@orcs//:orcs",
		#"@ext_jars//:jms",
		"@ext_jars//:javax.annotation",
		"@ats_api//:ats_api",
		"@ext_jars//:apache_commons_lang"
                ],
)
package(default_visibility = ["//visibility:public"])

