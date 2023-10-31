java_test(
    name = "OrcsDb_JUnit_TestSuite",
    srcs = glob(["**/*.java"]),
    resources = ["test_data"],
    deps = ["//org.eclipse.osee.orcs.db","//org.eclipse.osee.framework.core.packages",
            "//org.eclipse.osee.framework.core.model", "//org.eclipse.osee.framework.resource.management",
	    "//org.eclipse.osee.jdbc",
            "//org.eclipse.osee.orcs.core", "//org.eclipse.osee.orcs", "@maven//:org_mockito_mockito_core"]
)

filegroup(
    name = "test_data",
    srcs = glob(["src/org/eclipse/osee/orcs/db/internal/search/tagger/data/*"])
)
