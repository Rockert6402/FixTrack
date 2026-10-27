package com.fixtrack.intervention.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.intervention.entity.DeliveryDateChange;

public interface DeliveryDateChangeRepository extends JpaRepository<DeliveryDateChange, Long> {

	@EntityGraph(attributePaths = "changedBy")
	List<DeliveryDateChange> findByInterventionIdOrderByChangedAtDescIdDesc(Long interventionId);
}
