package com.fixtrack.intervention.dto.dashboard;

import com.fixtrack.person.entity.Availability;

public record TechnicianStatusResponse(Long id, String fullName, Availability availability, long openOrders) {
}
