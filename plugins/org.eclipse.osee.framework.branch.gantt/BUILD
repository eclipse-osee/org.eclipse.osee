load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.framework.branch.gantt",
)

java_library(
        name = "org.eclipse.osee.framework.branch.gantt",
	resources = ["gif_files"],
	srcs = glob(["**/*.java"]),
	deps = ["java_deps","//org.eclipse.osee.framework.core.packages",
		"//org.eclipse.osee.framework.ui.skynet",
	 	"//org.eclipse.osee.framework.core.model",
	 	"//org.eclipse.osee.framework.skynet.core",
		"//org.eclipse.osee.framework.ui.swt",
	        "//org.eclipse.osee.accessor",
	        "//org.eclipse.nebula.widgets.xviewer",
	        "//org.eclipse.nebula.widgets.xviewer.core" 
		],
	visibility = ["//visibility:public"]
)

filegroup(
    name = "gif_files",
    srcs =
          [
	   "OSEE-INF/images/branch.gif",
	   "OSEE-INF/images/sample.gif",
	   "META-INF/MANIFEST.MF"
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
                "@maven//:jakarta_ws_rs_jakarta_ws_rs_api",
                "@maven//:javax_activation_activation",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                "@maven//:org_slf4j_slf4j_api",
                "@maven//:javax_mail_mail",
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
		"@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
		"@maven//:org_apache_cxf_cxf_rt_rs_client",
		"@maven//:org_eclipse_jgit_org_eclipse_jgit",
		"@maven//:org_jsoup_jsoup",
		"@maven//:com_jcraft_jsch",
		"@maven//:org_eclipse_jdt_org_eclipse_jdt_core",
		"@maven//:org_apache_geronimo_specs_geronimo_jms_1_1_spec",
                "@maven//:org_apache_activemq_activemq_all",
		"@maven//:org_mockito_mockito_core",
	        "@maven//:org_hamcrest_hamcrest_core",
	        "@eclipse_equinox_registry//jar",
	        "@eclipse_core_runtime//jar",
		"@ext_jars//:eclipse_ui",
		"@ext_jars//:swt",
		"@ext_jars//:jface",
	        "@eclipse_equinox_common//jar",
		"@ext_jars//:nebula",
		"@ext_jars//:apache_commons_lang",
	],
)
