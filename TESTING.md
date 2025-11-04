# Testing Guide

Comprehensive testing guide for the Vehicle Storage Matcher application.

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=BinPackingServiceTest
mvn test -Dtest=StorageSpaceServiceTest
mvn test -Dtest=VehicleStorageControllerTest
```

### Run with Coverage

```bash
mvn test jacoco:report
```

View coverage report at: `target/site/jacoco/index.html`

## Test Coverage

The test suite includes:

### 1. BinPackingServiceTest (11 tests)
- Vehicle to space matching with multiple scenarios
- Empty/null input handling
- Large vehicle handling (no suitable space)
- Volume-based sorting verification
- Vehicle validation (null/invalid dimensions)

### 2. StorageSpaceServiceTest (4 tests)
- Loading storage spaces from JSON
- Retrieving all spaces
- Retrieving available spaces
- Physical fit verification

### 3. VehicleStorageControllerTest (6 tests)
- Successful matching endpoint
- Empty array error handling
- Invalid vehicle error handling
- Multiple vehicles matching
- Health check endpoint

## Manual Testing

### Start Application Locally

```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using JAR
java -jar target/vehicle-storage-matcher-1.0.0.jar
```

Wait for the message: `Started VehicleStorageApplication`

### Test Scenarios

#### 1. Basic Vehicle Matching

```bash
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[
    {
      "id": "vehicle-001",
      "type": "sedan",
      "length": 15.0,
      "width": 6.0,
      "height": 5.0
    }
  ]'
```

**Expected Response**: 200 OK with matched storage space

#### 2. Multiple Vehicles

```bash
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[
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
    },
    {
      "id": "vehicle-003",
      "type": "compact",
      "length": 12.0,
      "width": 5.5,
      "height": 4.5
    }
  ]'
```

**Expected Response**: Array with 3 matched options

#### 3. Oversized Vehicle (No Match)

```bash
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[
    {
      "id": "huge-truck",
      "type": "truck",
      "length": 50.0,
      "width": 20.0,
      "height": 15.0
    }
  ]'
```

**Expected Response**: 200 OK with message "No suitable storage space found"

#### 4. Invalid Input - Empty Array

```bash
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[]'
```

**Expected Response**: 400 Bad Request with error message

#### 5. Invalid Input - Negative Dimensions

```bash
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[
    {
      "id": "invalid-vehicle",
      "type": "sedan",
      "length": -15.0,
      "width": 6.0,
      "height": 5.0
    }
  ]'
```

**Expected Response**: 400 Bad Request with validation error

#### 6. Invalid Input - Missing Required Fields

```bash
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[
    {
      "id": "incomplete-vehicle",
      "type": "sedan",
      "length": 15.0
    }
  ]'
```

**Expected Response**: 400 Bad Request with validation error

#### 7. Health Check

```bash
curl http://localhost:8080/api/storage/health
```

**Expected Response**: 200 OK with status "UP"

## Performance Testing

### Load Testing with Apache Bench

```bash
# Install apache bench
sudo apt-get install apache2-utils  # Linux
brew install httpd                   # macOS

# Run load test (100 requests, 10 concurrent)
ab -n 100 -c 10 -p vehicle.json -T application/json \
  http://localhost:8080/api/storage/match
```

Create `vehicle.json`:
```json
[{"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0}]
```

### Expected Performance
- **Local**: < 100ms per request
- **Lambda (warm)**: 100-200ms
- **Lambda (cold start)**: 3-5 seconds

## Integration Testing

### Testing AWS Lambda Locally with SAM

```bash
# Build
sam build

# Test locally
sam local start-api

# Send request
curl -X POST http://localhost:3000/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[{"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0}]'
```

### Testing Deployed Lambda

```bash
# Replace YOUR-API-ENDPOINT with your actual endpoint
ENDPOINT="https://YOUR-API-ID.execute-api.us-east-1.amazonaws.com/prod"

# Test successful matching
curl -X POST $ENDPOINT/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[{"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0}]'

# Test health
curl $ENDPOINT/api/storage/health
```

## Test Data

### Sample Vehicles

Small vehicles that should easily find matches:
```json
[
  {"id": "compact-1", "type": "compact", "length": 12.0, "width": 5.5, "height": 4.5},
  {"id": "sedan-1", "type": "sedan", "length": 15.0, "width": 6.0, "height": 5.0},
  {"id": "sedan-2", "type": "sedan", "length": 14.5, "width": 6.2, "height": 5.2}
]
```

Medium vehicles:
```json
[
  {"id": "suv-1", "type": "suv", "length": 17.0, "width": 7.0, "height": 6.0},
  {"id": "suv-2", "type": "suv", "length": 18.0, "width": 7.5, "height": 6.5},
  {"id": "van-1", "type": "van", "length": 19.0, "width": 7.8, "height": 7.0}
]
```

Large vehicles (may not find matches):
```json
[
  {"id": "truck-1", "type": "truck", "length": 25.0, "width": 10.0, "height": 9.0},
  {"id": "truck-2", "type": "truck", "length": 30.0, "width": 12.0, "height": 10.0},
  {"id": "oversized", "type": "truck", "length": 50.0, "width": 20.0, "height": 15.0}
]
```

## Debugging

### Enable Debug Logging

Add to `application.properties`:
```properties
logging.level.com.vehiclesearch=DEBUG
```

### Common Issues

#### Application won't start
- Check port 8080 is not in use: `lsof -i :8080`
- Kill process: `kill -9 $(lsof -t -i:8080)`

#### Tests failing
- Ensure dependencies are installed: `mvn clean install`
- Check Java version: `java -version` (should be 17+)

#### Deployment issues
- Verify JAR size: `ls -lh target/*.jar`
- Check Lambda handler name matches exactly
- Increase Lambda memory if cold starts timeout

## Continuous Integration

### GitHub Actions Example

Create `.github/workflows/test.yml`:

```yaml
name: Test

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run tests
        run: mvn test
      - name: Generate coverage
        run: mvn jacoco:report
      - name: Build
        run: mvn package -DskipTests
```

## Success Criteria

All tests should:
- ✅ Pass with 0 failures
- ✅ Complete in < 60 seconds
- ✅ Have > 80% code coverage
- ✅ Produce no errors in logs
- ✅ Build successfully
- ✅ Create valid JAR artifacts
