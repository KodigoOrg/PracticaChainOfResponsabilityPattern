package handler;

import model.AuthRequest;
import model.AuthResult;
import model.User;

import java.time.format.DateTimeFormatter;

public class AccountStatusHandler extends AuthHandler {
    public AccountStatusHandler() {
        super("AccountStatus");
    }

    @Override
    public AuthResult handle(AuthRequest request, AuthResult currentResult) {
        User user = currentResult.getUser();
        if (user == null) {
            log("Error crítico: Usuario no disponible en AccountStatusHandler.");
            return currentResult.fail("Error interno de autenticación.");
        }

        log("Verificando estado de la cuenta para: " + user.getUsername());
        if (!user.isActive()) {
            log("❌ La cuenta de '" + user.getUsername() + "' está inactiva.");
            return currentResult.fail("La cuenta está inactiva. Contacte al soporte.");
        }

        if (user.isLockedOut()) {
            String lockoutTime = user.getLockoutUntil().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            log("❌ Usuario '" + user.getUsername() + "' bloqueado hasta: " + lockoutTime);
            return currentResult.fail("Cuenta bloqueada temporalmente hasta " + lockoutTime + ". Intente más tarde.");
        }
        log("✅ Cuenta activa y no bloqueada para: " + user.getUsername());

        return handleNext(request, currentResult.continueSuccess());
    }
}
