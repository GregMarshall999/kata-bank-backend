# Environment Variables

This document lists all the environment variables that can be configured for the Kata Bank API application.

## Database Configuration

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `DATABASE_URL` | Database connection URL | `jdbc:mysql://localhost:3306/kata_bank?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` | No |
| `DATABASE_USERNAME` | Database username | `root` | No |
| `DATABASE_PASSWORD` | Database password | (empty) | No |

## JPA Configuration

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `JPA_DDL_AUTO` | Hibernate DDL auto mode (create, create-drop, validate, update, none) | `update` | No |
| `JPA_SHOW_SQL` | Show SQL queries in logs | `true` | No |
| `JPA_FORMAT_SQL` | Format SQL output for better readability | `true` | No |

## Logging Configuration

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `LOG_LEVEL_ROOT` | Root logging level (TRACE, DEBUG, INFO, WARN, ERROR) | `INFO` | No |
| `LOG_LEVEL_WEB` | Spring Web logging level | `DEBUG` | No |
| `LOG_LEVEL_HIBERNATE_SQL` | Hibernate SQL logging level | `DEBUG` | No |
| `LOG_LEVEL_HIBERNATE_BINDER` | Hibernate parameter binding logging level | `TRACE` | No |

## Server Configuration

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `SERVER_PORT` | Server port number | `8080` | No |

## Security Configuration

| Variable | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `JWT_SECRET` | JWT secret key for token signing | `ZGVmYXVsdC1qd3Qtc2VjcmV0LWtleS1mb3ItZGV2ZWxvcG1lbnQtb25seS1jaGFuZ2UtaW4tcHJvZHVjdGlvbg==` | **Yes** (change in production) |
| `JWT_EXPIRATION` | JWT token expiration time in milliseconds | `86400000` (24 hours) | No |

## Example Environment Setup

### Development Environment
```bash
# Database
export DATABASE_URL="jdbc:mysql://localhost:3306/kata_bank?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DATABASE_USERNAME="root"
export DATABASE_PASSWORD=""

# JPA
export JPA_DDL_AUTO="update"
export JPA_SHOW_SQL="true"
export JPA_FORMAT_SQL="true"

# Logging
export LOG_LEVEL_ROOT="INFO"
export LOG_LEVEL_WEB="DEBUG"
export LOG_LEVEL_HIBERNATE_SQL="DEBUG"
export LOG_LEVEL_HIBERNATE_BINDER="TRACE"

# Server
export SERVER_PORT="8080"

# Security
export JWT_SECRET="your-development-jwt-secret"
export JWT_EXPIRATION="3600000"
```

### Production Environment
```bash
# Database
export DATABASE_URL="jdbc:mysql://production-db:3306/kata_bank?useSSL=true&serverTimezone=UTC"
export DATABASE_USERNAME="prod_user"
export DATABASE_PASSWORD="secure_production_password"

# JPA
export JPA_DDL_AUTO="validate"
export JPA_SHOW_SQL="false"
export JPA_FORMAT_SQL="false"

# Logging
export LOG_LEVEL_ROOT="WARN"
export LOG_LEVEL_WEB="INFO"
export LOG_LEVEL_HIBERNATE_SQL="WARN"
export LOG_LEVEL_HIBERNATE_BINDER="WARN"

# Server
export SERVER_PORT="8080"

# Security
export JWT_SECRET="your-secure-production-jwt-secret"
export JWT_EXPIRATION="86400000"
```

## Docker Environment Variables

When running with Docker, you can set these variables in your `docker-compose.yml`:

```yaml
environment:
  - DATABASE_URL=jdbc:mysql://db:3306/kata_bank?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
  - DATABASE_USERNAME=root
  - DATABASE_PASSWORD=password
  - JWT_SECRET=your-jwt-secret
  - LOG_LEVEL_ROOT=INFO
```

## Important Notes

1. **JWT Secret**: Always change the default JWT secret in production environments
2. **Database Password**: Never commit database passwords to version control
3. **Logging Levels**: Use appropriate logging levels for each environment (DEBUG for development, WARN/ERROR for production)
4. **SSL**: Enable SSL for database connections in production environments
