package com.fixtrack.person.dto;

import com.fixtrack.person.entity.Role;

public record AuthUserResponse(Long id, String fullName, String email, Role role) {
}
