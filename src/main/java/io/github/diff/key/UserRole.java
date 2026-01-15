package io.github.diff.key;

/**
 * Minimal entity representing an existing user-role record.
 */
public class UserRole {
    private final Long userId;
    private final String roleCode;

    public UserRole(Long userId, String roleCode) {
        this.userId = userId;
        this.roleCode = roleCode;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRoleCode() {
        return roleCode;
    }
}
