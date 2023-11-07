java_library(
    name = "org.eclipse.osee.framework.ui.skynet",
	resources = ["xml_files", "exsd_files", "src_img", "images", "txt_files"],
	srcs = glob(["**/*.java"]),
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
		"//org.eclipse.osee.framework.resource.management",
		"//org.eclipse.osee.account.rest.client",
	 	"//org.eclipse.osee.account.rest.model",
		"//org.eclipse.osee.framework.help.ui",
		"//org.eclipse.osee.framework.skynet.core",
		"//org.eclipse.osee.framework.ui.plugin",
		"//org.eclipse.osee.ats.api",
		"//org.eclipse.osee.framework.ui.ws"
		],
	visibility = ["//visibility:public"]
              )

filegroup(
    name = "xml_files",
    srcs =
          [
	    "plugin.xml",
	    "OSGI-INF/access.artifact.lock.topic.artifact.explorer.handler.xml",
	    "OSGI-INF/access.artifact.lock.topic.handler.xml",
	    "OSGI-INF/access.artifact.lock.topic.handler2.xml",
	    "OSGI-INF/access.user.authenticated.topic.artifact.explorer.handler.xml",
	    "OSGI-INF/default.cm.service.xml",
	    "OSEE-INF/DeleteInvalidAttributeTypesFromBranchUi.xml",
	    "OSEE-INF/blamUi/FixTemplateContentArtifactsUi.xml",
	    "OSEE-INF/blamUi/XWidgetsExampleBlamUi.xml",
	    "META-INF/MANIFEST.MF"
           ],
)

filegroup(
    name = "exsd_files",
    srcs =
          [
            "OSEE-INF/extensionPts/ArtifactEditorProvider.exsd",
	    "OSEE-INF/extensionPts/ArtifactImageProvider.exsd",
	    "OSEE-INF/extensionPts/ArtifactRenderer.exsd",
	    "OSEE-INF/extensionPts/ArtifactRendererTemplate.exsd",
	    "OSEE-INF/extensionPts/AttributeXWidgetProvider.exsd",
	    "OSEE-INF/extensionPts/BlamOperation.exsd",
	    "OSEE-INF/extensionPts/DatabaseHealthOperation.exsd",
	    "OSEE-INF/extensionPts/OseeDictionary.exsd",
	    "OSEE-INF/extensionPts/SimpleTemplateProviderTemplate.exsd",
	    "OSEE-INF/extensionPts/TemplateProvider.exsd",
	    "OSEE-INF/extensionPts/UserNavigateItem.exsd",
	    "OSEE-INF/extensionPts/WordMlProducer.exsd",
	    "OSEE-INF/extensionPts/WorkDefinitionProvider.exsd",
	    "OSEE-INF/extensionPts/XWidgetProvider.exsd"
           ],
)

filegroup(
    name = "src_img",
    srcs =
          [
		"src/org/eclipse/osee/framework/ui/skynet/widgets/checkbox/images/chkbox_checked.gif",
		"src/org/eclipse/osee/framework/ui/skynet/widgets/checkbox/images/chkbox_disabled.gif",
		"src/org/eclipse/osee/framework/ui/skynet/widgets/checkbox/images/chkbox_unchecked.gif",
		"src/org/eclipse/osee/framework/ui/skynet/widgets/checkbox/images/refresh.gif",
		"src/org/eclipse/osee/framework/ui/skynet/widgets/checkbox/images/report.gif"
	  ],
)

filegroup(
    name = "images",
    srcs = glob(["OSEE-INF/images/*"])
)

filegroup(
    name = "txt_files",
    srcs = 
    [
	    "OSEE-INF/spellCheck/AllWords.txt",
	    "OSEE-INF/spellCheck/altamer.0",
	    "OSEE-INF/spellCheck/altamer.1",
	    "OSEE-INF/spellCheck/altamer.2",
	    "OSEE-INF/spellCheck/american.0",
	    "OSEE-INF/spellCheck/american.1",
	    "OSEE-INF/spellCheck/american.2",
	    "OSEE-INF/spellCheck/british.0",
	    "OSEE-INF/spellCheck/british.1",
	    "OSEE-INF/spellCheck/british.2",
	    "OSEE-INF/spellCheck/english.0",
	    "OSEE-INF/spellCheck/english.1",
	    "OSEE-INF/spellCheck/english.2",
	    "OSEE-INF/spellCheck/english.3",
	    "OSEE-INF/spellCheck/README",
	    "OSEE-INF/spellCheck/README.txt"
    ],
)

java_library(
        name = "java_deps",
        exports = [
                	"@maven//:com_fasterxml_jackson_core_jackson_core",
                	"@maven//:com_fasterxml_jackson_core_jackson_databind",
                	"@maven//:com_fasterxml_jackson_core_jackson_annotations",
			"@maven//:com_vladsch_flexmark_flexmark_util_data",
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
			"@maven//:org_apache_cxf_cxf_rt_rs_client",
			"@maven//:org_eclipse_jgit_org_eclipse_jgit",
			"@maven//:org_jsoup_jsoup",
			"@maven//:com_jcraft_jsch",
			"@maven//:org_eclipse_jdt_org_eclipse_jdt_core",
			"@maven//:org_apache_geronimo_specs_geronimo_jms_1_1_spec",
                        "@maven//:org_apache_activemq_activemq_all",
			"@maven//:org_mockito_mockito_core",
			"@maven//:org_hamcrest_hamcrest_core",
			"@maven//:com_vladsch_flexmark_flexmark_ext_gfm_tasklist",
			"@maven//:com_vladsch_flexmark_flexmark_ext_tables",
			"@maven//:com_vladsch_flexmark_flexmark_util_ast",
			"@maven//:com_vladsch_flexmark_flexmark",
			"@ext_jars//:eclipse_osgi_services",
			"@ext_jars//:eclipse_ui",
			"@ext_jars//:swt",
			"@ext_jars//:jface",
			"@ext_jars//:eclipse_core",
			"@ext_jars//:eclipse_equinox_common",
			"@ext_jars//:apache_commons_lang",
			"@ext_jars//:nebula"
	],
)
