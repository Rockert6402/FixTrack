package com.fixtrack.device.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Equipo en el ingreso de una orden: deviceId reutiliza un equipo del cliente; si no, se busca por IMEI o por
// modelo y número de serie, y si no existe se registra con deviceModelId
public record DeviceRequest(
		Long deviceId,
		Long deviceModelId,
		@Pattern(regexp = "^(\\s*\\d{15}\\s*)?$", message = "debe tener 15 dígitos") String imei,
		@Size(max = 50) String serialNumber,
		@Size(max = 30) String color,
		@Size(max = 500) String observations) {
}
