load("@rules_osee//bat:toolchain.bzl","bat_toolchain")
bat_toolchain(
    name="local_bat",
    target_tool="//cli/bat:bat",
    bat_features=["no_config"]
)
toolchain(
    name = "local_bat_toolchain",
    exec_compatible_with = ["@platforms//os:linux"],
    toolchain = ":local_bat",
    toolchain_type = "@rules_osee//bat:toolchain_type",
)
toolchain(
    name = "local_bat_toolchain_win",
    exec_compatible_with = ["@platforms//os:windows"],
    toolchain = ":local_bat",
    toolchain_type = "@rules_osee//bat:toolchain_type",
)
package_group(
    name = "model",
    packages = ["//plugins/org.eclipse.osee.framework.jdk.core"],
)
