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
load("@rules_pkg//:mappings.bzl", "pkg_files", "strip_prefix")
load("@rules_pkg//:pkg.bzl", "pkg_tar")
pkg_files(
    name="files_to_release",
    srcs = [
        "BUILD",
    ] + glob(["*.bzl"]),
    strip_prefix = strip_prefix.from_root(),
    visibility = [
        "//bat:__pkg__",
    ]
)