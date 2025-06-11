# brokage Module

A Spring Boot application for managing stock orders in a brokerage firm. The application allows employees to create, list, cancel, and match buy/sell stock orders for customers. It also tracks asset balances for each customer.

## Features

- **Order Creation**: Create BUY/SELL orders for assets
- **Order Listing**: Retrieve customer orders with optional filtering by date and status
- **Order Cancellation**: Cancel only `PENDING` orders
- **Order Matching**: Match BUY and SELL orders manually (admin)
- **Asset Lookup**: Query customer's total and available balances for any asset
- **Pending Orders**: Retrieve unmatched orders across all customers

## Tech Stack

- Java 17
- Spring Boot 3.5.0
- Spring Data JPA
- Spring Security
- Lombok
- H2 Database
- Maven

## Prerequisites

- Java 17+
- Maven 3.6+

## Running the App

### Build

```bash
./mvnw clean install
```

### Run

```bash
./mvnw spring-boot:run
```

App runs on: `http://localhost:8080`

## Dockerization

You can also run the application using Docker.

### Build the Docker image

```bash
docker build -t brokage-module .
```

### Run the Docker container

```bash
docker run -p 8080:8080 brokage-module
```

## Authentication

All endpoints require Basic Auth:

- **Username**: `admin`
- **Password**: `admin123`

## API Overview

### Order APIs

#### Create Order

```http
POST /api/orders
```

Request Body:

```json
{
  "customerId": 1,
  "assetName": "AAPL",
  "side": "BUY",
  "size": 10,
  "price": 150.00
}
```

#### List Customer Orders

```http
GET /api/orders/customer/{customerId}
GET /api/orders/customer/{customerId}?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&status=PENDING
```

#### Cancel Order

```http
DELETE /api/orders/{orderId}
```

#### Match Order

```http
POST /api/orders/{orderId}/match
```

#### Get All Pending Orders

```http
GET /api/orders/pending
```

### Asset APIs

#### List Customer Assets

```http
GET /api/assets/customer/{customerId}
```

#### Get Specific Asset

```http
GET /api/assets/customer/{customerId}/asset/{assetName}
```

## Business Logic

### Order Sides

- **BUY**: Spend TRY to buy assets
- **SELL**: Sell owned assets to receive TRY

### Order Status

- `PENDING`: Awaiting match
- `MATCHED`: Executed
- `CANCELED`: Cancelled before execution

### Asset Flow

- All assets are traded against **TRY**
- On BUY: Checks TRY balance → reserves funds
- On SELL: Checks asset balance → reserves quantity
- Reserved funds/assets released on cancellation
- On MATCH: Asset quantities and TRY balances are transferred

### Auto-Balance Initialization

- New customers receive a default `TRY` asset with `100,000` balance upon first order placement

## H2 Console

- Web UI: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:brokage-db`
- Username: `sa`
- Password: *(empty)*

## Testing

```bash
./mvnw test
```

Includes:

- Unit tests for core business logic
- Integration tests for controllers and services

## Curl Examples

### Create Order

```bash
curl -X POST http://localhost:8080/api/orders \
 -H "Content-Type: application/json" \
 -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
 -d '{"customerId":1,"assetName":"AAPL","side":"BUY","size":10,"price":150}'
```

### List Orders

```bash
curl -X GET http://localhost:8080/api/orders/customer/1 \
 -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

### Check Assets

```bash
curl -X GET http://localhost:8080/api/assets/customer/1 \
 -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

### Match Order

```bash
curl -X POST http://localhost:8080/api/orders/1/match \
 -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

## Project Structure

```
src/
├── main/
│   ├── java/dev/sami/brokagemodule/
│   │   ├── controller/      # REST Controllers (Orders, Assets)
│   │   ├── domain/          # JPA Entities (Order, Asset)
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── exception/       # Custom Exceptions & Handlers
│   │   ├── mapper/          # Entity ↔ DTO Mapping
│   │   ├── repository/      # JPA Repositories
│   │   ├── service/         # Business Services (OrderService, AssetService)
│   │   └── config/          # Spring Security and App Config
│   └── resources/
│       └── application.properties
└── test/
    ├── java/                # Unit and Integration Tests
    └── resources/
        └── application-test.properties
```

## Error Handling

- **400**: Validation or business rule errors (e.g. insufficient funds)
- **404**: Resource not found
- **409**: Conflicts (e.g. attempting to cancel a non-pending order)
- **500**: Unexpected server errors

All responses include descriptive error messages.

