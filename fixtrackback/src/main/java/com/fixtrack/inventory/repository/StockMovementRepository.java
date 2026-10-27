package com.fixtrack.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.inventory.entity.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}
