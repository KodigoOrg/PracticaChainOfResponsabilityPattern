package service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTManager {
    private static final String SECRET_KEY = "esta_es_una_clave_secreta_muy_segura_y_larga_para_jwt";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
    private static JWTManager instance;

    private JWTManager() {}

    public static JWTManager getInstance() {
        if (instance == null) {
            instance = new JWTManager();
        }
        return instance;
    }

    public String generateToken(User user, long expirationMinutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(expirationMinutes);

        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(now.toInstant(ZoneOffset.UTC))
                .withExpiresAt(expirationTime.toInstant(ZoneOffset.UTC))
                .withClaim("role", user.getRole().getRoleName())
                .withClaim("permissions", user.getRole().getPermissions())
                .sign(algorithm);
    }

    public boolean verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception){
            return false;
        }
    }

    public Map<String, Object> getClaims(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Object> claimsMap = new HashMap<>();
            jwt.getClaims().forEach((key, value) -> {
                if (value.asString() != null) claimsMap.put(key, value.asString());
                else if (value.asLong() != null) claimsMap.put(key, value.asLong());
                else if (value.asBoolean() != null) claimsMap.put(key, value.asBoolean());
                else if (value.asList(String.class) != null) claimsMap.put(key, value.asList(String.class));
            });
            return claimsMap;
        } catch (JWTVerificationException exception){
            return null;
        }
    }

    public long getTimeUntilExpirationInSeconds(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt == null) {
                return 0;
            }
            long currentTimeMillis = System.currentTimeMillis();
            long expirationTimeMillis = expiresAt.getTime();
            long remainingSeconds = (expirationTimeMillis - currentTimeMillis) / 1000;
            return Math.max(0, remainingSeconds);
        } catch (JWTVerificationException exception) {
            return 0;
        }
    }
}