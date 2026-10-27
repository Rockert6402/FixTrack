package com.fixtrack.intervention.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.common.NotFoundException;
import com.fixtrack.intervention.dto.InterventionAmount;
import com.fixtrack.intervention.entity.ApprovalStatus;
import com.fixtrack.intervention.entity.FindingOrigin;
import com.fixtrack.intervention.entity.Intervention;
import com.fixtrack.intervention.repository.InterventionFindingRepository;
import com.fixtrack.intervention.repository.InterventionRepository;
import com.fixtrack.intervention.repository.InterventionSparePartRepository;
import com.fixtrack.intervention.service.InterventionPricingService;

import lombok.RequiredArgsConstructor;

// Total de la orden y aprobación automática de los daños adicionales que caben en el presupuesto
@Service
@RequiredArgsConstructor
public class InterventionPricingServiceImpl implements InterventionPricingService {

	private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2);

	private final InterventionRepository interventionRepository;
	private final InterventionSparePartRepository usageRepository;
	private final InterventionFindingRepository findingRepository;

	@Override
	@Transactional(readOnly = true)
	public Intervention getEntity(Long id) {
		return interventionRepository.findDetailById(id)
				.orElseThrow(() -> new NotFoundException("No existe la orden con id " + id));
	}

	// Total = mano de obra + cantidad × precio de venta guardado de cada repuesto usado
	@Override
	@Transactional(readOnly = true)
	public BigDecimal calculateTotal(Intervention intervention) {
		BigDecimal parts = usageRepository.sumPartsTotal(intervention.getId());
		return intervention.getLaborCost()
				.add(parts == null ? BigDecimal.ZERO : parts)
				.setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<Long, BigDecimal> calculateTotals(List<Intervention> interventions) {
		if (interventions.isEmpty()) {
			return Map.of();
		}
		Map<Long, BigDecimal> parts = usageRepository
				.sumPartsTotals(interventions.stream().map(Intervention::getId).toList()).stream()
				.collect(Collectors.toMap(InterventionAmount::interventionId, InterventionAmount::amount));
		return interventions.stream().collect(Collectors.toMap(Intervention::getId,
				intervention -> intervention.getLaborCost()
						.add(parts.getOrDefault(intervention.getId(), BigDecimal.ZERO))
						.setScale(2, RoundingMode.HALF_UP)));
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal amountDue(Intervention intervention) {
		return switch (intervention.getStatus()) {
			case CANCELLED -> ZERO;
			case DELIVERED -> intervention.getTotalPrice() != null ? intervention.getTotalPrice()
					: calculateTotal(intervention);
			default -> calculateTotal(intervention);
		};
	}

	// Si el total supera el presupuesto, los daños adicionales sin respuesta del cliente quedan pendientes; si no, aprobados
	@Override
	@Transactional
	public void reviewAdditionalFindings(Intervention intervention) {
		BigDecimal budget = intervention.getEstimatedPrice() == null ? BigDecimal.ZERO
				: intervention.getEstimatedPrice();
		ApprovalStatus status = calculateTotal(intervention).compareTo(budget) > 0 ? ApprovalStatus.PENDING
				: ApprovalStatus.APPROVED;
		findingRepository.findByInterventionIdAndOriginAndRespondedByIsNull(intervention.getId(), FindingOrigin.ADDITIONAL)
				.forEach(finding -> finding.setApprovalStatus(status));
	}
}
