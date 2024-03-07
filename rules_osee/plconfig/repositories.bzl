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
_ATTRS = {
    "osee_url" : attr.string(
        default=""
    ),
    "branch_id": attr.string(
        default=""
    ),
    "sha256": attr.string(
        default=""
    )
}

# note generated repositories default to @plconfig
def _product_line_config_impl(ctx):
    user = ctx.getenv("USERNAME","") if not ctx.getenv("USERNAME","")=="" else ctx.getenv("USER","3333") 
    ctx.report_progress("Fetching OSEE Product Line Configuration from "+ctx.attr.osee_url+"/orcs/branch/"+ctx.attr.branch_id+"/applic/bazel"+
     " as user "+user)
    # uncomment this line below for debugging as Joe Smith
    # user ="3333"
    ctx.download_and_extract(
        url=ctx.attr.osee_url+"/orcs/branch/"+ctx.attr.branch_id+"/applic/bazel",
        sha256=ctx.attr.sha256,
        headers={"Authorization":"Basic "+ user},
        type="zip"
    )
    ctx.report_progress("OSEE Product Line Configuration downloaded. Please run your build again.")

product_line_config= repository_rule(
    implementation=_product_line_config_impl,
    attrs=_ATTRS,
)