# CI/CD Setup for Kata Bank API

This document describes the CI/CD pipeline setup for the Kata Bank API project using GitLab CI/CD.

## Overview

The CI/CD pipeline includes the following stages:
1. **Validate** - Code quality checks and dependency validation
2. **Build** - Compilation and packaging
3. **Test** - Unit and integration tests
4. **Security** - Security scanning and code analysis
5. **Deploy** - Deployment to staging and production environments

## Pipeline Stages

### 1. Validate Stage
- Maven project structure validation
- Dependency vulnerability checks
- Code style validation (if configured)

### 2. Build Stage
- Compiles the application
- Runs unit tests
- Creates JAR package
- Builds Docker image (for main branch)

### 3. Test Stage
- Integration tests with MySQL database
- Test coverage reporting

### 4. Security Stage
- OWASP dependency vulnerability scanning
- SonarQube code quality analysis

### 5. Deploy Stage
- Manual deployment to staging environment
- Manual deployment to production environment

## Prerequisites

### GitLab Variables
Set the following variables in your GitLab project settings (Settings > CI/CD > Variables):

#### Required Variables:
- `SONAR_HOST_URL` - SonarQube server URL
- `SONAR_TOKEN` - SonarQube authentication token

#### Optional Variables:
- `DATABASE_URL` - Production database URL
- `DATABASE_USERNAME` - Production database username
- `DATABASE_PASSWORD` - Production database password
- `JWT_SECRET` - Production JWT secret

## Local Development

### Using Docker Compose

1. **Start the application with all services:**
   ```bash
   docker-compose up -d
   ```

2. **View logs:**
   ```bash
   docker-compose logs -f kata-bank-api
   ```

3. **Stop all services:**
   ```bash
   docker-compose down
   ```

4. **Rebuild and start:**
   ```bash
   docker-compose up --build -d
   ```

### Manual Docker Build

1. **Build the Docker image:**
   ```bash
   docker build -t kata-bank-api .
   ```

2. **Run the container:**
   ```bash
   docker run -p 8080:8080 \
     -e DATABASE_URL=jdbc:mysql://host.docker.internal:3306/kata_bank \
     -e DATABASE_USERNAME=root \
     -e DATABASE_PASSWORD=password \
     kata-bank-api
   ```

## Environment Configuration

### Development Environment
- Profile: `dev`
- Database: Local MySQL
- Logging: DEBUG level
- Swagger: Enabled

### Docker Environment
- Profile: `docker`
- Database: MySQL container
- Logging: INFO level
- Swagger: Enabled

### Production Environment
- Profile: `prod`
- Database: Production MySQL
- Logging: WARN level
- Swagger: Disabled
- Security: Enhanced

## Kubernetes Deployment

### Prerequisites
- Kubernetes cluster
- kubectl configured
- Helm (optional)

### Deployment Steps

1. **Create namespace:**
   ```bash
   kubectl create namespace kata-bank
   ```

2. **Create secrets:**
   ```bash
   kubectl apply -f k8s/secrets.yaml -n kata-bank
   ```

3. **Deploy application:**
   ```bash
   kubectl apply -f k8s/deployment.yaml -n kata-bank
   kubectl apply -f k8s/service.yaml -n kata-bank
   ```

4. **Check deployment:**
   ```bash
   kubectl get pods -n kata-bank
   kubectl get services -n kata-bank
   ```

## Security Considerations

### Docker Security
- Non-root user in container
- Minimal base image (JRE only)
- Health checks implemented
- Resource limits defined

### Kubernetes Security
- Secrets for sensitive data
- Resource limits and requests
- Liveness and readiness probes
- Network policies (recommended)

### Application Security
- JWT secrets in environment variables
- Database credentials in secrets
- HTTPS enforcement
- Input validation

## Monitoring and Logging

### Health Checks
- Application health endpoint: `/actuator/health`
- Database connectivity check
- Disk space monitoring

### Metrics
- Prometheus metrics enabled
- Application metrics exposed
- Custom business metrics (can be added)

### Logging
- Structured logging
- Log rotation
- Different log levels per environment

## Troubleshooting

### Common Issues

1. **Build fails with dependency issues:**
   ```bash
   mvn clean install -U
   ```

2. **Docker build fails:**
   - Check Docker daemon is running
   - Ensure sufficient disk space
   - Verify Dockerfile syntax

3. **Database connection issues:**
   - Verify database is running
   - Check connection string
   - Ensure network connectivity

4. **Kubernetes deployment issues:**
   ```bash
   kubectl describe pod <pod-name> -n kata-bank
   kubectl logs <pod-name> -n kata-bank
   ```

### Debug Commands

1. **Check pipeline status:**
   - GitLab CI/CD > Pipelines

2. **View build artifacts:**
   - Download JAR files from pipeline artifacts

3. **Test locally:**
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=dev
   ```

## Best Practices

### Code Quality
- Write unit tests for all new features
- Maintain test coverage above 80%
- Follow coding standards
- Use meaningful commit messages

### Security
- Never commit secrets to repository
- Use environment variables for configuration
- Regularly update dependencies
- Scan for vulnerabilities

### Performance
- Monitor resource usage
- Optimize database queries
- Use connection pooling
- Implement caching where appropriate

## Support

For issues related to:
- **CI/CD Pipeline**: Check GitLab CI/CD documentation
- **Docker**: Refer to Docker documentation
- **Kubernetes**: Check Kubernetes documentation
- **Application**: Review Spring Boot documentation

## Contributing

When contributing to this project:
1. Create a feature branch
2. Write tests for new functionality
3. Ensure all tests pass
4. Create a merge request
5. Wait for CI/CD pipeline to complete
6. Request code review
