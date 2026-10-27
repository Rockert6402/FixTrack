package com.fixtrack.setting.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// Parámetros del sistema con su valor por defecto (el que se usa mientras el administrador no guarde otro),
// su tipo y su rango: mínimo y máximo para los enteros, longitud máxima para los textos
@Getter
@RequiredArgsConstructor
public enum SettingKey {
	DEFAULT_MIN_STOCK("2", "Stock mínimo con que se crea un repuesto si no se indica otro", SettingType.INTEGER, 0, 1000),
	DEFAULT_WARRANTY_DAYS("30", "Días de garantía de una orden reparada si el técnico no indica otros",
			SettingType.INTEGER, 0, 3650),
	UNCLAIMED_ALERT_DAYS("30", "Días en «Listo para entregar» después de los cuales una orden cuenta como no reclamada",
			SettingType.INTEGER, 1, 3650),
	SERVICE_TERMS("", "Condiciones del servicio del comprobante de ingreso, una por línea; vacío usa las de por defecto",
			SettingType.TEXT, 0, 5000);

	private final String defaultValue;
	private final String description;
	private final SettingType type;
	private final int min;
	private final int max;
}
