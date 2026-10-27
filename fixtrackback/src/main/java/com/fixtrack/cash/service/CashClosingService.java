package com.fixtrack.cash.service;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import com.fixtrack.cash.dto.CashClosingRequest;
import com.fixtrack.cash.dto.CashClosingResponse;
import com.fixtrack.cash.dto.CashTodayResponse;
import com.fixtrack.common.PageResponse;
import com.fixtrack.infra.security.AuthenticatedUser;

public interface CashClosingService {

	CashTodayResponse today(AuthenticatedUser actor);

	CashClosingResponse close(CashClosingRequest request, AuthenticatedUser actor);

	// El administrador ve todos (y puede filtrar por usuario); los demás, solo los suyos
	PageResponse<CashClosingResponse> search(Long closedById, LocalDate from, LocalDate to, Pageable pageable,
			AuthenticatedUser actor);
}
