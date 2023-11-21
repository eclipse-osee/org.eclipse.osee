load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.framework.help.ui",
)

java_library(
        name = "org.eclipse.osee.framework.help.ui",
	resources = ["common_deps"],
        srcs = glob(["**/*.java"]),
	deps = ["java_deps",
		"mylyn",
		"//org.eclipse.osee.framework.core.packages"
	],
	visibility = ["//visibility:public"]
)
                        
filegroup(
    name = "common_deps",
    srcs = 
         glob(["**/*.xml",
		"**/*.gif",
	       "**/*.xsl",
	       "**/*.html",
	       "**/*.png",
	       "**/*.css",
	       "**/*.jpg",
	       "**/*.MF"
	 ])
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
		"@maven//:org_mockito_mockito_core",
		"@maven//:org_hamcrest_hamcrest_core",
		"@ext_jars//:eclipse_equinox_common",
		"@ext_jars//:eclipse_osgi_services"
                ],
)

java_import(
        name = "mylyn",
        jars = [
                "lib/org.eclipse.mylyn.wikitext.core_1.4.0.I20101018-2300-e3x.jar",
		"lib/org.eclipse.mylyn.wikitext.confluence.core_1.4.0.I20101018-2300-e3x.jar",
		"lib/org.eclipse.mylyn.wikitext.mediawiki.core_1.4.0.I20101018-2300-e3x.jar",
		"lib/org.eclipse.mylyn.wikitext.textile.core_1.4.0.I20101018-2300-e3x.jar",
                "lib/org.eclipse.mylyn.wikitext.tracwiki.core_1.4.0.I20101018-2300-e3x.jar",
		"lib/org.eclipse.mylyn.wikitext.twiki.core_1.4.0.I20101018-2300-e3x.jar"
	
       ]
)
