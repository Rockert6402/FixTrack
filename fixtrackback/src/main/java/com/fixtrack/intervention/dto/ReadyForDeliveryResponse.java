package com.fixtrack.intervention.dto;

import java.math.BigDecimal;

// Orden lista para entregar con lo que debe el cliente: total, abonado, saldo y saldo a favor
public record ReadyForDeliveryResponse(InterventionSummaryResponse order, BigDecimal total, BigDecimal paid,
		BigDecimal balance, BigDecimal refundDue) {
}
