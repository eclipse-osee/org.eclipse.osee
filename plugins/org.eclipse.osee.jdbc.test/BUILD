java_test(
    name = "AllJdbcTestSuite",
    resources = ["src/org/eclipse/osee/jdbc/migration/V1__JdbcTest.sql"],
    srcs = glob(["**/*.java"]),
    deps = ["//org.eclipse.osee.jdbc", "//org.eclipse.osee.framework.core.packages","@ext_jars//:eclipse_osgi_services",
            "@maven//:org_mockito_mockito_core"]
)
