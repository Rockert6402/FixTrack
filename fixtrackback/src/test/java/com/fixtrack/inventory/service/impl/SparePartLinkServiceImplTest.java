package com.fixtrack.inventory.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fixtrack.common.BusinessRuleException;
import com.fixtrack.common.ConflictException;
import com.fixtrack.device.entity.DeviceModel;
import com.fixtrack.device.service.DeviceModelService;
import com.fixtrack.inventory.dto.CompatibilitiesRequest;
import com.fixtrack.inventory.dto.SparePartSupplierRequest;
import com.fixtrack.inventory.dto.SparePartSuppliersRequest;
import com.fixtrack.inventory.entity.SparePart;
import com.fixtrack.inventory.entity.Supplier;
import com.fixtrack.inventory.mapper.SupplierMapper;
import com.fixtrack.inventory.repository.SparePartCompatibilityRepository;
import com.fixtrack.inventory.repository.SparePartRepository;
import com.fixtrack.inventory.repository.SupplierRepository;
import com.fixtrack.inventory.repository.SupplierSparePartRepository;

@ExtendWith(MockitoExtension.class)
class SparePartLinkServiceImplTest {

	@Mock
	private SparePartRepository sparePartRepository;

	@Mock
	private SupplierRepository supplierRepository;

	@Mock
	private SupplierSparePartRepository supplierSparePartRepository;

	@Mock
	private SparePartCompatibilityRepository compatibilityRepository;

	@Mock
	private DeviceModelService deviceModelService;

	@Spy
	private SupplierMapper supplierMapper;

	@InjectMocks
	private SparePartLinkServiceImpl linkService;

	@BeforeEach
	void setUp() {
		SparePart part = SparePart.builder().id(4L).name("Pin de carga")
				.deviceModel(DeviceModel.builder().id(1L).name("Galaxy A54").build()).build();
		given(sparePartRepository.findByIdAndActiveTrue(4L)).willReturn(Optional.of(part));
	}

	@Test
	void shouldRejectMoreThanOnePreferredSupplier() {
		SparePartSuppliersRequest request = new SparePartSuppliersRequest(List.of(
				new SparePartSupplierRequest(1L, null, null, null, true),
				new SparePartSupplierRequest(2L, null, null, null, true)));

		assertThatThrownBy(() -> linkService.replaceSuppliers(4L, request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessage("Solo un proveedor puede ser el preferido");
		verify(supplierSparePartRepository, never()).save(any());
	}

	@Test
	void shouldRejectRepeatedSupplier() {
		SparePartSuppliersRequest request = new SparePartSuppliersRequest(List.of(
				new SparePartSupplierRequest(1L, "A", null, null, false),
				new SparePartSupplierRequest(1L, "B", null, null, false)));

		assertThatThrownBy(() -> linkService.replaceSuppliers(4L, request))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessageContaining("repetido");
	}

	@Test
	void shouldRejectNewLinkToDeactivatedSupplier() {
		given(supplierSparePartRepository.findBySparePartId(4L)).willReturn(List.of());
		given(supplierRepository.findById(7L)).willReturn(
				Optional.of(Supplier.builder().id(7L).name("Cerrado SAS").taxId("800").active(false).build()));
		SparePartSuppliersRequest request = new SparePartSuppliersRequest(List.of(
				new SparePartSupplierRequest(7L, null, new BigDecimal("10000"), 3, true)));

		assertThatThrownBy(() -> linkService.replaceSuppliers(4L, request))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("dado de baja");
	}

	@Test
	void shouldRejectMainModelAsCompatibility() {
		assertThatThrownBy(() -> linkService.replaceCompatibilities(4L, new CompatibilitiesRequest(List.of(2L, 1L))))
				.isInstanceOf(BusinessRuleException.class)
				.hasMessageContaining("modelo principal");
		verify(compatibilityRepository, never()).save(any());
	}
}
