#
# Copyright (c) 2025 Boeing
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
    name = "validate_bof",
    srcs = glob(['src/**/*.rs']),
    deps = all_crate_deps(normal = True)+[
        "//cli/applicability:applicability",
        "//cli/applicability_parser_config:applicability_parser_config",
        "//cli/feature_definition:feature_definition",
    ],
    crate_features = [
        "default",
        "derive",
        "serde"
    ],
    proc_macro_deps = all_crate_deps(
        proc_macro = True,
    ),
)

rust_test(
    name="validate_bof_test",
    size="small",
    crate=":validate_bof",
    deps = all_crate_deps(normal_dev = True) + [],
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
)

rust_doc(
    name="validate_bof_doc",
    crate=":validate_bof"
)

rust_clippy(
    name="validate_bof_clippy",
    testonly = True,
    deps=[
        ":validate_bof"
    ]
)