# Architecture and flow diagrams

The existing [architecture image](iam-platform-architecture-dark.png) is a conceptual target view and includes roadmap capabilities. It must not be read as a list of fully implemented features. The Mermaid diagrams below describe the current implementation.

## High-level architecture

```mermaid
flowchart LR
    REST["REST API"] --> UC["Application use cases"]
    UC --> Domain["Identity domain"]
    UC --> Ports["Outbound ports"]
    Adapters["Infrastructure adapters"] --> Ports
    Adapters --> DB[("PostgreSQL")]
    Adapters --> Keys["RSA keys"]
```

## Registration flow

```mermaid
sequenceDiagram
    actor Client
    participant Controller as UserRegistrationController
    participant UseCase as RegisterUserUseCase
    participant Repository as UserRepository
    participant Hasher as PasswordHasher
    participant Generator as UserIdGenerator
    participant DB as PostgreSQL
    Client->>Controller: POST /identity/users/register
    Controller->>UseCase: RegisterUserCommand
    UseCase->>Repository: findByEmail
    Repository->>DB: query
    UseCase->>Hasher: hash(raw password)
    UseCase->>Generator: generate()
    UseCase->>Repository: save(User)
    Repository->>DB: insert user
    UseCase-->>Controller: RegisterUserResult
    Controller-->>Client: 201 Created
```

## Login flow

```mermaid
sequenceDiagram
    actor Client
    participant Controller as LoginController
    participant UseCase as LoginUserUseCase
    participant Repository as UserRepository
    participant Hasher as PasswordHasher
    participant Issuer as AccessTokenIssuer
    Client->>Controller: POST /auth/login
    Controller->>UseCase: LoginUserCommand
    UseCase->>Repository: findByEmail
    UseCase->>Hasher: matches(raw, hash)
    UseCase->>Issuer: issue(claims)
    Issuer-->>UseCase: signed access token
    UseCase-->>Controller: LoginUserResult
    Controller-->>Client: 200 JWT response
```

## JWT and JWKS flow

```mermaid
flowchart LR
    Private["Private RSA key"] --> Issuer["Rs256JwtAccessTokenIssuer"]
    Issuer --> JWT["JWT signed with RS256"]
    Public["Public RSA key"] --> Provider["Rs256JwkProvider"]
    Provider --> Endpoint["GET /.well-known/jwks.json"]
    JWT -. "kid selects key" .-> Endpoint
```

## Persistence mapping

```mermaid
flowchart LR
    Domain["User domain entity"] <--> Mapper["UserPersistenceMapper"]
    Mapper <--> Entity["UserJpaEntity"]
    Entity <--> Repository["SpringDataUserRepository"]
    Repository <--> DB[("PostgreSQL")]
```

## Package and layer dependencies

```mermaid
flowchart TD
    Interfaces["interfaces/rest"] --> Application["application"]
    Application --> Domain["domain"]
    Infrastructure["infrastructure"] --> Application
    Infrastructure --> Domain
    Domain -. "no outward framework dependency" .-> Domain
```

## Modular monolith deployment

```mermaid
flowchart TB
    Parent["Maven parent (pom)"] --> Shared["shared-kernel"]
    Parent --> App["iam-application (executable)"]
    Parent --> Gateway["api-gateway"]
    App --> Identity["Identity capability"]
    App --> PostgreSQL[("PostgreSQL 16")]
```
