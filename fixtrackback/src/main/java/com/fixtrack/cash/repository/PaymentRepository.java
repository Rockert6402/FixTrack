package com.fixtrack.cash.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.cash.dto.PaidAmount;
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
}
