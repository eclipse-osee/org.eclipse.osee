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
load("@rules_rust//rust:defs.bzl", "rust_binary", "rust_doc", "rust_clippy", "rust_test")

package(default_visibility = ["//visibility:public"])

rust_binary(
    name = "pat",
    srcs = glob(['src/**/*.rs']),
    deps = all_crate_deps(normal = True)+[
        "//cli/applicability:applicability",
        "//cli/applicability_parser:applicability_parser",
        "//cli/applicability_parser_config:applicability_parser_config",
        "//cli/applicability_substitution:applicability_substitution",
        "//cli/applicability_sanitization:applicability_sanitization",
        "//cli/applicability_path:applicability_path",
        "//cli/applicability_tokens_to_ast:applicability_tokens_to_ast"
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
    name="pat_test",
    crate=":pat",
    size="small",
    deps = all_crate_deps(normal_dev = True) + [],
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
)

rust_doc(
    name="pat_doc",
    crate=":pat"
)

rust_clippy(
    name="pat_clippy",
    testonly = True,
    deps=[
        ":pat"
    ]
)