package com.fixtrack.inventory.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;

import com.fixtrack.intervention.entity.Intervention;
import com.fixtrack.person.entity.Person;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Kárdex del inventario: cada entrada o salida de stock; los movimientos no se editan
@Entity
@Immutable
@Table(name = "stock_movement")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StockMovement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "spare_part_id", nullable = false)
	private SparePart sparePart;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 20)
	private StockMovementType type;

	// Positiva si entra, negativa si sale
	@Column(name = "quantity", nullable = false)
	private int quantity;

	// Stock del repuesto después del movimiento
	@Column(name = "balance_after", nullable = false)
	private int balanceAfter;

	@Column(name = "unit_cost", precision = 12, scale = 2)
	private BigDecimal unitCost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "intervention_id")
	private Intervention intervention;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "part_request_id")
	private PartRequest partRequest;

	@Column(name = "reason", length = 500)
	private String reason;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by_id", nullable = false)
	private Person createdBy;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}
