package com.fixtrack.inventory.service;

import java.math.BigDecimal;

import com.fixtrack.intervention.entity.Intervention;
import com.fixtrack.inventory.entity.SparePart;
import com.fixtrack.inventory.entity.StockMovement;
import com.fixtrack.person.entity.Person;

public interface StockService {

	// Descuenta el stock por uso en una orden y registra el movimiento OUT_REPAIR; devuelve el repuesto actualizado
	SparePart consumeForRepair(Long sparePartId, int quantity, Intervention intervention, Person actor);

	// Restituye el stock al eliminar un consumo y registra el movimiento RETURN con el costo del momento del uso
	SparePart returnFromRepair(Long sparePartId, int quantity, BigDecimal unitCost, Intervention intervention,
			Person actor);

	// Corrige la cantidad de un consumo por la diferencia: si sube sale stock (OUT_REPAIR), si baja vuelve (RETURN)
	SparePart correctRepairUsage(Long sparePartId, int difference, BigDecimal unitCost, Intervention intervention,
			Person actor);

	// Entrada de mercancía: suma stock y registra el movimiento IN_PURCHASE con su costo, motivo y responsable
	StockMovement registerPurchase(Long sparePartId, int quantity, BigDecimal unitCost, String reason, Person actor);
}
