package(default_visibility = ["//visibility:public"])

load("//:osgi.bzl", "server_p2")

load("@rules_pkg//:pkg.bzl", "pkg_zip")


pkg_zip(
    name = "osee_server_zip",
    srcs = [
        "//org.eclipse.osee.server.p2:p2",
	"//org.eclipse.osee.server.p2:server_files",
        "third_party_deps"
    ],
)

filegroup(
    name = "third_party_deps",
    srcs = [
	    "@maven//:commons_codec_commons_codec",
	    "@maven//:com_google_guava_guava",
	    "@maven//:com_fasterxml_jackson_core_jackson_annotations",
            "@maven//:com_fasterxml_jackson_core_jackson_core",
            "@maven//:com_fasterxml_jackson_core_jackson_databind",
	    "@maven//:com_fasterxml_jackson_jaxrs_jackson_jaxrs_base",
	    "@maven//:com_fasterxml_jackson_jaxrs_jackson_jaxrs_json_provider",
	    "@maven//:com_jcraft_jsch",
            "@maven//:com_vladsch_flexmark_flexmark_ext_gfm_tasklist",
	    "@maven//:com_vladsch_flexmark_flexmark_ext_tables",
	    "@maven//:com_vladsch_flexmark_flexmark_util_ast",
	    "@maven//:com_vladsch_flexmark_flexmark_util_data",
	    "@maven//:com_vladsch_flexmark_flexmark",
            "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
	    "@maven//:javax_ws_rs_javax_ws_rs_api",
            "@maven//:javax_activation_activation",
            "@maven//:javax_mail_mail",
            "@maven//:javax_servlet_javax_servlet_api",
            "@maven//:org_antlr_antlr_runtime",
	    "@maven//:org_apache_cxf_cxf_core",
	    "@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
	    "@maven//:org_apache_cxf_cxf_rt_rs_client",
	    "@maven//:org_apache_cxf_cxf_rt_rs_security_oauth",
	    "@maven//:org_apache_cxf_cxf_rt_rs_security_oauth2",
	    "@maven//:org_apache_cxf_cxf_rt_security",
	    "@maven//:org_apache_cxf_cxf_rt_transports_http",
	    "@maven//:org_apache_cxf_cxf_rt_rs_service_description",
	    "@maven//:org_apache_poi_poi",
            "@maven//:org_apache_poi_poi_ooxml",	
            "@maven//:org_eclipse_jgit_org_eclipse_jgit",
            "@maven//:org_eclipse_jdt_org_eclipse_jdt_annotation",
            "@maven//:org_eclipse_jdt_org_eclipse_jdt_core",
	    "@maven//:org_hsqldb_hsqldb",
            "@ext_jars//:eclipse_equinox_common",
            "@ext_jars//:eclipse_core",
	    "@ext_jars//:emf",
	    "@ext_jars//:apache_commons_lang",
	    "@ext_jars//:javax.annotation",
            "@ext_jars//:nebula",

         ]
)


alias(
   name = "accessor",
   actual = "//org.eclipse.osee.accessor:accessor"
)

alias(
   name = "account_rest_client",
   actual = "//org.eclipse.osee.account.rest.client:account_rest_client"
)


alias(
   name = "account_admin",
   actual = "//org.eclipse.osee.account.admin:account_admin"
)

alias(
   name = "account_rest",
   actual = "//org.eclipse.osee.account.rest:account_rest"
)

alias(
   name = "account_rest_model",
   actual = "//org.eclipse.osee.account.rest.model:account_rest_model"
)

alias(
   name = "activity",
   actual = "//org.eclipse.osee.activity"
)

alias(
   name = "activity_api",
   actual = "//org.eclipse.osee.activity.api"
)

alias(
   name = "app",
   actual = "//org.eclipse.osee.app"
)

alias(
   name = "ats_api",
   actual = "//org.eclipse.osee.ats.api"
)

alias(
   name = "ats_ide",
   actual = "//org.eclipse.osee.ats.ide"
)

alias(
   name = "ats_core",
   actual = "//org.eclipse.osee.ats.core"
)

alias(
   name = "ats_rest",
   actual = "//org.eclipse.osee.ats.rest"
)

alias(
   name = "authentication_admin", 
   actual = "//org.eclipse.osee.authentication.admin"
)

alias(
   name = "authentication_ldap", 
   actual = "//org.eclipse.osee.authentication.ldap"
)

alias(
   name = "authorization_admin", 
   actual = "//org.eclipse.osee.authorization.admin"
)

alias(
   name = "cache_admin", 
   actual = "//org.eclipse.osee.cache.admin"
)

alias(
   name = "config_admin", 
   actual = "//org.eclipse.osee.config.admin"
)

alias(
   name = "console_admin", 
   actual = "//org.eclipse.osee.console.admin"
)

alias(
   name = "define",
   actual = "//org.eclipse.osee.define"
)

alias(
   name = "define_api",
   actual = "//org.eclipse.osee.define.api"
)

alias(
   name = "define_ide",
   actual = "//org.eclipse.osee.define.ide"
)

alias(
   name = "disposition_rest",
   actual = "//org.eclipse.osee.disposition.rest"
)

alias(
   name = "disposition_rest_model",
   actual = "//org.eclipse.osee.disposition.rest.model"
)

alias(
   name = "framework_core_model",
   actual = "//org.eclipse.osee.framework.core.model"
)

alias(
   name = "framework_core_client",
   actual = "//org.eclipse.osee.framework.core.client"
)

alias(
   name = "framework_core_server",
   actual = "//org.eclipse.osee.framework.core.server"
)

alias(
   name = "framework_jdk_core",
   actual = "//org.eclipse.osee.framework.jdk.core"
)

alias(
   name = "framework_messaging",
   actual = "//org.eclipse.osee.framework.messaging"
)

alias(
   name = "framework_help_ui",
   actual = "//org.eclipse.osee.framework.help.ui"
)

alias(
   name = "framework_messaging_event_res",
   actual = "//org.eclipse.osee.framework.messaging.event.res"
)

alias(
   name = "framework_plugin_core",
   actual = "//org.eclipse.osee.framework.plugin.core"
)

alias(
   name = "framework_skynet_core",
   actual = "//org.eclipse.osee.framework.skynet.core"
)

alias(
   name = "framework_resource_management",
   actual = "//org.eclipse.osee.framework.resource.management"
)

alias(
   name = "framework_server_ide",
   actual = "//org.eclipse.osee.framework.server.ide"
)

alias(
   name = "framework_server_ide_api",
   actual = "//org.eclipse.osee.framework.server.ide.api"
)

alias(
   name = "framework_ui_swt",
   actual = "//org.eclipse.osee.framework.ui.swt"
)

alias(
   name = "framework_ui_plugin",
   actual = "//org.eclipse.osee.framework.ui.plugin"
)

alias(
   name = "framework_ui_skynet",
   actual = "//org.eclipse.osee.framework.ui.skynet"
)

alias(
   name = "framework_ui_ws",
   actual = "//org.eclipse.osee.framework.ui.ws"
)

alias(
   name = "jaxrs",
   actual = "//org.eclipse.osee.jaxrs"
)

alias(
   name = "jaxrs_server",
   actual = "//org.eclipse.osee.jaxrs.server"
)

alias(
   name = "jdbc",
   actual = "//org.eclipse.osee.jdbc"
)


alias(
   name = "logger",
   actual = "//org.eclipse.osee.logger"
)

alias(
   name = "logger_slf4j",
   actual = "//org.eclipse.osee.logger.slf4j"
)

alias(
   name = "mim",
   actual = "//org.eclipse.osee.mim"
)

alias(
    name = "orcs",
    actual = "//org.eclipse.osee.orcs",
)

alias(
    name = "orcs_core",
    actual = "//org.eclipse.osee.orcs.core",
)

alias(
    name = "orcs_rest",
    actual = "//org.eclipse.osee.orcs.rest",
)

alias(
    name = "orcs_rest_model",
    actual = "//org.eclipse.osee.orcs.rest.model",
)

alias(
    name = "ote_define",
    actual = "//org.eclipse.osee.ote.define",
)

alias(
    name = "ote_ui_define",
    actual = "//org.eclipse.osee.ote.ui.define",
)

alias(
    name = "template_engine",
    actual = "//org.eclipse.osee.template.engine"
)

alias(
    name = "vcast",
    actual = "//org.eclipse.osee.vcast"
)
