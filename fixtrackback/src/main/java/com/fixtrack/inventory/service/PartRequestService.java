package com.fixtrack.inventory.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.fixtrack.common.PageResponse;
import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.inventory.dto.ApprovePartRequestRequest;
import com.fixtrack.inventory.dto.CreatePartRequestRequest;
import com.fixtrack.inventory.dto.OrderPartRequestRequest;
import com.fixtrack.inventory.dto.PartReceiptResponse;
import com.fixtrack.inventory.dto.PartRequestResponse;
import com.fixtrack.inventory.dto.ReceivePartRequestRequest;
import com.fixtrack.inventory.dto.RejectPartRequestRequest;
import com.fixtrack.inventory.entity.PartRequestStatus;

public interface PartRequestService {

	PartRequestResponse create(Long interventionId, CreatePartRequestRequest request, AuthenticatedUser actor);

	PageResponse<PartRequestResponse> search(PartRequestStatus status, Pageable pageable);

	PartRequestResponse findById(Long id);

	PageResponse<PartRequestResponse> findMine(AuthenticatedUser actor, PartRequestStatus status, Pageable pageable);

	List<PartRequestResponse> findByIntervention(Long interventionId, AuthenticatedUser actor);

	PartRequestResponse approve(Long id, ApprovePartRequestRequest request, AuthenticatedUser actor);

	PartRequestResponse reject(Long id, RejectPartRequestRequest request, AuthenticatedUser actor);

	PartRequestResponse order(Long id, OrderPartRequestRequest request, AuthenticatedUser actor);

	PartReceiptResponse receive(Long id, ReceivePartRequestRequest request, AuthenticatedUser actor);

	// Pendientes o aprobadas, aún sin pedir al proveedor; las cancela cuando se cancela su orden
	int cancelNotOrdered(Long interventionId);

	// Solicitudes que esperan al coordinador (pendientes o aprobadas sin pedir), las más antiguas primero
	List<PartRequestResponse> findAwaitingCoordinator(int limit);

	long countAwaitingCoordinator();
}
