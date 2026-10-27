package com.fixtrack.inventory.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.common.PageResponse;
import com.fixtrack.infra.security.CurrentUserProvider;
import com.fixtrack.inventory.dto.CompatibilitiesRequest;
import com.fixtrack.inventory.dto.CompatibleModelResponse;
import com.fixtrack.inventory.dto.CreateSparePartRequest;
import com.fixtrack.inventory.dto.SparePartFilter;
import com.fixtrack.inventory.dto.SparePartResponse;
import com.fixtrack.inventory.dto.SparePartSupplierResponse;
import com.fixtrack.inventory.dto.SparePartSuppliersRequest;
import com.fixtrack.inventory.dto.StockEntryRequest;
import com.fixtrack.inventory.dto.StockEntryResponse;
import com.fixtrack.inventory.dto.UpdateSparePartRequest;
import com.fixtrack.inventory.entity.PartQuality;
import com.fixtrack.inventory.entity.StockStatus;
import com.fixtrack.inventory.service.SparePartLinkService;
import com.fixtrack.inventory.service.SparePartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/spare-parts")
@RequiredArgsConstructor
public class SparePartController {

	private final SparePartService sparePartService;
	private final SparePartLinkService sparePartLinkService;
	private final CurrentUserProvider currentUserProvider;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public PageResponse<SparePartResponse> list(@RequestParam(required = false) String search,
			@RequestParam(required = false) Long partTypeId,
			@RequestParam(required = false) Long deviceModelId,
			@RequestParam(required = false) Long brandId,
			@RequestParam(required = false) PartQuality quality,
			@RequestParam(required = false) StockStatus stockStatus,
			@RequestParam(defaultValue = "false") boolean alertsOnly,
			@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
		return sparePartService.search(
				new SparePartFilter(search, partTypeId, deviceModelId, brandId, quality, stockStatus, alertsOnly),
				pageable);
	}

	@GetMapping("/low-stock")
	@PreAuthorize("hasRole('COORDINATOR')")
	public List<SparePartResponse> lowStock() {
		return sparePartService.findLowStock();
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public SparePartResponse get(@PathVariable Long id) {
		return sparePartService.findById(id);
	}

	@PostMapping
	@PreAuthorize("hasRole('COORDINATOR')")
	public ResponseEntity<SparePartResponse> create(@Valid @RequestBody CreateSparePartRequest request) {
		SparePartResponse created = sparePartService.create(request);
		return ResponseEntity.created(URI.create("/api/spare-parts/" + created.id())).body(created);
	}

	@PatchMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	public SparePartResponse update(@PathVariable Long id, @Valid @RequestBody UpdateSparePartRequest request) {
		return sparePartService.update(id, request);
	}

	@PostMapping("/{id}/entries")
	@PreAuthorize("hasRole('COORDINATOR')")
	@ResponseStatus(HttpStatus.CREATED)
	public StockEntryResponse registerEntry(@PathVariable Long id, @Valid @RequestBody StockEntryRequest request) {
		return sparePartService.registerEntry(id, request, currentUserProvider.require());
	}

	@GetMapping("/{id}/suppliers")
	@PreAuthorize("hasRole('COORDINATOR')")
	public List<SparePartSupplierResponse> suppliers(@PathVariable Long id) {
		return sparePartLinkService.findSuppliers(id);
	}

	@PutMapping("/{id}/suppliers")
	@PreAuthorize("hasRole('COORDINATOR')")
	public List<SparePartSupplierResponse> replaceSuppliers(@PathVariable Long id,
			@Valid @RequestBody SparePartSuppliersRequest request) {
		return sparePartLinkService.replaceSuppliers(id, request);
	}

	@GetMapping("/{id}/compatibilities")
	@PreAuthorize("isAuthenticated()")
	public List<CompatibleModelResponse> compatibilities(@PathVariable Long id) {
		return sparePartLinkService.findCompatibilities(id);
	}

	@PutMapping("/{id}/compatibilities")
	@PreAuthorize("hasRole('COORDINATOR')")
	public List<CompatibleModelResponse> replaceCompatibilities(@PathVariable Long id,
			@Valid @RequestBody CompatibilitiesRequest request) {
		return sparePartLinkService.replaceCompatibilities(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('COORDINATOR')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deactivate(@PathVariable Long id) {
		sparePartService.deactivate(id);
	}
}
