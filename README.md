# brokage Module

A Spring Boot application for managing stock orders in a brokage firm. This application allows employees to create, list, cancel, and match stock orders for customers.

## Features

- **Create Order**: Create new buy/sell orders for customers
- **List Orders**: List orders by customer and date range with optional status filtering
- **Cancel Order**: Cancel pending orders (only PENDING orders can be cancelled)
- **List Assets**: List customer assets and their available balances
- **Match Orders**: Admin functionality to match pending orders

## Technology Stack

- Java 17
- Spring Boot 3.5.0
- Spring Data JPA
- Spring Security
- H2 Database
- Maven
- Lombok

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Getting Started

### Build the Application

```bash
./mvnw clean compile
```

### Run Tests

```bash
./mvnw test
```

### Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Authentication

All API endpoints require basic authentication:
- **Username**: `admin`
- **Password**: `admin123`

## API Endpoints

### Order Management

#### Create Order
```http
POST /api/orders
Content-Type: application/json
Authorization: Basic admin:admin123

{
    "customerId": 1,
    "assetName": "AAPL",
    "side": "BUY",
    "size": 10,
    "price": 150.00
}
```

#### List Orders by Customer
```http
GET /api/orders/customer/{customerId}
GET /api/orders/customer/{customerId}?startDate=2024-01-01&endDate=2024-01-31&status=PENDING
Authorization: Basic admin:admin123
```

#### Cancel Order
```http
DELETE /api/orders/{orderId}
Authorization: Basic admin:admin123
```

#### Match Order (Admin only)
```http
POST /api/orders/{orderId}/match
Authorization: Basic admin:admin123
```

#### Get Pending Orders
```http
GET /api/orders/pending
Authorization: Basic admin:admin123
```

### Asset Management

#### List Customer Assets
```http
GET /api/assets/customer/{customerId}
Authorization: Basic admin:admin123
```

#### Get Specific Asset
```http
GET /api/assets/customer/{customerId}/asset/{assetName}
Authorization: Basic admin:admin123
```

## Business Logic

### Order Types
- **BUY**: Purchase assets using TRY currency
- **SELL**: Sell assets to receive TRY currency

### Order Status
- **PENDING**: Order created and waiting to be matched
- **MATCHED**: Order has been executed
- **CANCELED**: Order has been cancelled

### Asset Management
- All orders are executed against TRY (Turkish Lira)
- When creating a BUY order, the system checks if the customer has enough TRY balance
- When creating a SELL order, the system checks if the customer has enough of the asset to sell
- Assets are reserved when orders are created and released when orders are cancelled
- When orders are matched, the actual asset transfers occur

### Initial Setup
- When a customer places their first order, a TRY asset is automatically created with an initial balance of 100,000 TRY

## Database

The application uses H2 in-memory database. You can access the H2 console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:brokage-db`
- Username: `sa`
- Password: (empty)

## Testing

The application includes:
- Unit tests for service layer logic
- Integration tests for REST endpoints
- Comprehensive test coverage for order creation, cancellation, and matching

Run tests with:
```bash
./mvnw test
```

## Example Usage

1. **Create a BUY order**:
   ```bash
   curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
     -d '{
       "customerId": 1,
       "assetName": "AAPL",
       "side": "BUY",
       "size": 10,
       "price": 150.00
     }'
   ```

2. **List orders for a customer**:
   ```bash
   curl -X GET http://localhost:8080/api/orders/customer/1 \
     -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
   ```

3. **Check customer assets**:
   ```bash
   curl -X GET http://localhost:8080/api/assets/customer/1 \
     -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
   ```

4. **Match an order**:
   ```bash
   curl -X POST http://localhost:8080/api/orders/1/match \
     -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
   ```

## Project Structure

```
src/
├── main/
│   ├── java/dev/sami/brokagemodule/
│   │   ├── controller/         # REST controllers
│   │   ├── domain/            # Entity classes
│   │   ├── dto/               # Data Transfer Objects
│   │   ├── exception/         # Custom exceptions
│   │   ├── mapper/            # Object mapping utilities
│   │   ├── repository/        # JPA repositories
│   │   ├── service/           # Business logic
│   │   └── config/           # Configuration classes
│   └── resources/
│       └── application.properties
└── test/
    ├── java/                  # Test classes
    └── resources/
        └── application-test.properties
```

## Error Handling

The application includes comprehensive error handling:
- Validation errors for invalid input
- Business logic errors (insufficient funds, invalid order status)
- Not found errors for non-existent resources
- Generic error handling for unexpected exceptions

All errors return appropriate HTTP status codes and descriptive error messages. 