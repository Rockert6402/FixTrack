package com.fixtrack.person.dto;

import com.fixtrack.person.entity.Availability;

public record TechnicianSummary(Long id, String fullName, Availability availability) {
}
