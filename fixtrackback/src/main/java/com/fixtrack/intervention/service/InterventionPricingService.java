package com.fixtrack.intervention.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fixtrack.intervention.entity.Intervention;

// Dinero de la orden que comparten la orden, sus repuestos, la caja y los comprobantes
public interface InterventionPricingService {

	Intervention getEntity(Long id);

	BigDecimal calculateTotal(Intervention intervention);

	// Totales de varias órdenes por id, con una sola consulta de repuestos
	Map<Long, BigDecimal> calculateTotals(List<Intervention> interventions);

	// Lo que el cliente debe pagar: nada si se canceló, el total guardado si se entregó o el total actual si sigue abierta
	BigDecimal amountDue(Intervention intervention);

	void reviewAdditionalFindings(Intervention intervention);
}
