# Deployment Guide

This guide provides step-by-step instructions for deploying the Vehicle Storage Matcher serverless function to AWS Lambda.

## Prerequisites

- AWS Account
- AWS CLI configured with appropriate credentials
- AWS SAM CLI installed (optional, for SAM deployment)
- Java 17 or higher
- Maven 3.6 or higher

## Build the Application

First, build the application and create the deployment package:

```bash
mvn clean package
```

This creates `target/vehicle-storage-matcher-1.0.0-aws.jar` (approx. 44MB).

## Deployment Option 1: AWS SAM (Recommended)

### 1. Install AWS SAM CLI

```bash
# On macOS
brew install aws-sam-cli

# On Linux
pip install aws-sam-cli

# On Windows
choco install aws-sam-cli
```

### 2. Deploy using SAM

```bash
# First time deployment
sam deploy --guided

# Subsequent deployments
sam deploy
```

During guided deployment, provide:
- **Stack name**: `vehicle-storage-matcher`
- **AWS Region**: Your preferred region (e.g., `us-east-1`)
- **Confirm changes**: Y
- **Allow SAM CLI IAM role creation**: Y
- **Save arguments to samconfig.toml**: Y

### 3. Get the API endpoint

```bash
sam list endpoints --output json
```

The output will include your API Gateway endpoint URL.

## Deployment Option 2: AWS Console

### Step 1: Create Lambda Function

1. Go to AWS Lambda Console
2. Click "Create function"
3. Choose "Author from scratch"
4. Configure:
   - **Function name**: `vehicle-storage-matcher`
   - **Runtime**: Java 17
   - **Architecture**: x86_64
   - **Execution role**: Create a new role with basic Lambda permissions

### Step 2: Upload JAR

1. In the function configuration page, go to "Code" tab
2. Click "Upload from" → ".zip or .jar file"
3. Upload `target/vehicle-storage-matcher-1.0.0-aws.jar`
4. Wait for upload to complete

### Step 3: Configure Handler

1. Go to "Runtime settings"
2. Click "Edit"
3. Set **Handler**: `com.vehiclesearch.lambda.StreamLambdaHandler::handleRequest`
4. Save

### Step 4: Configure Memory and Timeout

1. Go to "Configuration" → "General configuration"
2. Click "Edit"
3. Set:
   - **Memory**: 512 MB (minimum recommended)
   - **Timeout**: 30 seconds
4. Save

### Step 5: Create API Gateway

1. Go to "Configuration" → "Triggers"
2. Click "Add trigger"
3. Select "API Gateway"
4. Configure:
   - **API type**: REST API
   - **Security**: Open (or configure as needed)
5. Click "Add"

### Step 6: Configure API Gateway Routes

1. Click on the API Gateway trigger to open the API
2. Go to "Resources"
3. Create resource: `/api`
4. Under `/api`, create resource: `/storage`
5. Under `/storage`, create resource: `/match`
6. Create method: POST
   - **Integration type**: Lambda Function
   - **Lambda Function**: Select your function
   - **Use Lambda Proxy integration**: Checked
7. Deploy API to "prod" stage

### Step 7: Test the Deployment

Get your API endpoint URL from the API Gateway console and test:

```bash
curl -X POST https://YOUR-API-ID.execute-api.REGION.amazonaws.com/prod/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[{"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0}]'
```

## Deployment Option 3: AWS CLI

### 1. Create Lambda Function

```bash
aws lambda create-function \
  --function-name vehicle-storage-matcher \
  --runtime java17 \
  --role arn:aws:iam::YOUR-ACCOUNT-ID:role/lambda-execution-role \
  --handler com.vehiclesearch.lambda.StreamLambdaHandler::handleRequest \
  --zip-file fileb://target/vehicle-storage-matcher-1.0.0-aws.jar \
  --timeout 30 \
  --memory-size 512
```

### 2. Update Function Code (for subsequent deployments)

```bash
aws lambda update-function-code \
  --function-name vehicle-storage-matcher \
  --zip-file fileb://target/vehicle-storage-matcher-1.0.0-aws.jar
```

### 3. Create API Gateway

```bash
# Create REST API
aws apigateway create-rest-api \
  --name vehicle-storage-matcher-api \
  --endpoint-configuration types=REGIONAL

# Get API ID and Root Resource ID from the output above
# Then create resources and methods following AWS API Gateway documentation
```

## Testing the Deployed Function

### Test with curl

```bash
# Test successful matching
curl -X POST https://YOUR-API-ENDPOINT/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[
    {"id":"v1","type":"sedan","length":15.0,"width":6.0,"height":5.0},
    {"id":"v2","type":"suv","length":17.0,"width":7.0,"height":6.0}
  ]'

# Test health endpoint
curl https://YOUR-API-ENDPOINT/api/storage/health

# Test error handling (empty array)
curl -X POST https://YOUR-API-ENDPOINT/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[]'

# Test with oversized vehicle
curl -X POST https://YOUR-API-ENDPOINT/api/storage/match \
  -H "Content-Type: application/json" \
  -d '[{"id":"huge","type":"truck","length":50.0,"width":20.0,"height":15.0}]'
```

### Test with Postman

1. Import the following request:
   - **Method**: POST
   - **URL**: `https://YOUR-API-ENDPOINT/api/storage/match`
   - **Headers**: `Content-Type: application/json`
   - **Body (raw JSON)**:
   ```json
   [
     {
       "id": "vehicle-001",
       "type": "sedan",
       "length": 15.0,
       "width": 6.0,
       "height": 5.0
     }
   ]
   ```

## Monitoring and Logs

### View Lambda Logs

```bash
# Using AWS CLI
aws logs tail /aws/lambda/vehicle-storage-matcher --follow

# Or use CloudWatch Logs in AWS Console
```

### Monitor Performance

1. Go to Lambda function in AWS Console
2. Check "Monitor" tab for:
   - Invocations
   - Duration
   - Errors
   - Throttles
   - Cold starts

## Cost Estimation

Based on typical usage:
- **Lambda**: $0.20 per 1M requests + $0.0000166667 per GB-second
- **API Gateway**: $3.50 per million requests
- **CloudWatch Logs**: $0.50 per GB ingested

Example monthly cost for 10,000 requests:
- Lambda: ~$0.03
- API Gateway: ~$0.04
- Total: **~$0.07/month**

## Troubleshooting

### Cold Start Issues

If experiencing long cold starts:
1. Increase memory to 1024 MB
2. Consider using provisioned concurrency
3. Monitor cold start duration in CloudWatch

### Timeout Issues

If requests timeout:
1. Increase Lambda timeout to 60 seconds
2. Check CloudWatch logs for errors
3. Verify listings.json is loading correctly

### Permission Issues

If Lambda can't access resources:
1. Check IAM role has necessary permissions
2. Add CloudWatch Logs write permissions
3. Verify VPC configuration if using private resources

## Security Best Practices

1. **API Gateway**: Add API key requirement or AWS IAM authentication
2. **Lambda**: Use least-privilege IAM roles
3. **VPC**: Deploy in private subnet if accessing private resources
4. **Encryption**: Enable encryption at rest for any data storage
5. **Monitoring**: Enable AWS CloudTrail for audit logging

## Cleanup

To remove all resources:

### Using SAM
```bash
sam delete --stack-name vehicle-storage-matcher
```

### Using AWS Console
1. Delete API Gateway API
2. Delete Lambda function
3. Delete IAM roles
4. Delete CloudWatch log groups

### Using AWS CLI
```bash
aws lambda delete-function --function-name vehicle-storage-matcher
aws apigateway delete-rest-api --rest-api-id YOUR-API-ID
```
