package com.fixtrack.intervention.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.infra.security.CurrentUserProvider;
import com.fixtrack.intervention.dto.dashboard.DashboardResponse;
import com.fixtrack.intervention.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReportController {

	private final DashboardService dashboardService;
	private final CurrentUserProvider currentUserProvider;

	// El contenido depende del rol de quien consulta
	@GetMapping("/api/dashboard")
	@PreAuthorize("isAuthenticated()")
	public DashboardResponse dashboard() {
		return dashboardService.forUser(currentUserProvider.require());
	}
}
