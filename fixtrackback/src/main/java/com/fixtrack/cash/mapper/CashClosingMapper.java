package com.fixtrack.cash.mapper;

import org.springframework.stereotype.Component;

import com.fixtrack.cash.dto.CashClosingResponse;
import com.fixtrack.cash.entity.CashClosing;

@Component
public class CashClosingMapper {

	public CashClosingResponse toResponse(CashClosing closing, long payments) {
		return new CashClosingResponse(closing.getId(), closing.getClosingDate(), closing.getClosedBy().getId(),
				closing.getClosedBy().getFullName(), closing.getExpectedCash(), closing.getExpectedTransfer(),
				closing.getExpectedCard(),
				closing.getExpectedCash().add(closing.getExpectedTransfer()).add(closing.getExpectedCard()),
				closing.getCountedCash(), closing.getCountedCash().subtract(closing.getExpectedCash()),
				closing.getNotes(), payments, closing.getCreatedAt());
	}
}
