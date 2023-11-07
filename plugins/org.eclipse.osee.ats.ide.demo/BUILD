java_library(
        name = "org.eclipse.osee.ats.ide.demo",
        resources = ["files"],
        srcs = glob(["**/*.java"]),
        deps = ["java_deps",
		"//org.eclipse.osee.framework.core.packages",
		"//org.eclipse.osee.define.api",
		"//org.eclipse.osee.cache.admin",
		"//org.eclipse.osee.jdbc",
		"//org.eclipse.osee.jaxrs",
        	"//org.eclipse.osee.ats.core",
        	"//org.eclipse.osee.ats.api",
        	"//org.eclipse.osee.ats.ide",
        	"//org.eclipse.osee.define.ide",
        	"//org.eclipse.osee.support.test.util",
        	"//org.eclipse.osee.framework.ui.skynet",
        	"//org.eclipse.osee.framework.skynet.core",
        	"//org.eclipse.osee.framework.ui.plugin",
        	"//org.eclipse.osee.framework.ui.swt",
        	"//org.eclipse.osee.framework.database.init",
		"//org.eclipse.osee.framework.messaging",
		"//org.eclipse.osee.framework.messaging.event.res",
		"//org.eclipse.osee.orcs.rest.model",
		"//org.eclipse.osee.framework.core.model",
		"//org.eclipse.osee.framework.core.client",
		"//org.eclipse.osee.framework.plugin.core",
		"//org.eclipse.osee.activity.api"
	],
                visibility = ["//visibility:public"]
)

filegroup(
    name = "files",
    srcs = glob(["**/*"]),
)

java_library(
        name = "java_deps",
        exports = [
                "@maven//:com_fasterxml_jackson_core_jackson_core",
                "@maven//:com_fasterxml_jackson_core_jackson_databind",
                "@maven//:com_fasterxml_jackson_core_jackson_annotations",
                "@maven//:commons_codec_commons_codec",
                "@maven//:org_eclipse_jdt_org_eclipse_jdt_annotation",
                "@maven//:javax_ws_rs_javax_ws_rs_api",
                "@maven//:javax_activation_activation",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                "@maven//:org_slf4j_slf4j_api",
                "@maven//:javax_mail_mail",
		"@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
		"@maven//:org_hsqldb_hsqldb",
		"@maven//:org_jsoup_jsoup",
		"@ext_jars//:eclipse_core",
		"@ext_jars//:eclipse_equinox_common",
		"@ext_jars//:eclipse_osgi_services",
                "@ext_jars//:jface"
               ],
)

