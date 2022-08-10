FROM openjdk:8-jdk-alpine
ENV JAVA_OPTS '-XX:+UseContainerSupport -XX:MinRAMPercentage=50.0 -XX:MaxRAMPercentage=90.0 -XshowSettings:vm'
RUN apk --update add fontconfig ttf-dejavu
ARG JAR_FILE=target/image-service-*.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar
ENTRYPOINT java ${JAVA_OPTS}\
    -Djava.security.egd=file:/dev/./urandom\
    -Daws-accesskey="${ACCESS_KEY}"\
    -Daws-secretkey="${SECRET_KEY}"\
    -Daws-region="${REGION}"\
    -Daws-bucket-name="${BUCKET_NAME}"\
    -Dsource-root-url="${SOURCE_ROOT}"\
    -jar app.jar
EXPOSE 8080
