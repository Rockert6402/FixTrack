package com.fixtrack.setting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.setting.entity.SystemSetting;
import com.fixtrack.setting.entity.SettingKey;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, SettingKey> {
}
