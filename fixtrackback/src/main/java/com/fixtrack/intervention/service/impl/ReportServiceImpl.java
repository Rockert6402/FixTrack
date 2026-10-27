package com.fixtrack.intervention.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.common.BusinessRuleException;
import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.intervention.dto.report.MaintenanceReport;
import com.fixtrack.intervention.dto.report.OrderProfit;
import com.fixtrack.intervention.dto.report.ProfitReport;
import com.fixtrack.intervention.dto.report.ProfitTotals;
import com.fixtrack.intervention.dto.report.ReportFile;
import com.fixtrack.intervention.dto.report.StatusCount;
import com.fixtrack.intervention.dto.report.TechnicianCost;
import com.fixtrack.intervention.dto.report.TechnicianMaintenance;
import com.fixtrack.intervention.dto.report.TechnicianProfit;
import com.fixtrack.intervention.entity.InterventionStatus;
import com.fixtrack.intervention.mapper.InterventionCode;
import com.fixtrack.intervention.repository.InterventionRepository;
import com.fixtrack.intervention.repository.InterventionSparePartRepository;
import com.fixtrack.intervention.service.ReportService;
import com.fixtrack.person.entity.Role;

import lombok.RequiredArgsConstructor;

// Informes de mantenimientos y de ganancias: los totales salen de consultas agregadas en la base de datos
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

	private static final long MAX_DAYS = 366;
	private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);
	private static final Set<InterventionStatus> IN_PROGRESS = EnumSet.of(InterventionStatus.RECEIVED,
			InterventionStatus.IN_DIAGNOSIS, InterventionStatus.WAITING_PARTS, InterventionStatus.IN_REPAIR);

	private final InterventionRepository interventionRepository;
	private final InterventionSparePartRepository usageRepository;
	private final ProfitExcelExporter profitExcelExporter;

	// Órdenes recibidas en el rango: todos los estados (con 0 si no hay) y el reparto por técnico
	@Override
	@Transactional(readOnly = true)
	public MaintenanceReport maintenance(LocalDate from, LocalDate to) {
		validateRange(from, to);
		LocalDateTime start = from.atStartOfDay();
		LocalDateTime end = to.plusDays(1).atStartOfDay();
		Map<InterventionStatus, Long> counts = interventionRepository.countByStatusReceivedBetween(start, end).stream()
				.collect(Collectors.toMap(StatusCount::status, StatusCount::count));
		List<StatusCount> byStatus = Arrays.stream(InterventionStatus.values())
				.map(status -> new StatusCount(status, counts.getOrDefault(status, 0L)))
				.toList();
		List<TechnicianMaintenance> byTechnician = interventionRepository.countByTechnicianReceivedBetween(start, end,
				IN_PROGRESS, InterventionStatus.READY, InterventionStatus.DELIVERED, InterventionStatus.CANCELLED)
				.stream()
				.map(row -> row.technicianName() != null ? row
						: new TechnicianMaintenance(null, "Sin asignar", row.total(), row.inProgress(), row.ready(),
								row.delivered(), row.cancelled(), row.warranties()))
				.toList();
		long total = byStatus.stream().mapToLong(StatusCount::count).sum();
		long warranties = byTechnician.stream().mapToLong(TechnicianMaintenance::warranties).sum();
		return new MaintenanceReport(from, to, total, warranties, byStatus, byTechnician);
	}

	// Ganancia = cobrado − costo de repuestos de las órdenes entregadas; las garantías cobran 0 y su costo resta
	@Override
	@Transactional(readOnly = true)
	public ProfitReport profit(LocalDate from, LocalDate to, Long technicianId, AuthenticatedUser actor) {
		validateRange(from, to);
		Long scope = actor.role() == Role.TECHNICIAN ? actor.id() : technicianId;
		LocalDateTime start = from.atStartOfDay();
		LocalDateTime end = to.plusDays(1).atStartOfDay();

		// 1. Cobrado y costo por técnico, cada uno con su propia consulta agregada (sumar en una sola duplicaría filas)
		Map<Long, TechnicianCost> costs = new HashMap<>();
		usageRepository.sumCostByTechnician(start, end, scope, InterventionStatus.DELIVERED)
				.forEach(cost -> costs.put(cost.technicianId(), cost));
		List<TechnicianProfit> byTechnician = interventionRepository
				.sumRevenueByTechnician(start, end, scope, InterventionStatus.DELIVERED).stream()
				.map(revenue -> {
					TechnicianCost cost = costs.get(revenue.technicianId());
					BigDecimal partsCost = cost == null ? ZERO : money(cost.partsCost());
					BigDecimal warrantyCost = cost == null ? ZERO : money(cost.warrantyCost());
					BigDecimal income = money(revenue.revenue());
					return new TechnicianProfit(revenue.technicianId(),
							revenue.technicianName() == null ? "Sin técnico" : revenue.technicianName(),
							revenue.orders(), revenue.warranties(), income, partsCost, warrantyCost,
							income.subtract(partsCost));
				})
				.toList();

		// 2. Totales a partir de los agregados por técnico
		ProfitTotals totals = new ProfitTotals(
				byTechnician.stream().mapToLong(TechnicianProfit::orders).sum(),
				byTechnician.stream().mapToLong(TechnicianProfit::warranties).sum(),
				sum(byTechnician.stream().map(TechnicianProfit::revenue).toList()),
				sum(byTechnician.stream().map(TechnicianProfit::partsCost).toList()),
				sum(byTechnician.stream().map(TechnicianProfit::warrantyCost).toList()),
				sum(byTechnician.stream().map(TechnicianProfit::profit).toList()));

		// 3. Detalle por orden
		List<OrderProfit> orders = interventionRepository
				.findProfitRows(start, end, scope, InterventionStatus.DELIVERED).stream()
				.map(row -> {
					boolean warranty = row.warrantyOfId() != null;
					String code = warranty ? InterventionCode.format(row.warrantyOfId()) + "-G" + row.warrantyNumber()
							: InterventionCode.format(row.id());
					BigDecimal revenue = money(row.revenue());
					BigDecimal partsCost = money(row.partsCost());
					return new OrderProfit(row.id(), code, row.deliveredAt(), row.customerName(), row.technicianId(),
							row.technicianName() == null ? "Sin técnico" : row.technicianName(), warranty, revenue,
							partsCost, revenue.subtract(partsCost));
				})
				.toList();
		return new ProfitReport(from, to, scope, totals, byTechnician, orders);
	}

	@Override
	@Transactional(readOnly = true)
	public ReportFile exportProfit(LocalDate from, LocalDate to, Long technicianId, AuthenticatedUser actor) {
		ProfitReport report = profit(from, to, technicianId, actor);
		return new ReportFile("ganancias-" + from + "-a-" + to + ".xlsx", profitExcelExporter.export(report));
	}

	private void validateRange(LocalDate from, LocalDate to) {
		if (from.isAfter(to)) {
			throw new BusinessRuleException("La fecha inicial no puede ser posterior a la final");
		}
		if (ChronoUnit.DAYS.between(from, to) >= MAX_DAYS) {
			throw new BusinessRuleException("El rango del informe puede ser de máximo " + MAX_DAYS + " días");
		}
	}

	private BigDecimal sum(List<BigDecimal> values) {
		return values.stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal money(BigDecimal value) {
		return value == null ? ZERO : value.setScale(2, RoundingMode.HALF_UP);
	}
}
