package model;

import java.util.ArrayList;
import java.util.List;

public class AuthResult {
    private boolean success;
    private String message;
    private User user;
    private String jwtToken;
    private List<String> errors;

    public AuthResult() {
        this.errors = new ArrayList<>();
        this.success = false;
        this.message = "Operación no iniciada o pendiente.";
    }

    /**
     * Solucion al error en la grabacion, como se inicializa AuthResult con un
     * success false, por defecto, toda la cadena de validaciones contenia un
     * success=false, aunque la validacion haya sido exitosa, y nunca se
     * cambiaba el estado de AuthResult, por eso siempre inicializaba los
     * errores como null.
     *
     * @return booleano que se encarga de validar el estado de los handlers.
     */
    public AuthResult continueSuccess() {
        this.success = true;
        return this;
    }

    public AuthResult success(User user, String token) {
        this.success = true;
        this.user = user;
        this.jwtToken = token;
        this.message = "Autenticación exitosa";
        this.errors.clear();
        return this;
    }

    public AuthResult fail(String error) {
        this.success = false;
        String errorMessage = (error != null && !error.isEmpty()) ? error : "Error desconocido.";
        this.errors.add(errorMessage);
        this.message = errorMessage;
        return this;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public User getUser() { return user; }
    public String getJwtToken() { return jwtToken; }
    public List<String> getErrors() { return errors; }

    public void setUser(User user) {
        this.user = user;
    }
}
