package com.fixtrack.intervention.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.fixtrack.common.PageResponse;
import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.intervention.dto.AccessKeyResponse;
import com.fixtrack.intervention.dto.AdditionalFindingRequest;
import com.fixtrack.intervention.dto.CancelRequest;
import com.fixtrack.intervention.dto.CreateInterventionRequest;
import com.fixtrack.intervention.dto.CreatedInterventionResponse;
import com.fixtrack.intervention.dto.DeliveryDateChangeResponse;
import com.fixtrack.intervention.dto.DeliveryRequest;
import com.fixtrack.intervention.dto.DiagnosisRequest;
import com.fixtrack.intervention.dto.FindingApprovalRequest;
import com.fixtrack.intervention.dto.InterventionDetailResponse;
import com.fixtrack.intervention.dto.InterventionFilter;
import com.fixtrack.intervention.dto.InterventionSummaryResponse;
import com.fixtrack.intervention.dto.LaborCostRequest;
import com.fixtrack.intervention.dto.OutcomeRequest;
import com.fixtrack.intervention.dto.ReadyForDeliveryResponse;
import com.fixtrack.intervention.dto.RescheduleDeliveryRequest;
import com.fixtrack.intervention.dto.StatusChangeRequest;
import com.fixtrack.intervention.dto.StatusHistoryResponse;
import com.fixtrack.intervention.dto.UnclaimedOrderResponse;
import com.fixtrack.intervention.dto.WarrantyRequest;
import com.fixtrack.intervention.entity.Intervention;
import com.fixtrack.person.entity.Person;

public interface InterventionService {

	CreatedInterventionResponse create(CreateInterventionRequest request, AuthenticatedUser actor);

	PageResponse<InterventionSummaryResponse> search(InterventionFilter filter, Pageable pageable);

	// Del filtro solo se usan status, overdue e inProgress; el técnico es quien consulta
	PageResponse<InterventionSummaryResponse> findAssignedTo(AuthenticatedUser actor, InterventionFilter filter,
			Pageable pageable);

	List<ReadyForDeliveryResponse> findReadyForDelivery();

	List<UnclaimedOrderResponse> findUnclaimed();

	InterventionDetailResponse openWarranty(Long originalId, WarrantyRequest request, AuthenticatedUser actor);

	InterventionDetailResponse findById(Long id, AuthenticatedUser actor);

	List<StatusHistoryResponse> findHistory(Long id, AuthenticatedUser actor);

	List<DeliveryDateChangeResponse> findDeliveryChanges(Long id, AuthenticatedUser actor);

	InterventionDetailResponse assignTechnician(Long id, Long technicianId, AuthenticatedUser actor);

	InterventionDetailResponse rescheduleDelivery(Long id, RescheduleDeliveryRequest request, AuthenticatedUser actor);

	InterventionDetailResponse registerDiagnosis(Long id, DiagnosisRequest request, AuthenticatedUser actor);

	InterventionDetailResponse addFinding(Long id, AdditionalFindingRequest request, AuthenticatedUser actor);

	InterventionDetailResponse setLaborCost(Long id, LaborCostRequest request, AuthenticatedUser actor);

	InterventionDetailResponse answerFinding(Long id, Long findingId, FindingApprovalRequest request,
			AuthenticatedUser actor);

	InterventionDetailResponse registerOutcome(Long id, OutcomeRequest request, AuthenticatedUser actor);

	InterventionDetailResponse changeStatus(Long id, StatusChangeRequest request, AuthenticatedUser actor);

	InterventionDetailResponse deliver(Long id, DeliveryRequest request, AuthenticatedUser actor);

	InterventionDetailResponse cancel(Long id, CancelRequest request, AuthenticatedUser actor);

	AccessKeyResponse regenerateAccessKey(Long id, AuthenticatedUser actor);

	// Pasa la orden a WAITING_PARTS desde diagnóstico o reparación y lo deja en el historial; si ya espera, no cambia
	void markWaitingParts(Intervention intervention, Person actor, String note);
}
