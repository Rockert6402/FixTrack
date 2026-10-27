package com.fixtrack.intervention.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fixtrack.intervention.dto.ReceiptFile;
import com.fixtrack.intervention.dto.portal.PortalLookupRequest;
import com.fixtrack.intervention.dto.portal.PortalOrderResponse;
import com.fixtrack.intervention.service.PortalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Portal público del cliente, sin token: cada petición se valida con el código y la clave de consulta
@RestController
@RequestMapping("/api/public/interventions")
@RequiredArgsConstructor
public class PortalController {

	private final PortalService portalService;

	@PostMapping("/lookup")
	public PortalOrderResponse lookup(@Valid @RequestBody PortalLookupRequest request, HttpServletRequest http) {
		return portalService.lookup(request, http.getRemoteAddr());
	}

	@PostMapping("/receipts/intake")
	public ResponseEntity<byte[]> intakeReceipt(@Valid @RequestBody PortalLookupRequest request,
			HttpServletRequest http) {
		return pdf(portalService.intakeReceipt(request, http.getRemoteAddr()));
	}

	@PostMapping("/receipts/delivery")
	public ResponseEntity<byte[]> deliveryReceipt(@Valid @RequestBody PortalLookupRequest request,
			HttpServletRequest http) {
		return pdf(portalService.deliveryReceipt(request, http.getRemoteAddr()));
	}

	private ResponseEntity<byte[]> pdf(ReceiptFile file) {
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment().filename(file.fileName()).build().toString())
				.body(file.content());
	}
}
