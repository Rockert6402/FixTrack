package com.fixtrack.common;

import org.springframework.http.HttpStatus;

// 401: credenciales o clave de consulta inválidas
public class UnauthorizedException extends BusinessException {

	public UnauthorizedException(String message) {
		super(HttpStatus.UNAUTHORIZED, message);
	}
}
