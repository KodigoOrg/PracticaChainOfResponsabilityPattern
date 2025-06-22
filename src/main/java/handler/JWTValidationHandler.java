package handler;

import model.AuthRequest;
import model.AuthResult;
import model.User;
import service.JWTManager;
import service.UserDatabase;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class JWTValidationHandler extends AuthHandler {
    private JWTManager jwtManager;

    public JWTValidationHandler() {
        super("JWTValidation");
        this.jwtManager = JWTManager.getInstance();
    }

    @Override
    public AuthResult handle(AuthRequest request, AuthResult currentResult) {
        String token = request.getJwtToken();

        if (token == null || token.isEmpty()) {
            log("❌ Token JWT nulo o vacío para validación.");
            return currentResult.fail("Token JWT es nulo o vacío.");
        }

        log("Validando token JWT...");
        if (!jwtManager.verifyToken(token)) {
            log("❌ Token JWT inválido o expirado.");
            return currentResult.fail("Token JWT inválido o expirado.");
        }

        Map<String, Object> claims = jwtManager.getClaims(token);
        if (claims == null || !claims.containsKey("sub")) {
            log("❌ Token JWT válido, pero sin claim 'sub' (sujeto).");
            return currentResult.fail("Token JWT válido, pero sin información de usuario (claim 'sub').");
        }

        String username = (String) claims.get("sub");
        User user = UserDatabase.findByUsername(username);

        if (user == null) {
            log("❌ Usuario asociado al token '" + username + "' no encontrado en la base de datos.");
            return currentResult.fail("Usuario asociado al token no encontrado.");
        }

        if (!user.isActive()) {
            log("❌ Usuario '" + username + "' asociado al token está inactivo.");
            return currentResult.fail("Usuario asociado al token está inactivo.");
        }

        if (user.isLockedOut()) {
            String lockoutTime = user.getLockoutUntil().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            log("❌ Usuario '" + username + "' asociado al token está bloqueado hasta: " + lockoutTime);
            return currentResult.fail("Usuario asociado al token está bloqueado temporalmente hasta " + lockoutTime + ".");
        }

        log("✅ Token JWT válido para usuario: " + user.getUsername());
        currentResult.continueSuccess();
        return currentResult.success(user, token);
    }
}
