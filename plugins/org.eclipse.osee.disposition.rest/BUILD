
java_library(
        name = "org.eclipse.osee.disposition.rest",
        resources = ["outfile","xml_files"],
	srcs = glob(["**/*.java"]),
        deps = ["java_deps", "//org.eclipse.osee.framework.core.packages","//org.eclipse.osee.disposition.rest.model",
                "//org.eclipse.osee.vcast","//org.eclipse.osee.jdbc","//org.eclipse.osee.orcs", "//org.eclipse.osee.ats.api"],
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
                "@maven//:io_swagger_core_v3_swagger_annotations",
                "@maven//:io_swagger_core_v3_swagger_jaxrs2",
                "@maven//:io_swagger_core_v3_swagger_models",
                "@ext_jars//:javax.annotation",
		"@ext_jars//:apache_commons_lang"
                ],
)

