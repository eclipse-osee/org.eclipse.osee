java_test(
    name = "Disposition_JUnit_TestSuite",
    resources = ["manifest","tmz"],
    srcs = glob(["**/*.java"]),
    deps = ["//org.eclipse.osee.disposition.rest",
            "//org.eclipse.osee.disposition.rest.model",
            "//org.eclipse.osee.framework.core.packages","//org.eclipse.osee.jaxrs",
	    "//org.eclipse.osee.orcs", "@maven//:org_mockito_mockito_core",
	    "@maven//:com_fasterxml_jackson_core_jackson_core",
	    "@maven//:com_fasterxml_jackson_core_jackson_annotations",
            "@maven//:javax_ws_rs_javax_ws_rs_api","@maven//:com_fasterxml_jackson_core_jackson_databind","@ext_jars//:eclipse_osgi_services"]
)


filegroup(
    name = "tmz",
    srcs = glob(["**/*"])

)

filegroup(
    name = "manifest",
    srcs =
          [
            "META-INF/MANIFEST.MF",
	    "support/sampleTmo.tmo"
           ]
)


