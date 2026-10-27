package com.fixtrack.common;

import org.springframework.http.HttpStatus;

// 404: el registro buscado no existe
public class NotFoundException extends BusinessException {

	public NotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
}
