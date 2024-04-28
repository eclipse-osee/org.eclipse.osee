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
# Feature[INTERNAL_RELEASE_ONLY]
load("@rules_pkg//:mappings.bzl", "pkg_files", "strip_prefix")
load("@rules_pkg//:pkg.bzl", "pkg_tar")
load("@rules_osee//bat:apply_applicability.bzl","apply_applicability")
apply_applicability(
    name="applic_BUILD",
    srcs=["BUILD"],
    applic_config="@rules_osee//:rules_osee.json"
)
pkg_files(
    name="files_to_release",
    srcs = [
        "applic_BUILD",
    ] + glob(["*.bzl"]),
    strip_prefix = strip_prefix.from_root(),
    visibility = [
        "//visibility:public",
    ]
)
# End Feature