package model;

import java.time.LocalDateTime;

public class User {
    private String username;
    private String passwordHash;
    private UserRole role;
    private boolean isActive;
    private int failedLoginAttempts;
    private LocalDateTime lockoutUntil;

    public User(String username, String passwordHash, UserRole role, boolean isActive) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = isActive;
        this.failedLoginAttempts = 0;
        this.lockoutUntil = null;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public boolean isActive() { return isActive; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public LocalDateTime getLockoutUntil() { return lockoutUntil; }

    public void incrementFailedAttempts() { this.failedLoginAttempts++; }
    public void resetFailedAttempts() { this.failedLoginAttempts = 0; this.lockoutUntil = null; }
    public void setLockoutUntil(LocalDateTime lockoutUntil) { this.lockoutUntil = lockoutUntil; }

    public boolean isLockedOut() {
        return lockoutUntil != null && LocalDateTime.now().isBefore(lockoutUntil);
    }
}
