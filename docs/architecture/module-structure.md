# Module and package structure

## Maven modules

| Module | Current role |
|---|---|
| `shared-kernel` | Foundation for deliberately shared, stable concepts. |
| `iam-application` | Main executable Spring Boot application and current Identity implementation. |
| `api-gateway` | Boundary reserved for gateway and edge concerns. |

The root `pom.xml` has `pom` packaging. Run Spring Boot against `iam-application`, not the parent project.

## Identity package organization

```text
com.alexguedes.iam.identity
├── domain
│   ├── model
│   ├── valueobject
│   └── exception
├── application
│   ├── usecase
│   │   ├── registration
│   │   └── authentication
│   ├── port
│   └── exception
├── infrastructure
│   ├── config
│   ├── identity
│   ├── persistence
│   └── security
└── interfaces
    └── rest
```

The organization makes architectural ownership visible. Domain packages contain business language; application packages contain orchestration and contracts; infrastructure packages contain adapters; REST packages contain transport concerns.

## Modular monolith evolution

The current system is deployed as one application. Future service extraction is a roadmap option, not a current property. A capability should become a service only with a clear reason such as independent scaling, ownership, release cadence, or isolation. Ports and explicit packages reduce coupling ahead of that decision.
