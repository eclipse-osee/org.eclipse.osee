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
load(":version.bzl", "version")

package(
    default_visibility = ["//visibility:private"],
)

pkg_files(
    name="files_to_publish",
    srcs = [
        "//:BUILD",
        "//:WORKSPACE",
        "//:MODULE.bazel",
        "//bat:files_to_release"
    ],
    strip_prefix = strip_prefix.from_root(),
)

pkg_tar(
    name="rules_osee",
    srcs = [
        "//release:files_to_publish"
    ],
    out = "rules-osee-%s.tar.gz" % version,
    extension = "tar.gz",
    mode = "0644",
    owner = "0.0",
    strip_prefix = strip_prefix.from_root()
)
# End Feature