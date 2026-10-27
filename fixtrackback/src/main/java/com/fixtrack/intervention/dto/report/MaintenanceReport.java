package com.fixtrack.intervention.dto.report;

import java.time.LocalDate;
import java.util.List;

// Órdenes recibidas en el rango, con totales por estado (todos, aunque sean 0) y por técnico
public record MaintenanceReport(LocalDate from, LocalDate to, long totalOrders, long warranties,
		List<StatusCount> byStatus, List<TechnicianMaintenance> byTechnician) {
}
