#
# Copyright (c) 2024 Boeing
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Boeing - initial API and implementation
# 
load("@rules_osee//bat:apply_applicability.bzl","apply_applicability")
load("@rules_osee//bat:toolchain.bzl", "bat_toolchain")
load("@rules_pkg//pkg:mappings.bzl", "pkg_files", "strip_prefix")
load("@rules_pkg//pkg:zip.bzl", "pkg_zip")
load("@aspect_bazel_lib//lib:diff_test.bzl", "diff_test")
load("@my_product_line//config_files:defs.bzl","AVAILABLE_PLATFORMS")

apply_applicability(
    name="example_processed_markdown_files",
    srcs=["//:test.md","//deeply/nested/folder:test2.md"],
    applic_config = ":PRODUCT_A.json"
)
# The following would be called like so:
# bazel build //:example_processed_markdown_files_resolved --platforms=@my_product_line//platforms/configurations:Product_A
apply_applicability(
    name="example_processed_markdown_files_resolved",
    srcs=["//:test.md","//deeply/nested/folder:test2.md"],
    begin_comment_syntax=["abc"],
    applic_config = "@my_product_line//config_files:resolved_config"
)
apply_applicability(
    name="example_processed_markdown_files_direct",
    srcs=["//:test.md","//deeply/nested/folder:test2.md"],
    begin_comment_syntax=["abc"],
    applic_config="@my_product_line//config_files:Product_A.json"
)
[
    apply_applicability(
        name="example_processed_markdown_files_"+key,
        srcs=["//:test.md","//deeply/nested/folder:test2.md"],
        begin_comment_syntax=["abc"],
        applic_config = values
    )
for key, values in AVAILABLE_PLATFORMS.items()
]
pkg_files(
    name="example_packaged_markdown_files",
    srcs = ["example_processed_markdown_files"],
    strip_prefix = strip_prefix.from_pkg(),
)
pkg_zip(
    name="example_zipped_markdown_files",
    srcs = ["example_packaged_markdown_files"],
    out="example_zipped_markdown_files.zip"
)
diff_test(
    name = "applic_test",
    file1 = "//:example_zipped_markdown_files",
    file2 = "//__snapshot__:example_zipped_markdown_files_snapshot.zip",
)


## example of vendoring a bat toolchain:

# bat_toolchain(
#     name = "my_bat_toolchain",
#     target_tool_path = "./bat",
# )
# toolchain(
#     name = "my_bat",
#     exec_compatible_with = [
#         "@platforms//os:linux",
#         "@platforms//cpu:x86_64",
#     ],
#     toolchain = ":my_bat_toolchain",
#     toolchain_type = "@rules_osee//bat:toolchain_type",
# )