package com.fixtrack.intervention.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fixtrack.inventory.entity.SparePart;
import com.fixtrack.person.entity.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Repuesto usado en una orden, con el precio y el costo congelados al momento del uso
@Entity
@Table(name = "intervention_spare_part")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InterventionSparePart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "intervention_id", nullable = false)
	private Intervention intervention;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "spare_part_id", nullable = false)
	private SparePart sparePart;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	// Precio de venta al momento del uso
	@Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal unitPrice;

	// Costo al momento del uso
	@Column(name = "cost_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal costPrice;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by_id", nullable = false)
	private Person createdBy;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}
