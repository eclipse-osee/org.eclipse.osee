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
load("@rules_rust//rust:defs.bzl", "rust_clippy", "rust_doc", "rust_library")

package(default_visibility = ["//visibility:public"])

rust_library(
    name = "java_rust_ffi_core",
    srcs = glob(["src/**/*.rs"]),
    crate_features = [
        "default",
        "derive",
        "serde",
    ],
    deps = all_crate_deps(normal = True) + [
        "//cli/applicability",
        "//cli/applicability_parser",
        "//cli/applicability_parser_config",
        "//cli/applicability_sanitization",
        "//cli/applicability_substitution",
        "//cli/applicability_tokens_to_ast",
    ],
)

rust_doc(
    name = "java_rust_ffi_core_doc",
    crate = ":java_rust_ffi_core",
)

rust_clippy(
    name = "java_rust_ffi_core_clippy",
    testonly = True,
    deps = [
        ":java_rust_ffi_core",
    ],
)
