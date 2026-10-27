package com.fixtrack.infra.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// Cifra con AES-GCM los textos sensibles antes de guardarlos y los descifra al leerlos
@Component
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH_BITS = 128;

	private final SecretKey key;
	private final SecureRandom random = new SecureRandom();

	public EncryptedStringConverter(@Value("${fixtrack.encryption-key}") String base64Key) {
		byte[] keyBytes = Base64.getDecoder().decode(base64Key);
		if (keyBytes.length != 32) {
			throw new IllegalStateException("ENCRYPTION_KEY debe ser una llave de 32 bytes codificada en Base64");
		}
		this.key = new SecretKeySpec(keyBytes, "AES");
	}

	// Guarda el IV aleatorio junto al texto cifrado, todo en Base64
	@Override
	public String convertToDatabaseColumn(String plainText) {
		if (plainText == null) {
			return null;
		}
		try {
			byte[] iv = new byte[IV_LENGTH];
			random.nextBytes(iv);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
			byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			byte[] stored = ByteBuffer.allocate(iv.length + encrypted.length).put(iv).put(encrypted).array();
			return Base64.getEncoder().encodeToString(stored);
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("No se pudo cifrar el valor", e);
		}
	}

	@Override
	public String convertToEntityAttribute(String storedValue) {
		if (storedValue == null) {
			return null;
		}
		try {
			byte[] stored = Base64.getDecoder().decode(storedValue);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, stored, 0, IV_LENGTH));
			byte[] plain = cipher.doFinal(stored, IV_LENGTH, stored.length - IV_LENGTH);
			return new String(plain, StandardCharsets.UTF_8);
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("No se pudo descifrar el valor", e);
		}
	}
}
