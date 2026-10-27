package com.fixtrack.cash.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.cash.dto.MethodTotal;
import com.fixtrack.cash.dto.PaidAmount;
import com.fixtrack.cash.entity.CashClosing;
import com.fixtrack.cash.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	@EntityGraph(attributePaths = { "receivedBy" })
	List<Payment> findByInterventionIdOrderByCreatedAtAscIdAsc(Long interventionId);

	// Lo pagado neto de la orden: abonos y cobros suman, devoluciones restan
	@Query("""
			SELECT COALESCE(SUM(CASE WHEN p.type = com.fixtrack.cash.entity.PaymentType.REFUND THEN -p.amount
			                         ELSE p.amount END), 0)
			FROM Payment p
			WHERE p.intervention.id = :interventionId
			""")
	BigDecimal sumNetPaid(@Param("interventionId") Long interventionId);

	// Lo pagado neto de varias órdenes a la vez; las que no tienen pagos no aparecen
	@Query("""
			SELECT new com.fixtrack.cash.dto.PaidAmount(p.intervention.id,
			  SUM(CASE WHEN p.type = com.fixtrack.cash.entity.PaymentType.REFUND THEN -p.amount ELSE p.amount END))
			FROM Payment p
			WHERE p.intervention.id IN :interventionIds
			GROUP BY p.intervention.id
			""")
	List<PaidAmount> sumNetPaidByIntervention(@Param("interventionIds") Collection<Long> interventionIds);

	// Pagos del usuario aún sin cierre, hasta el final de hoy, por medio de pago
	@Query("""
			SELECT new com.fixtrack.cash.dto.MethodTotal(p.method,
			  SUM(CASE WHEN p.type = com.fixtrack.cash.entity.PaymentType.REFUND THEN -p.amount ELSE p.amount END),
			  COUNT(p), SUM(CASE WHEN p.createdAt < :startOfToday THEN 1 ELSE 0 END))
			FROM Payment p
			WHERE p.receivedBy.id = :userId AND p.cashClosing IS NULL AND p.createdAt < :endOfToday
			GROUP BY p.method
			""")
	List<MethodTotal> sumPendingByMethod(@Param("userId") Long userId,
			@Param("startOfToday") LocalDateTime startOfToday, @Param("endOfToday") LocalDateTime endOfToday);

	// Pagos de un cierre por medio de pago
	@Query("""
			SELECT new com.fixtrack.cash.dto.MethodTotal(p.method,
			  SUM(CASE WHEN p.type = com.fixtrack.cash.entity.PaymentType.REFUND THEN -p.amount ELSE p.amount END),
			  COUNT(p), 0L)
			FROM Payment p
			WHERE p.cashClosing.id = :closingId
			GROUP BY p.method
			""")
	List<MethodTotal> sumByClosing(@Param("closingId") Long closingId);

	// Liga al cierre los pagos del usuario aún sin cierre hasta el final de hoy
	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("""
			UPDATE Payment p SET p.cashClosing = :closing
			WHERE p.receivedBy.id = :userId AND p.cashClosing IS NULL AND p.createdAt < :endOfToday
			""")
	int linkPendingToClosing(@Param("closing") CashClosing closing, @Param("userId") Long userId,
			@Param("endOfToday") LocalDateTime endOfToday);

	long countByCashClosingId(Long cashClosingId);
}
