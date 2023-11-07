java_library(
    name = "org.eclipse.osee.ats.ide.help.ui",
    resources = [":web_files"],
    deps = ["//org.eclipse.osee.framework.core"],
    srcs = ["src/org/eclipse/osee/ats/help/ui/AtsHelpContext.java"],
    visibility = ["//org.eclipse.osee.ats.ide.help.ui.test:__pkg__","//org.eclipse.osee.ats.ide:__pkg__"]
)

filegroup(
    name = "web_files",
    srcs = glob(["**/*"])
)
