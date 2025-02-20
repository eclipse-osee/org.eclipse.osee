FROM alpine:3.21.3

ARG IMAGE_DIRECTORY
ARG SPOTBUGS_VERSION=4.9.1

LABEL maintainer="Torin"

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get -y update \
    && apt-get -y install unzip git \
    && wget https://github.com/spotbugs/spotbugs/releases/download/${SPOTBUGS_VERSION}/spotbugs-${SPOTBUGS_VERSION}.zip  \
    && unzip spotbugs-${SPOTBUGS_VERSION}.zip -d /root/spotbugs \
    && rm spotbugs-${SPOTBUGS_VERSION}.zip  \
    && chmod a+x /root/spotbugs/bin/spotbugs \ 
    && rm -rf /var/lib/apt/lists/*

CMD ["echo", "Hi there! This image has expect, telnet, sshpass, xvfb, libswt-gtk-4-jni, libswt-gtk-4-java, libswt-webkit-gtk-4-jni, ant, ant-contrib, msmtp, Maven, Chrome, curl, npm, pnpm, node.js, and JDK-11;"]