# Identity REST API

The API accepts and returns JSON. Validation failures and application/domain errors use a common body with `status`, `error`, `message`, `path`, and `timestamp`.

## Endpoints

| Method and path | Purpose | Success |
|---|---|---|
| `POST /identity/users/register` | Create a user identity. | `201 Created` |
| `POST /auth/login` | Verify credentials and issue an access token. | `200 OK` |
| `GET /.well-known/jwks.json` | Publish RSA public verification keys. | `200 OK` |

## Register

```json
{
  "name": "Alex Guedes",
  "email": "alex@example.com",
  "password": "StrongPass123"
}
```

```json
{
  "id": "c84d9f2d-5349-4f4b-b9a3-0cbb2c75be89",
  "name": "Alex Guedes",
  "email": "alex@example.com",
  "status": "ACTIVE",
  "createdAt": "2026-07-11T18:00:00Z"
}
```

Validation requires a nonblank name, valid email, and password of 8–72 characters containing at least one letter and one number. Statuses: 201, 400, 409, and 415. More detail: [registration endpoint](register-user-endpoint.md).

## Login

```json
{
  "email": "alex@example.com",
  "password": "StrongPass123"
}
```

```json
{
  "userId": "c84d9f2d-5349-4f4b-b9a3-0cbb2c75be89",
  "email": "alex@example.com",
  "status": "ACTIVE",
  "accessToken": "eyJhbGciOiJSUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresAt": "2026-07-11T18:15:00Z"
}
```

Validation requires a nonblank, valid email and a nonblank password. Statuses: 200, 400, 401, and 415.

## JWKS

```json
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "local-dev-key",
      "use": "sig",
      "alg": "RS256",
      "n": "shortened-modulus...",
      "e": "AQAB"
    }
  ]
}
```

The endpoint exposes public material only. See [JWT and JWKS](jwt-and-jwks.md).

## Error example

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication failed",
  "path": "/auth/login",
  "timestamp": "2026-07-11T18:00:00Z"
}
```

Malformed JSON returns 400, unsupported media type returns 415, duplicate email returns 409, and invalid credentials return 401.
