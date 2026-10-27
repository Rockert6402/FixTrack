package com.fixtrack.inventory.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.common.PageResponse;
import com.fixtrack.infra.security.CurrentUserProvider;
import com.fixtrack.inventory.dto.ApprovePartRequestRequest;
import com.fixtrack.inventory.dto.CreatePartRequestRequest;
import com.fixtrack.inventory.dto.OrderPartRequestRequest;
import com.fixtrack.inventory.dto.PartReceiptResponse;
import com.fixtrack.inventory.dto.PartRequestResponse;
import com.fixtrack.inventory.dto.ReceivePartRequestRequest;
import com.fixtrack.inventory.dto.RejectPartRequestRequest;
import com.fixtrack.inventory.entity.PartRequestStatus;
import com.fixtrack.inventory.service.PartRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PartRequestController {

	private final PartRequestService partRequestService;
	private final CurrentUserProvider currentUserProvider;

	@PostMapping("/api/interventions/{interventionId}/part-requests")
	@PreAuthorize("hasRole('TECHNICIAN')")
	@ResponseStatus(HttpStatus.CREATED)
	public PartRequestResponse create(@PathVariable Long interventionId,
			@Valid @RequestBody CreatePartRequestRequest request) {
		return partRequestService.create(interventionId, request, currentUserProvider.require());
	}

	@GetMapping("/api/interventions/{interventionId}/part-requests")
	@PreAuthorize("isAuthenticated()")
	public List<PartRequestResponse> byIntervention(@PathVariable Long interventionId) {
		return partRequestService.findByIntervention(interventionId, currentUserProvider.require());
	}

	@GetMapping("/api/part-requests")
	@PreAuthorize("hasRole('COORDINATOR')")
	public PageResponse<PartRequestResponse> list(@RequestParam(required = false) PartRequestStatus status,
			@PageableDefault(size = 20, sort = "requestedAt", direction = Sort.Direction.ASC) Pageable pageable) {
		return partRequestService.search(status, pageable);
	}

	// Las solicitudes que hizo el técnico que consulta
	@GetMapping("/api/part-requests/mine")
	@PreAuthorize("hasRole('TECHNICIAN')")
	public PageResponse<PartRequestResponse> mine(@RequestParam(required = false) PartRequestStatus status,
			@PageableDefault(size = 20, sort = "requestedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return partRequestService.findMine(currentUserProvider.require(), status, pageable);
	}

	@GetMapping("/api/part-requests/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	public PartRequestResponse get(@PathVariable Long id) {
		return partRequestService.findById(id);
	}

	@PatchMapping("/api/part-requests/{id}/approve")
	@PreAuthorize("hasRole('COORDINATOR')")
	public PartRequestResponse approve(@PathVariable Long id,
			@Valid @RequestBody(required = false) ApprovePartRequestRequest request) {
		return partRequestService.approve(id, request, currentUserProvider.require());
	}

	@PatchMapping("/api/part-requests/{id}/reject")
	@PreAuthorize("hasRole('COORDINATOR')")
	public PartRequestResponse reject(@PathVariable Long id, @Valid @RequestBody RejectPartRequestRequest request) {
		return partRequestService.reject(id, request, currentUserProvider.require());
	}

	@PatchMapping("/api/part-requests/{id}/order")
	@PreAuthorize("hasRole('COORDINATOR')")
	public PartRequestResponse order(@PathVariable Long id,
			@Valid @RequestBody(required = false) OrderPartRequestRequest request) {
		return partRequestService.order(id, request, currentUserProvider.require());
	}

	@PatchMapping("/api/part-requests/{id}/receive")
	@PreAuthorize("hasRole('COORDINATOR')")
	public PartReceiptResponse receive(@PathVariable Long id, @Valid @RequestBody ReceivePartRequestRequest request) {
		return partRequestService.receive(id, request, currentUserProvider.require());
	}
}
