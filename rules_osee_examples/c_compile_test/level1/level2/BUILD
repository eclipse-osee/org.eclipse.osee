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
load("@rules_osee//bat:apply_applicability.bzl","apply_applicability")

apply_applicability(
    name="processed_subhdrs",
    srcs=glob(["test2.h"]),
    applic_config = "@my_product_line//config_files:resolved_config",
    # applic_config = "//rules_osee_examples/c_compile_test:PRODUCT_A.json",
    visibility = [ "//visibility:public"]
)

exports_files(["test2.h"])