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
package(default_visibility = ["//visibility:public"])
load("@bazel_skylib//:bzl_library.bzl", "bzl_library")
load("@rules_osee//bat/private:toolchains_repo.bzl", "PLATFORMS")
# Feature[INTERNAL_RELEASE_ONLY]
load("@rules_osee//bat:apply_applicability.bzl","apply_applicability")
# End Feature
toolchain_type(
    name = "toolchain_type",
    visibility = ["//visibility:public"],
)

[
    platform(
        name = key,
        constraint_values = values.compatible_with,
    )
    for key, values in PLATFORMS.items()
]
# Feature[INTERNAL_RELEASE_ONLY]
apply_applicability(
    name="applic_BUILD",
    srcs=["BUILD"],
    applic_config="@rules_osee//:rules_osee.json"
)
# note: this is filegroup and not pkg_files due to a weird module dependency
filegroup(
    name = "files_to_release",
    srcs = [
        "applic_BUILD",
        "//bat/private:files_to_release"
    ] + glob(["*.bzl"]),
    visibility = [
        "//visibility:public",
    ]
)
# End Feature