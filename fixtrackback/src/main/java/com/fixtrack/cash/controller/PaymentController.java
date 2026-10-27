package com.fixtrack.cash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.cash.dto.PaymentRequest;
import com.fixtrack.cash.dto.PaymentSummaryResponse;
import com.fixtrack.cash.service.PaymentService;
import com.fixtrack.infra.security.CurrentUserProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/interventions/{interventionId}/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	private final CurrentUserProvider currentUserProvider;

	@GetMapping
	@PreAuthorize("hasRole('ADVISOR')")
	public PaymentSummaryResponse list(@PathVariable Long interventionId) {
		return paymentService.findByIntervention(interventionId);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADVISOR')")
	@ResponseStatus(HttpStatus.CREATED)
	public PaymentSummaryResponse register(@PathVariable Long interventionId,
			@Valid @RequestBody PaymentRequest request) {
		return paymentService.register(interventionId, request, currentUserProvider.require());
	}
}
