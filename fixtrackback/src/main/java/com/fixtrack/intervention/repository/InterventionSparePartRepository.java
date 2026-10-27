package com.fixtrack.intervention.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.intervention.dto.InterventionAmount;
import com.fixtrack.intervention.entity.InterventionSparePart;

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
}
