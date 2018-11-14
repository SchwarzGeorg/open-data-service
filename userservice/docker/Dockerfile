#REPO: mojo-docker.cs.fau.de/osrg_ods_public/ods

FROM adoptopenjdk/openjdk8-openj9:alpine

ENV ODS_LOG_DIR /var/log/userservice

COPY assets/userservice*-all.jar /userservice.jar
COPY assets/userservice-configuration.yml.template /userservice-configuration.yml
COPY assets/start.sh /start.sh
COPY assets/set-config.sh set-config.sh

CMD /bin/sh start.sh
