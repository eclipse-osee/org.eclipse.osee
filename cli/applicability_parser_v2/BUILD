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
    name = "applicability_parser_v2",
    srcs = glob(['src/**/*.rs']),
    deps = all_crate_deps(normal = True)+[
        "//cli/applicability:applicability" ,
        "//cli/applicability_lexer_base:applicability_lexer_base" ,
        "//cli/applicability_document_schema:applicability_document_schema" ,
        "//cli/applicability_lexer_applicability_structure:applicability_lexer_applicability_structure" ,
        "//cli/applicability_lexer_applicability_structure_code_block:applicability_lexer_applicability_structure_code_block" ,
        "//cli/applicability_lexer_applicability_structure_single_line_terminated:applicability_lexer_applicability_structure_single_line_terminated" ,
        "//cli/applicability_lexer_applicability_structure_single_line_non_terminated:applicability_lexer_applicability_structure_single_line_non_terminated" ,
        "//cli/applicability_lexer_applicability_structure_multi_line:applicability_lexer_applicability_structure_multi_line" ,
        "//cli/applicability_lexer_document_structure:applicability_lexer_document_structure" ,
        "//cli/applicability_lexer_chunker:applicability_lexer_chunker" ,
        "//cli/applicability_lexer_multi_stage_lexer:applicability_lexer_multi_stage_lexer" ,
        "//cli/applicability_tokens_to_ast:applicability_tokens_to_ast" ,
    ],
    crate_features = [
        "default",
    ],
    proc_macro_deps = all_crate_deps(
        proc_macro = True,
    ),
)

rust_test(
    name="applicability_parser_v2_test",
    crate=":applicability_parser_v2",
    size="small",
    deps = all_crate_deps(normal_dev=True) + [
        "//cli/applicability_parser_types:applicability_parser_types" ,
        "//cli/applicability_lexer_config_markdown:applicability_lexer_config_markdown",
    ],
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
)

rust_doc(
    name="applicability_parser_v2_doc",
    crate=":applicability_parser_v2"
)

rust_clippy(
    name="applicability_parser_v2_clippy",
    testonly = True,
    deps=[
        ":applicability_parser_v2"
    ]
)