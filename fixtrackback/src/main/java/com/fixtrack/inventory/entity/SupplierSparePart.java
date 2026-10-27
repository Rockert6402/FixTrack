package com.fixtrack.inventory.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Repuestos que vende cada proveedor, con su referencia, último costo y tiempo de entrega
@Entity
@Table(name = "supplier_spare_part")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupplierSparePart {

	@EmbeddedId
	private SupplierSparePartId id;

	@MapsId("supplierId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@MapsId("sparePartId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "spare_part_id")
	private SparePart sparePart;

	@Column(name = "supplier_sku", length = 50)
	private String supplierSku;

	@Column(name = "last_cost", precision = 12, scale = 2)
	private BigDecimal lastCost;

	@Column(name = "lead_time_days")
	private Integer leadTimeDays;

	@Column(name = "preferred", nullable = false)
	private boolean preferred = false;

	public SupplierSparePart(Supplier supplier, SparePart sparePart) {
		this.id = new SupplierSparePartId(supplier.getId(), sparePart.getId());
		this.supplier = supplier;
		this.sparePart = sparePart;
	}
}
