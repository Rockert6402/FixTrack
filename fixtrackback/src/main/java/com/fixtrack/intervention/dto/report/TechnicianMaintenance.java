package com.fixtrack.intervention.dto.report;

// technicianId null agrupa las órdenes sin técnico asignado
public record TechnicianMaintenance(Long technicianId, String technicianName, Long total, Long inProgress,
		Long ready, Long delivered, Long cancelled, Long warranties) {
}
