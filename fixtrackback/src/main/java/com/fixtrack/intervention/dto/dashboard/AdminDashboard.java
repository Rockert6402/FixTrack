package com.fixtrack.intervention.dto.dashboard;

import java.util.List;

import com.fixtrack.intervention.dto.InterventionSummaryResponse;
import com.fixtrack.person.entity.Role;

public record AdminDashboard(
		Role role,
		AdminCounters counters,
		List<InterventionSummaryResponse> recentOrders,
		List<StockAlertResponse> stockAlerts) implements DashboardResponse {

	public record AdminCounters(long receivedToday, long overdue, long inProgress, long ready) {
	}
}
