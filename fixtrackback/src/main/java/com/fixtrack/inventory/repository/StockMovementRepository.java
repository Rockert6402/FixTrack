package com.fixtrack.inventory.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.inventory.entity.StockMovement;
import com.fixtrack.inventory.entity.StockMovementType;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

	// Kárdex con filtros opcionales por repuesto, tipo y rango de fechas [from, to)
	@EntityGraph(attributePaths = { "sparePart", "createdBy", "intervention", "intervention.warrantyOf", "partRequest" })
	@Query("""
			SELECT m FROM StockMovement m
			WHERE (:sparePartId IS NULL OR m.sparePart.id = :sparePartId)
			  AND (:type IS NULL OR m.type = :type)
			  AND (:from IS NULL OR m.createdAt >= :from)
			  AND (:to IS NULL OR m.createdAt < :to)
			""")
	Page<StockMovement> search(@Param("sparePartId") Long sparePartId, @Param("type") StockMovementType type,
			@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);
}
