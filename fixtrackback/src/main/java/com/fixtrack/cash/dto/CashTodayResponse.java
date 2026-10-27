package com.fixtrack.cash.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Lo esperado del día por medio de pago (pagos sin cerrar, devoluciones restando); closing llega si ya cerró hoy
public record CashTodayResponse(
		LocalDate date,
		BigDecimal expectedCash,
		BigDecimal expectedTransfer,
		BigDecimal expectedCard,
		BigDecimal expectedTotal,
		long payments,
		long fromPreviousDays,
		CashClosingResponse closing) {
}
