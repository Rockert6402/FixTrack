package com.fixtrack.cash.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Cierre de caja diario de un asesor; la diferencia entre lo esperado y lo contado se calcula
@Entity
@Table(name = "cash_closing", uniqueConstraints = @UniqueConstraint(name = "uk_cash_closing_user_date", columnNames = {
		"closed_by_id", "closing_date" }))
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CashClosing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "closed_by_id", nullable = false)
	private Person closedBy;

	@Column(name = "closing_date", nullable = false)
	private LocalDate closingDate;

	@Column(name = "expected_cash", nullable = false, precision = 12, scale = 2)
	private BigDecimal expectedCash;

	@Column(name = "expected_transfer", nullable = false, precision = 12, scale = 2)
	private BigDecimal expectedTransfer;

	@Column(name = "expected_card", nullable = false, precision = 12, scale = 2)
	private BigDecimal expectedCard;

	@Column(name = "counted_cash", nullable = false, precision = 12, scale = 2)
	private BigDecimal countedCash;

	@Column(name = "notes", length = 500)
	private String notes;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}
