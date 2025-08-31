# Bank API - Test Documentation

## Overview

This document serves as a guide for the Bank API.
The test suite is designed to ensure the reliability, security, and functionality of the banking application through multiple testing layers.

## Test Structure

The test suite follows a layered testing approach with the following structure:

```
src/test/java/com/exalt_company/kata_bank_api/
├── KataBankApiApplicationTests.java          # Application context tests
├── entity/                                   # Entity layer tests
│   ├── BankUserTest.java                     # BankUser entity tests
│   ├── FundTest.java                         # Fund entity tests
│   └── user_fields/                          # Embedded field tests
│       ├── IdentityTest.java                 # Identity embedded class tests
│       └── CredentialsTest.java              # Credentials embedded class tests
├── service/                                  # Service layer tests
│   └── AuthServiceTest.java                  # Authentication service tests
├── repository/                               # Repository layer tests
│   └── BankUserRepositoryTest.java           # BankUser repository tests
├── dto/auth/                                 # DTO tests
│   ├── AuthenticationRequestTest.java        # Authentication request DTO tests
│   └── AuthenticationResponseTest.java       # Authentication response DTO tests
├── enums/                                    # Enum tests
│   └── BankRoleTest.java                     # BankRole enum tests
├── exception/                                # Exception handling tests
│   ├── AuthExceptionTest.java                # Authentication exception tests
│   ├── BaseExceptionTest.java                # Base exception tests
│   ├── FundExceptionTest.java                # Fund exception tests
│   ├── ErrorResponseTest.java                # Error response DTO tests
│   └── GlobalExceptionHandlerTest.java       # Global exception handler tests
└── integration/                              # Integration tests
    ├── AuthIntegrationTest.java              # End-to-end authentication tests
    └── FundIntegrationTest.java              # End-to-end fund management tests
```

## Test Configuration

### Test Properties

The test suite uses a dedicated test configuration file: `src/test/resources/application-test.properties`

```properties
# H2 In-Memory Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console (for debugging)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT Configuration
security.jwt.secret=dGVzdFNlY3JldEtleUZvclRlc3RpbmdQdXJwb3Nlc09ubHlEb05vdFVzZUluUHJvZHVjdGlvbg==
security.jwt.expiration=86400000

# Logging Configuration
logging.level.com.exalt_company.kata_bank_api=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Dependencies

The test suite requires the following dependencies (already included in pom.xml):

```xml
<!-- Spring Boot Test Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Security Test -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- H2 Database for Testing -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## Running Tests

### Running All Tests

```bash
mvn test
```

### Running Specific Test Categories

```bash
# Run only unit tests
mvn test -Dtest="*Test" -DfailIfNoTests=false

# Run only integration tests
mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false

# Run only entity tests
mvn test -Dtest="*EntityTest" -DfailIfNoTests=false

# Run only service tests
mvn test -Dtest="*ServiceTest" -DfailIfNoTests=false

# Run only exception tests
mvn test -Dtest="*ExceptionTest" -DfailIfNoTests=false
```

### Running Individual Test Classes

```bash
# Run specific test class
mvn test -Dtest=BankUserTest

# Run specific test method
mvn test -Dtest=BankUserTest#testBankUserCreation
```

## Test Coverage

The test suite provides comprehensive coverage for:

- ✅ **Entity Layer**: 100% coverage of entity classes and embedded fields
- ✅ **Service Layer**: Business logic testing with mocked dependencies
- ✅ **Repository Layer**: Database operation testing with H2
- ✅ **DTO Layer**: Data transfer object validation
- ✅ **Enum Layer**: Enum value and behavior testing
- ✅ **Exception Layer**: Exception handling and error response testing
- ✅ **Integration Layer**: End-to-end flow testing for authentication and fund management
- ✅ **Application Context**: Spring context loading verification

## Security Testing

The test suite includes security-focused tests:

- JWT token generation and validation
- Password encoding and verification
- Role-based access control
- Authentication failure scenarios
- User registration security
- Fund operation authorization

## Performance Considerations

- Tests use in-memory H2 database for fast execution
- Integration tests use @Transactional for automatic rollback
- Test data is minimal and focused
- MockMvc tests run without full application context where appropriate

## Troubleshooting

### Common Issues

1. **Test Database Connection Issues**
   - Ensure H2 dependency is included
   - Check application-test.properties configuration
   - Verify @ActiveProfiles("test") annotation

2. **Integration Test Failures**
   - Check @SpringBootTest configuration
   - Ensure @Transactional annotation is present
   - Verify test data cleanup

3. **JWT Token Issues**
   - Check JWT secret configuration in test properties
   - Verify token expiration settings
   - Ensure proper token format in test requests

### Debugging Tests

1. **Enable Debug Logging**
   ```properties
   logging.level.com.exalt_company.kata_bank_api=DEBUG
   ```

2. **Use H2 Console**
   - Access at http://localhost:8080/h2-console during tests
   - JDBC URL: jdbc:h2:mem:testdb

3. **Run Tests in IDE**
   - Use IDE test runners for better debugging
   - Set breakpoints in test methods

## Future Enhancements

Potential improvements for the test suite:

1. **Controller Tests**: Add dedicated controller layer tests with MockMvc
2. **Performance Tests**: Add load testing for critical endpoints
3. **Contract Tests**: Implement consumer-driven contract testing
4. **Mutation Testing**: Add mutation testing for better test quality
5. **API Documentation Tests**: Test OpenAPI/Swagger documentation
6. **Database Migration Tests**: Test schema evolution scenarios