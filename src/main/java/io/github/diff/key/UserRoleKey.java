package io.github.diff.key;

/**
 * 用户-角色关联的业务唯一键
 *
 * <p>
 * 由 (userId, roleCode) 共同决定一条记录是否相同
 */
public record UserRoleKey(
        Long userId,
        String roleCode
) {

    public static UserRoleKey fromDto(UserRoleDTO dto) {
        return new UserRoleKey(dto.getUserId(), dto.getRoleCode());
    }

    public static UserRoleKey fromEntity(UserRole entity) {
        return new UserRoleKey(entity.getUserId(), entity.getRoleCode());
    }
}
