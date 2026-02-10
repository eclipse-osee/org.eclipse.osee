FROM maven:3.9.9-eclipse-temurin-21

ARG IMAGE_DIRECTORY

LABEL maintainer="Zac"

ENV RUSTUP_HOME="/.rustup" \
    CARGO_HOME="/.cargo" \
    PATH="/.cargo/bin:${PATH}" \
    DEBIAN_FRONTEND=noninteractive

RUN apt-get -y update \
    && apt-get -y install expect telnet sshpass xvfb libjsch-java libswt-gtk-4-jni libswt-gtk-4-java libswt-webkit-gtk-4-jni ant ant-optional msmtp libvips libvips-tools libvips-dev \
    # Beginning of Angular setup.
    && wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get -y install ./google-chrome-stable_current_amd64.deb ca-certificates curl gnupg \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    # Set Node.js version
    && NODE_MAJOR=22 \
    && NODE_MINOR=.14.0 \
    && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list \
    && apt-get update \
    && apt-get -y install nodejs=${NODE_MAJOR}${NODE_MINOR}-1nodesource1 \
    && npm install -g @pnpm/exe \
    # Install prerequisites for Rust and rustup
    && apt-get install -y --no-install-recommends build-essential musl-tools \
    # Install Rust using rustup init
    && curl https://sh.rustup.rs -sSf | sh -s -- -y \
    && rustup target add x86_64-unknown-linux-musl --toolchain stable-x86_64-unknown-linux-gnu \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY ${IMAGE_DIRECTORY}/files/settings.xml /

CMD ["echo", "Hi there! This image has expect, telnet, sshpass, xvfb, libswt-gtk-4-jni, libswt-gtk-4-java, libswt-webkit-gtk-4-jni, ant, ant-contrib, msmtp, musl-tools, build-essential, rust, Maven, Chrome, curl, npm, pnpm, node.js, and JDK-21;"]