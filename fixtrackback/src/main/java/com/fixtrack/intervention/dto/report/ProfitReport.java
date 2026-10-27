package com.fixtrack.intervention.dto.report;

import java.time.LocalDate;
import java.util.List;

// Órdenes entregadas en el rango; technicianId llega cuando el informe se limita a un técnico
public record ProfitReport(LocalDate from, LocalDate to, Long technicianId, ProfitTotals totals,
		List<TechnicianProfit> byTechnician, List<OrderProfit> orders) {
}
