package com.fixtrack.intervention.dto;

import jakarta.validation.constraints.Size;

public record DeliveryRequest(@Size(max = 1000) String notes) {
}
