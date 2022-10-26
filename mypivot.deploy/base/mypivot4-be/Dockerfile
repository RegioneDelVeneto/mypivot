FROM registry.access.redhat.com/ubi8/openjdk-11 as base
USER root

ARG USERNAME
ARG PASSWORD
ARG BRANCH

RUN microdnf install -y yum && \
    yum install -y git && \
    mkdir -p /mypivot4.pa && \
    mkdir -p /mypivot4

WORKDIR /mypivot4.pa



RUN mv yourpivot4.sources mypivot4

WORKDIR mypivot4/mypivot4-be

ENV GRADLE_OPTS "-Xmx2048m"

RUN git rev-parse --short HEAD

RUN ./gradlew -PSERVER clean bootJar && \
    cp /mypivot4.pa/mypivot4/mypivot4-be/build/libs/*.jar /mypivot4

FROM registry.access.redhat.com/ubi8/openjdk-11 as server

USER root

RUN groupadd -g 9000 mypay4 \
 && chmod -R 777 /home/jboss \
 && adduser \
    --home "/mypivot4" \
    --gid 9000 \
    --no-create-home \
    --uid 9000 \
    "mypay4"

ENV JAVA_HOME="/usr/lib/jvm/java-11" \
    JAVA_VENDOR="openjdk" \
    JAVA_VERSION="11" \
    JBOSS_CONTAINER_OPENJDK_JDK_MODULE="/opt/jboss/container/openjdk/jdk" 

RUN mkdir -p /mypivot4 && \
    microdnf install -y yum && \
    yum install -y nano && \
    yum install -y curl && \
    microdnf install -y nmap 

RUN chown -R 9000:9000 /mypivot4 && \
    chmod -R 777 /mypivot4 

COPY --from=base --chown=mypay4:mypay4 /mypivot4 /mypivot4

#USER mypay4

COPY --chown=mypay4:mypay4 start.sh /mypivot4/start.sh
RUN chmod +x /mypivot4/start.sh

##COPY --chown=mypay4:mypay4 mypivot4-be-*.jar /mypivot4/

WORKDIR /mypivot4

#CMD [ "/mypivot4/start.sh" ]
CMD exec /bin/sh -c "trap : TERM INT; sleep 9999999999d & wait"

EXPOSE 8081

