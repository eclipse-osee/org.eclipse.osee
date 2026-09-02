FROM postgres:18.6-alpine3.24

ARG IMAGE_DIRECTORY

LABEL maintainer="Zac"

COPY ${IMAGE_DIRECTORY}/files/init.sql /docker-entrypoint-initdb.d/init.sql