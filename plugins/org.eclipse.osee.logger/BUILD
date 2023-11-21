load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.logger",
)

java_library(
	name = "org.eclipse.osee.logger",
 	resources = ["manifest"],
	srcs = ["src/org/eclipse/osee/logger/Log.java"],
 	visibility = ["//visibility:public"]
)	

filegroup(
    name = "manifest",
    srcs = ["META-INF/MANIFEST.MF"]
)
