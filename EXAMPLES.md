# Example API Requests

This file contains ready-to-use curl commands for testing the Vehicle Storage Matcher API.

## Local Testing (http://localhost:8080)

### Example 1: Single Vehicle Match
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

### Example 2: Multiple Vehicles
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

### Example 3: Large Vehicle (No Match)
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

### Example 4: Health Check
```bash
curl http://localhost:8080/api/storage/health
```

### Example 5: Error - Empty Array
```bash
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[]'
```

### Example 6: Error - Invalid Dimensions
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

## AWS Lambda Testing

Replace `YOUR-API-ENDPOINT` with your actual API Gateway endpoint:

```bash
export API_ENDPOINT="https://YOUR-API-ID.execute-api.us-east-1.amazonaws.com/prod"
```

### Single Vehicle
```bash
curl -X POST $API_ENDPOINT/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[{"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0}]'
```

### Multiple Vehicles
```bash
curl -X POST $API_ENDPOINT/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[
    {"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0},
    {"id":"v2","type":"suv","length":17.0,"width":7.0,"height":6.0}
  ]'
```

### Health Check
```bash
curl $API_ENDPOINT/api/storage/health
```

## Using jq for Pretty Output

Install jq: `sudo apt-get install jq` or `brew install jq`

```bash
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[{"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0}]' \
  | jq .
```

## Sample JSON Files

### vehicles.json
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

Use with:
```bash
curl -X POST http://localhost:8080/api/storage/match \
  -H "Content-Type: application/json" \
  -d @vehicles.json
```

## Expected Responses

### Successful Match
```json
[
  {
    "vehicleId": "vehicle-001",
    "matchedSpace": {
      "id": "space-006",
      "type": "garage",
      "length": 15.0,
      "width": 7.5,
      "height": 6.5,
      "pricePerMonth": 90.0,
      "location": "East Side",
      "features": ["covered"]
    },
    "fitScore": 75.42,
    "message": "Match found"
  }
]
```

### No Match Found
```json
[
  {
    "vehicleId": "huge-truck",
    "matchedSpace": null,
    "fitScore": 0.0,
    "message": "No suitable storage space found"
  }
]
```

### Error Response
```json
{
  "status": "error",
  "message": "Vehicle list cannot be empty"
}
```

### Health Check
```json
{
  "status": "UP",
  "service": "Vehicle Storage Matcher"
}
```
