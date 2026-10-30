package com.fixtrack.intervention.dto;

import java.time.LocalDate;

import com.fixtrack.intervention.entity.InterventionStatus;

// Filtros combinables de la consulta de órdenes; los que llegan en null o false no filtran
public record InterventionFilter(
		String code,
		String documentNumber,
		Long customerId,
		Long technicianId,
		InterventionStatus status,
		LocalDate receivedFrom,
		LocalDate receivedTo,
		Boolean overdue,
		boolean openOnly,
		boolean inProgress,
		boolean unclaimed) {

	public static InterventionFilter ofTechnician(Long technicianId, InterventionStatus status, Boolean overdue,
			boolean inProgress) {
		return new InterventionFilter(null, null, null, technicianId, status, null, null, overdue, true, inProgress, false);
	}
}
