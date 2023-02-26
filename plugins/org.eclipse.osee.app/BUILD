java_library(
        name = "app",
        resources = ["js_files","applet_files","html_files"],
        srcs =                
		glob(["**/*.java"]),
                deps = ["java_deps"],
                visibility = ["//visibility:public"]
              )

filegroup(
    name = "js_files",
    srcs = 
	    glob(["OSGI-INF/oseeApp/js/*.js"])
  , 
    visibility = ["//visibility:public"]
)

filegroup(
    name = "applet_files",
    srcs =
	[
	   "src/org/eclipse/osee/app/html/oseeApplet.css",
	   "src/org/eclipse/osee/app/html/oseeApplet.html",
	   "src/org/eclipse/osee/app/html/oseeApplet.js",
	   "OSEE-INF/oseeApp/css/app.css",
	   "OSGI-INF/app.application.xml"
	]

  ,
    visibility = ["//visibility:public"]
)

filegroup(
    name = "html_files",
    srcs =
            glob(["OSEE-INF/oseeApp/*.html"])
  ,
    visibility = ["//visibility:public"]
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
		"@maven//:org_hsqldb_hsqldb",
		"@maven//:javax_servlet_javax_servlet_api",
                "@framework_core//:framework_core",
		"@framework_core_server//:framework_core_server",
                "@framework_logging//:framework_logging",
		"@framework_jdk_core//:framework_jdk_core",
		"@ext_jars//:eclipse_osgi_services",
		"@framework_core_model//:framework_core_model",
        	"@logger//:logger",
		"@jdbc//:jdbc",
		"@orcs//:orcs",
		"@console_admin//:console_admin",
		"@cache_admin//:cache_admin",
		"@template_engine//:template_engine"
		],
)
package(default_visibility = ["//visibility:public"])
