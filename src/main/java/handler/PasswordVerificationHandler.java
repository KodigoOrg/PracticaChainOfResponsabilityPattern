package handler;

import model.AuthRequest;
import model.AuthResult;
import model.User;
import service.UserDatabase;

import java.time.LocalDateTime;

public class PasswordVerificationHandler extends AuthHandler {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 5;

    public PasswordVerificationHandler() {
        super("PasswordVerification");
    }

    @Override
    public AuthResult handle(AuthRequest request, AuthResult currentResult) {
        User user = currentResult.getUser();
        if (user == null) {
            log("Error crítico: Usuario no disponible en PasswordVerificationHandler.");
            return currentResult.fail("Error interno de autenticación.");
        }

        if (request.getPassword() == null) {
            log("Error: Contraseña no proporcionada para verificación.");
            return currentResult.fail("Contraseña no proporcionada.");
        }

        log("Verificando contraseña para: " + user.getUsername());
        if (!UserDatabase.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            user.incrementFailedAttempts();
            log("❌ Contraseña incorrecta para '" + user.getUsername() + "'. Intentos fallidos: " + user.getFailedLoginAttempts());

            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setLockoutUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
                log("🚨 Usuario '" + user.getUsername() + "' bloqueado por múltiples intentos fallidos.");
                return currentResult.fail("Contraseña incorrecta. Cuenta bloqueada por " + LOCKOUT_DURATION_MINUTES + " minutos.");
            }
            return currentResult.fail("Contraseña incorrecta.");
        }

        user.resetFailedAttempts();
        log("✅ Contraseña verificada correctamente para: " + user.getUsername());

        return handleNext(request, currentResult.continueSuccess());
    }
}
