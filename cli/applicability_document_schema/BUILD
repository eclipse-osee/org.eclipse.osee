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
    name = "applicability_document_schema",
    srcs = glob(['src/**/*.rs']),
    deps = all_crate_deps(normal = True) + [
        "//cli/applicability_lexer_base:applicability_lexer_base",
        "//cli/applicability_lexer_document_structure:applicability_lexer_document_structure",
        "//cli/applicability_lexer_config_markdown:applicability_lexer_config_markdown",
        "//cli/applicability_lexer_config_cpp_like:applicability_lexer_config_cpp_like",
        "//cli/applicability_lexer_config_build_file:applicability_lexer_config_build_file",
        "//cli/applicability_lexer_config_rust:applicability_lexer_config_rust",
        "//cli/applicability_lexer_config_latex:applicability_lexer_config_latex",
        "//cli/applicability_lexer_config_plantuml:applicability_lexer_config_plantuml",
        "//cli/applicability_lexer_config_custom:applicability_lexer_config_custom",
    ],
    crate_features = [
        "default"
    ],
    proc_macro_deps = all_crate_deps(
        proc_macro = True,
    ),
)

rust_test(
    name="applicability_document_schema_test",
    crate=":applicability_document_schema",
    size="small",
    deps = all_crate_deps(normal_dev=True) + [],
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
)

rust_doc(
    name="applicability_document_schema_doc",
    crate=":applicability_document_schema"
)

rust_clippy(
    name="applicability_document_schema_clippy",
    testonly = True,
    deps=[
        ":applicability_document_schema"
    ]
)