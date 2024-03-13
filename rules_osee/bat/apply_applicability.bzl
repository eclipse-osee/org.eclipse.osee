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
load("@aspect_bazel_lib//lib:paths.bzl", "to_output_relative_path","to_repository_relative_path","to_rlocation_path")
load("@aspect_bazel_lib//lib:run_binary.bzl", "run_binary")
_ATTRS = {
    "srcs": attr.label_list(
        allow_files = True,
    ),
    "applic_config": attr.label(allow_single_file = True,),
    "begin_comment_syntax":attr.string_list(doc="""
     This field is optional.
     This field is for setting the begin comment syntax to parse files with.
     The PLE/BAT tool has built in defaults for some popular file extensions.
     """),
    "end_comment_syntax":attr.string_list(doc="""
     This field is optional.
     This field is for setting the end comment syntax to parse files with.
     The PLE/BAT tool has built in defaults for some popular file extensions.
     """),
}
def _create_config_directory(ctx,file):
    path = "%s/%s" % ("/".join(["config",file.basename.removesuffix(".json")]), "marker")
    out=ctx.actions.declare_file(path)
    ctx.actions.run_shell(
        inputs=[file],
        outputs = [out],
        command = """mkdir -p $$(dirname %s) && touch %s""" % (path, path),
        use_default_shell_env = True
    )
    outputs = [out]
    return DefaultInfo(
        files = depset(outputs)
    )
def _apply_applicability_impl(ctx):
    tool_path = ctx.toolchains["@rules_osee//bat:toolchain_type"].batinfo.target_tool_path
    tool_files = ctx.toolchains["@rules_osee//bat:toolchain_type"].batinfo.tool_files
    input_files = []
    if(len(ctx.attr.begin_comment_syntax)>1):
        fail("Length of begin comment syntax is too long")
    if(len(ctx.attr.end_comment_syntax)>1):
        fail("Length of end comment syntax is too long")
    output_dir = _create_config_directory(ctx,ctx.file.applic_config)
    outputs = []
    for src in ctx.attr.srcs:
        for file in src.files.to_list():
            args = ctx.actions.args()
            args.add("-a",ctx.file.applic_config.path)
            output = ctx.actions.declare_file(file.path,sibling=output_dir.files.to_list()[0])
            outputs.append(output)
            args.add("-s",file)
            args.add("-o",output_dir.files.to_list()[0].dirname+"/../../")
            if(len(ctx.attr.begin_comment_syntax)>0):
                args.add("-b",ctx.attr.begin_comment_syntax[0])
            if(len(ctx.attr.end_comment_syntax)>0):
                args.add("-b",ctx.attr.end_comment_syntax[0])
            args.add("--use-direct-output")
            ctx.actions.run(
                inputs = [file,ctx.file.applic_config],
                outputs = [output],
                arguments = [args],
                toolchain = "@rules_osee//bat:toolchain_type",
                progress_message ="Processing %{label}'s applicability. File: %{input}",
                executable = tool_path,
                mnemonic ="BATPreProcess"
            )
    return [
        DefaultInfo(
            files = depset(outputs)
        ),
        OutputGroupInfo(
            bat_files=depset(outputs)
        )
    ]

apply_applicability = rule(
    implementation = _apply_applicability_impl,
    attrs = _ATTRS,
    toolchains = ["@rules_osee//bat:toolchain_type"],
)
