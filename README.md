# Vehicle Storage Matcher

A Java Spring Boot serverless function that matches vehicles to available storage spaces using a bin packing algorithm.

## Features

- **RESTful API**: POST endpoint `/api/storage/match` for vehicle-to-space matching
- **Bin Packing Algorithm**: First-Fit Decreasing (FFD) algorithm for optimal space allocation
- **Input Validation**: Comprehensive validation of vehicle dimensions
- **Error Handling**: Robust error handling with meaningful error messages
- **AWS Lambda Ready**: Optimized for serverless deployment with cold start handling
- **Unit Tests**: Comprehensive test coverage for all components

## Architecture

```
┌─────────────────┐
│   API Gateway   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  AWS Lambda     │
│  (Java 17)      │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────┐
│     Spring Boot Application     │
├─────────────────────────────────┤
│  VehicleStorageController       │
│         ↓                        │
│  BinPackingService               │
│         ↓                        │
│  StorageSpaceService             │
│         ↓                        │
│  listings.json                   │
└─────────────────────────────────┘
```

## API Documentation

### POST /api/storage/match

Matches an array of vehicles to available storage spaces.

**Request Body:**
```json
[
  {
    "id": "vehicle-001",
    "type": "sedan",
    "length": 15.0,
    "width": 6.0,
    "height": 5.0
  },
  {
    "id": "vehicle-002",
    "type": "suv",
    "length": 17.0,
    "width": 7.0,
    "height": 6.0
  }
]
```

**Response (200 OK):**
```json
[
  {
    "vehicleId": "vehicle-001",
    "matchedSpace": {
      "id": "space-002",
      "type": "parking_lot",
      "length": 18.0,
      "width": 9.0,
      "height": 7.0,
      "pricePerMonth": 100.0,
      "location": "Midtown",
      "features": ["outdoor", "24/7 access"]
    },
    "fitScore": 87.5,
    "message": "Match found"
  },
  {
    "vehicleId": "vehicle-002",
    "matchedSpace": {
      "id": "space-001",
      "type": "garage",
      "length": 20.0,
      "width": 10.0,
      "height": 8.0,
      "pricePerMonth": 150.0,
      "location": "Downtown",
      "features": ["covered", "secure"]
    },
    "fitScore": 92.3,
    "message": "Match found"
  }
]
```

**Error Response (400 Bad Request):**
```json
{
  "status": "error",
  "message": "Validation failed",
  "errors": ["Length must be positive"]
}
```

### GET /api/storage/health

Health check endpoint.

**Response (200 OK):**
```json
{
  "status": "UP",
  "service": "Vehicle Storage Matcher"
}
```

## Bin Packing Algorithm

The service implements a **First-Fit Decreasing (FFD)** bin packing algorithm:

1. **Sort vehicles by volume** (largest first)
2. **For each vehicle**, find the best-fitting available storage space
3. **Score each space** based on:
   - Physical fit (can the vehicle fit?)
   - Space efficiency (minimizes wasted space)
   - Cost efficiency (price per cubic unit)
   - Dimensional efficiency (how well dimensions match)
4. **Assign the best match** and remove from available spaces

### Fit Score Calculation

```
score = (wasteRatio × 40%) + (costPerUnit × 30%) + (dimensionEfficiency × 30%)
```

Where:
- `wasteRatio`: Proportion of unused space (0 = perfect fit)
- `costPerUnit`: Price per cubic unit of storage
- `dimensionEfficiency`: How closely dimensions match

## Building and Running

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- AWS CLI (for deployment)
- AWS SAM CLI (optional, for local testing)

### Build

```bash
mvn clean package
```

This creates two JAR files:
- `target/vehicle-storage-matcher-1.0.0.jar` - Standard Spring Boot JAR
- `target/vehicle-storage-matcher-1.0.0-aws.jar` - Fat JAR for AWS Lambda

### Run Locally

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`

### Test Locally

```bash
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[{"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0}]'
```

### Run Tests

```bash
mvn test
```

## AWS Lambda Deployment

### Using AWS SAM

1. **Build the application:**
```bash
mvn clean package
```

2. **Deploy using SAM:**
```bash
sam deploy --guided
```

Follow the prompts to configure:
- Stack name: `vehicle-storage-matcher`
- AWS Region: Your preferred region
- Confirm changes: Y
- Allow SAM CLI IAM role creation: Y

3. **Get the API URL:**
```bash
sam list endpoints --output json
```

### Using AWS Console

1. **Create Lambda Function:**
   - Runtime: Java 17
   - Handler: `com.vehiclesearch.lambda.StreamLambdaHandler::handleRequest`
   - Upload: `target/vehicle-storage-matcher-1.0.0-aws.jar`
   - Memory: 512 MB
   - Timeout: 30 seconds

2. **Create API Gateway:**
   - Type: REST API
   - Create resource: `/api/storage/match`
   - Create method: POST
   - Integration: Lambda Function
   - Deploy API

3. **Test the endpoint:**
```bash
curl -X POST https://YOUR-API-ID.execute-api.REGION.amazonaws.com/prod/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[{"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0}]'
```

## Storage Spaces Configuration

Storage spaces are defined in `src/main/resources/listings.json`. Each space includes:

- `id`: Unique identifier
- `type`: Type of storage (garage, parking_lot, warehouse)
- `length`, `width`, `height`: Dimensions in feet
- `pricePerMonth`: Monthly rental price in USD
- `location`: Location description
- `features`: Array of amenities

Example:
```json
{
  "id": "space-001",
  "type": "garage",
  "length": 20.0,
  "width": 10.0,
  "height": 8.0,
  "pricePerMonth": 150.0,
  "location": "Downtown",
  "features": ["covered", "secure"]
}
```

## Cold Start Optimization

The Lambda handler is optimized for cold starts:

- Static initialization of Spring Boot context
- Lazy bean initialization where applicable
- Lambda profile for production-specific configuration

Typical cold start: ~3-5 seconds
Warm invocation: ~100-200ms

## Testing

The project includes comprehensive unit tests:

- **BinPackingServiceTest**: Tests bin packing algorithm logic
- **StorageSpaceServiceTest**: Tests storage space loading
- **VehicleStorageControllerTest**: Tests REST API endpoints

Run tests with coverage:
```bash
mvn test jacoco:report
```

Coverage report: `target/site/jacoco/index.html`

## Error Handling

The API handles various error scenarios:

- **400 Bad Request**: Invalid input (missing fields, negative dimensions)
- **500 Internal Server Error**: Unexpected errors

All errors include:
- `status`: "error"
- `message`: Human-readable error description
- `errors`: (optional) Array of specific validation errors

## License

This project is provided as-is for evaluation purposes.

## Support

For issues or questions, please open an issue in the repository.