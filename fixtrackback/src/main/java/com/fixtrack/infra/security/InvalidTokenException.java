package com.fixtrack.infra.security;

import org.springframework.security.core.AuthenticationException;

// 401: el token no es válido, expiró o su usuario ya no puede iniciar sesión
public class InvalidTokenException extends AuthenticationException {

	public InvalidTokenException(String message) {
		super(message);
	}
}
