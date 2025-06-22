package model;

public class AuthRequest {
    private String username;
    private String password;
    private String jwtToken;
    private String clientIp;

    public AuthRequest(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía.");
        }
        this.username = username.trim();
        this.password = password;
        this.jwtToken = null;
    }

    public AuthRequest(String jwtToken) {
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            throw new IllegalArgumentException("El token JWT no puede ser nulo o vacío.");
        }
        this.jwtToken = jwtToken.trim();
        this.username = null;
        this.password = null;
    }

    public AuthRequest withClientIp(String ip) { this.clientIp = ip; return this; }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getJwtToken() { return jwtToken; }
    public String getClientIp() { return clientIp; }
}