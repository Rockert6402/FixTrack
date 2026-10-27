package com.fixtrack.common;

import org.springframework.http.HttpStatus;

// 429: demasiados intentos en poco tiempo (por ejemplo, consultas fallidas del portal desde una misma IP)
public class TooManyRequestsException extends BusinessException {

	public TooManyRequestsException(String message) {
		super(HttpStatus.TOO_MANY_REQUESTS, message);
	}
}
