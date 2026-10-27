package com.fixtrack.intervention.dto;

// accessKey solo se entrega aquí, una vez, para imprimirla en el comprobante; en la base queda su hash
public record CreatedInterventionResponse(InterventionDetailResponse intervention, String accessKey,
		String receiptWarning) {
}
