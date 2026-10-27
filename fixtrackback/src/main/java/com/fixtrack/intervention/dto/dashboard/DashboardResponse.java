package com.fixtrack.intervention.dto.dashboard;

import com.fixtrack.person.entity.Role;

// Panel de Inicio: cada rol recibe su propia forma, identificada por role
public sealed interface DashboardResponse permits AdminDashboard, FrontDeskDashboard, TechnicianDashboard {

	Role role();
}
