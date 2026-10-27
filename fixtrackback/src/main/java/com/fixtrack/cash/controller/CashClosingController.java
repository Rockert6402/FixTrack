package com.fixtrack.cash.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.cash.dto.CashClosingRequest;
import com.fixtrack.cash.dto.CashClosingResponse;
import com.fixtrack.cash.dto.CashTodayResponse;
import com.fixtrack.cash.service.CashClosingService;
import com.fixtrack.common.PageResponse;
import com.fixtrack.infra.security.CurrentUserProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cash-closings")
@RequiredArgsConstructor
public class CashClosingController {

	private final CashClosingService cashClosingService;
	private final CurrentUserProvider currentUserProvider;

	@GetMapping("/today")
	@PreAuthorize("hasRole('ADVISOR')")
	public CashTodayResponse today() {
		return cashClosingService.today(currentUserProvider.require());
	}

	@PostMapping
	@PreAuthorize("hasRole('ADVISOR')")
	@ResponseStatus(HttpStatus.CREATED)
	public CashClosingResponse close(@Valid @RequestBody CashClosingRequest request) {
		return cashClosingService.close(request, currentUserProvider.require());
	}

	@GetMapping
	@PreAuthorize("hasRole('ADVISOR')")
	public PageResponse<CashClosingResponse> list(@RequestParam(required = false) Long closedById,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@PageableDefault(size = 20, sort = "closingDate", direction = Sort.Direction.DESC) Pageable pageable) {
		return cashClosingService.search(closedById, from, to, pageable, currentUserProvider.require());
	}
}
