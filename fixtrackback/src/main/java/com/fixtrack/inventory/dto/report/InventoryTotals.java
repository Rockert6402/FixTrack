package com.fixtrack.inventory.dto.report;

import java.math.BigDecimal;

// parts: repuestos; units: unidades en stock; inAlert: en el mínimo o agotados; outOfStock: agotados
public record InventoryTotals(Long parts, Long units, BigDecimal costValue, Long inAlert, Long outOfStock) {
}
