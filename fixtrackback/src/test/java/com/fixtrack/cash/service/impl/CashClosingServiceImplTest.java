package com.fixtrack.cash.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fixtrack.cash.dto.CashClosingRequest;
import com.fixtrack.cash.entity.CashClosing;
import com.fixtrack.cash.mapper.CashClosingMapper;
import com.fixtrack.cash.repository.CashClosingRepository;
import com.fixtrack.cash.repository.PaymentRepository;
import com.fixtrack.common.ConflictException;
import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.person.entity.Role;
import com.fixtrack.person.service.PersonService;

@ExtendWith(MockitoExtension.class)
class CashClosingServiceImplTest {

	private static final LocalDate TODAY = LocalDate.of(2026, 10, 27);
	private static final AuthenticatedUser ADVISOR = new AuthenticatedUser(3L, "Asesora", "a@fixtrack.test",
			Role.ADVISOR, "hash", true);
	private static final AuthenticatedUser ADMIN = new AuthenticatedUser(1L, "Admin", "admin@fixtrack.test",
			Role.ADMIN, "hash", true);

	@Mock
	private CashClosingRepository cashClosingRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PersonService personService;

	private CashClosingServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new CashClosingServiceImpl(cashClosingRepository, paymentRepository, personService,
				new CashClosingMapper(), Clock.fixed(Instant.parse("2026-10-27T20:00:00Z"), ZoneId.of("America/Bogota")));
	}

	@Test
	void shouldRejectSecondClosingOfTheDay() {
		given(cashClosingRepository.existsByClosedByIdAndClosingDate(3L, TODAY)).willReturn(true);

		assertThatThrownBy(() -> service.close(new CashClosingRequest(BigDecimal.TEN, null), ADVISOR))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("no se edita");
		verify(cashClosingRepository, never()).save(any(CashClosing.class));
	}

	@Test
	void shouldShowOnlyOwnClosingsToAdvisor() {
		given(cashClosingRepository.search(eq(3L), isNull(), isNull(), any(Pageable.class))).willReturn(Page.empty());

		service.search(99L, null, null, Pageable.unpaged(), ADVISOR);

		verify(cashClosingRepository).search(eq(3L), isNull(), isNull(), any(Pageable.class));
	}

	@Test
	void shouldLetAdminSeeEveryoneOrFilterByUser() {
		given(cashClosingRepository.search(isNull(), isNull(), isNull(), any(Pageable.class))).willReturn(Page.empty());
		given(cashClosingRepository.search(eq(4L), isNull(), isNull(), any(Pageable.class))).willReturn(Page.empty());

		service.search(null, null, null, Pageable.unpaged(), ADMIN);
		service.search(4L, null, null, Pageable.unpaged(), ADMIN);

		verify(cashClosingRepository).search(isNull(), isNull(), isNull(), any(Pageable.class));
		verify(cashClosingRepository).search(eq(4L), isNull(), isNull(), any(Pageable.class));
	}
}
