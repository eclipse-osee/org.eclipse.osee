FROM postgres:16.2-alpine3.19

ARG IMAGE_DIRECTORY

LABEL maintainer="Zac"

COPY ${IMAGE_DIRECTORY}/files/init.sql /docker-entrypoint-initdb.d/init.sql