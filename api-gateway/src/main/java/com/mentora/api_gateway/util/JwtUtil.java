package com.mentora.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class JwtUtil {

    // Pastikan secret key ini SAMA PERSIS dengan yang ada di user-service
    @Value("${secret.key}")
    private String secretKey;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        var keys = Keys.hmacShaKeyFor(keyBytes);
        return keys;
    }

    public void validateToken(final String token) {
        Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
    }

    public String extractUserId(String token) {
        Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        return claims.getSubject(); // Asumsinya userId disimpan di 'subject' saat token dibuat
    }
}