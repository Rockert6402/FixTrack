package com.fixtrack.setting.dto;

import java.time.LocalDateTime;

import com.fixtrack.setting.entity.SettingKey;
import com.fixtrack.setting.entity.SettingType;

// customized indica si el administrador guardó un valor; si no, value es el de por defecto
public record SettingResponse(
		SettingKey key,
		String value,
		String defaultValue,
		boolean customized,
		String description,
		SettingType type,
		int min,
		int max,
		String updatedByName,
		LocalDateTime updatedAt) {
}
