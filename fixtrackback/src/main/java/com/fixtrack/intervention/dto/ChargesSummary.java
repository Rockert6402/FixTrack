package com.fixtrack.intervention.dto;

import java.math.BigDecimal;

// Total a pagar, lo pagado neto, el saldo pendiente y el saldo a favor del cliente
public record ChargesSummary(BigDecimal total, BigDecimal paid, BigDecimal balance, BigDecimal refundDue) {
}
