java_library(
	name = "framework_jdk_core",
	resources = ["txt_files"],
	srcs = glob(["**/*.java"]),	
	deps = ["java_deps"],
    visibility = ["//visibility:public"]

)
        

filegroup(
    name = "txt_files",
    srcs =
        ["src/org/eclipse/osee/framework/jdk/core/util/ReservedCharacters.txt"]
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
		        "@maven//:org_apache_poi_poi",
		        "@maven//:org_apache_poi_poi_ooxml"
                ],
)
