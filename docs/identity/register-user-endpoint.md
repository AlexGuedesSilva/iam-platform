# Register user endpoint

`POST /identity/users/register` creates a new identity. The required content type is `application/json`.

## Request

| Field | Rules |
|---|---|
| `name` | Required and nonblank. |
| `email` | Required, nonblank, and valid email format. |
| `password` | Required, 8–72 characters, with at least one letter and one number. |

```json
{
  "name": "Alex Guedes",
  "email": "alex@example.com",
  "password": "StrongPass123"
}
```

## Success

`201 Created`

```json
{
  "id": "c84d9f2d-5349-4f4b-b9a3-0cbb2c75be89",
  "name": "Alex Guedes",
  "email": "alex@example.com",
  "status": "ACTIVE",
  "createdAt": "2026-07-11T18:00:00Z"
}
```

## Errors

| Status | Condition |
|---|---|
| `400 Bad Request` | Validation failure, malformed JSON, or invalid domain input. |
| `409 Conflict` | Email already belongs to a user. |
| `415 Unsupported Media Type` | Content type is unsupported. |

Errors contain `status`, `error`, `message`, `path`, and `timestamp`.

```bash
curl -i -X POST "http://localhost:8080/identity/users/register" \
  -H "Content-Type: application/json" \
  -d '{"name":"Alex Guedes","email":"alex@example.com","password":"StrongPass123"}'
```

See [registration flow](register-user-flow.md) and the complete [REST API](rest-api.md).
