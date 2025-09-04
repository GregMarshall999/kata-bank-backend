# Bank API - Test Documentation

## Overview

This document serves as a guide for the Bank API.
The test suite is designed to ensure the reliability, security, and functionality of the banking application through multiple testing layers.

## Test Structure

The test suite follows a layered testing approach with the following structure:

```
src/test/java/com/exalt_company/kata_bank_api/
├── entity/                                   # Entity layer tests
│   ├── AccountAuditTest.java                 # AccountAudit entity tests
│   ├── BankUserTest.java                     # BankUser entity tests
│   ├── FundTest.java                         # Fund entity tests
│   ├── FundOverdrawTest.java                 # Fund overdraw functionality tests
│   ├── SavingTest.java                       # Saving entity tests
│   └── user_fields/                          # Embedded field tests
│       ├── IdentityTest.java                 # Identity embedded class tests
│       └── CredentialsTest.java              # Credentials embedded class tests
├── service/                                  # Service layer tests
│   ├── AuditServiceTest.java                 # Audit service tests
│   ├── AuthServiceTest.java                  # Authentication service tests
│   ├── FundServiceTest.java                  # Fund service tests (including overdraw)
│   └── SavingServiceTest.java                # Saving service tests
├── repository/                               # Repository layer tests
│   ├── AccountAuditRepositoryTest.java       # AccountAudit repository tests
│   └── BankUserRepositoryTest.java           # BankUser repository tests
├── controller/                               # Controller layer tests
│   └── AccountAuditControllerTest.java       # AccountAudit controller tests
├── dto/                                      # DTO tests
│   ├── auth/                                 # Authentication DTOs
│   │   ├── AuthenticationRequestTest.java    # Authentication request DTO tests
│   │   └── AuthenticationResponseTest.java   # Authentication response DTO tests
│   ├── fund/                                 # Fund DTOs
│   │   └── OverdrawDtoTest.java              # Overdraw DTO tests
│   └── statement/                            # Statement DTOs
│       ├── AccountStatementDtoTest.java      # Account statement DTO tests
│       └── OperationDtoTest.java             # Operation DTO tests
├── enums/                                    # Enum tests
│   ├── AccountTypeTest.java                  # AccountType enum tests
│   ├── AuditOperationTest.java               # AuditOperation enum tests
│   └── BankRoleTest.java                     # BankRole enum tests
├── exception/                                # Exception handling tests
│   ├── AccountAuditExceptionTest.java        # AccountAudit exception tests
│   ├── AuthExceptionTest.java                # Authentication exception tests
│   ├── BaseExceptionTest.java                # Base exception tests
│   ├── FundExceptionTest.java                # Fund exception tests
│   ├── SavingExceptionTest.java              # Saving exception tests
│   ├── ErrorResponseTest.java                # Error response DTO tests
│   └── GlobalExceptionHandlerTest.java       # Global exception handler tests
└── integration/                              # Integration tests
    ├── AccountAuditIntegrationTest.java      # End-to-end audit tests
    ├── AuthIntegrationTest.java              # End-to-end authentication tests
    ├── FundIntegrationTest.java              # End-to-end fund management tests
    ├── OverdrawIntegrationTest.java          # End-to-end overdraw functionality tests
    └── SavingIntegrationTest.java            # End-to-end saving operations tests
```

## Test Configuration

### Test Properties

The test suite uses a dedicated test configuration file: `src/test/resources/application-test.properties`

```properties
# H2 In-Memory Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true

# H2 Console (for debugging)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT Configuration
security.jwt.secret=dGVzdFNlY3JldEtleUZvclRlc3RpbmdQdXJwb3Nlc09ubHlEb05vdFVzZUluUHJvZHVjdGlvbg==
security.jwt.expiration=86400000

# Logging Configuration
logging.level.com.exalt_company.kata_bank_api=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
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
mvn test -Dtest="*Test" -DfailIfNoTests=false

# Run only service tests
mvn test -Dtest="*ServiceTest" -DfailIfNoTests=false

# Run only exception tests
mvn test -Dtest="*ExceptionTest" -DfailIfNoTests=false

# Run only overdraw-related tests
mvn test -Dtest="*Overdraw*" -DfailIfNoTests=false

# Run fund-related tests (including overdraw)
mvn test -Dtest="*Fund*" -DfailIfNoTests=false

# Run saving-related tests
mvn test -Dtest="*Saving*" -DfailIfNoTests=false

# Run audit-related tests
mvn test -Dtest="*Audit*" -DfailIfNoTests=false

# Run authentication-related tests
mvn test -Dtest="*Auth*" -DfailIfNoTests=false

# Run statement-related tests
mvn test -Dtest="*Statement*" -DfailIfNoTests=false
```

### Running Individual Test Classes

```bash
# Run specific test class
mvn test -Dtest=BankUserTest

# Run specific test method
mvn test -Dtest=BankUserTest#testBankUserCreation

# Run overdraw-related test classes
mvn test -Dtest=FundServiceTest
mvn test -Dtest=OverdrawIntegrationTest
mvn test -Dtest=FundOverdrawTest
mvn test -Dtest=OverdrawDtoTest

# Run specific overdraw test methods
mvn test -Dtest=FundServiceTest#testRequestOverdrawCapabilitiesSuccess
mvn test -Dtest=OverdrawIntegrationTest#testWithdrawWithOverdrawEnabledSuccess

# Run saving-related test classes
mvn test -Dtest=SavingServiceTest
mvn test -Dtest=SavingIntegrationTest
mvn test -Dtest=SavingTest
mvn test -Dtest=SavingExceptionTest

# Run specific saving test methods
mvn test -Dtest=SavingServiceTest#testOpenSavingsAccountSuccess
mvn test -Dtest=SavingIntegrationTest#testCompleteSavingsWorkflow

# Run audit-related test classes
mvn test -Dtest=AuditServiceTest
mvn test -Dtest=AuditIntegrationTest
mvn test -Dtest=AccountAuditTest
mvn test -Dtest=AccountAuditRepositoryTest
mvn test -Dtest=AccountAuditControllerTest

# Run authentication-related test classes
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=AuthIntegrationTest
mvn test -Dtest=AuthExceptionTest

# Run statement-related test classes
mvn test -Dtest=AccountStatementDtoTest
mvn test -Dtest=OperationDtoTest
```

## Test Coverage

The test suite provides comprehensive coverage for:

- **Entity Layer**: 100% coverage of entity classes and embedded fields including AccountAudit, BankUser, Fund, Saving, and user field classes
- **Service Layer**: Business logic testing with mocked dependencies for Audit, Auth, Fund, and Saving services
- **Repository Layer**: Database operation testing with H2 for AccountAudit and BankUser repositories
- **Controller Layer**: REST endpoint testing for AccountAudit operations
- **DTO Layer**: Data transfer object validation for authentication, fund operations, and account statements
- **Enum Layer**: Enum value and behavior testing for AccountType, AuditOperation, and BankRole
- **Exception Layer**: Exception handling and error response testing for all custom exceptions
- **Integration Layer**: End-to-end flow testing for authentication, fund management, savings operations, and audit operations
- **Overdraw Banking**: Comprehensive testing of overdraw functionality including request, cancel, and withdraw operations
- **Savings Banking**: Complete testing of savings account operations including open, close, deposit, and withdraw with max balance validation
- **Audit System**: Full testing of audit operations, repository operations, and controller endpoints
- **Statement System**: Complete testing of account statement and operation DTOs

## Security Testing

The test suite includes security-focused tests:

- JWT token generation and validation
- Password encoding and verification
- Role-based access control
- Authentication failure scenarios
- User registration security
- Fund operation authorization
- Overdraw operation authorization and validation
- Savings operation authorization and validation
- Audit operation authorization
- Controller endpoint security

## Performance Considerations

- Tests use in-memory H2 database for fast execution
- Integration tests use @SpringBootTest for full application context
- Repository tests use @SpringBootTest for database integration
- Test data is minimal and focused
- Controller tests use MockMvc for isolated endpoint testing

## Troubleshooting

### Common Issues

1. **Test Database Connection Issues**
   - Ensure H2 dependency is included
   - Check application-test.properties configuration
   - Verify @SpringBootTest configuration for integration tests

2. **Integration Test Failures**
   - Check @SpringBootTest configuration
   - Ensure proper test data setup and cleanup
   - Verify database schema initialization

3. **JWT Token Issues**
   - Check JWT secret configuration in test properties
   - Verify token expiration settings
   - Ensure proper token format in test requests

### Debugging Tests

1. **Enable Debug Logging**
   ```properties
   logging.level.com.exalt_company.kata_bank_api=DEBUG
   logging.level.org.hibernate.SQL=DEBUG
   logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
   ```

2. **Use H2 Console**
   - Access at http://localhost:8080/h2-console during tests
   - JDBC URL: jdbc:h2:mem:testdb

3. **Run Tests in IDE**
   - Use IDE test runners for better debugging
   - Set breakpoints in test methods
   - Use @SpringBootTest for full context integration tests

## Future Enhancements

Potential improvements for the test suite:

1. **Additional Controller Tests**: Add dedicated controller layer tests for remaining controllers
2. **Performance Tests**: Add load testing for critical endpoints
3. **Contract Tests**: Implement consumer-driven contract testing
4. **Mutation Testing**: Add mutation testing for better test quality
5. **API Documentation Tests**: Test OpenAPI/Swagger documentation
6. **Database Migration Tests**: Test schema evolution scenarios
7. **Savings Interest Tests**: Add tests for interest calculation if implemented
8. **Savings Transfer Tests**: Add tests for transfers between savings accounts
9. **User Management Tests**: Add comprehensive tests for user CRUD operations
10. **Security Penetration Tests**: Add security vulnerability testing