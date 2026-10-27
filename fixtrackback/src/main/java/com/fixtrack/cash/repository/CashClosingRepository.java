package com.fixtrack.cash.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.cash.entity.CashClosing;

public interface CashClosingRepository extends JpaRepository<CashClosing, Long> {

	Optional<CashClosing> findByClosedByIdAndClosingDate(Long closedById, LocalDate closingDate);

	boolean existsByClosedByIdAndClosingDate(Long closedById, LocalDate closingDate);
}
