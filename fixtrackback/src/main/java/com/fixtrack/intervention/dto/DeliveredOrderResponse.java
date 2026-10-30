package com.fixtrack.intervention.dto;

import java.time.LocalDateTime;

// Orden entregada en la Salida del día, para volver a descargar su comprobante de entrega
public record DeliveredOrderResponse(
		Long id,
		String code,
		String customerName,
		String device,
		LocalDateTime deliveredAt) {
}
