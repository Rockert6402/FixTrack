package com.fixtrack.inventory.dto;

// consumedInOrder es false si la orden ya no estaba en trabajo: entonces el repuesto quedó disponible en el stock
public record PartReceiptResponse(PartRequestResponse request, boolean consumedInOrder, int stockAfter) {
}
