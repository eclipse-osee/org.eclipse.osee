java_library(
        name = "account_admin",
        resources = ["x_files"],
	srcs = glob(["**/*.java"]),
        deps = ["java_deps","//framework_core_packages",
        "//org.eclipse.osee.account.rest.model:account_rest_model",
        "//org.eclipse.osee.authentication.admin:authentication_admin"],
        visibility = ["//visibility:public"]
)

filegroup(
    name = "x_files",
    srcs =
          [
            "OSGI-INF/account.admin.xml",
            "OSGI-INF/subscriptions.admin.xml",
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
                "@maven//:javax_ws_rs_javax_ws_rs_api",
                "@maven//:javax_activation_activation",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                ],
)

