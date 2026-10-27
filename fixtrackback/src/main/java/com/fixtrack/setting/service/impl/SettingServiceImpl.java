package com.fixtrack.setting.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixtrack.setting.entity.SettingKey;
import com.fixtrack.setting.entity.SystemSetting;
import com.fixtrack.setting.repository.SystemSettingRepository;
import com.fixtrack.setting.service.SettingService;

import lombok.RequiredArgsConstructor;

// Lee los parámetros del sistema; si el administrador no ha guardado uno, usa su valor por defecto
@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

	private final SystemSettingRepository systemSettingRepository;

	@Override
	@Transactional(readOnly = true)
	public int getIntValue(SettingKey key) {
		return Integer.parseInt(getValue(key).trim());
	}

	@Override
	@Transactional(readOnly = true)
	public String getValue(SettingKey key) {
		return systemSettingRepository.findById(key)
				.map(SystemSetting::getSettingValue)
				.orElse(key.getDefaultValue());
	}
}
