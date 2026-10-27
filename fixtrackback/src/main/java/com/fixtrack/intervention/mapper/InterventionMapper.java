package com.fixtrack.intervention.mapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fixtrack.device.entity.Device;
import com.fixtrack.intervention.dto.ChargesSummary;
import com.fixtrack.intervention.dto.CustomerSummary;
import com.fixtrack.intervention.dto.DeliveryDateChangeResponse;
import com.fixtrack.intervention.dto.DeviceSummary;
import com.fixtrack.intervention.dto.FindingResponse;
import com.fixtrack.intervention.dto.InterventionDetailResponse;
import com.fixtrack.intervention.dto.InterventionSummaryResponse;
import com.fixtrack.intervention.dto.PersonRef;
import com.fixtrack.intervention.dto.StatusHistoryResponse;
import com.fixtrack.intervention.dto.UnclaimedOrderResponse;
import com.fixtrack.intervention.dto.WarrantySummary;
import com.fixtrack.intervention.entity.DeliveryDateChange;
import com.fixtrack.intervention.entity.Intervention;
import com.fixtrack.intervention.entity.InterventionFinding;
import com.fixtrack.intervention.entity.InterventionStatus;
import com.fixtrack.intervention.entity.InterventionStatusHistory;
import com.fixtrack.person.entity.Person;

@Component
public class InterventionMapper {

	public InterventionSummaryResponse toSummary(Intervention intervention, LocalDate today) {
		Person customer = intervention.getDevice().getOwner();
		return new InterventionSummaryResponse(
				intervention.getId(),
				InterventionCode.of(intervention),
				intervention.getStatus(),
				isOverdue(intervention, today),
				customer.getFullName(),
				customer.getDocumentType() + " " + customer.getDocumentNumber(),
				deviceName(intervention.getDevice()),
				intervention.getTechnician() == null ? null : intervention.getTechnician().getFullName(),
				intervention.getEstimatedDeliveryDate(),
				intervention.getReceivedAt());
	}

	// Qué ve quien consulta (contraseña de desbloqueo, mano de obra) y las transiciones válidas los decide el servicio
	public InterventionDetailResponse toDetail(Intervention intervention, List<InterventionFinding> findings,
			boolean showUnlockCode, boolean showBreakdown, ChargesSummary charges,
			List<InterventionStatus> nextStatuses, List<Intervention> warranties, LocalDate today) {
		Person customer = intervention.getDevice().getOwner();
		Device device = intervention.getDevice();
		return new InterventionDetailResponse(
				intervention.getId(),
				InterventionCode.of(intervention),
				intervention.getStatus(),
				isOverdue(intervention, today),
				new CustomerSummary(customer.getId(), customer.getDocumentType(), customer.getDocumentNumber(),
						customer.getFullName(), customer.getPhone(), customer.getEmail()),
				new DeviceSummary(device.getId(), device.getDeviceModel().getBrand().getName(),
						device.getDeviceModel().getName(), device.getDeviceModel().getDeviceType(), device.getImei(),
						device.getSerialNumber(), device.getColor(), device.getObservations()),
				toRef(intervention.getAdvisor()),
				toRef(intervention.getTechnician()),
				intervention.getProblemDescription(),
				intervention.getPhysicalCondition(),
				showUnlockCode ? intervention.getUnlockCode() : null,
				findings.stream().map(this::toFinding).toList(),
				intervention.getDiagnosis(),
				intervention.getSolution(),
				intervention.getRepairOutcome(),
				intervention.getOutcomeReason(),
				intervention.getEstimatedPrice(),
				showBreakdown ? intervention.getLaborCost() : null,
				intervention.getTotalPrice(),
				charges,
				intervention.getEstimatedDeliveryDate(),
				intervention.getWarrantyOf() == null ? null : InterventionCode.of(intervention.getWarrantyOf()),
				intervention.getWarrantyDays(),
				intervention.getWarrantyUntil(),
				warranties.stream().map(this::toWarranty).toList(),
				intervention.getDeliveryNotes(),
				intervention.getReceivedAt(),
				intervention.getReadyAt(),
				intervention.getDeliveredAt(),
				nextStatuses);
	}

	public WarrantySummary toWarranty(Intervention warranty) {
		return new WarrantySummary(warranty.getId(), InterventionCode.of(warranty), warranty.getStatus(),
				warranty.getReceivedAt(), warranty.getDeliveredAt());
	}

	// noContact cuando el cliente no dejó celular ni correo
	public UnclaimedOrderResponse toUnclaimed(Intervention intervention, LocalDate today) {
		Person customer = intervention.getDevice().getOwner();
		return new UnclaimedOrderResponse(intervention.getId(), InterventionCode.of(intervention),
				customer.getFullName(), customer.getDocumentType() + " " + customer.getDocumentNumber(),
				deviceName(intervention.getDevice()), customer.getPhone(), customer.getEmail(),
				customer.getPhone() == null && customer.getEmail() == null, intervention.getReadyAt(),
				ChronoUnit.DAYS.between(intervention.getReadyAt().toLocalDate(), today));
	}

	public DeliveryDateChangeResponse toDeliveryChange(DeliveryDateChange change) {
		return new DeliveryDateChangeResponse(change.getId(), change.getPreviousDate(), change.getNewDate(),
				change.getReason(), toRef(change.getChangedBy()), change.getChangedAt());
	}

	public StatusHistoryResponse toHistory(InterventionStatusHistory history) {
		return new StatusHistoryResponse(history.getId(), history.getFromStatus(), history.getToStatus(),
				toRef(history.getChangedBy()), history.getNote(), history.getChangedAt());
	}

	public FindingResponse toFinding(InterventionFinding finding) {
		return new FindingResponse(finding.getId(), finding.getServiceType(), finding.getDescription(),
				finding.getOrigin(), finding.getApprovalStatus(), toRef(finding.getRespondedBy()),
				finding.getRespondedAt(), finding.getCreatedAt());
	}

	private boolean isOverdue(Intervention intervention, LocalDate today) {
		return intervention.getStatus().canBeOverdue() && intervention.getEstimatedDeliveryDate().isBefore(today);
	}

	private String deviceName(Device device) {
		return device.getDeviceModel().getBrand().getName() + " " + device.getDeviceModel().getName();
	}

	private PersonRef toRef(Person person) {
		return person == null ? null : new PersonRef(person.getId(), person.getFullName());
	}
}
