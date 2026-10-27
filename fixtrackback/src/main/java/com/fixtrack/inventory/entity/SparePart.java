package com.fixtrack.inventory.entity;

import java.math.BigDecimal;

import com.fixtrack.device.entity.DeviceModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Repuesto del inventario; cada calidad es un repuesto distinto con su propio SKU, stock y precio
@Entity
@Table(name = "spare_part", uniqueConstraints = {
		@UniqueConstraint(name = "uk_spare_part_sku", columnNames = "sku"),
		@UniqueConstraint(name = "uk_spare_part_type_model_quality", columnNames = { "part_type_id",
				"device_model_id", "quality" }) })
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SparePart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "sku", nullable = false, length = 50)
	private String sku;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "part_type_id", nullable = false)
	private PartType partType;

	// Modelo principal; los otros modelos compatibles están en SparePartCompatibility
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "device_model_id", nullable = false)
	private DeviceModel deviceModel;

	@Enumerated(EnumType.STRING)
	@Column(name = "quality", length = 20)
	private PartQuality quality;

	@Builder.Default
	@Column(name = "stock", nullable = false)
	private int stock = 0;

	@Builder.Default
	@Column(name = "min_stock", nullable = false)
	private int minStock = 0;

	@Column(name = "cost_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal costPrice;

	@Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal salePrice;

	@Builder.Default
	@Column(name = "active", nullable = false)
	private boolean active = true;

	// Bloqueo optimista para que dos usos simultáneos no descuadren el stock
	@Version
	@Column(name = "version")
	private Long version;
}
