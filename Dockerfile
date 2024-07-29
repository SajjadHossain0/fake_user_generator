FROM openjdk:17-jdk-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=build /target/fake_user_generator-0.0.1-SNAPSHOT.jar fake_user_generator.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","fake_user_generator.jar"]
