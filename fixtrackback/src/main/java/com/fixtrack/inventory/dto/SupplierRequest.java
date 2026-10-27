package com.fixtrack.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// El NIT puede llegar con puntos o espacios (900.123.456-7); el servicio lo normaliza antes de validarlo
public record SupplierRequest(
		@NotBlank @Size(max = 150) String name,
		@NotBlank @Size(max = 20) String taxId,
		@Size(max = 150) String contactName,
		@Size(max = 20) @Pattern(regexp = "^[0-9+()\\s-]*$", message = "solo puede tener números, espacios, +, ( ) y -") String phone,
		@Email @Size(max = 150) String email) {
}
