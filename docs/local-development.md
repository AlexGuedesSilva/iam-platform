# Local development

## Requirements

- Java 21
- Maven
- Docker Desktop or a compatible Docker runtime
- PostgreSQL 16 through the provided Docker Compose configuration
- OpenSSL

## Configuration

| Variable | Purpose | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/iam_platform` |
| `SPRING_DATASOURCE_USERNAME` | Database user | `iam_user` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `iam_password` |
| `JWT_PRIVATE_KEY` | PKCS#8 PEM signing key | Required |
| `JWT_PUBLIC_KEY` | X.509 PEM verification key | Required |
| `JWT_KEY_ID` | JWT/JWK key identifier | `local-dev-key` |

Never commit private keys or `.env` files containing secrets. Local keys are for development only.

## Generate RSA keys

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private_key.pem
openssl pkey -in private_key.pem -pubout -out public_key.pem
```

Expected formats are `-----BEGIN PRIVATE KEY-----` and `-----BEGIN PUBLIC KEY-----`.

Load them in Bash:

```bash
export JWT_PRIVATE_KEY="$(cat private_key.pem)"
export JWT_PUBLIC_KEY="$(cat public_key.pem)"
export JWT_KEY_ID="local-dev-key"
```

Or PowerShell:

```powershell
$env:JWT_PRIVATE_KEY = Get-Content ./private_key.pem -Raw
$env:JWT_PUBLIC_KEY = Get-Content ./public_key.pem -Raw
$env:JWT_KEY_ID = "local-dev-key"
```

## Run

```bash
docker compose up -d postgres
mvn -pl iam-application spring-boot:run
```

The root project uses `pom` packaging; run Spring Boot from the executable `iam-application` module as shown.

## Test

```bash
mvn -pl iam-application -am clean test
```

Docker must be available for Testcontainers. For a manual API sequence, read the [REST API](identity/rest-api.md).
