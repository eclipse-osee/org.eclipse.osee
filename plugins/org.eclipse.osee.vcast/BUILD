load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.vcast",
    visibility = ["//visibility:public"]
)

java_library(
        name = "org.eclipse.osee.vcast",
        resources = ["manifest"],
	srcs = glob(["**/*.java"]),
        deps = ["java_deps","//org.eclipse.osee.framework.core.packages","//org.eclipse.osee.jdbc"],
        visibility = ["//org.eclipse.osee.disposition.rest:__pkg__","//org.eclipse.osee.server.p2:__subpackages__"]
)

filegroup(
    name = "manifest",
    srcs = ["META-INF/MANIFEST.MF"]
)

java_library(
        name = "java_deps",
        exports = [
                "@maven//:com_fasterxml_jackson_core_jackson_core",
                "@maven//:com_fasterxml_jackson_core_jackson_databind",
                "@maven//:com_fasterxml_jackson_core_jackson_annotations",
                "@maven//:commons_codec_commons_codec",
                "@maven//:org_eclipse_jdt_org_eclipse_jdt_annotation",
                "@maven//:jakarta_ws_rs_jakarta_ws_rs_api",
                "@maven//:javax_activation_activation",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                "@maven//:org_slf4j_slf4j_api",
                "@maven//:javax_mail_mail",
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
                ],
)

