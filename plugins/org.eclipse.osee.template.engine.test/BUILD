java_test(
    name = "OseeTemplateEngineTestSuite",
    resources = ["html_files", "css_files", "xml_files"],
    srcs = glob(["**/*.java"]),
    deps = ["//org.eclipse.osee.template.engine","@maven//:org_mockito_mockito_core",
            "//org.eclipse.osee.framework.jdk.core"]
)

filegroup(
    name = "html_files", 
    srcs = glob(["src/org/eclipse/osee/template/engine/html/*.html"])
)

filegroup(
    name = "css_files", 
    srcs = ["src/org/eclipse/osee/template/engine/css/myTest.css"]
)

filegroup(
    name = "xml_files", 
    srcs = ["src/org/eclipse/osee/template/engine/xml/testWord.xml"]
)

