package service;

import model.User;
import model.UserRole;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserDatabase {
    private static final Map<String, User> users = new ConcurrentHashMap<>();

    static {
        users.put("admin", new User("admin", hashPassword("admin123"), UserRole.ADMIN, true));
        users.put("manager1", new User("manager1", hashPassword("manager123"), UserRole.MANAGER, true));
        users.put("customer1", new User("customer1", hashPassword("customer123"), UserRole.CUSTOMER, true));
        users.put("inactive_user", new User("inactive_user", hashPassword("password123"), UserRole.CUSTOMER, false));
    }

    public static User findByUsername(String username) {
        return users.get(username);
    }

    private static String hashPassword(String password) {
        return "hashed_" + password; // ¡Solo para demostración!
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword); // ¡Solo para demostración!
    }

    public static void resetUserStates() {
        users.forEach((username, user) -> {
            user.resetFailedAttempts();
        });
    }
}

