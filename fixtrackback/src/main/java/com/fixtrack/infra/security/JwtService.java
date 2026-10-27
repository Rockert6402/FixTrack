package com.fixtrack.infra.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

// Emite y verifica los tokens de sesión firmados con HMAC-SHA256; la llave y la duración llegan por variables de entorno
@Component
public class JwtService {

	public static final String ROLE_CLAIM = "role";
	public static final String USER_ID_CLAIM = "uid";
	private static final String ISSUER = "fixtrack";

	private final NimbusJwtEncoder encoder;
	private final NimbusJwtDecoder decoder;
	private final Duration expiration;
	private final Clock clock;

	public JwtService(@Value("${fixtrack.jwt.secret}") String base64Secret,
			@Value("${fixtrack.jwt.expiration}") Duration expiration, Clock clock) {
		byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
		if (keyBytes.length < 32) {
			throw new IllegalStateException("JWT_SECRET debe ser una llave de al menos 32 bytes codificada en Base64");
		}
		SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
		this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
		this.decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
		// Sin tolerancia: el token deja de servir en el instante en que expira
		JwtTimestampValidator timestampValidator = new JwtTimestampValidator(Duration.ZERO);
		timestampValidator.setClock(clock);
		this.decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(timestampValidator,
				new JwtIssuerValidator(ISSUER)));
		this.expiration = expiration;
		this.clock = clock;
	}

	public IssuedToken issue(AuthenticatedUser user) {
		Instant now = clock.instant();
		Instant expiresAt = now.plus(expiration);
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(ISSUER)
				.subject(user.email())
				.issuedAt(now)
				.expiresAt(expiresAt)
				.claim(USER_ID_CLAIM, user.id())
				.claim(ROLE_CLAIM, user.role().name())
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return new IssuedToken(encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue(), expiresAt);
	}

	// Lanza JwtException si la firma no coincide, el token expiró o no lo emitió FixTrack
	public Jwt decode(String token) throws JwtException {
		return decoder.decode(token);
	}
}
