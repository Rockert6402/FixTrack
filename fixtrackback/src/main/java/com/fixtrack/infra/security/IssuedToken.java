package com.fixtrack.infra.security;

import java.time.Instant;

public record IssuedToken(String token, Instant expiresAt) {
}
