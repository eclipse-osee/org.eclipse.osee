java_library(
    name = "ats_ide_help_ui",
    resources = [":web_files"],
    deps = ["//org.eclipse.osee.framework.core:framework_core"],
    srcs = ["src/org/eclipse/osee/ats/help/ui/AtsHelpContext.java"],
    visibility = ["//org.eclipse.osee.ats.ide.help.ui.test:__pkg__","//org.eclipse.osee.ats.ide:__pkg__"]
)

filegroup(
    name = "web_files",
    srcs = glob(["**/*"])
)
