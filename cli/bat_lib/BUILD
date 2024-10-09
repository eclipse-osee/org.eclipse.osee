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
    name = "bat_lib",
    srcs = glob(['src/**/*.rs']),
    deps = all_crate_deps(normal = True)+[
        "//cli/applicability:applicability",
        "//cli/applicability_parser:applicability_parser",
        "//cli/applicability_parser_config:applicability_parser_config",
        "//cli/applicability_substitution:applicability_substitution",
        "//cli/applicability_sanitization:applicability_sanitization",
        "//cli/applicability_tokens_to_ast:applicability_tokens_to_ast",
        "//cli/doc_definitions:doc_definitions"
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
    name="bat_lib_test",
    crate=":bat_lib",
    size="small",
    deps = all_crate_deps(normal_dev=True) + [],
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
)

rust_doc(
    name="bat_lib_doc",
    crate=":bat_lib"
)

rust_clippy(
    name="bat_lib_clippy",
    testonly = True,
    deps=[
        ":bat_lib"
    ]
)