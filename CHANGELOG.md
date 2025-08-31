# Changelog

All notable changes to this project will be documented in this file.

## [0.0.2-SNAPSHOT] - 2024-12-19

### Added
- **Overdraw Banking Functionality**
  - **Request Overdraw Capabilities**: Users can request authorization to withdraw more than their current balance
  - **Cancel Overdraw Capabilities**: Users can cancel overdraw functionality when balance is not negative
  - **Enhanced Withdraw Operations**: Withdraw functionality now supports overdraw when enabled
  - **Overdraw Limit Management**: Configurable maximum overdraw amounts per fund
  - **Business Rule Enforcement**: Comprehensive validation of overdraw operations

- **New API Endpoints**
  - `PUT /api/fund/request-overdraw` - Enable overdraw capabilities on a fund
  - `PUT /api/fund/cancel-overdraw` - Disable overdraw capabilities on a fund
  - Enhanced `POST /api/fund/withdraw` - Now supports overdraw functionality

- **Enhanced Data Models**
  - **Fund Entity**: Added `canOverdraw` boolean field and `maxOverdraw` double field
  - **OverdrawDto**: New DTO for overdraw operations with maxOverdraw parameter
  - **Enhanced DTOs**: Added comprehensive Swagger documentation to all fund-related DTOs

- **Comprehensive Testing Suite**
  - **Service Layer Tests**: Complete unit tests for all overdraw business logic
  - **Integration Tests**: End-to-end API testing for overdraw functionality
  - **Entity Tests**: Fund entity overdraw capabilities testing
  - **DTO Tests**: OverdrawDto validation and edge cases
  - **Error Handling Tests**: Comprehensive error scenario coverage

- **Enhanced API Documentation**
  - **Swagger Integration**: Complete OpenAPI documentation for overdraw endpoints
  - **Detailed Descriptions**: Business rules and constraints clearly documented
  - **Error Response Documentation**: Comprehensive error handling documentation
  - **Schema Documentation**: Enhanced DTO and response schema documentation

- **Business Rules Implementation**
  - **Overdraw Authorization**: Users must request overdraw capabilities before using them
  - **Balance Protection**: Cannot cancel overdraw when balance is negative
  - **Limit Enforcement**: Withdrawals cannot exceed overdraw limits
  - **Authorization Validation**: All operations require proper user authorization

### Technical Improvements
- **Enhanced Error Handling**: Specific error messages for overdraw-related failures
- **Security Enhancements**: Authorization checks for all overdraw operations
- **Code Quality**: Comprehensive test coverage for new functionality
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
  - Comprehensive test coverage including unit and integration tests
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

- **0.0.2-SNAPSHOT** - Overdraw banking functionality release
  - Comprehensive overdraw capabilities (request, cancel, withdraw)
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
