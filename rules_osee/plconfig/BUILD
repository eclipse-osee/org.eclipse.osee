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
# Feature[INTERNAL_RELEASE_ONLY]
load("@rules_osee//bat:apply_applicability.bzl","apply_applicability")
apply_applicability(
    name="applic_BUILD",
    srcs=["BUILD"],
    applic_config="@rules_osee//:rules_osee.json"
)
filegroup(
    name = "files_to_release",
    srcs = [
        "applic_BUILD",
    ] + glob(["*.bzl"]),
    visibility = [
        "//visibility:public",
    ]
)
# End Feature