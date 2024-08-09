load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.nebula.widgets.xviewer",
    visibility = ["//visibility:public"]
)


java_library(
	name = "org.eclipse.nebula.widgets.xviewer",
	resources = ["manifest"],
	srcs = glob(["**/*.java"]),	
	deps = ["@ext_jars//:swt","@ext_jars//:eclipse_ui","@eclipse_osgi//jar","@ext_jars//:jface","@eclipse_core_runtime//jar","@eclipse_equinox_registry//jar",
	        "@eclipse_core_jobs//jar","@eclipse_core_commands//jar","//org.eclipse.nebula.widgets.xviewer.core","@eclipse_equinox_common//jar",],
        visibility = ["//visibility:public"]
)

filegroup(
        name = "manifest",
        srcs = ["META-INF/MANIFEST.MF"]
)

