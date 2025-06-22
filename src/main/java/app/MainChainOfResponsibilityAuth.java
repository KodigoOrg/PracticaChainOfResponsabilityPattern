package app;

import model.AuthRequest;
import model.AuthResult;
import service.AuthService;
import service.JWTManager;
import service.UserDatabase;

public class MainChainOfResponsibilityAuth {
    public static void main(String[] args) {
        AuthService authService = new AuthService();

        System.out.println("--- DEMOSTRACIÓN DE AUTENTICACIÓN CON CADENA DE RESPONSABILIDAD ---");

        // Reinicia los estados de usuario para que las pruebas sean consistentes
        UserDatabase.resetUserStates();

        // 1. Autenticación exitosa
        System.out.println("\n--- Prueba 1: Autenticación exitosa (admin) ---");
        try {
            AuthRequest adminAuthRequest = new AuthRequest("admin", "admin123");
            AuthResult adminResult = authService.authenticate(adminAuthRequest);

            if (adminResult.isSuccess()) {
                System.out.println("✅ " + adminResult.getMessage());
                System.out.println("Usuario: " + adminResult.getUser().getUsername() + ", Rol: " + adminResult.getUser().getRole().getRoleName());
                System.out.println("Token JWT: " + adminResult.getJwtToken().substring(0, 30) + "...");
            } else {
                System.out.println("❌ " + adminResult.getMessage() + " Errores: " + adminResult.getErrors());
            }

            // 2. Validación de un token JWT existente
            if (adminResult.isSuccess() && adminResult.getJwtToken() != null) {
                System.out.println("\n--- Prueba 2: Validación de Token JWT del admin ---");
                AuthRequest validateTokenRequest = new AuthRequest(adminResult.getJwtToken());
                AuthResult tokenValidationResult = authService.validateToken(validateTokenRequest);

                if (tokenValidationResult.isSuccess()) {
                    System.out.println("✅ " + tokenValidationResult.getMessage());
                    System.out.println("Token válido para usuario: " + tokenValidationResult.getUser().getUsername());
                    System.out.println("Tiempo restante (segundos): " + JWTManager.getInstance().getTimeUntilExpirationInSeconds(tokenValidationResult.getJwtToken()));
                } else {
                    System.out.println("❌ " + tokenValidationResult.getMessage() + " Errores: " + tokenValidationResult.getErrors());
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("🚨 Error al inicializar AuthRequest: " + e.getMessage());
        }


        // Reinicia los estados de usuario para que la prueba de bloqueo sea consistente
        UserDatabase.resetUserStates();
        System.out.println("\n--- Prueba 3: Contraseña incorrecta y bloqueo de cuenta (customer1) ---");
        for (int i = 1; i <= 4; i++) {
            System.out.println("Intento " + i + ":");
            try {
                AuthRequest customerAuthRequest = new AuthRequest("customer1", "contrasena_incorrecta");
                AuthResult customerResult = authService.authenticate(customerAuthRequest);
                System.out.println((customerResult.isSuccess() ? "✅" : "❌") + " " + customerResult.getMessage());
                if (!customerResult.isSuccess() && !customerResult.getErrors().isEmpty()) {
                    System.out.println("    Detalles: " + customerResult.getErrors().get(0));
                }
            } catch (IllegalArgumentException e) {
                System.out.println("🚨 Error al inicializar AuthRequest: " + e.getMessage());
            }
        }

        // 4. Intento con cuenta bloqueada
        System.out.println("\n--- Prueba 4: Intento de login con cuenta bloqueada (customer1) ---");
        try {
            AuthRequest blockedCustomerAuthRequest = new AuthRequest("customer1", "customer123");
            AuthResult blockedCustomerResult = authService.authenticate(blockedCustomerAuthRequest);
            System.out.println((blockedCustomerResult.isSuccess() ? "✅" : "❌") + " " + blockedCustomerResult.getMessage());
            if (!blockedCustomerResult.isSuccess() && !blockedCustomerResult.getErrors().isEmpty()) {
                System.out.println("    Detalles: " + blockedCustomerResult.getErrors().get(0));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("🚨 Error al inicializar AuthRequest: " + e.getMessage());
        }


        // 5. Usuario no existente
        System.out.println("\n--- Prueba 5: Usuario no existente ---");
        try {
            AuthRequest nonExistentUserRequest = new AuthRequest("usuario_fantasma", "password123");
            AuthResult nonExistentResult = authService.authenticate(nonExistentUserRequest);
            System.out.println((nonExistentResult.isSuccess() ? "✅" : "❌") + " " + nonExistentResult.getMessage());
            if (!nonExistentResult.isSuccess() && !nonExistentResult.getErrors().isEmpty()) {
                System.out.println("    Detalles: " + nonExistentResult.getErrors().get(0));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("🚨 Error al inicializar AuthRequest: " + e.getMessage());
        }


        // 6. Cuenta inactiva
        System.out.println("\n--- Prueba 6: Cuenta inactiva (inactive_user) ---");
        try {
            AuthRequest inactiveUserRequest = new AuthRequest("inactive_user", "password123");
            AuthResult inactiveUserResult = authService.authenticate(inactiveUserRequest);
            System.out.println((inactiveUserResult.isSuccess() ? "✅" : "❌") + " " + inactiveUserResult.getMessage());
            if (!inactiveUserResult.isSuccess() && !inactiveUserResult.getErrors().isEmpty()) {
                System.out.println("    Detalles: " + inactiveUserResult.getErrors().get(0));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("🚨 Error al inicializar AuthRequest: " + e.getMessage());
        }


        // 7. Validación de token JWT inválido
        System.out.println("\n--- Prueba 7: Validación de Token JWT inválido ---");
        try {
            AuthRequest invalidTokenRequest = new AuthRequest("un.token.completamente.falso.y.sin.firma.valida");
            AuthResult invalidTokenResult = authService.validateToken(invalidTokenRequest);
            System.out.println((invalidTokenResult.isSuccess() ? "✅" : "❌") + " " + invalidTokenResult.getMessage());
            if (!invalidTokenResult.isSuccess() && !invalidTokenResult.getErrors().isEmpty()) {
                System.out.println("    Detalles: " + invalidTokenResult.getErrors().get(0));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("🚨 Error al inicializar AuthRequest: " + e.getMessage());
        }


        // Prueba extra: AuthRequest con username nulo/vacío
        System.out.println("\n--- Prueba Extra: AuthRequest con username nulo ---");
        try {
            // Esto ahora lanzará una IllegalArgumentException en el constructor
            AuthRequest nullUserRequest = new AuthRequest(null, "password123");
            authService.authenticate(nullUserRequest);
        } catch (IllegalArgumentException e) {
            System.out.println("🚨 ¡Excepción esperada! " + e.getMessage());
        }

        System.out.println("\n--- Prueba Extra: AuthRequest con contraseña vacía ---");
        try {
            // Esto ahora lanzará una IllegalArgumentException en el constructor
            AuthRequest emptyPasswordRequest = new AuthRequest("testuser", "");
            authService.authenticate(emptyPasswordRequest);
        } catch (IllegalArgumentException e) {
            System.out.println("🚨 ¡Excepción esperada! " + e.getMessage());
        }

        System.out.println("\n--- Prueba Extra: AuthRequest con token JWT nulo ---");
        try {
            // Esto ahora lanzará una IllegalArgumentException en el constructor
            AuthRequest nullTokenRequest = new AuthRequest((String) null);
            authService.validateToken(nullTokenRequest);
        } catch (IllegalArgumentException e) {
            System.out.println("🚨 ¡Excepción esperada! " + e.getMessage());
        }
    }
}
