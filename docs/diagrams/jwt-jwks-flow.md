# JWT RS256 and JWKS Flow

The IAM Platform signs access tokens with an RSA private key. Consumers and resource servers validate those tokens with the corresponding RSA public key: the private key is never exposed, while the public key is published through the platform's JSON Web Key Set (JWKS) endpoint.

```mermaid
flowchart TB
    subgraph issuance["JWT issuance - private signing path"]
        direction LR
        user["Authenticated User"] --> login["LoginUserUseCase"]
        login --> claims["AccessTokenClaims<br/>sub<br/>iss<br/>iat<br/>exp<br/>email<br/>status<br/>token_type"]
        claims --> issuer["AccessTokenIssuer"]
        issuer --> rsIssuer["Rs256JwtAccessTokenIssuer"]
        rsIssuer --> privateKey["RSA Private Key<br/>PKCS#8"]
        privateKey --> signedJwt["Signed JWT<br/>RS256"]
        signedJwt --> response["Login Response"]
        jwtHeader["JWT Header<br/>alg = RS256<br/>typ = JWT<br/>kid = configured key ID"] --> signedJwt
    end

    subgraph publication["JWKS publication - public verification material"]
        direction LR
        publicKey["RSA Public Key<br/>X.509"] --> provider["Rs256JwkProvider"]
        provider --> jwk["JWK<br/>public RSA parameters"]
        jwk --> controller["JwksController"]
        controller --> endpoint["GET /.well-known/jwks.json"]
        endpoint --> consumer["Consumer / Resource Server"]
    end

    subgraph validation["Token validation - public verification path"]
        direction LR
        receive["Consumer receives JWT"] --> readKid["Reads kid from JWT header"]
        readKid --> fetch["Fetches JWKS"]
        fetch --> match["Finds matching JWK"]
        match --> verify["Validates RS256 signature"]
        verify --> validateClaims["Validates iss and exp"]
        validateClaims --> decision{"Token valid?"}
        decision -->|Yes| accept["Accepts token"]
        decision -->|No| reject["Rejects token"]
    end

    response -. "JWT presented to consumer" .-> receive
    consumer --> receive
    fetch -. "HTTP GET" .-> endpoint
    endpoint -. "JWKS response" .-> match
    readKid -. "selects key by kid" .-> jwk

    classDef privatePath fill:#fce8e6,stroke:#b3261e,color:#601410,stroke-width:2px;
    classDef publicPath fill:#e6f4ea,stroke:#137333,color:#0d4d24,stroke-width:2px;
    classDef headerNode fill:#fff4ce,stroke:#9a6700,color:#5c3b00,stroke-width:2px;
    classDef claimsNode fill:#f3e8fd,stroke:#7b1fa2,color:#4a1261,stroke-width:2px;
    classDef endpointNode fill:#e8f0fe,stroke:#1967d2,color:#0b3d91,stroke-width:3px;

    class user,login,issuer,rsIssuer,privateKey,signedJwt,response privatePath;
    class publicKey,provider,jwk,controller,consumer,receive,readKid,fetch,match,verify,validateClaims,decision,accept,reject publicPath;
    class jwtHeader headerNode;
    class claims claimsNode;
    class endpoint endpointNode;
```

## Security notes

- The RSA private key signs access tokens and must remain confidential.
- The corresponding RSA public key validates token signatures without granting signing capability.
- JWKS must expose only public RSA parameters. Private RSA parameters must never appear in the response.
- The `kid` value links the JWT header to the correct JWK in the published key set.
- Refresh tokens and key rotation are not implemented yet.
