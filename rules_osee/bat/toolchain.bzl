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
BatInfo = provider(
    fields = {
        "target_tool_path":"Path to the bat executable",
        "tool_files":"Files required in runfiles to make the bat executable available"
    }
)
def _to_manifest_path(ctx, file):
    if file.short_path.startswith("../"):
        return "../../../../../external/" + file.short_path[3:] #todo figure out better workaround
    else:
        return file.short_path

def _bat_toolchain_impl(context):
    if (context.attr.target_tool and context.attr.target_tool_path):
        fail("Can only set one of target tool and target tool path.")
    
    tool_files = []
    target_tool_path = context.attr.target_tool_path

    if context.attr.target_tool:
        tool_files = [context.file.target_tool]
        target_tool_path = _to_manifest_path(context, context.file.target_tool)
    
    template_variables = platform_common.TemplateVariableInfo({"BAT_PATH":target_tool_path})
    default = DefaultInfo(
        files = depset(tool_files),
        runfiles = context.runfiles(files = tool_files)
    )

    batinfo = BatInfo(
        target_tool_path = target_tool_path,
        tool_files = tool_files
    )

    toolchain_info = platform_common.ToolchainInfo(
        batinfo = batinfo,
        template_variables = template_variables,
        default = default
    )
    return [
        default,
        toolchain_info,
        template_variables
    ]

bat_toolchain = rule (
    implementation = _bat_toolchain_impl,
    attrs = {
        "target_tool": attr.label(
            mandatory = False, 
            allow_single_file = True
        ),
        "target_tool_path" : attr.string(
            mandatory = False
        )
    }
)