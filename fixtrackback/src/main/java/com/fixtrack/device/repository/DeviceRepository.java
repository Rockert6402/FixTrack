package com.fixtrack.device.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.device.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {

	@EntityGraph(attributePaths = { "owner", "deviceModel", "deviceModel.brand" })
	Optional<Device> findByImei(String imei);

	boolean existsByImei(String imei);

	boolean existsByImeiAndIdNot(String imei, Long id);

	@EntityGraph(attributePaths = { "owner", "deviceModel", "deviceModel.brand" })
	Optional<Device> findFirstByDeviceModelIdAndSerialNumber(Long deviceModelId, String serialNumber);

	@EntityGraph(attributePaths = { "deviceModel", "deviceModel.brand" })
	List<Device> findByOwnerIdOrderByIdDesc(Long ownerId);
}
