package com.fixtrack.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier", uniqueConstraints = @UniqueConstraint(name = "uk_supplier_tax_id", columnNames = "tax_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Supplier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	// NIT
	@Column(name = "tax_id", nullable = false, length = 20)
	private String taxId;

	@Column(name = "contact_name", length = 150)
	private String contactName;

	@Column(name = "phone", length = 20)
	private String phone;

	@Column(name = "email", length = 150)
	private String email;

	@Builder.Default
	@Column(name = "active", nullable = false)
	private boolean active = true;
}
