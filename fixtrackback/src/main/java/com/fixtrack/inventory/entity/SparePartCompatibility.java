package com.fixtrack.inventory.entity;

import com.fixtrack.device.entity.DeviceModel;

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

// Otros modelos, además del principal, en los que sirve un repuesto
@Entity
@Table(name = "spare_part_compatibility")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SparePartCompatibility {

	@EmbeddedId
	private SparePartCompatibilityId id;

	@MapsId("sparePartId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "spare_part_id")
	private SparePart sparePart;

	@MapsId("deviceModelId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "device_model_id")
	private DeviceModel deviceModel;

	public SparePartCompatibility(SparePart sparePart, DeviceModel deviceModel) {
		this.id = new SparePartCompatibilityId(sparePart.getId(), deviceModel.getId());
		this.sparePart = sparePart;
		this.deviceModel = deviceModel;
	}
}
