java_library(
        name = "logger_slf4j",
	srcs = glob(["**/*.java"]),
	deps = ["java_deps","//:framework_jdk_core","//:logger"],
        visibility = ["//visibility:public"]
)

java_library(
        name = "java_deps",
        exports = [
		"@maven//:org_slf4j_slf4j_api",
		"@ext_jars//:eclipse_osgi_services"
                ],
)

