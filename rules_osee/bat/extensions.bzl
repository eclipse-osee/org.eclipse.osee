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
load(":repositories.bzl", "DEFAULT_BAT_REPOSITORY", "DEFAULT_BAT_VERSION", "bat_register_toolchains")

def _toolchain_extension(module_context):
    registrations = {}
    for module in module_context.modules:
        for toolchain in module.tags.toolchain:
            if toolchain.name != DEFAULT_BAT_REPOSITORY and not module.is_root:
                fail("Only the root module may provide a name for the bat toolchain.")
            
            if toolchain.name in registrations.keys():
                if toolchain.name == DEFAULT_BAT_REPOSITORY:
                    continue
                if toolchain.bat_version == registrations[toolchain.name]:
                    continue
                fail("Multiple conflicting toolchain definitions declared for the bat tool.")
            else:
                registrations[toolchain.name]=toolchain.bat_version
    
    for name, bat_version in registrations.items():
        bat_register_toolchains(
            name = name,
            bat_version = bat_version,
            register = False
        )

        
bat = module_extension(
    implementation = _toolchain_extension,
    tag_classes = {
        "toolchain": tag_class(attrs = {
            "name":attr.string(
                default = DEFAULT_BAT_REPOSITORY
            ),
            "bat_version": attr.string(
                default = DEFAULT_BAT_VERSION
            )
        })
    }
)