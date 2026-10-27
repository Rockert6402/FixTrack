package com.fixtrack.intervention.dto.dashboard;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fixtrack.intervention.dto.InterventionSummaryResponse;
import com.fixtrack.inventory.dto.PartRequestResponse;
import com.fixtrack.person.entity.Role;

// Asesor y coordinador; stockAlerts y las solicitudes de repuestos solo llegan al coordinador
public record FrontDeskDashboard(
		Role role,
		FrontDeskCounters counters,
		List<InterventionSummaryResponse> recentOrders,
		List<TechnicianStatusResponse> technicians,
		@JsonInclude(JsonInclude.Include.NON_NULL) List<StockAlertResponse> stockAlerts,
		@JsonInclude(JsonInclude.Include.NON_NULL) Long pendingPartRequestCount,
		@JsonInclude(JsonInclude.Include.NON_NULL) List<PartRequestResponse> pendingPartRequests) implements DashboardResponse {

	public record FrontDeskCounters(long ready, long receivedToday, long overdue, long inProgress, long unclaimed) {
	}
}
