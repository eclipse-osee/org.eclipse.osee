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
load("@rules_rust//rust:defs.bzl", "rust_clippy", "rust_doc", "rust_library", "rust_test")

package(default_visibility = ["//visibility:public"])

rust_library(
    name = "applicability_lexer_config_rust",
    srcs = glob(["src/**/*.rs"]),
    crate_features = [
        "default",
    ],
    proc_macro_deps = all_crate_deps(
        proc_macro = True,
    ),
    deps = all_crate_deps(normal = True) + [
        "//cli/applicability_lexer_base",
    ],
)

rust_test(
    name = "applicability_lexer_config_rust_test",
    size = "small",
    crate = ":applicability_lexer_config_rust",
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
    deps = all_crate_deps(normal_dev = True) + [],
)

rust_doc(
    name = "applicability_lexer_config_rust_doc",
    crate = ":applicability_lexer_config_rust",
)

rust_clippy(
    name = "applicability_lexer_config_rust_clippy",
    testonly = True,
    deps = [
        ":applicability_lexer_config_rust",
    ],
)
