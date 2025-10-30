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
load("@rules_rust//rust:defs.bzl", "rust_clippy", "rust_doc", "rust_library")

package(default_visibility = ["//visibility:public"])

rust_library(
    name = "jni_wrapper",
    srcs = glob(["src/**/*.rs"]),
    crate_features = [
        "default",
    ],
    deps = all_crate_deps(normal = True) + [
        "//cli/java_rust_ffi_core",
        "@crates//:jni",
    ],
)

rust_doc(
    name = "jni_wrapper_doc",
    crate = ":jni_wrapper",
)

rust_clippy(
    name = "jni_wrapper_clippy",
    testonly = True,
    deps = [
        ":jni_wrapper",
    ],
)
