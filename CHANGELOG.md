# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0]

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

- **1.0.0** - Initial release with core banking functionality
  - User authentication and authorization
  - Fund deposit and withdrawal operations
  - Negative balance prevention
  - Complete API infrastructure

---

## License

This project is part of the Exalt Company kata challenge.
