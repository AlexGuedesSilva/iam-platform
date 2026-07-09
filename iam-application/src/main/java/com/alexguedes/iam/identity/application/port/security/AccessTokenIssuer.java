package com.alexguedes.iam.identity.application.port.security;

public interface AccessTokenIssuer {

    IssuedAccessToken issue(AccessTokenClaims claims);
}
