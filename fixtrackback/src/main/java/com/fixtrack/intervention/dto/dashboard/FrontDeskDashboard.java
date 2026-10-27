package com.fixtrack.intervention.dto.dashboard;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fixtrack.intervention.dto.InterventionSummaryResponse;
import com.fixtrack.person.entity.Role;

// Asesor y coordinador; stockAlerts solo llega al coordinador
public record FrontDeskDashboard(
		Role role,
		FrontDeskCounters counters,
		List<InterventionSummaryResponse> recentOrders,
		List<TechnicianStatusResponse> technicians,
		@JsonInclude(JsonInclude.Include.NON_NULL) List<StockAlertResponse> stockAlerts) implements DashboardResponse {

	public record FrontDeskCounters(long ready, long receivedToday, long overdue, long inProgress, long unclaimed) {
	}
}
