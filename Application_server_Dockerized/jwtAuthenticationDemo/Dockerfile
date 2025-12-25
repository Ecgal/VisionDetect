FROM gradle:8.7-jdk21 AS build
WORKDIR /home/gradle/src

# Only copy Gradle wrapper and build files first to cache dependencies
COPY build.gradle.kts settings.gradle.kts gradlew gradlew.bat gradle.properties /home/gradle/src/
COPY gradle /home/gradle/src/gradle

# Download dependencies separately (cached layer)
RUN gradle build --no-daemon || return 0

# Now copy source (this layer changes whenever code changes)
COPY src /home/gradle/src/src

# Build the fat jar
RUN gradle shadowJar --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

RUN apt-get update && apt-get install -y sqlite3 && rm -rf /var/lib/apt/lists/*

ENV LDAP_URL=ldap://ldap:389 \
    LDAP_BASE_DN=dc=ldap,dc=asd,dc=msd,dc=localhost \
    LDAP_USERS_RDN=ou=users \
    LDAP_BIND_DN=cn=admin,dc=ldap,dc=asd,dc=msd,dc=localhost \
    LDAP_BIND_PASSWORD=shouldntMatter \
    JWT_SECRET=useSomethingBetter \
    JWT_ISSUER=http://0.0.0.0:8080/ \
    JWT_AUDIENCE=ktorAudience \
    JWT_REALM=ktorRealm

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]


