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
│   ├── BaseEntityTest.java                   # Base entity tests
│   ├── FundTest.java                         # Fund entity tests
│   ├── FundOverdrawTest.java                 # Fund overdraw functionality tests
│   ├── SavingTest.java                       # Saving entity tests
│   └── user_fields/                          # Embedded field tests
│       ├── IdentityTest.java                 # Identity embedded class tests
│       └── CredentialsTest.java              # Credentials embedded class tests
├── service/                                  # Service layer tests
│   ├── AuditServiceTest.java                 # Audit service tests (including statement functionality)
│   ├── AuthServiceTest.java                  # Authentication service tests
│   ├── BankUserServiceTest.java              # Bank user service tests
│   ├── BaseServiceTest.java                  # Base service tests
│   ├── FundServiceTest.java                  # Fund service tests (including overdraw)
│   └── SavingServiceTest.java                # Saving service tests
├── repository/                               # Repository layer tests
│   ├── AccountAuditRepositoryTest.java       # AccountAudit repository tests
│   ├── BankUserRepositoryTest.java           # BankUser repository tests
│   ├── FundRepositoryTest.java               # Fund repository tests
│   └── SavingRepositoryTest.java             # Saving repository tests
├── controller/                               # Controller layer tests
│   ├── AccountAuditControllerTest.java       # AccountAudit controller tests (statement endpoints)
│   ├── AuthControllerTest.java               # Authentication controller tests
│   ├── BankUserControllerTest.java           # Bank user controller tests
│   ├── BaseControllerTest.java               # Base controller tests
│   ├── FundControllerTest.java               # Fund controller tests
│   └── SavingControllerTest.java             # Saving controller tests
├── dto/                                      # DTO tests
│   ├── auth/                                 # Authentication DTOs
│   │   ├── AuthenticationRequestTest.java    # Authentication request DTO tests
│   │   ├── AuthenticationResponseTest.java   # Authentication response DTO tests
│   │   └── RegisterRequestTest.java          # Registration request DTO tests
│   ├── fund/                                 # Fund DTOs
│   │   ├── BaseFundDtoTest.java              # Base fund DTO tests
│   │   ├── FundDtoTest.java                  # Fund DTO tests
│   │   ├── FundOpDtoTest.java                # Fund operation DTO tests
│   │   └── OverdrawDtoTest.java              # Overdraw DTO tests
│   ├── statement/                            # Statement DTOs
│   │   ├── AccountStatementDtoTest.java      # Account statement DTO tests
│   │   └── OperationDtoTest.java             # Operation DTO tests
│   ├── BankUserDtoTest.java                  # Bank user DTO tests
│   ├── BaseDtoTest.java                      # Base DTO tests
│   ├── PageDtoTest.java                      # Page DTO tests
│   ├── PasswordedBankUserDtoTest.java        # Passworded bank user DTO tests
│   └── SavingDtoTest.java                    # Saving DTO tests
├── enums/                                    # Enum tests
│   ├── AccountTypeTest.java                  # AccountType enum tests
│   ├── AuditOperationTest.java               # AuditOperation enum tests
│   └── BankRoleTest.java                     # BankRole enum tests
├── exception/                                # Exception handling tests
│   ├── AuditExceptionTest.java               # Audit exception tests
│   ├── AuthExceptionTest.java                # Authentication exception tests
│   ├── BankApiExceptionTest.java             # Bank API exception tests
│   ├── BaseExceptionTest.java                # Base exception tests
│   ├── ErrorResponseTest.java                # Error response DTO tests
│   ├── FundExceptionTest.java                # Fund exception tests
│   ├── GlobalExceptionHandlerTest.java       # Global exception handler tests
│   └── SavingExceptionTest.java              # Saving exception tests
├── integration/                              # Integration tests
│   ├── AccountAuditIntegrationTest.java      # End-to-end audit and statement tests
│   ├── AuthIntegrationTest.java              # End-to-end authentication tests
│   ├── BankUserIntegrationTest.java          # End-to-end bank user management tests
│   ├── FundIntegrationTest.java              # End-to-end fund management tests
│   ├── OverdrawIntegrationTest.java          # End-to-end overdraw functionality tests
│   ├── SavingIntegrationTest.java            # End-to-end saving operations tests
│   └── SecurityIntegrationTest.java          # End-to-end security and authorization tests
└── security/                                 # Security tests
    └── SecurityConfigTest.java               # Security configuration tests
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

# Run audit-related tests (including statement functionality)
mvn test -Dtest="*Audit*" -DfailIfNoTests=false
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

# Run bank user-related test classes
mvn test -Dtest=BankUserServiceTest
mvn test -Dtest=BankUserIntegrationTest
mvn test -Dtest=BankUserControllerTest
mvn test -Dtest=BankUserDtoTest

# Run security-related test classes
mvn test -Dtest=SecurityConfigTest
mvn test -Dtest=SecurityIntegrationTest
```

## Test Coverage

The test suite provides coverage for:

- **Entity Layer**: 100% coverage of entity classes and embedded fields including AccountAudit, BankUser, Fund, Saving, BaseEntity, and user field classes
- **Service Layer**: Business logic testing with mocked dependencies for Audit, Auth, BankUser, Base, Fund, and Saving services
- **Repository Layer**: Database operation testing with H2 for AccountAudit, BankUser, Fund, and Saving repositories
- **Controller Layer**: REST endpoint testing for AccountAudit, Auth, BankUser, Base, Fund, and Saving operations
- **DTO Layer**: Data transfer object validation for authentication, fund operations, account statements, bank user management, and savings operations
- **Enum Layer**: Enum value and behavior testing for AccountType, AuditOperation, and BankRole
- **Exception Layer**: Exception handling and error response testing for all custom exceptions including Audit, Auth, BankApi, Base, Fund, and Saving exceptions
- **Integration Layer**: End-to-end flow testing for authentication, bank user management, fund management, savings operations, audit operations, overdraw functionality, and security
- **Security Layer**: Security configuration and authorization testing for all endpoints and operations
- **Overdraw Banking**: Testing of overdraw functionality including request, cancel, and withdraw operations
- **Savings Banking**: Complete testing of savings account operations including open, close, deposit, and withdraw with max balance validation
- **Audit System**: Full testing of audit operations, repository operations, controller endpoints, and statement generation
- **Statement System**: Complete testing of account statement and operation DTOs with comprehensive validation scenarios
- **Bank User Management**: Complete testing of user registration, authentication, and management operations

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
- Audit operation authorization and statement access control
- Controller endpoint security
- Bank user management authorization
- Security configuration validation
- Cross-user resource access prevention

## Performance Considerations

- Tests use in-memory H2 database for fast execution
- Integration tests use @SpringBootTest for full application context
- Repository tests use @SpringBootTest for database integration
- Test data is minimal and focused
- Controller tests use MockMvc for isolated endpoint testing
- Statement tests use pagination to handle large datasets efficiently

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
9. **User Management Tests**: Add tests for user CRUD operations
10. **Security Penetration Tests**: Add security vulnerability testing