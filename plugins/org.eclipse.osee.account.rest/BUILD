load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.account.rest",
    visibility = ["//visibility:public"],
)

java_library(
     name = "org.eclipse.osee.account.rest",
     resources = ["x_files", "html_files"],
     srcs = glob(["**/*.java"]),
     deps = ["java_deps","//org.eclipse.osee.framework.core.packages","//org.eclipse.osee.orcs",
	     "//org.eclipse.osee.jaxrs", "//org.eclipse.osee.jaxrs.server",
	     "//org.eclipse.osee.jdbc",
	     "//org.eclipse.osee.account.rest.model",
	     "//org.eclipse.osee.account.admin",
	     "//org.eclipse.osee.authorization.admin",
	     "//org.eclipse.osee.authentication.admin",
	     "//org.eclipse.osee.framework.resource.management"],
     visibility = ["//visibility:public"]

)

filegroup(
    name = "x_files",
    srcs = 
	  [
	   	"OSGI-INF/account.rest.application.xml",
		"OSGI-INF/jaxrs.authenticator.xml",
		"META-INF/MANIFEST.MF"
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
			"@maven//:jakarta_ws_rs_jakarta_ws_rs_api",
			"@maven//:javax_activation_activation",
			"@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
			"@maven//:org_slf4j_slf4j_api",
			"@maven//:javax_mail_mail",
			"@maven//:org_antlr_antlr_runtime",
			"@maven//:com_google_guava_guava",
			"@maven//:javax_servlet_javax_servlet_api",
			"@maven//:org_hsqldb_hsqldb",
			"@javax_annotation//jar",
                ],
)

