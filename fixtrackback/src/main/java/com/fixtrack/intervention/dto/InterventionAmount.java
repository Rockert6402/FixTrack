package com.fixtrack.intervention.dto;

import java.math.BigDecimal;

public record InterventionAmount(Long interventionId, BigDecimal amount) {
}
