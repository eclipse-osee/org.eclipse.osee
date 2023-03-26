java_library(
        name = "config_admin",
        resources = ["x_files"],
        srcs = glob(["**/*.java"]),
        deps = ["java_deps","//org.eclipse.osee.framework.jdk.core:framework_jdk_core",
                "//org.eclipse.osee.framework.core:framework_core", "//org.eclipse.osee.logger:logger"],
        visibility = ["//org.eclipse.osee.config.admin.test:__pkg__"]
)

filegroup(
    name = "x_files",
    srcs =
          [
            "OSGI-INF/metatype/config.manager.metatypes.xml",
            "OSGI-INF/config.manager.xml",
           ]
  ,
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
                "@ext_jars//:eclipse_osgi_services"
                ],
)



