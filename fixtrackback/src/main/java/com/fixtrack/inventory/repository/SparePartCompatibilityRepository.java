package com.fixtrack.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.inventory.entity.SparePartCompatibility;
import com.fixtrack.inventory.entity.SparePartCompatibilityId;

public interface SparePartCompatibilityRepository
		extends JpaRepository<SparePartCompatibility, SparePartCompatibilityId> {

	// Repuestos que, además de su modelo principal, sirven para este modelo
	@Query("SELECT c.sparePart.id FROM SparePartCompatibility c WHERE c.deviceModel.id = :deviceModelId")
	List<Long> findSparePartIdsByDeviceModelId(@Param("deviceModelId") Long deviceModelId);
}
