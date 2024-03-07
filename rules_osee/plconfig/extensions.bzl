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
load(":repositories.bzl","product_line_config")

def _plconfig_dependencies_impl(ctx):
    for mod in ctx.modules:
        for config in mod.tags.config:
            product_line_config(name=config.name,osee_url=config.osee_url,branch_id=config.branch_id,sha256=config.sha256)

plconfig = module_extension(
    implementation = _plconfig_dependencies_impl,
    tag_classes = {
        "config":tag_class( attrs ={
            "name": attr.string(
                default = "plconfig"
            ),
            "osee_url" : attr.string(
                default=""
            ),
            "branch_id": attr.string(
                default=""
            ),
            "sha256": attr.string(
                default=""
            )
        })
    }
)