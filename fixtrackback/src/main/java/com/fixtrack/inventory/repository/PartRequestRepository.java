package com.fixtrack.inventory.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.inventory.entity.PartRequest;
import com.fixtrack.inventory.entity.PartRequestStatus;

import jakarta.persistence.LockModeType;

public interface PartRequestRepository extends JpaRepository<PartRequest, Long> {

	@EntityGraph(attributePaths = { "intervention", "sparePart", "supplier", "requestedBy", "handledBy" })
	@Query("SELECT r FROM PartRequest r WHERE (:status IS NULL OR r.status = :status)")
	Page<PartRequest> search(@Param("status") PartRequestStatus status, Pageable pageable);

	@EntityGraph(attributePaths = { "intervention", "sparePart", "supplier", "requestedBy", "handledBy" })
	List<PartRequest> findByInterventionIdOrderByRequestedAtDescIdDesc(Long interventionId);

	// Solicitudes de un técnico, con filtro opcional por estado
	@EntityGraph(attributePaths = { "intervention", "sparePart", "supplier", "requestedBy", "handledBy" })
	@Query("SELECT r FROM PartRequest r WHERE r.requestedBy.id = :technicianId AND (:status IS NULL OR r.status = :status)")
	Page<PartRequest> searchByTechnician(@Param("technicianId") Long technicianId,
			@Param("status") PartRequestStatus status, Pageable pageable);

	List<PartRequest> findByInterventionIdAndStatusIn(Long interventionId, Collection<PartRequestStatus> statuses);

	@EntityGraph(attributePaths = { "intervention", "sparePart", "supplier", "requestedBy", "handledBy" })
	List<PartRequest> findByStatusInOrderByRequestedAtAscIdAsc(Collection<PartRequestStatus> statuses,
			Pageable pageable);

	long countByStatusIn(Collection<PartRequestStatus> statuses);

	// Bloquea la fila para que dos personas no aprueben, pidan o reciban la misma solicitud a la vez
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT r FROM PartRequest r WHERE r.id = :id")
	Optional<PartRequest> findForUpdate(@Param("id") Long id);
}
