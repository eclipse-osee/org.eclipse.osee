load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "define",
)

java_library(
    name = "org.eclipse.osee.define",
    resources = ["xml_files","html_files"],
    srcs = glob(["**/*.java"]),
    deps = ["java_deps","//org.eclipse.osee.framework.core.packages","//org.eclipse.osee.jdbc","//org.eclipse.osee.orcs",
            "//org.eclipse.osee.jaxrs","//org.eclipse.osee.orcs.core",
            "//org.eclipse.osee.orcs.rest.model","//org.eclipse.osee.template.engine",
            "//org.eclipse.osee.framework.core.server","//org.eclipse.osee.define.api",
	    "//org.eclipse.osee.ats.api","//org.eclipse.osee.activity.api","//org.eclipse.osee.app","//org.eclipse.osee.framework.core.model"],
    visibility = ["//visibility:public"]

)

filegroup(
    name = "xml_files",
    srcs = [
	     "OSGI-INF/define.application.impl.xml",
	     "OSGI-INF/define.operations.impl.xml",
	     "OSGI-INF/define.attachment.writer.xml",
           ]
  ,
    visibility = ["//visibility:public"]
)


#genrule(
#    name = "bazel_jar_to_osgi_jar",
#    srcs = [
#	    ":define"  # a filegroup with multiple files in it ==> $(locations)
#    ],
#    outs = ["orgf.eclipse.osee.define.jar"],
#    cmd = "cp $(locations :define) .;mkdir amadeus;chmod 777 *; mv *.jar amadeus; cd  amadeus;chmod 777 *; \
#		    jar xf *.jar; rm -rf bin META-INF; chmod 777 *;ls org.eclipse.osee.define; mv org.eclipse.osee.define/* .;chmod 777 *;rm -rf org.eclipse.osee.define; \
#		    jar cfm *.jar META-INF/MANIFEST.MF *;pwd;cp libdefine.jar ..;cd ..;date '+%Y-%m-%d'; cp libdefine.jar $@",
#)



filegroup(
    name = "html_files",
    srcs =
          [
            "src/org/eclipse/osee/define/rest/internal/html/dataRightsReport.html",
            "src/org/eclipse/osee/define/rest/internal/html/publishLowHighReport.html",
            "src/org/eclipse/osee/define/rest/internal/html/systemSafetyReport.html",
	    "src/ApplicabilityGrammar.tokens",
	    "META-INF/MANIFEST.MF"
	  ]
  ,
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
		"@maven//:org_jsoup_jsoup",
		"@maven//:com_jcraft_jsch",
		"@maven//:org_eclipse_jgit_org_eclipse_jgit",
		"@ext_jars//:eclipse_osgi_services",
		"@ext_jars//:nebula",
		"@ext_jars//:apache_commons_lang",
		"@ext_jars//:rmf",
		"@ext_jars//:emf",
		"@ext_jars//:jgit"
                ],
)
