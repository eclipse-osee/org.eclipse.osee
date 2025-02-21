ARG BASE_REGISTRY=registry1.dso.mil
ARG BASE_TAG=1.11

FROM registry.access.redhat.com/ubi8/openjdk-11:1.13-1.1653918221

ENV JAVA_HOME /usr/lib/jvm/java

ARG user=osee
ARG group=osee
ARG uid=1000
ARG gid=1000
ARG OSEE_HOME=/var/osee
ARG http_port=8089
ARG ZIP=org.eclipse.osee.server.runtime.zip

USER root

RUN groupadd -g ${gid} ${group} && \
    useradd -d "$OSEE_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user} && \
    chown ${uid}:${gid} ${OSEE_HOME} && \
    microdnf update -y && \
    microdnf install -y unzip --setopt=tsflags=nodocs && \
    microdnf clean all && \
    rm -rf /var/cache/dnf && \
    curl -O https://ci.eclipse.org/osee/job/osee_nightly/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.server.p2/target/org.eclipse.osee.server.runtime.zip && \
    unzip ${ZIP} -d ${OSEE_HOME} && \
    rm -rf ${ZIP}

RUN chown -R 1000:1000 ${OSEE_HOME} && \
    chmod 777 ${OSEE_HOME} && \
    chmod -R 777 .

EXPOSE ${http_port}

USER ${user}

WORKDIR ${OSEE_HOME}

HEALTHCHECK --interval=10s --timeout=5s --start-period=1m --retries=5 CMD curl -I -f --max-time 5 http://localhost:8089 || exit 1

# Comment when debugging with entrypoint below
ENTRYPOINT java -server  -Xmx3G -Dorg.osgi.service.http.port=8089 -Dlogback.configurationFile=logback-dev.xml -Dorg.eclipse.equinox.http.jetty.context.sessioninactiveinterval=3600 -Dcm.config.uri=etc/osee.postgresql.json -Dosee.authentication.protocol=loginId -Dosee.application.server.data="demo/binary_data" -jar plugins/org.eclipse.equinox.launcher_1.6.900.v20240613-2009.jar -console -consoleLog 2>&1

