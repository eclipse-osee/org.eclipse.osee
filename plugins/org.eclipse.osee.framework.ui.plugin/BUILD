java_library(
    name = "org.eclipse.osee.framework.ui.plugin",
	resources = ["xml_files", "exsd_files", "img"],
	srcs = 
	glob(["**/*.java"]),
	deps = ["java_deps",
		"//org.eclipse.osee.framework.core.packages",
	 	"//org.eclipse.osee.framework.core.client",
	 	"//org.eclipse.osee.framework.plugin.core",
	 	"//org.eclipse.osee.framework.ui.swt",
	 	"//org.eclipse.osee.activity.api",
	 	"//org.eclipse.osee.framework.messaging.event.res",
	 	"//org.eclipse.osee.framework.server.ide.api",
	 	"//org.eclipse.osee.framework.core.server",
	 	"//org.eclipse.osee.framework.messaging",
	 	"//org.eclipse.osee.cache.admin",
	 	"//org.eclipse.osee.framework.core.model",
	 	"//org.eclipse.osee.jaxrs",
	 	"//org.eclipse.osee.orcs",
	 	"//org.eclipse.osee.orcs.rest.model",
	 	"//org.eclipse.osee.jdbc",
	 	"//org.eclipse.osee.framework.server.ide",
	 	"//org.eclipse.osee.console.admin",
	 	"//org.eclipse.osee.orcs.core",
	 	"//org.eclipse.osee.orcs.rest",
	 	"//org.eclipse.osee.activity",
	 	"//org.eclipse.osee.define.api",
	 	"//org.eclipse.osee.framework.resource.management"
		],
	visibility = ["//visibility:public"]
              )

filegroup(
    name = "xml_files",
    srcs =
          [
	    "plugin.xml"
           ],
)

filegroup(
    name = "exsd_files",
    srcs =
          [
            "OSEE-INF/extensionPts/PropertyStoreControl.exsd",
            "OSEE-INF/extensionPts/WorkbenchTargetProvider.exsd",
	    "OSEE-INF/extensionPts/XNavigateItemProvider.exsd",
	    "META-INF/MANIFEST.MF"
           ],
)

filegroup(
    name = "img",
    srcs =
          [
		"OSEE-INF/images/admin.gif",
		"OSEE-INF/images/browse.gif",
		"OSEE-INF/images/bug.gif",
		"OSEE-INF/images/chkbox_disabled.gif",
		"OSEE-INF/images/chkbox_enabled.gif",
		"OSEE-INF/images/edit.gif",
		"OSEE-INF/images/errorSm.gif",
		"OSEE-INF/images/faq.gif",
		"OSEE-INF/images/filter.gif",
		"OSEE-INF/images/folder.gif",
		"OSEE-INF/images/gear.gif",
		"OSEE-INF/images/lockkey.gif",
		"OSEE-INF/images/needSave.gif",
		"OSEE-INF/images/new.gif",
		"OSEE-INF/images/open.gif",
		"OSEE-INF/images/osee_16.png",
		"OSEE-INF/images/permission.bmp",
		"OSEE-INF/images/publish.gif",
		"OSEE-INF/images/refresh.gif",
		"OSEE-INF/images/remove.gif",
		"OSEE-INF/images/report.gif",
		"OSEE-INF/images/sample.gif",
		"OSEE-INF/images/saved.gif",
		"OSEE-INF/images/train.gif",
		"OSEE-INF/images/viewMenu.gif",
		"OSEE-INF/images/www.gif"
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
                "@maven//:javax_ws_rs_javax_ws_rs_api",
                "@maven//:javax_activation_activation",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                "@maven//:org_slf4j_slf4j_api",
                "@maven//:javax_mail_mail",
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
		"@maven//:org_apache_cxf_cxf_rt_frontend_jaxrs",
		"@maven//:org_eclipse_jgit_org_eclipse_jgit",
		"@maven//:org_jsoup_jsoup",
		"@maven//:com_jcraft_jsch",
		"@maven//:org_eclipse_jdt_org_eclipse_jdt_core",
		"@maven//:org_apache_geronimo_specs_geronimo_jms_1_1_spec",
                "@maven//:org_apache_activemq_activemq_all",
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:eclipse_ui",
		"@ext_jars//:swt",
		"@ext_jars//:jface",
		"@ext_jars//:eclipse_core",
		"@ext_jars//:eclipse_equinox_common",
		"@ext_jars//:help",
		"@ext_jars//:junit"
	],
)
