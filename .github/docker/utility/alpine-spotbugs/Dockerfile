FROM 23.0.2_7-jre-alpine-3.21

ARG IMAGE_DIRECTORY
ARG SPOTBUGS_VERSION=4.9.1
ARG SPOTBUGS_HOME=/root/spotbugs

LABEL maintainer="Torin"

ENV DEBIAN_FRONTEND=noninteractive

RUN apk add --no-cache unzip git \
    && wget --no-check-certificate https://github.com/spotbugs/spotbugs/releases/download/${SPOTBUGS_VERSION}/spotbugs-${SPOTBUGS_VERSION}.zip  \
    && unzip -o spotbugs-${SPOTBUGS_VERSION}.zip -d /root/spotbugs \
    && rm spotbugs-${SPOTBUGS_VERSION}.zip  \
    && chmod a+x ${SPOTBUGS_HOME}/spotbugs-${SPOTBUGS_VERSION}/bin/spotbugs

CMD ["echo", "Hi there! This image has expect, telnet, sshpass, xvfb, libswt-gtk-4-jni, libswt-gtk-4-java, libswt-webkit-gtk-4-jni, ant, ant-contrib, msmtp, Maven, Chrome, curl, npm, pnpm, node.js, and JDK-11;"]