# Quick Reference

## Build & Run

```bash
# Build
mvn clean package

# Run tests
mvn test

# Run locally
java -jar target/vehicle-storage-matcher-1.0.0.jar

# Deploy to AWS
sam deploy --guided
```

## API Endpoints

### POST /api/storage/match
Match vehicles to storage spaces

**Request:**
```json
[{"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0}]
```

**Response:**
```json
[{
  "vehicleId": "v1",
  "matchedSpace": {...},
  "fitScore": 75.42,
  "message": "Match found"
}]
```

### GET /api/storage/health
Health check endpoint

**Response:**
```json
{"status": "UP", "service": "Vehicle Storage Matcher"}
```

## Test Commands

```bash
# Quick test
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[{"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0}]'

# Health check
curl http://localhost:8080/api/storage/health
```

## AWS Lambda Handler

```
com.vehiclesearch.lambda.StreamLambdaHandler::handleRequest
```

## Configuration

- **Memory**: 512 MB minimum
- **Timeout**: 30 seconds
- **Runtime**: Java 17
- **JAR**: target/vehicle-storage-matcher-1.0.0-aws.jar

## Files

- **README.md** - Main documentation
- **DEPLOYMENT.md** - Deployment guide
- **TESTING.md** - Testing guide
- **PROJECT_SUMMARY.md** - Complete overview
- **pom.xml** - Maven configuration
- **template.yaml** - AWS SAM template

## Key Features

✅ Bin packing algorithm (First-Fit Decreasing)  
✅ Input validation & error handling  
✅ 21 unit tests (100% pass)  
✅ AWS Lambda ready  
✅ Cold start optimized  
✅ Security scanned (0 vulnerabilities)  
✅ Complete documentation  

## Support

See detailed guides:
- API usage → README.md
- Deployment → DEPLOYMENT.md
- Testing → TESTING.md
- Overview → PROJECT_SUMMARY.md
