java_test(
    name = "OrcsDb_JUnit_TestSuite",
    srcs = glob(["**/*.java"]),
    resources = ["test_data"],
    deps = ["//org.eclipse.osee.orcs.db:orcs_db","//framework_core_packages",
            "//:framework_core_model", "//:framework_resource_management","//:jdbc",
            "//:orcs_core", "//:orcs", "@maven//:org_mockito_mockito_core"]
)

filegroup(
    name = "test_data",
    srcs = glob(["src/org/eclipse/osee/orcs/db/internal/search/tagger/data/*"])
)
