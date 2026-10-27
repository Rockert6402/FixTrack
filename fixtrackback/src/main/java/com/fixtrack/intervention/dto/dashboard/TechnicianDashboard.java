package com.fixtrack.intervention.dto.dashboard;

import java.util.List;

import com.fixtrack.intervention.dto.InterventionSummaryResponse;
import com.fixtrack.person.entity.Role;

public record TechnicianDashboard(
		Role role,
		TechnicianCounters counters,
		List<InterventionSummaryResponse> openOrders) implements DashboardResponse {

	public record TechnicianCounters(long assigned, long overdue, long inProgress, long waitingParts) {
	}
}
