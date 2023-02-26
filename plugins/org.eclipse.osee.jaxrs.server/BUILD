java_library(
	name = "jaxrs_server",
	resources = ["wadl","html_files","xml_files"],
	srcs =		
	        glob(["**/*.java"]),	
		deps = ["java_deps"],
	        visibility = ["//visibility:public"]
)

filegroup(
    name = "wadl",
    srcs = 
	  [
	    "OSEE-INF/transforms/wadl.xsl"
	   ]   
  , 
    visibility = ["//visibility:public"]
)

filegroup(
    name = "html_files",
    srcs =
         glob(["OSEE-INF/templates/*.html"])
  ,
    visibility = ["//visibility:public"]
)

filegroup(
    name = "xml_files",
    srcs =
         glob(["OSGI-INF/*.xml"])
  ,
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
		"@activity_api//:activity_api",
		"@framework_jdk_core//:framework_jdk_core",
                "@framework_core//:framework_core",
                "@framework_logging//:framework_logging",
		"@logger//:logger",
		"@jdbc//:jdbc",
		"@jaxrs//:jaxrs",
		"@orcs//:orcs",
		"@account_admin//:account_admin",
		"@ext_jars//:eclipse_osgi_services"
                ],
)

