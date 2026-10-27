package com.fixtrack.inventory.dto;

import java.math.BigDecimal;

public record SuggestedSupplierResponse(Long id, String name, BigDecimal lastCost, Integer leadTimeDays) {
}
