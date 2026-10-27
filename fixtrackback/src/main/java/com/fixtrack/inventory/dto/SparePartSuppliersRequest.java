package com.fixtrack.inventory.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

// Lista completa de proveedores del repuesto: reemplaza la anterior (vacía los quita todos); lastCost null conserva el actual
public record SparePartSuppliersRequest(@NotNull List<@Valid @NotNull SparePartSupplierRequest> suppliers) {
}
