java_test(
    name = "OseeDefineOperationsTestSuite",
    srcs = glob(["**/*.java"]),
    deps = ["java_deps", "//org.eclipse.osee.framework.jdk.core", "//org.eclipse.osee.framework.core",
            "//org.eclipse.osee.orcs", "//org.eclipse.osee.define"]
)

java_library(
        name = "java_deps",
        exports = [
                "@maven//:com_google_guava_guava",
		"@maven//:org_mockito_mockito_core",
		"@maven//:org_hamcrest_hamcrest_core",
		"@eclipse_osgi//jar",
		"@eclipse_equinox_common//jar"
                ],
)
