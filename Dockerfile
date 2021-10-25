FROM openjdk:13-jdk-alpine as build
RUN apk add --update ca-certificates && rm -rf /var/cache/apk/* && \
 find /usr/share/ca-certificates/mozilla/ -name "*.crt" -exec keytool -import -trustcacerts \
 -keystore -cacerts -storepass changeit -noprompt \
 -file {} -alias {} \; && \
 keytool -list -keystore -cacerts --storepass changeit
ENV MAVEN_VERSION 3.5.4
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH ${MAVEN_HOME}/bin:${PATH}
ENV MAVEN_FOLDER apache-maven-${MAVEN_VERSION}
ENV MAVEN_TAR_FILE ${MAVEN_FOLDER}-bin.tar.gz
RUN wget http://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/${MAVEN_TAR_FILE} && \
 tar -zxvf ${MAVEN_TAR_FILE} && \
 rm ${MAVEN_TAR_FILE} && \
 mv ${MAVEN_FOLDER} /usr/lib/mvn
WORKDIR /workspace/app
COPY pom.xml .
COPY src src
RUN mvn install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
FROM openjdk:13-jdk-alpine
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","example.App"]
