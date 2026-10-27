package com.fixtrack.common;

import org.springframework.http.HttpStatus;

// 400: la petición incumple una regla de negocio (por ejemplo, IMEI faltante en un teléfono)
public class BusinessRuleException extends BusinessException {

	public BusinessRuleException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}
}
