load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.activity.api",
)


java_library(
        name = "org.eclipse.osee.activity.api",
        resources = ["manifest"],
	srcs = glob(["**/*.java"]),
	deps = ["java_deps",
                 "//org.eclipse.osee.framework.core", "//org.eclipse.osee.framework.jdk.core",
                "//org.eclipse.osee.logger"],	
	visibility = ["//visibility:public"]
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
                "@maven//:javax_ws_rs_javax_ws_rs_api",
                "@maven//:javax_activation_activation",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                ],
)
