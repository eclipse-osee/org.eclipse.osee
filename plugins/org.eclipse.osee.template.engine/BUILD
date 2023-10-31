
java_library(
        name = "org.eclipse.osee.template.engine",
        resources = ["x_files","html_files"],
	srcs = glob(["**/*.java"]),
        deps = ["java_deps","//org.eclipse.osee.framework.core.packages","//org.eclipse.osee.jaxrs"],
        visibility = ["//visibility:public"]

)

filegroup(
    name = "x_files",
    srcs =
	  [
	    "OSGI-INF/jaxrs.mvc.page.factory.resolver.xml",
	    "OSGI-INF/jaxrs.template.admin.application.xml",
	    "OSGI-INF/template.registry.xml",
	    "src/org/eclipse/osee/template/engine/css/built-editor.css",
	    "src/org/eclipse/osee/template/engine/html/exception.html",
	    "src/org/eclipse/osee/template/engine/html/pageDeclaration.html",
	    "src/org/eclipse/osee/template/engine/js/built-editor.js",
	    "src/org/eclipse/osee/template/engine/js/oseeCore.js"
	  ]   
  , 
)

filegroup(
    name = "html_files",
    srcs = glob(["src/org/eclipse/osee/template/engine/xml/*.xml"]),
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
		"@ext_jars//:eclipse_osgi_services",
              #  "@ext_jars//:eclipse_core"
                ],
)
