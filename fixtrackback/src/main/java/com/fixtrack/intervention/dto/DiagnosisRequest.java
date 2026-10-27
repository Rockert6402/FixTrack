package com.fixtrack.intervention.dto;

import jakarta.validation.constraints.Size;

// Diagnóstico y solución aplicada; los campos que no se envían conservan su valor
public record DiagnosisRequest(@Size(max = 2000) String diagnosis, @Size(max = 2000) String solution) {
}
