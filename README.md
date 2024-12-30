# Getting Started

### Comments

1. Application uses reactive stack with R2DBC driver and Spring Webflux
2. Application runs on Docker environment using containers for database and app container
3. You can find OpenAPI documentation for application endpoints on http://localhost:8080/v3/api-docs
4. Also Swagger UI is presented on http://localhost:8080/swagger-ui/index.html
5. Kafka for logging will be added soon possibly

### System requirements

1. Installed maven
2. Installed Docker
3. At least JDK17 must be installed

### Application start

1. Copy application from GitHub
2. Navigate to application folder and run following commands:
3. mvn install
4. docker-compose build
5. docker-compose up

