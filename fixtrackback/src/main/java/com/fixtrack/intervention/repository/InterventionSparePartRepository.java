package com.fixtrack.intervention.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.intervention.dto.InterventionAmount;
import com.fixtrack.intervention.dto.report.TechnicianCost;
import com.fixtrack.intervention.entity.InterventionSparePart;
import com.fixtrack.intervention.entity.InterventionStatus;

public interface InterventionSparePartRepository extends JpaRepository<InterventionSparePart, Long> {

	@EntityGraph(attributePaths = { "sparePart", "createdBy" })
	List<InterventionSparePart> findByInterventionIdOrderByCreatedAtAscIdAsc(Long interventionId);

	Optional<InterventionSparePart> findByIdAndInterventionId(Long id, Long interventionId);

	// Suma de cantidad × precio de venta guardado de los repuestos usados; null si la orden no usó repuestos
	@Query("SELECT SUM(p.quantity * p.unitPrice) FROM InterventionSparePart p WHERE p.intervention.id = :interventionId")
	BigDecimal sumPartsTotal(@Param("interventionId") Long interventionId);

	// Suma de repuestos de varias órdenes a la vez; las que no usaron repuestos no aparecen
	@Query("""
			SELECT new com.fixtrack.intervention.dto.InterventionAmount(p.intervention.id, SUM(p.quantity * p.unitPrice))
			FROM InterventionSparePart p
			WHERE p.intervention.id IN :interventionIds
			GROUP BY p.intervention.id
			""")
	List<InterventionAmount> sumPartsTotals(@Param("interventionIds") Collection<Long> interventionIds);

	// Informe de ganancias: costo de repuestos por técnico de las órdenes entregadas en [from, to), y cuánto fue de garantías
	@Query("""
			SELECT new com.fixtrack.intervention.dto.report.TechnicianCost(t.id, SUM(u.quantity * u.costPrice),
			  SUM(CASE WHEN i.warrantyOf IS NOT NULL THEN u.quantity * u.costPrice ELSE 0 END))
			FROM InterventionSparePart u JOIN u.intervention i LEFT JOIN i.technician t
			WHERE i.status = :delivered AND i.deliveredAt >= :from AND i.deliveredAt < :to
			  AND (:technicianId IS NULL OR t.id = :technicianId)
			GROUP BY t.id
			""")
	List<TechnicianCost> sumCostByTechnician(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
			@Param("technicianId") Long technicianId, @Param("delivered") InterventionStatus delivered);
}
