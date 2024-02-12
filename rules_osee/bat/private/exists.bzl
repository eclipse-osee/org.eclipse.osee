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
load("@aspect_bazel_lib//lib:repo_utils.bzl", "repo_utils")
load(":bat_versions.bzl", "BAT_VERSIONS")

def bat_exists_for_os(bat_version, os_name, bat_repositories):
    if not bat_repositories:
        bat_repositories = BAT_VERSIONS

    return "-".join([bat_version, os_name]) in bat_repositories.keys()

def assert_bat_exists_for_host(rctx):
    bat_version = rctx.attr.bat_version
    bat_repositories = rctx.attr.bat_repositories

    if not bat_exists_for_os(bat_version, repo_utils.platform(rctx),bat_repositories):
        fail("No bat is available for {} at version {}".format(repo_utils.platform(rctx), bat_version) +
             "\n    Consider upgrading by setting bat_version in a call to bat_repositories in WORKSPACE." +
             "\n    Note that Linux and Windows x64 are the only currently supported versions")