FROM maven:3.6.1-jdk-8
ENV PROJECT_DIR=/opt/project

RUN mkdir -p $PROJECT_DIR

ADD pom.xml $PROJECT_DIR
ADD src/ $PROJECT_DIR/src
WORKDIR $PROJECT_DIR

RUN mvn dependency:resolve -B
RUN mvn install -B

FROM openjdk:8-jdk
ENV PROJECT_DIR=/opt/project
ENV ROOT_STORE_DIR=/var/files/store
RUN mkdir -p $PROJECT_DIR
RUN mkdir -p $ROOT_STORE_DIR
WORKDIR $PROJECT_DIR
COPY --from=0 $PROJECT_DIR/target/file-service* $PROJECT_DIR/
EXPOSE 8080

CMD ["java", "-jar", "-Dspring.profiles.active=docker", "/opt/project/file-service.jar"]