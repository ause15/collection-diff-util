package io.github.diff.key;

/**
 * Minimal DTO representing a user-role submission.
 */
public class UserRoleDTO {
    private final Long userId;
    private final String roleCode;

    public UserRoleDTO(Long userId, String roleCode) {
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
