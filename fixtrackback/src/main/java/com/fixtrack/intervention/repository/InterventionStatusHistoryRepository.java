package com.fixtrack.intervention.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.intervention.entity.InterventionStatusHistory;

public interface InterventionStatusHistoryRepository extends JpaRepository<InterventionStatusHistory, Long> {

	@EntityGraph(attributePaths = "changedBy")
	List<InterventionStatusHistory> findByInterventionIdOrderByChangedAtAscIdAsc(Long interventionId);
}
