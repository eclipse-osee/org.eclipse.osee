FROM eclipse-temurin:21.0.9_10-jdk-noble

LABEL maintainer="Zac"

ARG IMAGE_DIRECTORY=".github/docker/osee-server"
ARG SERVER_PATH="plugins/org.eclipse.osee.server.p2/target"
ARG SERVER_ZIP="org.eclipse.osee.server.runtime.zip"
ARG http_port="8089"
ARG WEB_URI="http://localhost:${http_port}"
ARG CM_CONFIG_URI="etc/osee.postgresql_docker.json"
ARG APPLICATION_SERVER_DATA="demo/binary_data/osee_server_data"

ARG user="osee"
ARG group="osee"
ARG uid="2000"
ARG gid="2000"
ARG OSEE_HOME="/var/osee"

# Can be overwritten.
ENV DEBIAN_FRONTEND="noninteractive"
ENV WEB_URI=${WEB_URI}
ENV CM_CONFIG_URI=${CM_CONFIG_URI}
ENV APPLICATION_SERVER_DATA=${APPLICATION_SERVER_DATA}

USER root

RUN --mount=type=bind,source=${SERVER_PATH},target=/${SERVER_PATH} \
    groupadd -g ${gid} ${group} \
    && useradd -d "$OSEE_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user} \
    && apt-get -y update \
    && apt-get install -y unzip \
    && rm -rf /var/lib/apt/lists/* \
    && unzip ${SERVER_PATH}/${SERVER_ZIP} -d ${OSEE_HOME} \
    && chown -R ${uid}:${gid} ${OSEE_HOME} \
    && chmod -R 755 ${OSEE_HOME}

EXPOSE ${http_port}

USER ${user}

WORKDIR ${OSEE_HOME}

HEALTHCHECK --interval=10s --start-period=1m --retries=5 CMD curl -I -f --max-time 5 http://localhost:8089/health/http/headers || exit 1

ENTRYPOINT java -server -Xmx3G -Dorg.osgi.service.http.port=8089 -Dlogback.configurationFile=logback-dev.xml -Djava.util.logging.config.file=etc/logging.properties -Dosee.application.server.web.uri=${WEB_URI} -Dorg.eclipse.equinox.http.jetty.context.sessioninactiveinterval=3600 -Dcm.config.uri=${CM_CONFIG_URI} -Dosee.authentication.protocol=loginId -Dosee.application.server.data=${APPLICATION_SERVER_DATA} -jar plugins/org.eclipse.equinox.launcher_1.7.0.v20250519-0528.jar -console -consoleLog -data workspace 2>&1
