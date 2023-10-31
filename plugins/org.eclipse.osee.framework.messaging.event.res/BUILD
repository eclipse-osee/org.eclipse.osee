
java_library(
        name = "framework_messaging_event_res",
        resources = ["xsd_files"],
	srcs = glob(["**/*.java"]),
	deps = ["java_deps",
		"//org.eclipse.osee.framework.core.packages",
		"//org.eclipse.osee.framework.messaging"
	],
	visibility = ["//visibility:public"]
)

filegroup(
    name = "xsd_files",
    srcs =
        [
        "jaxb/resMessages.xsd",
        "OSGI-INF/osee.core.model.event.service.xml"
        ],
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
                ],
)
