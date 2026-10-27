package com.fixtrack.cash.dto;

import java.math.BigDecimal;

import com.fixtrack.cash.entity.PaymentMethod;

// Neto por medio de pago (devoluciones restando), cuántos pagos y cuántos son de días anteriores
public record MethodTotal(PaymentMethod method, BigDecimal amount, Long payments, Long fromPreviousDays) {
}
