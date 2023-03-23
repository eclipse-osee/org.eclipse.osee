
java_library(
        name = "framework_ui_swt",
        srcs = glob(["**/*.java"]),
        deps = ["java_deps", "framework_core_packages"],
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
		"@ext_jars//:eclipse_equinox_common",
                "@ext_jars//:swt",
		"@ext_jars//:jface",
		"@ext_jars//:eclipse_ui",
		"@ext_jars//:eclipse_core"
		],
)
