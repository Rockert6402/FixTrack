package com.fixtrack.infra.security;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fixtrack.common.UnauthorizedException;

// Entrega el usuario que hace la petición; require() responde 401 si la petición no trae credenciales
@Component
public class CurrentUserProvider {

	public Optional<AuthenticatedUser> find() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
			return Optional.of(user);
		}
		return Optional.empty();
	}

	public AuthenticatedUser require() {
		return find().orElseThrow(() -> new UnauthorizedException("Debe iniciar sesión para realizar esta acción"));
	}
}
