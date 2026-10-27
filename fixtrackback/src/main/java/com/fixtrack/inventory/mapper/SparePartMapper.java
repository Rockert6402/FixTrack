package com.fixtrack.inventory.mapper;

import org.springframework.stereotype.Component;

import com.fixtrack.device.entity.DeviceModel;
import com.fixtrack.inventory.dto.SparePartCandidateResponse;
import com.fixtrack.inventory.dto.SparePartResponse;
import com.fixtrack.inventory.dto.StockMovementResponse;
import com.fixtrack.inventory.entity.SparePart;
import com.fixtrack.inventory.entity.StockMovement;
import com.fixtrack.inventory.entity.StockStatus;

@Component
public class SparePartMapper {

	public SparePartResponse toResponse(SparePart part) {
		DeviceModel model = part.getDeviceModel();
		return new SparePartResponse(
				part.getId(),
				part.getName(),
				part.getDescription(),
				part.getSku(),
				part.getPartType().getId(),
				part.getPartType().getName(),
				model.getId(),
				model.getName(),
				model.getBrand().getName(),
				model.getDeviceType(),
				part.getQuality(),
				part.getStock(),
				part.getMinStock(),
				StockStatus.of(part.getStock(), part.getMinStock()),
				part.getCostPrice(),
				part.getSalePrice());
	}

	public SparePartCandidateResponse toCandidate(SparePart part, boolean compatible) {
		DeviceModel model = part.getDeviceModel();
		return new SparePartCandidateResponse(part.getId(), part.getName(), part.getSku(), part.getPartType().getName(),
				model.getName(), model.getBrand().getName(), part.getQuality(), part.getStock(), part.getSalePrice(),
				compatible);
	}

	public StockMovementResponse toMovement(StockMovement movement) {
		return new StockMovementResponse(movement.getId(), movement.getType(), movement.getQuantity(),
				movement.getBalanceAfter(), movement.getUnitCost(), movement.getReason(),
				movement.getCreatedBy().getId(), movement.getCreatedBy().getFullName(), movement.getCreatedAt());
	}
}
