# Register User Endpoint

Registers a new identity user.

## Endpoint

| Item | Value |
| --- | --- |
| Method | `POST` |
| URL | `/identity/users/register` |
| Required `Content-Type` | `application/json` |

## Request Body

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `name` | string | Yes | User display name. Must not be blank. |
| `email` | string | Yes | User email address. Must not be blank and must use a valid email format. |
| `password` | string | Yes | User password. Must not be blank, must be 8 to 72 characters, and must contain at least one letter and one number. |

### Example Request

```json
{
  "name": "Alex Guedes",
  "email": "alex@example.com",
  "password": "Senha123456"
}
```

## Successful Response

`201 Created` is returned when the user is registered successfully.

The response body uses the current `RegisterUserResponse` structure:

| Field | Type | Description |
| --- | --- | --- |
| `id` | string | Registered user identifier. |
| `name` | string | Registered user name. |
| `email` | string | Registered user email address. |
| `status` | string | Registered user status, for example `ACTIVE`. |
| `createdAt` | string | User creation timestamp in ISO-8601 format. |

### Example Successful Response

```json
{
  "id": "b7c3a9e2-7b48-4c4d-9a73-7d7f1d8a9c10",
  "name": "Alex Guedes",
  "email": "alex@example.com",
  "status": "ACTIVE",
  "createdAt": "2026-06-30T20:30:00Z"
}
```

## Error Responses

Error responses use the current `ErrorResponse` structure:

| Field | Type | Description |
| --- | --- | --- |
| `status` | number | HTTP status code. |
| `error` | string | HTTP reason phrase. |
| `message` | string | Error message. |
| `path` | string | Request path. |
| `timestamp` | string | Error timestamp in ISO-8601 format. |

| Status | When it happens | Example message |
| --- | --- | --- |
| `400 Bad Request` | Validation error for invalid `name`, `email`, or `password`. | `Email format is invalid` |
| `400 Bad Request` | Malformed JSON or empty request body. | `Request body is invalid or malformed` |
| `409 Conflict` | A user with the requested email already exists. | `User already exists` or `Email is already registered` |
| `415 Unsupported Media Type` | Request `Content-Type` is not supported. | `Content type is not supported` |

### Example Validation Error

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email format is invalid",
  "path": "/identity/users/register",
  "timestamp": "2026-06-30T20:30:00Z"
}
```

### Example Malformed Or Empty Body Error

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Request body is invalid or malformed",
  "path": "/identity/users/register",
  "timestamp": "2026-06-30T20:30:00Z"
}
```

### Example Existing Email Conflict

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "User already exists",
  "path": "/identity/users/register",
  "timestamp": "2026-06-30T20:30:00Z"
}
```

### Example Unsupported Media Type Error

```json
{
  "status": 415,
  "error": "Unsupported Media Type",
  "message": "Content type is not supported",
  "path": "/identity/users/register",
  "timestamp": "2026-06-30T20:30:00Z"
}
```

## Curl Examples

### Successful Registration

```bash
curl -i -X POST "http://localhost:8080/identity/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alex Guedes",
    "email": "alex@example.com",
    "password": "Senha123456"
  }'
```

### Invalid Request

```bash
curl -i -X POST "http://localhost:8080/identity/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alex Guedes",
    "email": "invalid-email",
    "password": "Senha123456"
  }'
```

### Existing Email Conflict

```bash
curl -i -X POST "http://localhost:8080/identity/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alex Guedes",
    "email": "alex@example.com",
    "password": "Senha123456"
  }'
```
