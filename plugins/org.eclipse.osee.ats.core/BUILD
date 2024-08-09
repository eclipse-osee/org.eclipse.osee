load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.ats.core",
    visibility = ["//visibility:public"]
)

java_library(
        name = "org.eclipse.osee.ats.core",
	resources = ["xml_files","osee_inf_files"],
	srcs = glob(["**/*.java"]),
        deps = ["java_deps","//org.eclipse.osee.framework.core.packages", "//org.eclipse.osee.jdbc",
                "//org.eclipse.osee.orcs", "//org.eclipse.osee.jaxrs",
                "//org.eclipse.nebula.widgets.xviewer",
		"//org.eclipse.nebula.widgets.xviewer.core",
                "//org.eclipse.osee.ats.api", "//org.eclipse.osee.framework.core.model",
                "//org.eclipse.osee.orcs.core","//org.eclipse.osee.framework.core.server", "//org.eclipse.osee.orcs.rest.model"],
        visibility = ["//visibility:public"]

)

filegroup(
    name = "xml_files",
    srcs = glob(["OSGI-INF/*.xml"])
  ,
    visibility = ["//visibility:public"]
)

filegroup(
    name = "osee_inf_files",
    srcs =
          [
            "OSEE-INF/atsConfig/convert/StateDef.txt",
	    "OSEE-INF/atsConfig/convert/Widget.txt",
	    "OSEE-INF/images/workflow.gif",
	    "OSEE-INF/web/agilebl/LineChart.html",
	    "OSEE-INF/web/agilebl/LineChart.json",
	    "OSEE-INF/web/agilebl/LineDataset.json",
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
                "@maven//:org_slf4j_slf4j_api",
                "@maven//:javax_mail_mail",
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
		"@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
		"@ext_jars//:apache_commons_lang",
		"@eclipse_osgi//jar",
		"@eclipse_osgi_services//jar"
		
                ],
)
