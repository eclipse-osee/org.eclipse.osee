load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.authentication.ldap",
    visibility = ["//visibility:public"]
)

java_library(
        name = "org.eclipse.osee.authentication.ldap",
        resources = ["x_files"],
        srcs = glob(["**/*.java"]),
        deps = ["java_deps","//org.eclipse.osee.framework.core.packages","//org.eclipse.osee.authentication.admin"],
        visibility = ["//visibility:public"]
)

filegroup(
    name = "x_files",
    srcs =
          [
            "OSGI-INF/ldap.authentication.provider.xml",
            "OSGI-INF/metatype/ldap.authentication.provider.metatypes.xml",
	    "META-INF/MANIFEST.MF"
           ]
  ,
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
                "@maven//:jakarta_ws_rs_jakarta_ws_rs_api",
                "@maven//:javax_activation_activation",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                ],
)

