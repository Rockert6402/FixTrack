package com.fixtrack.inventory.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SupplierSparePartId implements Serializable {

	@Column(name = "supplier_id")
	private Long supplierId;

	@Column(name = "spare_part_id")
	private Long sparePartId;
}
