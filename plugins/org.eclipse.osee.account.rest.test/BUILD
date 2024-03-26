java_test(
        name = "AccountRestTestSuite",
        srcs = glob(["**/*.java"]),
        deps = ["java_deps","//org.eclipse.osee.account.rest", "//org.eclipse.osee.account.admin","//org.eclipse.osee.framework.core.packages",
	        "//org.eclipse.osee.account.rest.model"],
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
		    "@maven//:org_hamcrest_hamcrest_core",
		    "@maven//:org_mockito_mockito_core",
		    "@ext_jars//:apache_commons_lang",
                ],
)

