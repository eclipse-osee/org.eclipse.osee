load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.app",
    visibility = ["//visibility:public"]
)



java_library(
        name = "org.eclipse.osee.app",
        resources = ["js_files","applet_files","html_files"],
        srcs = glob(["**/*.java"]),
        deps = ["java_deps", "//org.eclipse.osee.framework.core.packages","//org.eclipse.osee.jdbc",
                "//org.eclipse.osee.template.engine", "//org.eclipse.osee.orcs"],
        visibility = ["//visibility:public"]

)

filegroup(
    name = "js_files",
    srcs = 
	    glob(["OSGI-INF/oseeApp/js/*.js"])
  , 

)

filegroup(
    name = "applet_files",
    srcs =
	[
	   "src/org/eclipse/osee/app/html/oseeApplet.css",
	   "src/org/eclipse/osee/app/html/oseeApplet.html",
	   "src/org/eclipse/osee/app/html/oseeApplet.js",
	   "OSEE-INF/oseeApp/css/app.css",
	   "OSGI-INF/app.application.xml",
	   "META-INF/MANIFEST.MF"
	]

  ,
)

filegroup(
    name = "html_files",
    srcs =
            glob(["OSEE-INF/oseeApp/*.html"])
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
                "@maven//:jakarta_ws_rs_jakarta_ws_rs_api",
                "@maven//:javax_activation_activation",
                "@maven//:jakarta_xml_bind_jakarta_xml_bind_api",
                "@maven//:org_slf4j_slf4j_api",
                "@maven//:javax_mail_mail",
                "@maven//:org_antlr_antlr_runtime",
                "@maven//:com_google_guava_guava",
		"@maven//:org_hsqldb_hsqldb",
		"@maven//:javax_servlet_javax_servlet_api",
		],
)
