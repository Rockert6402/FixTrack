package com.fixtrack.device.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixtrack.device.entity.DeviceModel;
import com.fixtrack.device.entity.DeviceType;

public interface DeviceModelRepository extends JpaRepository<DeviceModel, Long> {

	Optional<DeviceModel> findByBrandIdAndName(Long brandId, String name);

	boolean existsByBrandIdAndName(Long brandId, String name);

	boolean existsByBrandIdAndNameAndIdNot(Long brandId, String name, Long id);

	boolean existsByBrandId(Long brandId);

	@EntityGraph(attributePaths = "brand")
	Optional<DeviceModel> findWithBrandById(Long id);

	// Listado con filtros opcionales por marca y tipo, trayendo la marca en la misma consulta
	@EntityGraph(attributePaths = "brand")
	@Query("""
			SELECT m FROM DeviceModel m
			WHERE (:brandId IS NULL OR m.brand.id = :brandId)
			  AND (:deviceType IS NULL OR m.deviceType = :deviceType)
			ORDER BY m.brand.name, m.name
			""")
	List<DeviceModel> search(@Param("brandId") Long brandId, @Param("deviceType") DeviceType deviceType);
}
