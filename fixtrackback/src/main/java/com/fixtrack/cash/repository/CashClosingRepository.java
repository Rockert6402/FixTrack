package com.fixtrack.cash.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.cash.entity.CashClosing;

public interface CashClosingRepository extends JpaRepository<CashClosing, Long> {

	@EntityGraph(attributePaths = { "closedBy" })
	Optional<CashClosing> findByClosedByIdAndClosingDate(Long closedById, LocalDate closingDate);

	boolean existsByClosedByIdAndClosingDate(Long closedById, LocalDate closingDate);

	// Cierres con filtros opcionales por usuario y rango de fechas
	@EntityGraph(attributePaths = { "closedBy" })
	@Query("""
			SELECT c FROM CashClosing c
			WHERE (:closedById IS NULL OR c.closedBy.id = :closedById)
			  AND (:from IS NULL OR c.closingDate >= :from)
			  AND (:to IS NULL OR c.closingDate <= :to)
			""")
	Page<CashClosing> search(@Param("closedById") Long closedById, @Param("from") LocalDate from,
			@Param("to") LocalDate to, Pageable pageable);
}
