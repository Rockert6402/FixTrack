package com.fixtrack.setting.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// Parámetros del sistema con el valor que se usa mientras el administrador no guarde otro
@Getter
@RequiredArgsConstructor
public enum SettingKey {
	DEFAULT_MIN_STOCK("2"),
	DEFAULT_WARRANTY_DAYS("30"),
	UNCLAIMED_ALERT_DAYS("30"),
	SERVICE_TERMS("");

	private final String defaultValue;
}
