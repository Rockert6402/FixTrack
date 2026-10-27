package com.fixtrack.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixtrack.inventory.entity.PartRequest;

public interface PartRequestRepository extends JpaRepository<PartRequest, Long> {
}
