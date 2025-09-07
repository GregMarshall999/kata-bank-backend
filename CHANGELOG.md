# Changelog

All notable changes to this project will be documented in this file.

## [0.0.4-SNAPSHOT]

### Added
- **Account Statement Functionality**
  - **Monthly Statement Requests**: Users can request detailed bank statements for their accounts showing past month activity
  - **Account Activity Tracking**: Audit trail of all account operations including deposits, withdrawals, and account modifications
  - **Pagination Support**: Large statement histories are paginated for optimal performance and user experience
  - **Account Type Support**: Statement requests work for both FUND and SAVING account types
  - **Operation History**: Detailed operation records with timestamps, amounts, and operation authors

- **New API Endpoints**
  - `GET /api/audit-account/statement/{accountType}/{ownerId}/{page}/{size}` - Request account statement with pagination

- **Enhanced Data Models**
  - **AccountStatementDto**: New DTO for statement responses with account balance and paginated operations
  - **OperationDto**: New DTO for individual operation records in statements
  - **AccountAuditController**: New controller for account auditing and statement requests
  - **Enhanced AuditService**: Extended audit service with statement generation capabilities

- **Testing Suite**
  - **Controller Tests**: Complete unit tests for AccountAuditController (`AccountAuditControllerTest.java`)
  - **Service Tests**: Unit tests for audit service statement functionality (`AuditServiceTest.java`)
  - **Integration Tests**: End-to-end API testing for statement requests (`AccountAuditIntegrationTest.java`)
  - **DTO Tests**: Statement DTO validation and edge cases (`AccountStatementDtoTest.java`, `OperationDtoTest.java`)
  - **Security Tests**: Authorization validation for statement access

- **Enhanced API Documentation**
  - **Swagger Integration**: Complete OpenAPI documentation for statement endpoints
  - **Detailed Descriptions**: Business rules and constraints clearly documented
  - **Error Response Documentation**: Error handling documentation
  - **Schema Documentation**: Enhanced DTO and response schema documentation

- **Business Rules Implementation**
  - **Authorization Validation**: Users can only access their own account statements
  - **Account Type Validation**: Proper validation of FUND vs SAVING account types
  - **Pagination Validation**: Page number and size validation for optimal performance
  - **Monthly Activity Scope**: Statements show operations from the past month (rolling window)
  - **Operation Chronology**: Operations are sorted in reverse chronological order (newest first)

### Technical Improvements
- **Enhanced Error Handling**: Specific error messages for statement-related failures
- **Security Enhancements**: Authorization checks for all statement operations
- **Code Quality**: Test coverage for new functionality
- **Documentation**: Updated API documentation with statement functionality
- **Service Layer**: Enhanced AuditService with statement generation capabilities
- **Controller Layer**: New AccountAuditController with RESTful statement endpoints

### Breaking Changes
- None

### Deprecated
- None

---

## [0.0.3-SNAPSHOT]

### Added
- **Savings Account Functionality**
  - **Open Savings Account**: Users can create new savings accounts with configurable maximum balance limits
  - **Close Savings Account**: Users can close savings accounts when balance is zero
  - **Savings Deposit Operations**: Deposit money into savings accounts with maximum balance validation
  - **Savings Withdraw Operations**: Withdraw money from savings accounts with balance protection
  - **Maximum Balance Management**: Configurable maximum balance limits per savings account
  - **Business Rule Enforcement**: Validation of savings operations

- **New API Endpoints**
  - `POST /api/saving/open` - Create a new savings account
  - `POST /api/saving/close` - Close an existing savings account
  - `POST /api/saving/deposit` - Deposit money into a savings account
  - `POST /api/saving/withdraw` - Withdraw money from a savings account

- **Enhanced Data Models**
  - **Saving Entity**: New entity with balance, maxBalance, and owner relationship
  - **SavingDto**: New DTO for savings operations with validation
  - **SavingMapper**: MapStruct mapper for entity-DTO conversions
  - **SavingRepository**: Repository interface for savings data persistence

- **Testing Suite**
  - **Entity Tests**: Complete unit tests for Saving entity (`SavingTest.java`)
  - **Service Tests**: Unit tests for all savings business logic (`SavingServiceTest.java`)
  - **Integration Tests**: End-to-end API testing for savings functionality (`SavingIntegrationTest.java`)
  - **Exception Tests**: Savings exception handling and validation (`SavingExceptionTest.java`)
  - **Error Handling Tests**: Error scenario coverage for savings operations

- **Enhanced API Documentation**
  - **Swagger Integration**: Complete OpenAPI documentation for savings endpoints
  - **Detailed Descriptions**: Business rules and constraints clearly documented
  - **Error Response Documentation**: Error handling documentation
  - **Schema Documentation**: Enhanced DTO and response schema documentation

- **Business Rules Implementation**
  - **Maximum Balance Protection**: Deposits cannot exceed the configured maximum balance
  - **Zero Balance Requirement**: Savings accounts can only be closed when balance is zero
  - **Negative Balance Prevention**: Withdrawals cannot result in negative balances
  - **Authorization Validation**: All operations require proper user authorization
  - **Account Uniqueness**: Users cannot open multiple savings accounts with the same ID

### Technical Improvements
- **Enhanced Error Handling**: Specific error messages for savings-related failures
- **Security Enhancements**: Authorization checks for all savings operations
- **Code Quality**: Test coverage for new functionality
- **Documentation**: Updated TEST.md with savings testing documentation
- **Service Layer**: New SavingService with complete CRUD operations
- **Controller Layer**: New SavingController with RESTful endpoints

### Breaking Changes
- None

### Deprecated
- None

---

## [0.0.2-SNAPSHOT]

### Added
- **Overdraw Banking Functionality**
  - **Request Overdraw Capabilities**: Users can request authorization to withdraw more than their current balance
  - **Cancel Overdraw Capabilities**: Users can cancel overdraw functionality when balance is not negative
  - **Enhanced Withdraw Operations**: Withdraw functionality now supports overdraw when enabled
  - **Overdraw Limit Management**: Configurable maximum overdraw amounts per fund
  - **Business Rule Enforcement**: Validation of overdraw operations

- **New API Endpoints**
  - `PUT /api/fund/request-overdraw` - Enable overdraw capabilities on a fund
  - `PUT /api/fund/cancel-overdraw` - Disable overdraw capabilities on a fund
  - Enhanced `POST /api/fund/withdraw` - Now supports overdraw functionality

- **Enhanced Data Models**
  - **Fund Entity**: Added `canOverdraw` boolean field and `maxOverdraw` double field
  - **OverdrawDto**: New DTO for overdraw operations with maxOverdraw parameter
  - **Enhanced DTOs**: Added Swagger documentation to all fund-related DTOs

- **Testing Suite**
  - **Service Layer Tests**: Complete unit tests for all overdraw business logic
  - **Integration Tests**: End-to-end API testing for overdraw functionality
  - **Entity Tests**: Fund entity overdraw capabilities testing
  - **DTO Tests**: OverdrawDto validation and edge cases
  - **Error Handling Tests**: Error scenario coverage

- **Enhanced API Documentation**
  - **Swagger Integration**: Complete OpenAPI documentation for overdraw endpoints
  - **Detailed Descriptions**: Business rules and constraints clearly documented
  - **Error Response Documentation**: Error handling documentation
  - **Schema Documentation**: Enhanced DTO and response schema documentation

- **Business Rules Implementation**
  - **Overdraw Authorization**: Users must request overdraw capabilities before using them
  - **Balance Protection**: Cannot cancel overdraw when balance is negative
  - **Limit Enforcement**: Withdrawals cannot exceed overdraw limits
  - **Authorization Validation**: All operations require proper user authorization

### Technical Improvements
- **Enhanced Error Handling**: Specific error messages for overdraw-related failures
- **Security Enhancements**: Authorization checks for all overdraw operations
- **Code Quality**: Test coverage for new functionality
- **Documentation**: Updated TEST.md with overdraw testing documentation

### Breaking Changes
- None

### Deprecated
- None

---

## [0.0.1-SNAPSHOT]

### Added
- **Bank User Management**
  - User registration and creation functionality
  - User authentication with JWT (JSON Web Tokens)
  - Role-based access control with `BankRole` enum
  - Secure password handling and validation

- **Fund Operations**
  - Deposit functionality for adding funds to accounts
  - Withdrawal functionality for removing funds from accounts
  - Transaction validation and processing
  - Fund balance tracking and management

- **Security Features**
  - JWT-based authentication system
  - Spring Security integration
  - Protected API endpoints
  - Authorization filters for secure access

- **Business Rules**
  - **No Negative Balance Policy**: Withdrawals are prevented when they would result in a negative balance
  - Transaction validation to ensure account integrity
  - Business logic enforcement for fund operations

- **Technical Infrastructure**
  - Spring Boot 3.5.5 application framework
  - MySQL database integration for production
  - H2 database for testing environment
  - JPA/Hibernate for data persistence
  - MapStruct for object mapping
  - Test coverage including unit and integration tests
  - RESTful API design with proper HTTP status codes
  - Global exception handling with custom error responses

- **API Endpoints**
  - Authentication endpoints (login, registration)
  - Bank user management endpoints
  - Fund operation endpoints (deposit, withdrawal)
  - Base CRUD operations with pagination support

- **API Documentation**
  - Swagger/OpenAPI 3 integration for interactive API documentation
  - Auto-generated API documentation from code annotations
  - Interactive API testing interface at `/swagger-ui.html`
  - API documentation endpoint at `/api-docs`
  - Customized Swagger UI configuration for better user experience

- **Data Models**
  - `BankUser` entity with user information and roles
  - `Fund` entity for managing account balances and transactions
  - DTOs for API request/response handling
  - Base entities and DTOs for common functionality

### Technical Details
- **Java Version**: 17
- **Framework**: Spring Boot 3.5.5
- **Database**: MySQL 9.4.0 (production), H2 (testing)
- **Security**: JWT 0.13.0
- **Mapping**: MapStruct 1.6.3
- **Architecture**: Hexagonal architecture with clear separation of concerns

### Security
- JWT token-based authentication
- Password encryption and secure storage
- Role-based authorization
- Protected API endpoints
- Input validation and sanitization

### Testing
- Unit tests for all business logic
- Integration tests for API endpoints
- Repository layer testing
- Service layer testing with mocked dependencies
- Security testing with Spring Security Test

---

## Version History

- **0.0.4-SNAPSHOT** - Account statement functionality release
  - Monthly account statement requests with pagination
  - Audit trail and operation history
  - Enhanced API documentation for statement endpoints
  - Complete test coverage for statement functionality

- **0.0.3-SNAPSHOT** - Savings account functionality release
  - Complete savings account management (open, close, deposit, withdraw)
  - Maximum balance validation and protection
  - Test coverage for savings operations
  - Enhanced API documentation and business rule enforcement

- **0.0.2-SNAPSHOT** - Overdraw banking functionality release
  - Overdraw capabilities (request, cancel, withdraw)
  - Enhanced API documentation with Swagger
  - Complete test coverage for overdraw functionality
  - Business rule enforcement and validation

- **0.0.1-SNAPSHOT** - Initial release with core banking functionality
  - User authentication and authorization
  - Fund deposit and withdrawal operations
  - Negative balance prevention
  - Complete API infrastructure

---

## License

This project is part of the Exalt Company kata challenge.
