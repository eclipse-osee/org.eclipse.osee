
java_library(
        name = "disposition_rest",
        resources = ["outfile","xml_files"],
	srcs = glob(["**/*.java"]),
        deps = ["java_deps", "//framework_core_packages","//org.eclipse.osee.disposition.rest.model:disposition_rest_model",
                "//org.eclipse.osee.vcast:vcast","//:jdbc","//:orcs", "//:ats_api"],
        visibility = ["//org.eclipse.osee.disposition.rest.test:__pkg__"]
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
    srcs = glob(["OSGI-INF/*.xml"]), 
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
		"@ext_jars//:javax.annotation",
		"@ext_jars//:apache_commons_lang"
                ],
)

