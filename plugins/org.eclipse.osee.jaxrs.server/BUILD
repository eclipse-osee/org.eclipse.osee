
java_library(
	name = "org.eclipse.osee.jaxrs.server",
	resources = ["wadl","html_files","xml_files"],
	srcs = glob(["**/*.java"]),	
	deps = ["java_deps", "//org.eclipse.osee.framework.core.packages",
	        "//org.eclipse.osee.jaxrs", "//org.eclipse.osee.jdbc",
		"//org.eclipse.osee.account.admin", "//org.eclipse.osee.orcs"],
	visibility = ["//visibility:public"]

)

filegroup(
    name = "wadl",
    srcs = 
	  [
	    "OSEE-INF/transforms/wadl.xsl"
	   ]   
  , 
)

filegroup(
    name = "html_files",
    srcs =
         glob(["OSEE-INF/templates/*.html"])
  ,
)

filegroup(
    name = "xml_files",
    srcs =
         glob(["OSGI-INF/*.xml"])
  ,
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
		        "@maven//:org_apache_cxf_cxf_rt_rs_service_description",
		        "@maven//:org_eclipse_jdt_org_eclipse_jdt_annotation",
		        "@maven//:commons_codec_commons_codec",
		        "@maven//:javax_activation_activation",
		        "@maven//:org_slf4j_slf4j_api",
		        "@maven//:javax_mail_mail",
		        "@maven//:org_antlr_antlr_runtime",
		        "@maven//:com_google_guava_guava",
		        "@maven//:org_apache_poi_poi",
		        "@maven//:org_apache_poi_poi_ooxml",
		        "@maven//:javax_servlet_javax_servlet_api",
		        "@maven//:javax_annotation_javax_annotation_api",
		"@ext_jars//:eclipse_osgi_services"
                ],
)

