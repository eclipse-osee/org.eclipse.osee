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
load("@crates//:defs.bzl", "all_crate_deps")
load("@rules_rust//rust:defs.bzl", "rust_clippy", "rust_doc", "rust_library", "rust_test")

package(default_visibility = ["//visibility:public"])

rust_library(
    name = "applicability_match",
    srcs = glob(["src/**/*.rs"]),
    crate_features = [
        "default",
        "derive",
        "serde",
    ],
    deps = all_crate_deps(normal = True) + [
        "//cli/applicability",
        "//cli/applicability_parser_types",
        "//cli/feature_definition",
    ],
)

rust_test(
    name = "applicability_match_test",
    size = "small",
    crate = ":applicability_match",
)

rust_doc(
    name = "applicability_match_doc",
    crate = ":applicability_match",
)

rust_clippy(
    name = "applicability_match_clippy",
    testonly = True,
    deps = [
        ":applicability_match",
    ],
)
