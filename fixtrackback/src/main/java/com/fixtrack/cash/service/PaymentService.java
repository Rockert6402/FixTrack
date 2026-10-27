package com.fixtrack.cash.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import com.fixtrack.cash.dto.PaymentRequest;
import com.fixtrack.cash.dto.PaymentSummaryResponse;
import com.fixtrack.infra.security.AuthenticatedUser;

public interface PaymentService {

	PaymentSummaryResponse register(Long interventionId, PaymentRequest request, AuthenticatedUser actor);

	PaymentSummaryResponse findByIntervention(Long interventionId);

	// Abonos + cobros finales − devoluciones
	BigDecimal netPaid(Long interventionId);

	Map<Long, BigDecimal> netPaidByIntervention(Collection<Long> interventionIds);
}
