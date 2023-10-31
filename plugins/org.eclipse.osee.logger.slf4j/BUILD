java_library(
        name = "org.eclipse.osee.logger.slf4j",
	srcs = glob(["**/*.java"]),
	deps = ["java_deps","//org.eclipse.osee.framework.jdk.core","//org.eclipse.osee.logger"],
        visibility = ["//visibility:public"]
)

java_library(
        name = "java_deps",
        exports = [
		"@maven//:org_slf4j_slf4j_api",
		"@ext_jars//:eclipse_osgi_services"
                ],
)

