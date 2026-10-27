package com.fixtrack.cash.mapper;

import org.springframework.stereotype.Component;

import com.fixtrack.cash.dto.PaymentResponse;
import com.fixtrack.cash.entity.Payment;

@Component
public class PaymentMapper {

	public PaymentResponse toResponse(Payment payment) {
		return new PaymentResponse(payment.getId(), payment.getType(), payment.getMethod(), payment.getAmount(),
				payment.getReceivedBy().getId(), payment.getReceivedBy().getFullName(), payment.getCreatedAt());
	}
}
