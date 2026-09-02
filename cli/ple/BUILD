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
load("@rules_rust//rust:defs.bzl", "rust_binary", "rust_clippy", "rust_doc", "rust_test")

package(default_visibility = ["//visibility:public"])

rust_binary(
    name = "ple",
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
        "//cli/bat_lib",
        "//cli/cli_logging",
        "//cli/generate_cli_lib",
        "//cli/migrate_cli_lib",
        "//cli/pat_lib",
        "//cli/validate_cli_lib",
    ],
)

rust_test(
    name = "ple_test",
    size = "small",
    crate = ":ple",
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
    deps = all_crate_deps(normal_dev = True) + [],
)

rust_doc(
    name = "ple_doc",
    crate = ":ple",
)

rust_clippy(
    name = "ple_clippy",
    testonly = True,
    deps = [
        ":ple",
    ],
)
