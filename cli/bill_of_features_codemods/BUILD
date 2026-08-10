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
    name = "bill_of_features_codemods",
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
        "//cli/bill_of_features",
    ],
)

rust_test(
    name = "bill_of_features_codemods_test",
    size = "small",
    crate = ":bill_of_features_codemods",
    proc_macro_deps = all_crate_deps(
        proc_macro_dev = True,
    ),
    deps = all_crate_deps(normal_dev = True) + [],
)

rust_doc(
    name = "bill_of_features_codemods_doc",
    crate = ":bill_of_features_codemods",
)

rust_clippy(
    name = "bill_of_features_codemods_clippy",
    testonly = True,
    deps = [
        ":bill_of_features_codemods",
    ],
)
