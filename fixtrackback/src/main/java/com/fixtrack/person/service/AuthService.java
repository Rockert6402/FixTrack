package com.fixtrack.person.service;

import com.fixtrack.infra.security.AuthenticatedUser;
import com.fixtrack.person.dto.ChangePasswordRequest;
import com.fixtrack.person.dto.LoginRequest;
import com.fixtrack.person.dto.LoginResponse;

public interface AuthService {

	AuthenticatedUser authenticate(String email, String password);

	LoginResponse login(LoginRequest request);

	void changePassword(AuthenticatedUser actor, ChangePasswordRequest request);
}
