package service;

import handler.*;
import model.AuthRequest;
import model.AuthResult;

public class AuthService {
    private AuthHandler loginChain;
    private JWTValidationHandler jwtValidationHandler;

    public AuthService() {
        UserExistenceHandler userExistence = new UserExistenceHandler();
        AccountStatusHandler accountStatus = new AccountStatusHandler();
        PasswordVerificationHandler passwordVerification = new PasswordVerificationHandler();
        JWTGenerationHandler jwtGeneration = new JWTGenerationHandler();

        userExistence.setNext(accountStatus);
        accountStatus.setNext(passwordVerification);
        passwordVerification.setNext(jwtGeneration);

        this.loginChain = userExistence;

        this.jwtValidationHandler = new JWTValidationHandler();
    }

    public AuthResult authenticate(AuthRequest request) {
        return loginChain.handle(request, new AuthResult());
    }

    public AuthResult validateToken(AuthRequest request) {
        return jwtValidationHandler.handle(request, new AuthResult());
    }
}