FROM maven:3.8.6-openjdk-11-slim

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src
COPY data ./data

RUN mvn clean compile assembly:single

CMD ["java", "-cp", "target/lucene-1.0-SNAPSHOT-jar-with-dependencies.jar", "com.svb.DocuSearcher"]