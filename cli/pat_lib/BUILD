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
    name = "pat_lib",
    srcs = glob(["src/**/*.rs"]),
    crate_features = [
        "default",
        "derive",
        "serde",
    ],
    proc_macro_deps = all_crate_deps(
        proc_macro = True,
    ),
    deps = all_crate_deps(normal = True) + [
        "//cli/applicability",
        "//cli/applicability_parser_config",
        "//cli/applicability_parser_errors",
        "//cli/applicability_project",
        "//cli/applicability_sanitization",
        "//cli/applicability_tokens_to_ast",
        "//cli/bill_of_features",
        "//cli/doc_definitions",
        "//cli/feature_definition",
        "//cli/pat_config",
        "//cli/validate_bof",
    ],
)

rust_test(
    name = "pat_lib_test",
    size = "small",
    crate = ":pat_lib",
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
    deps = all_crate_deps(normal_dev = True) + [],
)

rust_doc(
    name = "pat_lib_doc",
    crate = ":pat_lib",
)

rust_clippy(
    name = "pat_lib_clippy",
    testonly = True,
    deps = [
        ":pat_lib",
    ],
)
