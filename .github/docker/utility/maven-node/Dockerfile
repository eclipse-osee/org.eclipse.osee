FROM maven:3.9.0-eclipse-temurin-11

ARG IMAGE_DIRECTORY

LABEL maintainer="Zac"

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get -y update \
    && apt-get -y install expect telnet sshpass xvfb libjsch-java libswt-gtk-4-jni libswt-gtk-4-java libswt-webkit-gtk-4-jni ant ant-optional msmtp \
    # Beginning of Angular setup.
    && wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get -y install ./google-chrome-stable_current_amd64.deb ca-certificates curl gnupg \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    # Set Node.js version
    && NODE_MAJOR=20 \
    && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list \
    && apt-get update \
    && apt-get -y install nodejs \
    && npm install -g @pnpm/exe \
    && rm -rf /var/lib/apt/lists/*

COPY ${IMAGE_DIRECTORY}/files/settings.xml /

CMD ["echo", "Hi there! This image has expect, telnet, sshpass, xvfb, libswt-gtk-4-jni, libswt-gtk-4-java, libswt-webkit-gtk-4-jni, ant, ant-contrib, msmtp, Maven, Chrome, curl, npm, pnpm, node.js, and JDK-11;"]