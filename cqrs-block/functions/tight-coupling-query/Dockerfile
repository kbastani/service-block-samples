FROM openjdk:8

ENV SPRING_PROFILES_ACTIVE=docker

ADD target/tight-coupling-query-1.0.0-SNAPSHOT.jar app.jar
ADD src/main/resources/META-INF/thin.properties thin.properties

RUN java -jar app.jar --thin.root=/m2 --thin.dryrun --thin.trace=true

ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar app.jar --thin.root=/m2" ]

EXPOSE 2000