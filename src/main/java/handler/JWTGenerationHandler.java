package handler;

import model.AuthRequest;
import model.AuthResult;
import model.User;
import service.JWTManager;

public class JWTGenerationHandler extends AuthHandler {
    private JWTManager jwtManager;

    public JWTGenerationHandler() {
        super("JWTGeneration");
        this.jwtManager = JWTManager.getInstance();
    }

    @Override
    public AuthResult handle(AuthRequest request, AuthResult currentResult) {
        User user = currentResult.getUser();
        if (user == null) {
            log("Error crítico: Usuario no disponible para generar JWT.");
            return currentResult.fail("Error interno al generar el token.");
        }

        log("Generando JWT para: " + user.getUsername());
        try {
            String token = jwtManager.generateToken(user, 60);
            log("Token JWT generado exitosamente.");

            return currentResult.success(user, token);
        } catch (Exception e) {
            String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : "Error desconocido al generar JWT.";
            log("Error al generar JWT: " + errorMessage);
            return currentResult.fail("Error interno al generar el token de sesión: " + errorMessage);
        }
    }
}
