# ==============================================================================
# SentinelGate - Multi-Stage Production Dockerfile
# Stage 1: Build (Maven + JDK 17)
# Stage 2: Minimal Distroless / Temurin JRE 17 Runtime with Least Privilege User
# ==============================================================================

# ----------------- Stage 1: Build -----------------
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Cache dependencies first (Docker layer caching optimization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build executable jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ----------------- Stage 2: Runtime -----------------
FROM eclipse-temurin:17-jre-alpine AS runner

LABEL maintainer="SentinelGate System Architecture Team"
LABEL description="Kritik Altyapı İzleme ve Güvenli Mesajlaşma Backend Sistemi"

WORKDIR /app

# Security: Create non-root user and group
RUN addgroup -S sentinel && adduser -S sentinel -G sentinel

# Copy jar from builder stage
COPY --from=builder /build/target/*.jar /app/app.jar

# Set permissions
RUN chown -R sentinel:sentinel /app

# Expose API port
EXPOSE 8080

# Switch to least-privileged non-root user
USER sentinel:sentinel

# Memory & GC tuned environment variables (Render / Koyeb free tier safe)
ENV JAVA_OPTS="-Xmx350m -Xms128m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

# Container healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q 'UP' || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
