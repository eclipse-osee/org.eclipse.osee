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
    name = "pat_config",
    srcs = glob(["src/**/*.rs"]),
    crate_features = [
        "default",
    ],
    proc_macro_deps = all_crate_deps(
        proc_macro = True,
    ),
    deps = all_crate_deps(normal = True) + [
        "//cli/applicability_parser_config",
        "//cli/feature_definition",
    ],
)

rust_test(
    name = "pat_config_test",
    size = "small",
    crate = ":pat_config",
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
    deps = all_crate_deps(normal_dev = True) + [
        "//cli/applicability",
    ],
)

rust_doc(
    name = "pat_config_doc",
    crate = ":pat_config",
)

rust_clippy(
    name = "pat_config_clippy",
    testonly = True,
    deps = [
        ":pat_config",
    ],
)
