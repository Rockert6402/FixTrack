package com.fixtrack.setting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fixtrack.setting.entity.SettingKey;
import com.fixtrack.setting.entity.SystemSetting;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, SettingKey> {

	@EntityGraph(attributePaths = { "updatedBy" })
	@Query("SELECT s FROM SystemSetting s")
	List<SystemSetting> findAllWithUpdater();
}
