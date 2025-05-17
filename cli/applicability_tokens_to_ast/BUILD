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
    name = "applicability_tokens_to_ast",
    srcs = glob(['src/**/*.rs']),
    deps = all_crate_deps(normal = True)+[
        "//cli/applicability:applicability",
        "//cli/applicability_lexer_base:applicability_lexer_base",
        "//cli/applicability_parser_types:applicability_parser_types",
        "//cli/applicability_match:applicability_match",
    ],
    crate_features = [
        "default",
    ],
    proc_macro_deps = all_crate_deps(
        proc_macro = True,
    ),
)

rust_test(
    name="applicability_tokens_to_ast_test",
    crate=":applicability_tokens_to_ast",
    size="small",
    deps = all_crate_deps(normal_dev=True) + [
        "//cli/applicability_lexer_config_markdown:applicability_lexer_config_markdown",
        "//cli/applicability_lexer_multi_stage_lexer:applicability_lexer_multi_stage_lexer",
    ],
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
)

rust_doc(
    name="applicability_tokens_to_ast_doc",
    crate=":applicability_tokens_to_ast"
)

rust_clippy(
    name="applicability_tokens_to_ast_clippy",
    testonly = True,
    deps=[
        ":applicability_tokens_to_ast"
    ]
)