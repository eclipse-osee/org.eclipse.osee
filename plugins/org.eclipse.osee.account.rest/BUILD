
java_library(
        name = "account_rest",
        resources = ["x_files", "html_files"],
	    srcs = glob(["**/*.java"]),
        deps = ["java_deps","//framework_core_packages","//org.eclipse.osee.orcs:orcs",
		        "//org.eclipse.osee.jaxrs:jaxrs", "//org.eclipse.osee.jaxrs.server:jaxrs_server",
				"//org.eclipse.osee.jdbc:jdbc",
				"//org.eclipse.osee.account.rest.model:account_rest_model",
				"//org.eclipse.osee.account.admin:account_admin",
				"//org.eclipse.osee.authorization.admin:authorization_admin",
				"//org.eclipse.osee.authentication.admin:authentication_admin",
				"//org.eclipse.osee.framework.resource.management:framework_resource_management"],
        visibility = ["//visibility:public"]

)

filegroup(
    name = "x_files",
    srcs = 
	  [
	   	"OSGI-INF/account.rest.application.xml",
		"OSGI-INF/jaxrs.authenticator.xml"
	  ]   
  , 
)

filegroup(
    name = "html_files",
    srcs =
	[
		"OSEE-INF/templates/unsubscribe_no_subscription.html",
		"OSEE-INF/templates/unsubscribe_success.html",
		"OSEE-INF/templates/unsubscribe.html"
	],
	    
)

java_library(
        name = "java_deps",
        exports = [
			"@maven//:com_fasterxml_jackson_core_jackson_core",
			"@maven//:com_fasterxml_jackson_core_jackson_databind",
			"@maven//:com_fasterxml_jackson_core_jackson_annotations",
			"@maven//:com_fasterxml_jackson_jaxrs_jackson_jaxrs_base",
			"@maven//:com_fasterxml_jackson_jaxrs_jackson_jaxrs_json_provider",
			"@maven//:org_apache_cxf_cxf_core",
			"@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
			"@maven//:org_apache_cxf_cxf_rt_rs_client",
			"@maven//:org_apache_cxf_cxf_rt_rs_security_oauth",
			"@maven//:org_apache_cxf_cxf_rt_rs_security_oauth2",
			"@maven//:org_apache_cxf_cxf_rt_security",
			"@maven//:org_apache_cxf_cxf_rt_transports_http",
			"@maven//:org_apache_cxf_cxf_rt_rs_service_description",
			"@maven//:commons_codec_commons_codec",
			"@maven//:org_eclipse_jdt_org_eclipse_jdt_annotation",
			"@maven//:javax_ws_rs_javax_ws_rs_api",
			"@maven//:javax_activation_activation",
			"@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
			"@maven//:org_slf4j_slf4j_api",
			"@maven//:org_osgi_org_osgi_service_log",
			"@maven//:org_osgi_org_osgi_service_event",
			"@maven//:org_osgi_org_osgi_service_component",
			"@maven//:org_osgi_org_osgi_framework",
			"@maven//:org_eclipse_core_runtime",
			"@maven//:org_eclipse_core_org_eclipse_core_resources",
			"@maven//:javax_mail_mail",
			"@maven//:org_antlr_antlr_runtime",
			"@maven//:com_google_guava_guava",
			"@maven//:org_apache_poi_poi",
        	"@maven//:org_apache_poi_poi_ooxml",
			"@maven//:javax_servlet_javax_servlet_api",
			"@maven//:org_hsqldb_hsqldb",
			"@ext_jars//:javax.annotation",
			"@ext_jars//:eclipse_osgi_services"
                ],
)

