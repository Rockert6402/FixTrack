package com.fixtrack.inventory.entity;

// Estado del stock frente al mínimo: OUT sin unidades, LOW en el mínimo o por debajo, OK por encima
public enum StockStatus {
	OK, LOW, OUT;

	public static StockStatus of(int stock, int minStock) {
		if (stock <= 0) {
			return OUT;
		}
		return stock <= minStock ? LOW : OK;
	}
}
