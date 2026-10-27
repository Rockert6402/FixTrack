package com.fixtrack.intervention.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.intervention.dto.OrderCounters;
import com.fixtrack.intervention.dto.TechnicianWorkload;
import com.fixtrack.intervention.entity.Intervention;
import com.fixtrack.intervention.entity.InterventionStatus;

public interface InterventionRepository extends JpaRepository<Intervention, Long>,
		JpaSpecificationExecutor<Intervention> {

	// Listado con cliente, equipo y técnico en la misma consulta
	@Override
	@EntityGraph(attributePaths = { "device", "device.owner", "device.deviceModel", "device.deviceModel.brand",
			"technician", "warrantyOf" })
	Page<Intervention> findAll(Specification<Intervention> spec, Pageable pageable);

	@Override
	@EntityGraph(attributePaths = { "device", "device.owner", "device.deviceModel", "device.deviceModel.brand",
			"technician", "warrantyOf" })
	List<Intervention> findAll(Specification<Intervention> spec, Sort sort);

	@EntityGraph(attributePaths = { "device", "device.owner", "device.deviceModel", "device.deviceModel.brand",
			"technician", "advisor", "warrantyOf" })
	Optional<Intervention> findDetailById(Long id);

	// Contadores del panel de Inicio en una sola consulta; technicianId null cuenta las órdenes de todo el taller
	@Query("""
			SELECT new com.fixtrack.intervention.dto.OrderCounters(
			  COALESCE(SUM(CASE WHEN i.receivedAt >= :startOfToday AND i.receivedAt < :startOfTomorrow THEN 1 ELSE 0 END), 0),
			  COALESCE(SUM(CASE WHEN i.status IN :overdueStatuses AND i.estimatedDeliveryDate < :today THEN 1 ELSE 0 END), 0),
			  COALESCE(SUM(CASE WHEN i.status IN :inProgressStatuses THEN 1 ELSE 0 END), 0),
			  COALESCE(SUM(CASE WHEN i.status = :waitingParts THEN 1 ELSE 0 END), 0),
			  COALESCE(SUM(CASE WHEN i.status = :ready THEN 1 ELSE 0 END), 0),
			  COALESCE(SUM(CASE WHEN i.status = :ready AND i.readyAt < :unclaimedBefore THEN 1 ELSE 0 END), 0),
			  COALESCE(SUM(CASE WHEN i.status NOT IN :closedStatuses THEN 1 ELSE 0 END), 0))
			FROM Intervention i
			WHERE (:technicianId IS NULL OR i.technician.id = :technicianId)
			""")
	OrderCounters countForDashboard(@Param("technicianId") Long technicianId,
			@Param("startOfToday") LocalDateTime startOfToday, @Param("startOfTomorrow") LocalDateTime startOfTomorrow,
			@Param("today") LocalDate today, @Param("unclaimedBefore") LocalDateTime unclaimedBefore,
			@Param("overdueStatuses") Collection<InterventionStatus> overdueStatuses,
			@Param("inProgressStatuses") Collection<InterventionStatus> inProgressStatuses,
			@Param("closedStatuses") Collection<InterventionStatus> closedStatuses,
			@Param("waitingParts") InterventionStatus waitingParts, @Param("ready") InterventionStatus ready);

	// Órdenes abiertas de cada técnico, para mostrar su carga junto a su disponibilidad
	@Query("""
			SELECT new com.fixtrack.intervention.dto.TechnicianWorkload(i.technician.id, COUNT(i))
			FROM Intervention i
			WHERE i.technician IS NOT NULL AND i.status NOT IN :closedStatuses
			GROUP BY i.technician.id
			""")
	List<TechnicianWorkload> countOpenByTechnician(@Param("closedStatuses") Collection<InterventionStatus> closedStatuses);
}
