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
        "tool_files":"Files required in runfiles to make the bat executable available",
        "bat_features":"Features bat tool is capable of(used for compatibility checking)."
    }
)

def _bat_toolchain_impl(context):
    if (context.attr.target_tool and context.attr.target_tool_path):
        fail("Can only set one of target tool and target tool path.")
    
    tool_files = []

    if context.attr.target_tool:
        tool_files = [context.file.target_tool]
    
    template_variables = platform_common.TemplateVariableInfo({"BAT_PATH":context.file.target_tool.path})
    default = DefaultInfo(
        files = depset(tool_files),
        runfiles = context.runfiles(files = tool_files)
    )

    batinfo = BatInfo(
        target_tool_path = context.attr.target_tool_path,
        tool_files = context.file.target_tool,
        bat_features = context.attr.bat_features
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
            executable = True,
            cfg = "exec", 
            allow_single_file = True
        ),
        "target_tool_path" : attr.string(
        ),
        "bat_features": attr.string_list()
    }
)