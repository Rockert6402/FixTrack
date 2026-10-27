package com.fixtrack.intervention.service;

import com.fixtrack.intervention.dto.ReceiptFile;
import com.fixtrack.intervention.dto.portal.PortalLookupRequest;
import com.fixtrack.intervention.dto.portal.PortalOrderResponse;

// Consulta pública del cliente con código y clave; todo fallo responde el mismo mensaje genérico
public interface PortalService {

	PortalOrderResponse lookup(PortalLookupRequest request, String clientIp);

	ReceiptFile intakeReceipt(PortalLookupRequest request, String clientIp);

	ReceiptFile deliveryReceipt(PortalLookupRequest request, String clientIp);
}
