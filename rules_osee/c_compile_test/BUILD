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
load("@rules_pkg//pkg:zip.bzl", "pkg_zip")
load("@aspect_bazel_lib//lib:diff_test.bzl", "diff_test")

apply_applicability(
    name="processed_subhdrs",
    srcs=glob(["level3/**/*.h"]),
    applic_config = "@my_product_line//config_files:resolved_config"
)

apply_applicability(
    name="processed_hdrs",
    srcs=glob(["*.h"]),
    applic_config = "@my_product_line//config_files:resolved_config"
)

apply_applicability(
    name="processed_srcs",
    srcs=[":hello_world.c"],
    applic_config = "@my_product_line//config_files:resolved_config"
)

cc_library(
    name="subheaders",
    hdrs=["//level1/level2:processed_subhdrs"]
)

cc_library(
    name="subheaders_from_top",
    hdrs=[":processed_subhdrs"]
)

cc_library(
    name="headers",
    hdrs=[":processed_hdrs"]
)

cc_library(
    name="test_nested",
    srcs=[":processed_srcs"],
    hdrs=["//level1/level2:processed_subhdrs",":headers"],
    includes = ["//level1/level2:processed_subhdrs",":processed_hdrs", "level1/level2"],
    deps=[":subheaders",":headers"],
    data=[
        "//level1/level2:processed_subhdrs",
        ":processed_hdrs"
    ]
)

cc_library(
    name="test_flat",
    srcs=[":processed_srcs"],
    hdrs=[":subheaders_from_top",":headers"],
    includes = [":processed_subhdrs",":processed_hdrs"],
    deps=[":subheaders_from_top",":headers"],
    data=[
        ":processed_subhdrs",
        ":processed_hdrs"
    ]
)

pkg_zip(
    name="example_zipped_test_flat",
    srcs = [":test_flat"],
    out="test_flat.zip"
)

pkg_zip(
    name="example_zipped_test_nested",
    srcs = [":test_nested"],
    out="test_nested.zip"
)

diff_test(
    name = "flat_zip_test",
    file1 = ":example_zipped_test_flat",
    file2 = "//__snapshot__:test_flat.zip",
)

diff_test(
    name = "nested_zip_test",
    file1 = ":example_zipped_test_nested",
    file2 = "//__snapshot__:test_nested.zip",
)