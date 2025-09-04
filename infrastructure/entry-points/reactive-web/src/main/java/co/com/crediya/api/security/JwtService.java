package co.com.crediya.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationTimeInHours;

    public JwtService(
            @Value("${jwt.secret:mySecretKey123456789012345678901234567890}") String secretKeyString, // Obtiene los env o uso los datos por defecto
            @Value("${jwt.expiration:24}") long expirationTimeInHours // Obtiene los env o uso los datos por defecto
    ) {
        this.expirationTimeInHours = expirationTimeInHours;

        // Si no se proporciona una clave o es muy corta, generar una clave segura para HS512
        if (secretKeyString == null || secretKeyString.trim().isEmpty() || secretKeyString.getBytes().length < 64) {
            log.warn("JWT secret key no configurada o muy corta. Generando clave segura para HS512...");
            this.secretKey = Jwts.SIG.HS512.key().build();
        } else {
            // Verificar que la clave proporcionada sea lo suficientemente larga
            byte[] keyBytes = secretKeyString.getBytes();
            if (keyBytes.length < 64) {
                log.warn("JWT secret key muy corta ({} bytes). Generando clave segura para HS512...", keyBytes.length);
                this.secretKey = Jwts.SIG.HS512.key().build();
            } else {
                this.secretKey = Keys.hmacShaKeyFor(keyBytes);
            }
        }

        log.info("JwtService inicializado correctamente con clave segura para HS512");
    }

    public String generateToken(Long userId, String email, String nombre, String apellido, String rolNombre, String documentoIdentidad) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationTimeInHours, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("nombre", nombre)
                .claim("apellido", apellido)
                .claim("rol", rolNombre)
                .claim("documentoIdentidad", documentoIdentidad)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
            throw new RuntimeException("Token inválido", e);
        }
    }

    public String getEmailFromToken(String token) {
        return validateToken(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return validateToken(token).get("userId", Long.class);
    }

    public String getRolFromToken(String token) {
        return validateToken(token).get("rol", String.class);
    }

    public String getDocumentoIdentidadFromToken(String token) {
        return validateToken(token).get("documentoIdentidad", String.class);
    }

    public boolean isTokenExpired(String token) {
        try {
            return validateToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
