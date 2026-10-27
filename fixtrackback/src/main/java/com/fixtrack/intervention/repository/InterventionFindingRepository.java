package com.fixtrack.intervention.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.intervention.entity.ApprovalStatus;
import com.fixtrack.intervention.entity.FindingOrigin;
import com.fixtrack.intervention.entity.InterventionFinding;

public interface InterventionFindingRepository extends JpaRepository<InterventionFinding, Long> {

	@EntityGraph(attributePaths = { "respondedBy" })
	List<InterventionFinding> findByInterventionIdOrderByCreatedAtAscIdAsc(Long interventionId);

	Optional<InterventionFinding> findByIdAndInterventionId(Long id, Long interventionId);

	// Daños adicionales que el cliente aún no ha respondido, para aprobarlos o dejarlos pendientes según el presupuesto
	List<InterventionFinding> findByInterventionIdAndOriginAndRespondedByIsNull(Long interventionId,
			FindingOrigin origin);

	boolean existsByInterventionIdAndApprovalStatus(Long interventionId, ApprovalStatus approvalStatus);
}
