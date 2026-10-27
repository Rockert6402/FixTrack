package com.fixtrack.intervention.dto;

import java.time.LocalDateTime;

import com.fixtrack.intervention.entity.ApprovalStatus;
import com.fixtrack.intervention.entity.FindingOrigin;
import com.fixtrack.intervention.entity.ServiceType;

public record FindingResponse(Long id, ServiceType serviceType, String description, FindingOrigin origin,
		ApprovalStatus approvalStatus, PersonRef respondedBy, LocalDateTime respondedAt, LocalDateTime createdAt) {
}
