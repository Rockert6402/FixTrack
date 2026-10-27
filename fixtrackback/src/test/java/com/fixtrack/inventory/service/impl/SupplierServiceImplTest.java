package com.fixtrack.inventory.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fixtrack.common.BusinessRuleException;
import com.fixtrack.common.ConflictException;
import com.fixtrack.inventory.dto.SupplierRequest;
import com.fixtrack.inventory.dto.SupplierResponse;
import com.fixtrack.inventory.entity.Supplier;
import com.fixtrack.inventory.mapper.SupplierMapper;
import com.fixtrack.inventory.repository.SupplierRepository;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

	@Mock
	private SupplierRepository supplierRepository;

	@Spy
	private SupplierMapper supplierMapper;

	@InjectMocks
	private SupplierServiceImpl supplierService;

	@Test
	void shouldNormalizeTaxIdBeforeSaving() {
		given(supplierRepository.findByTaxId("900123456-7")).willReturn(Optional.empty());
		given(supplierRepository.save(any(Supplier.class))).willAnswer(invocation -> invocation.getArgument(0));

		SupplierResponse created = supplierService.create(
				new SupplierRequest(" Repuestos Andinos ", "900.123 456-7", null, "", "VENTAS@Andinos.co"));

		assertThat(created.taxId()).isEqualTo("900123456-7");
		assertThat(created.name()).isEqualTo("Repuestos Andinos");
		assertThat(created.phone()).isNull();
		assertThat(created.email()).isEqualTo("ventas@andinos.co");
	}

	@Test
	void shouldRejectTaxIdOfDeactivatedSupplier() {
		Supplier existing = Supplier.builder().id(4L).name("Viejo proveedor").taxId("900123456-7").active(false).build();
		given(supplierRepository.findByTaxId("900123456-7")).willReturn(Optional.of(existing));

		assertThatThrownBy(() -> supplierService.create(new SupplierRequest("Nuevo", "900123456-7", null, null, null)))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("(dado de baja)");
		verify(supplierRepository, never()).save(any());
	}

	@Test
	void shouldRejectMalformedTaxId() {
		assertThatThrownBy(() -> supplierService.create(new SupplierRequest("Nuevo", "ABC-12", null, null, null)))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessageContaining("NIT");
	}

	@Test
	void shouldAllowKeepingOwnTaxIdOnUpdate() {
		Supplier supplier = Supplier.builder().id(4L).name("Andinos").taxId("900123456-7").build();
		given(supplierRepository.findById(4L)).willReturn(Optional.of(supplier));
		given(supplierRepository.findByTaxId("900123456-7")).willReturn(Optional.of(supplier));

		SupplierResponse updated = supplierService.update(4L,
				new SupplierRequest("Andinos SAS", "900123456-7", "Marta", "3001234567", null));

		assertThat(updated.name()).isEqualTo("Andinos SAS");
		assertThat(updated.contactName()).isEqualTo("Marta");
	}
}
