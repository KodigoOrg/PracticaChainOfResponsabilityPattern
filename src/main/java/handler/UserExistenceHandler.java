package handler;

import model.AuthRequest;
import model.AuthResult;
import model.User;
import service.UserDatabase;

public class UserExistenceHandler extends AuthHandler {
    public UserExistenceHandler() {
        super("UserExistence");
    }

    @Override
    public AuthResult handle(AuthRequest request, AuthResult currentResult) {
        if (request.getUsername() == null) {
            log("Error interno: Se esperaba un nombre de usuario pero es nulo.");
            return currentResult.fail("Nombre de usuario no proporcionado.");
        }

        log("Verificando existencia de usuario: " + request.getUsername());
        User user = UserDatabase.findByUsername(request.getUsername());
        if (user == null) {
            log("❌ Usuario '" + request.getUsername() + "' no encontrado en la base de datos.");
            return currentResult.fail("Usuario no encontrado.");
        }
        currentResult.setUser(user);
        log("✅ Usuario encontrado: " + user.getUsername());
        currentResult.continueSuccess();
        return handleNext(request, currentResult.continueSuccess());
    }
}