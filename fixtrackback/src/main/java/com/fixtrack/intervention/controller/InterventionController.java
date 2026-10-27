package com.fixtrack.intervention.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.common.PageResponse;
import com.fixtrack.infra.security.CurrentUserProvider;
import com.fixtrack.intervention.dto.AccessKeyResponse;
import com.fixtrack.intervention.dto.AdditionalFindingRequest;
import com.fixtrack.intervention.dto.AssignTechnicianRequest;
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
import com.fixtrack.intervention.entity.InterventionStatus;
import com.fixtrack.intervention.service.InterventionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/interventions")
@RequiredArgsConstructor
public class InterventionController {

	private final InterventionService interventionService;
	private final CurrentUserProvider currentUserProvider;

	@PostMapping
	@PreAuthorize("hasRole('ADVISOR')")
	public ResponseEntity<CreatedInterventionResponse> create(@Valid @RequestBody CreateInterventionRequest request) {
		CreatedInterventionResponse created = interventionService.create(request, currentUserProvider.require());
		return ResponseEntity.created(URI.create("/api/interventions/" + created.intervention().id())).body(created);
	}

	@GetMapping
	@PreAuthorize("hasRole('ADVISOR')")
	public PageResponse<InterventionSummaryResponse> list(
			@RequestParam(required = false) String code,
			@RequestParam(required = false) String documentNumber,
			@RequestParam(required = false) Long technicianId,
			@RequestParam(required = false) InterventionStatus status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate receivedFrom,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate receivedTo,
			@RequestParam(required = false) Boolean overdue,
			@RequestParam(defaultValue = "false") boolean openOnly,
			@RequestParam(defaultValue = "false") boolean inProgress,
			@RequestParam(defaultValue = "false") boolean unclaimed,
			@PageableDefault(size = 20, sort = "receivedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		InterventionFilter filter = new InterventionFilter(code, documentNumber, technicianId, status, receivedFrom,
				receivedTo, overdue, openOnly, inProgress, unclaimed);
		return interventionService.search(filter, pageable);
	}

	@GetMapping("/mine")
	@PreAuthorize("hasRole('TECHNICIAN')")
	public PageResponse<InterventionSummaryResponse> mine(@RequestParam(required = false) InterventionStatus status,
			@RequestParam(required = false) Boolean overdue, @RequestParam(defaultValue = "false") boolean inProgress,
			@PageableDefault(size = 20, sort = "receivedAt", direction = Sort.Direction.ASC) Pageable pageable) {
		return interventionService.findAssignedTo(currentUserProvider.require(),
				InterventionFilter.ofTechnician(null, status, overdue, inProgress), pageable);
	}

	@GetMapping("/unclaimed")
	@PreAuthorize("hasRole('ADVISOR')")
	public List<UnclaimedOrderResponse> unclaimed() {
		return interventionService.findUnclaimed();
	}

	@PostMapping("/{id}/warranty")
	@PreAuthorize("hasRole('ADVISOR')")
	public ResponseEntity<InterventionDetailResponse> openWarranty(@PathVariable Long id,
			@Valid @RequestBody WarrantyRequest request) {
		InterventionDetailResponse warranty = interventionService.openWarranty(id, request, currentUserProvider.require());
		return ResponseEntity.created(URI.create("/api/interventions/" + warranty.id())).body(warranty);
	}

	// Salida: órdenes listas para entregar con su total, lo abonado y el saldo
	@GetMapping("/ready-for-delivery")
	@PreAuthorize("hasRole('ADVISOR')")
	public List<ReadyForDeliveryResponse> readyForDelivery() {
		return interventionService.findReadyForDelivery();
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public InterventionDetailResponse get(@PathVariable Long id) {
		return interventionService.findById(id, currentUserProvider.require());
	}

	@GetMapping("/{id}/history")
	@PreAuthorize("isAuthenticated()")
	public List<StatusHistoryResponse> history(@PathVariable Long id) {
		return interventionService.findHistory(id, currentUserProvider.require());
	}

	@GetMapping("/{id}/delivery-changes")
	@PreAuthorize("isAuthenticated()")
	public List<DeliveryDateChangeResponse> deliveryChanges(@PathVariable Long id) {
		return interventionService.findDeliveryChanges(id, currentUserProvider.require());
	}

	@PatchMapping("/{id}/technician")
	@PreAuthorize("hasRole('ADVISOR')")
	public InterventionDetailResponse assignTechnician(@PathVariable Long id,
			@Valid @RequestBody AssignTechnicianRequest request) {
		return interventionService.assignTechnician(id, request.technicianId(), currentUserProvider.require());
	}

	@PatchMapping("/{id}/estimated-delivery")
	@PreAuthorize("hasAnyRole('ADVISOR', 'TECHNICIAN')")
	public InterventionDetailResponse rescheduleDelivery(@PathVariable Long id,
			@Valid @RequestBody RescheduleDeliveryRequest request) {
		return interventionService.rescheduleDelivery(id, request, currentUserProvider.require());
	}

	@PatchMapping("/{id}/diagnosis")
	@PreAuthorize("hasRole('TECHNICIAN')")
	public InterventionDetailResponse registerDiagnosis(@PathVariable Long id,
			@Valid @RequestBody DiagnosisRequest request) {
		return interventionService.registerDiagnosis(id, request, currentUserProvider.require());
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasAnyRole('ADVISOR', 'TECHNICIAN')")
	public InterventionDetailResponse changeStatus(@PathVariable Long id, @Valid @RequestBody StatusChangeRequest request) {
		return interventionService.changeStatus(id, request, currentUserProvider.require());
	}

	@PostMapping("/{id}/findings")
	@PreAuthorize("hasRole('TECHNICIAN')")
	@ResponseStatus(HttpStatus.CREATED)
	public InterventionDetailResponse addFinding(@PathVariable Long id,
			@Valid @RequestBody AdditionalFindingRequest request) {
		return interventionService.addFinding(id, request, currentUserProvider.require());
	}

	@PatchMapping("/{id}/findings/{findingId}/approval")
	@PreAuthorize("hasRole('ADVISOR')")
	public InterventionDetailResponse answerFinding(@PathVariable Long id, @PathVariable Long findingId,
			@Valid @RequestBody FindingApprovalRequest request) {
		return interventionService.answerFinding(id, findingId, request, currentUserProvider.require());
	}

	@PatchMapping("/{id}/labor")
	@PreAuthorize("hasRole('TECHNICIAN')")
	public InterventionDetailResponse setLaborCost(@PathVariable Long id, @Valid @RequestBody LaborCostRequest request) {
		return interventionService.setLaborCost(id, request, currentUserProvider.require());
	}

	@PatchMapping("/{id}/outcome")
	@PreAuthorize("hasRole('TECHNICIAN')")
	public InterventionDetailResponse registerOutcome(@PathVariable Long id, @Valid @RequestBody OutcomeRequest request) {
		return interventionService.registerOutcome(id, request, currentUserProvider.require());
	}

	@PatchMapping("/{id}/deliver")
	@PreAuthorize("hasRole('ADVISOR')")
	public InterventionDetailResponse deliver(@PathVariable Long id,
			@Valid @RequestBody(required = false) DeliveryRequest request) {
		return interventionService.deliver(id, request, currentUserProvider.require());
	}

	@PatchMapping("/{id}/cancel")
	@PreAuthorize("hasRole('ADVISOR')")
	public InterventionDetailResponse cancel(@PathVariable Long id, @Valid @RequestBody CancelRequest request) {
		return interventionService.cancel(id, request, currentUserProvider.require());
	}

	@PostMapping("/{id}/access-key")
	@PreAuthorize("hasRole('ADVISOR')")
	public AccessKeyResponse regenerateAccessKey(@PathVariable Long id) {
		return interventionService.regenerateAccessKey(id, currentUserProvider.require());
	}
}
