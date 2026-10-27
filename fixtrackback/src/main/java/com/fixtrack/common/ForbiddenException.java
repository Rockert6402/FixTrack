package com.fixtrack.common;

import org.springframework.http.HttpStatus;

// 403: el usuario no puede actuar sobre este registro (por ejemplo, una orden de otro técnico)
public class ForbiddenException extends BusinessException {

	public ForbiddenException(String message) {
		super(HttpStatus.FORBIDDEN, message);
	}
}
