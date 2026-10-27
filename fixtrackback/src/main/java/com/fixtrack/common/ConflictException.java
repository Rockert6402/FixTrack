package com.fixtrack.common;

import org.springframework.http.HttpStatus;

// 409: choca con el estado actual de los datos (duplicado, transición de estado no permitida)
public class ConflictException extends BusinessException {

	public ConflictException(String message) {
		super(HttpStatus.CONFLICT, message);
	}
}
