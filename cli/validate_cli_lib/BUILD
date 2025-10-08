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
    name = "validate_cli_lib",
    srcs = glob(['src/**/*.rs']),
    deps = all_crate_deps(normal = True)+[
        "//cli/validate_bof:validate_bof",
        "//cli/applicability_parser_config:applicability_parser_config",
        "//cli/pat_config:pat_config",
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
    name="validate_cli_lib_test",
    size="small",
    crate=":validate_cli_lib",
    deps = all_crate_deps(normal_dev = True) + [],
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
)

rust_doc(
    name="validate_cli_lib_doc",
    crate=":validate_cli_lib"
)

rust_clippy(
    name="validate_cli_lib_clippy",
    testonly = True,
    deps=[
        ":validate_cli_lib"
    ]
)