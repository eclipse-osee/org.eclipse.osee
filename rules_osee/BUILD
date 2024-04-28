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
package(
    default_visibility = ["//visibility:__pkg__"],
)
# Feature[INTERNAL_RELEASE_ONLY]
load("@rules_osee//bat:apply_applicability.bzl","apply_applicability")
apply_applicability(
    name="applic_BUILD",
    srcs=["BUILD"],
    applic_config="@rules_osee//:rules_osee.json",
    visibility=["//visibility:public"]
)
apply_applicability(
    name="applic_MODULE",
    srcs=["MODULE.bazel"],
    applic_config="@rules_osee//:rules_osee.json",
    visibility=["//visibility:public"]
)
# End Feature


exports_files([ "MODULE.bazel", "WORKSPACE"
# Feature[INTERNAL_RELEASE_ONLY]
,"rules_osee.json"
# End Feature
])