load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.framework.core",
    visibility = ["//visibility:public"]
)

java_library(
        name = "org.eclipse.osee.framework.core",
        resources = ["manifest","osgi"],
	srcs =glob(["**/*.java"]),
        deps = ["java_deps","//org.eclipse.osee.framework.jdk.core","//org.eclipse.osee.logger",
                "//org.eclipse.osee.framework.logging"],
        visibility = ["//visibility:public"]
)

filegroup(
    name = "manifest",
    srcs = ["META-INF/MANIFEST.MF"]
)

filegroup(
    name = "osgi",
    srcs = glob(["OSGI-INF/*"])
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
		"@eclipse_core_runtime//jar",
		"@eclipse_core_jobs//jar",
		"@eclipse_core_resources//jar",
		"@eclipse_equinox_common//jar",
		"@eclipse_osgi//jar",
		"@eclipse_osgi_services//jar"
                ],
)

