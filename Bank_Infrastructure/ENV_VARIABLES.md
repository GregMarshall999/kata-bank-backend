# Environment Variables Setup Guide

This document describes all the environment variables required to run the Bank Infrastructure application. These variables are referenced in `src/main/resources/application.properties`.

## Quick Setup

Create a `.env` file (or set these as system environment variables) with the following variables:

```bash
# Database Configuration
SPRING_DATASOURCE_INITIALIZATION_MODE=always
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/kata_bank_dev
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_GENERATE_STATISTICS=false

# JWT Configuration
JWT_SECRET=your-secret-key-at-least-64-characters-long-for-security
JWT_ACCESS_TOKEN_EXPIRATION=900000
JWT_REFRESH_TOKEN_EXPIRATION=604800000

# Spring Security Configuration
SPRING_SECURITY_USER_NAME=admin
SPRING_SECURITY_USER_PASSWORD=your_admin_password
```

## Detailed Variable Descriptions

### Database Configuration

#### `SPRING_DATASOURCE_INITIALIZATION_MODE`
- **Description**: Controls whether Spring Boot should initialize the database on startup
- **Type**: String
- **Allowed Values**: 
  - `always` - Always initialize the database
  - `never` - Never initialize
  - `embedded` - Only initialize for embedded databases
- **Example**: `always`
- **Default (Development)**: `always`

#### `SPRING_DATASOURCE_URL`
- **Description**: JDBC connection URL for the database
- **Type**: String
- **Format**: `jdbc:mysql://host:port/database_name`
- **Example**: `jdbc:mysql://localhost:3306/kata_bank_dev`
- **Default (Development)**: `jdbc:mysql://localhost:3306/kata_bank_dev`

#### `SPRING_DATASOURCE_USERNAME`
- **Description**: Database username for authentication
- **Type**: String
- **Example**: `root`
- **Default (Development)**: `root`

#### `SPRING_DATASOURCE_PASSWORD`
- **Description**: Database password for authentication
- **Type**: String
- **Example**: `your_secure_password`
- **Default (Development)**: `` (empty)
- **⚠️ Important**: Use a strong password in production!

#### `SPRING_DATASOURCE_DRIVER_CLASS_NAME`
- **Description**: Fully qualified class name of the JDBC driver to use
- **Type**: String
- **Example**: `com.mysql.cj.jdbc.Driver`
- **Default (Development)**: `com.mysql.cj.jdbc.Driver`
- **Note**: For MySQL 8+, use `com.mysql.cj.jdbc.Driver`. For older MySQL versions, use `com.mysql.jdbc.Driver`.

### JPA/Hibernate Configuration

#### `SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT`
- **Description**: Hibernate SQL dialect for the database. This tells Hibernate how to generate SQL for a specific database.
- **Type**: String
- **Example**: `org.hibernate.dialect.MySQLDialect`
- **Default (Development)**: `org.hibernate.dialect.MySQLDialect`
- **Note**: For MySQL 8+, use `MySQLDialect`. For other databases, use the appropriate dialect (e.g., `PostgreSQLDialect`, `H2Dialect`).

#### `SPRING_JPA_HIBERNATE_DDL_AUTO`
- **Description**: Defines how Hibernate should handle database schema changes
- **Type**: String
- **Allowed Values**:
  - `validate` - Validates the schema, makes no changes
  - `update` - Updates the schema if necessary
  - `create` - Creates the schema, destroying previous data
  - `create-drop` - Creates the schema and drops it when the SessionFactory closes
  - `none` - Does nothing
- **Example**: `update`
- **Default (Development)**: `update`
- **⚠️ Important**: Use `validate` or `none` in production to avoid accidental data loss!

#### `SPRING_JPA_SHOW_SQL`
- **Description**: Enables/disables logging of SQL statements to the console
- **Type**: Boolean
- **Allowed Values**: `true`, `false`
- **Example**: `true`
- **Default (Development)**: `true`
- **Note**: Set to `false` in production for better performance and security.

#### `SPRING_JPA_PROPERTIES_HIBERNATE_GENERATE_STATISTICS`
- **Description**: Enables/disables Hibernate statistics collection (performance metrics)
- **Type**: Boolean
- **Allowed Values**: `true`, `false`
- **Example**: `false`
- **Default (Development)**: `false`
- **Note**: Enable only when needed for performance monitoring as it has a slight performance impact.

### JWT Configuration

#### `JWT_SECRET`
- **Description**: Secret key used to sign and verify JWT tokens. Must be at least 64 characters long for security.
- **Type**: String
- **Example**: `mySecretKey123456789012345678901234567890123456789012345678901234567890`
- **Default (Development)**: `mySecretKey123456789012345678901234567890123456789012345678901234567890`
- **⚠️ Critical**: Use a strong, randomly generated secret in production! Never commit this value to version control.

#### `JWT_ACCESS_TOKEN_EXPIRATION`
- **Description**: Expiration time for JWT access tokens in milliseconds
- **Type**: Long (milliseconds)
- **Example**: `900000` (15 minutes)
- **Default (Development)**: `900000`
- **Common Values**:
  - 15 minutes: `900000`
  - 30 minutes: `1800000`
  - 1 hour: `3600000`

#### `JWT_REFRESH_TOKEN_EXPIRATION`
- **Description**: Expiration time for JWT refresh tokens in milliseconds
- **Type**: Long (milliseconds)
- **Example**: `604800000` (7 days)
- **Default (Development)**: `604800000`
- **Common Values**:
  - 7 days: `604800000`
  - 14 days: `1209600000`
  - 30 days: `2592000000`

### Spring Security Configuration

#### `SPRING_SECURITY_USER_NAME`
- **Description**: Default username for basic authentication (used for development/testing)
- **Type**: String
- **Example**: `admin`
- **Default (Development)**: `admin`
- **Note**: This is typically only used for development. In production, users should be managed through the application's user management system.

#### `SPRING_SECURITY_USER_PASSWORD`
- **Description**: Default password for basic authentication (used for development/testing)
- **Type**: String
- **Example**: `admin123`
- **Default (Development)**: `admin123`
- **⚠️ Important**: Change this in production! Use a strong password.

## Setting Environment Variables

### Windows (PowerShell)
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/kata_bank_dev"
$env:SPRING_DATASOURCE_USERNAME="root"
# ... set other variables
```

### Windows (Command Prompt)
```cmd
set SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/kata_bank_dev
set SPRING_DATASOURCE_USERNAME=root
# ... set other variables
```

### Linux/macOS (Bash)
```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/kata_bank_dev"
export SPRING_DATASOURCE_USERNAME="root"
# ... set other variables
```

### Using a `.env` File (with Spring Boot)
If you're using a tool like `dotenv-java` or `spring-dotenv`, you can create a `.env` file in the root of your project and load it automatically.

## Production Recommendations

1. **Never commit secrets to version control** - Use environment variables or a secrets management system
2. **Use strong, randomly generated secrets** - Especially for `JWT_SECRET`
3. **Set `SPRING_JPA_HIBERNATE_DDL_AUTO` to `validate` or `none`** - Avoid accidental schema changes
4. **Set `SPRING_JPA_SHOW_SQL` to `false`** - Prevents SQL statements from being logged
5. **Use environment-specific configuration** - Consider using Spring profiles (`application-prod.properties`) for production
6. **Rotate secrets regularly** - Especially JWT secrets and database passwords

## Troubleshooting

### Database Connection Issues
- Verify `SPRING_DATASOURCE_URL` is correct and the database is running
- Check `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` are correct
- Ensure the database exists before running the application

### JWT Token Issues
- Ensure `JWT_SECRET` is at least 64 characters long
- Verify token expiration values are set correctly (in milliseconds)

### Hibernate Schema Issues
- If tables aren't being created, check `SPRING_JPA_HIBERNATE_DDL_AUTO`
- Verify the dialect matches your database version
- Check database user has CREATE/ALTER privileges

