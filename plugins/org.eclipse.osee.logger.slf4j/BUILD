
load("//:osgi.bzl", "osgi_jar_converter")

osgi_jar_converter(
    name = "osgi_jar",
    arg = "org.eclipse.osee.logger.slf4j",
    visibility = ["//visibility:public"]
)

java_library(
        name = "org.eclipse.osee.logger.slf4j",
	resources = ["manifest"],
	srcs = glob(["**/*.java"]),
	deps = ["java_deps","//org.eclipse.osee.framework.jdk.core","//org.eclipse.osee.logger"],
        visibility = ["//visibility:public"]
)

filegroup(
    name = "manifest",
    srcs = glob(["**/*"])
)

java_library(
        name = "java_deps",
        exports = ["@maven//:org_slf4j_slf4j_api","@eclipse_osgi//jar","@eclipse_osgi_services//jar"],
)

