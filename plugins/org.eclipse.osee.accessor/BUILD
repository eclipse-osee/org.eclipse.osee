load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
       name = "osgi_jar",
       arg = "org.eclipse.osee.accessor",
       visibility = ["//visibility:public"]
)

java_library(
       name = "org.eclipse.osee.accessor",
       resources = ["xml_files"],
       srcs = glob(["**/*.java"]),
       deps = ["java_deps",
               "//org.eclipse.osee.framework.core",
               "//org.eclipse.osee.framework.jdk.core",
	       "//org.eclipse.osee.orcs",
	       "//org.eclipse.osee.orcs.core"
               ],
       visibility = ["//visibility:public"]
)

filegroup(
    name = "xml_files",
    srcs =
  [
	"OSGI-INF/artifact.accessor.xml",
	"META-INF/MANIFEST.MF"
  ]
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
                "@maven//:javax_mail_mail",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                "@maven//:org_slf4j_slf4j_api",
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
                "@maven//:io_swagger_core_v3_swagger_core",
                "@maven//:io_swagger_core_v3_swagger_jaxrs2",
    		"@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
    		"@maven//:io_swagger_core_v3_swagger_models",		
		"@ext_jars//:nebula",
		"@ext_jars//:apache_commons_lang",
                ],

)
