package com.fixtrack.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

// Base de las excepciones de negocio; cada una define el código HTTP con que responde la API
@Getter
public abstract class BusinessException extends RuntimeException {

	private final HttpStatus status;

	protected BusinessException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}
}
