# ********************************************************
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
# ********************************************************
FROM ubuntu:22.04

ARG USERNAME=bazel-runner
ARG USER_UID=1000
ARG USER_GID=$USER_UID
WORKDIR /workspace

ADD https://github.com/bazelbuild/bazelisk/releases/download/v1.19.0/bazelisk-linux-amd64 /usr/local/bin/bazel

RUN chmod +x /usr/local/bin/bazel \
  && apt-get update \
  && apt-get dist-upgrade -y \
  && apt-get install -y build-essential ca-certificates


# Create the user
RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
    #
    # Add sudo support. Omit if you don't need to install software after connecting.
    && apt-get update \
    && apt-get install -y sudo \
    && echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME
    
# Install python3 for rules_pkg and aspect_bazel_lib as it is expected for bootstrapping
RUN sudo apt install -y python3

# ********************************************************
# * Anything else you want to do like clean up goes here *
# ********************************************************

# [Optional] Set the default user. Omit if you want to keep the default as root.
USER $USERNAME

ENTRYPOINT [ "/usr/local/bin/bazel" ]