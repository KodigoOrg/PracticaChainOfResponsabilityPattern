package model;

import java.util.Arrays;
import java.util.List;

public enum UserRole {
    ADMIN("admin", Arrays.asList("CREATE_ORDER", "VIEW_ALL_ORDERS", "CANCEL_ANY_ORDER", "MANAGE_USERS")),
    MANAGER("manager", Arrays.asList("CREATE_ORDER", "VIEW_TEAM_ORDERS", "CANCEL_TEAM_ORDER")),
    CUSTOMER("customer", Arrays.asList("CREATE_ORDER", "VIEW_OWN_ORDERS", "CANCEL_OWN_ORDER")),
    GUEST("guest", Arrays.asList("VIEW_CATALOG"));

    private final String roleName;
    private final List<String> permissions;

    UserRole(String roleName, List<String> permissions) {
        this.roleName = roleName;
        this.permissions = permissions;
    }

    public String getRoleName() { return roleName; }
    public List<String> getPermissions() { return permissions; }
}
