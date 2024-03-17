java_test(
    name = "Disposition_JUnit_TestSuite",
    resources = ["resources",],
    srcs = glob(["**/*.java"]),
    deps = ["//org.eclipse.osee.disposition.rest",
            "//org.eclipse.osee.disposition.rest.model",
            "//org.eclipse.osee.framework.core.packages","//org.eclipse.osee.jaxrs",
	    "//org.eclipse.osee.orcs", "@maven//:org_mockito_mockito_core",
	    "@maven//:com_fasterxml_jackson_core_jackson_core",
	    "@maven//:com_fasterxml_jackson_core_jackson_annotations",
            "@maven//:jakarta_ws_rs_jakarta_ws_rs_api","@maven//:com_fasterxml_jackson_core_jackson_databind"]
)


filegroup(
    name = "resources",
    srcs = glob(["**/*"])

)

