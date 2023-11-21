load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.jaxrs",
)

java_library(
	name = "org.eclipse.osee.jaxrs",
	resources = ["x_file"],
	srcs = glob(["**/*.java"]),	
        deps = ["java_deps","//org.eclipse.osee.framework.core.packages",
	        "//org.eclipse.osee.activity.api"],
	visibility = ["//visibility:public"]
)

filegroup(
    name = "x_file",
    srcs = 
	  [
	    "OSGI-INF/jaxrs.api.xml",
	    "META-INF/MANIFEST.MF"
	  ], 
    visibility = ["//visibility:public"]
)

java_library(
        name = "java_deps",
        exports = [
                    "@maven//:com_fasterxml_jackson_core_jackson_core",
                    "@maven//:com_fasterxml_jackson_core_jackson_databind",
                    "@maven//:com_fasterxml_jackson_core_jackson_annotations",
		    "@maven//:com_fasterxml_jackson_jaxrs_jackson_jaxrs_base",
		    "@maven//:com_fasterxml_jackson_jaxrs_jackson_jaxrs_json_provider",
		    "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
		    "@maven//:javax_ws_rs_javax_ws_rs_api",
	            "@maven//:org_apache_cxf_cxf_core",
		    "@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
		    "@maven//:org_apache_cxf_cxf_rt_rs_client",
		    "@maven//:org_apache_cxf_cxf_rt_rs_security_oauth",
                    "@maven//:org_apache_cxf_cxf_rt_rs_security_oauth2",
		    "@maven//:org_apache_cxf_cxf_rt_security",
		    "@maven//:org_apache_cxf_cxf_rt_transports_http",
		    "@maven//:org_eclipse_jdt_org_eclipse_jdt_annotation",
		    "@maven//:commons_codec_commons_codec",
		    "@maven//:javax_activation_activation",
		    "@maven//:org_slf4j_slf4j_api",
		    "@maven//:javax_mail_mail",
		    "@maven//:org_antlr_antlr_runtime",
		    "@maven//:com_google_guava_guava",
		    "@maven//:org_apache_poi_poi",
		    "@maven//:org_apache_poi_poi_ooxml",
                ],
)

