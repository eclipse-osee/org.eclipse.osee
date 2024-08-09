load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.nebula.widgets.xviewer.core",
    visibility = ["//visibility:public"]
)


java_library(
	name = "org.eclipse.nebula.widgets.xviewer.core",
	resources = ["manifest"],
	srcs = glob(["**/*.java"]),	
        visibility = ["//visibility:public"]
)

filegroup(
        name = "manifest",
        srcs = ["META-INF/MANIFEST.MF"]
)

