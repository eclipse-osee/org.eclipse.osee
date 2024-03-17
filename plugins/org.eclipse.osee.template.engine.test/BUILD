java_test(
    name = "OseeTemplateEngineTestSuite",
    resources = ["resources"],
    srcs = glob(["**/*.java"]),
    deps = ["//org.eclipse.osee.template.engine","@maven//:org_mockito_mockito_core",
            "//org.eclipse.osee.framework.jdk.core"]
)

filegroup(
    name = "resources", 
    srcs = glob(["**/*"])
)
