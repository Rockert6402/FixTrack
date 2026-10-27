package com.fixtrack.intervention.dto.report;

import java.math.BigDecimal;

// Costo de repuestos de las órdenes entregadas de un técnico; warrantyCost es la parte que fue de garantías
public record TechnicianCost(Long technicianId, BigDecimal partsCost, BigDecimal warrantyCost) {
}
