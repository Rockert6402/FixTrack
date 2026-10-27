package com.fixtrack.intervention.dto;

import java.time.LocalDateTime;

// Orden lista sin reclamar; noContact marca «Sin contacto» cuando el cliente no tiene celular ni correo
public record UnclaimedOrderResponse(
		Long id,
		String code,
		String customerName,
		String customerDocument,
		String device,
		String phone,
		String email,
		boolean noContact,
		LocalDateTime readyAt,
		long daysWaiting) {
}
