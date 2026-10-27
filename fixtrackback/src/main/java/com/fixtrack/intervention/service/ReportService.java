package com.fixtrack.intervention.service;

import java.time.LocalDate;

import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.intervention.dto.report.MaintenanceReport;
import com.fixtrack.intervention.dto.report.ProfitReport;
import com.fixtrack.intervention.dto.report.ReportFile;

public interface ReportService {

	MaintenanceReport maintenance(LocalDate from, LocalDate to);

	// El técnico solo obtiene sus propias órdenes; el administrador, todas o las de un técnico
	ProfitReport profit(LocalDate from, LocalDate to, Long technicianId, AuthenticatedUser actor);

	ReportFile exportProfit(LocalDate from, LocalDate to, Long technicianId, AuthenticatedUser actor);
}
