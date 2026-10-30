package com.fixtrack.person.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Datos de contacto del cliente que corrige la recepción; cada uno se llena o se marca «No tiene»
public record CustomerContactRequest(
		@Pattern(regexp = "^(\\s*\\d{7,10}\\s*)?$", message = "debe tener entre 7 y 10 dígitos, solo números") String phone,
		Boolean noPhone,
		@Email @Size(max = 150) String email,
		Boolean noEmail) {
}
