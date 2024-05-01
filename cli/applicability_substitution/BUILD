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
load("@rules_rust//rust:defs.bzl", "rust_library", "rust_doc", "rust_clippy", "rust_test")

package(default_visibility = ["//visibility:public"])

rust_library(
    name = "applicability_substitution",
    srcs = glob(['src/**/*.rs']),
    deps = all_crate_deps(normal = True)+[
        "//cli/applicability:applicability",
        "//cli/applicability_parser_types:applicability_parser_types",
    ],
    crate_features = [
        "default",
        "derive",
        "serde"
    ]
)

rust_test(
    name="applicability_substitution_test",
    crate=":applicability_substitution",
    size="small"
)

rust_doc(
    name="applicability_substitution_doc",
    crate=":applicability_substitution"
)

rust_clippy(
    name="applicability_substitution_clippy",
    testonly = True,
    deps=[
        ":applicability_substitution"
    ]
)