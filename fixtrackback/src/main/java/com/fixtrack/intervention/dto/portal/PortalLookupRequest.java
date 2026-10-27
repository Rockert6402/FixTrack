package com.fixtrack.intervention.dto.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Código de la orden o de una de sus garantías (FT-00001, FT-00001-G1) y la clave de consulta del comprobante
public record PortalLookupRequest(
		@NotBlank @Size(max = 20) String code,
		@NotBlank @Pattern(regexp = "^\\d{4}$", message = "debe tener 4 dígitos") String accessKey) {
}
