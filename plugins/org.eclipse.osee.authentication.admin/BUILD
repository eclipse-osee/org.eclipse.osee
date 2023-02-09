java_library(
        name = "authentication_admin",
        resources = ["x_files"],
        srcs =
			glob(["**/*.java"]),
            deps = ["java_deps",
			],
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
                "@framework_jdk_core//:framework_jdk_core",
                "@logger//:logger",
                ],
)

filegroup(
    name = "x_files",
    srcs =
	  [
	    "OSGI-INF/authentication.admin.xml",
	    "OSGI-INF/none.authentication.provider.xml",
	   ]   
  , 
    visibility = ["//visibility:public"]
)

package(default_visibility = ["//visibility:public"])
