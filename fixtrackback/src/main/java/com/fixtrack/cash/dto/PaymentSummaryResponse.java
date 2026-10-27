package com.fixtrack.cash.dto;

import java.math.BigDecimal;
import java.util.List;

// Pagos de la orden: total a pagar, lo pagado neto (abonos + cobros − devoluciones), saldo y saldo a favor del cliente
public record PaymentSummaryResponse(
		BigDecimal total,
		BigDecimal paid,
		BigDecimal balance,
		BigDecimal refundDue,
		List<PaymentResponse> payments) {
}
