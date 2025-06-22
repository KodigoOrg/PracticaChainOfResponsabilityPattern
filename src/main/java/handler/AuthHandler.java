package handler;

import model.AuthRequest;
import model.AuthResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AuthHandler {
    protected AuthHandler nextHandler;
    protected String handlerName;

    public AuthHandler(String handlerName) {
        this.handlerName = handlerName;
    }

    public void setNext(AuthHandler handler) {
        this.nextHandler = handler;
    }

    public abstract AuthResult handle(AuthRequest request, AuthResult currentResult);

    protected AuthResult handleNext(AuthRequest request, AuthResult currentResult) {
        if (nextHandler != null && currentResult.isSuccess() && currentResult.getErrors().isEmpty()) {
            return nextHandler.handle(request, currentResult);
        }
        return currentResult;
    }

    protected void log(String message) {
        String messageToLog = (message != null && !message.isEmpty()) ? message : "Mensaje de log vacío/nulo.";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println(String.format("[%s] %s: %s", timestamp, handlerName, messageToLog));
    }
}