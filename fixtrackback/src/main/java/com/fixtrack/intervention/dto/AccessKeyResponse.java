package com.fixtrack.intervention.dto;

// code es el de la orden con que se consulta en el portal (en garantías, el de la original)
public record AccessKeyResponse(String code, String accessKey) {
}
