package com.fixtrack.intervention.service;

import com.fixtrack.intervention.dto.ReceiptFile;

public interface ReceiptService {

	// accessKey es la clave vigente de la orden (recién creada o regenerada); se imprime destacada
	ReceiptFile intakeReceipt(Long id, String accessKey);

	ReceiptFile deliveryReceipt(Long id);
}
