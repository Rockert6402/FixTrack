package com.fixtrack.cash.dto;

import java.math.BigDecimal;

public record PaidAmount(Long interventionId, BigDecimal amount) {
}
