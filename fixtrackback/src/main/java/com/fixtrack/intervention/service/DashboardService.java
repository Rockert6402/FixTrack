package com.fixtrack.intervention.service;

import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.intervention.dto.dashboard.DashboardResponse;

public interface DashboardService {

	DashboardResponse forUser(AuthenticatedUser user);
}
