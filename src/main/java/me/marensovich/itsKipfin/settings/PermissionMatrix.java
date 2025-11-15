package me.marensovich.itsKipfin.settings;

import me.marensovich.itsKipfin.data.Permission;
import me.marensovich.itsKipfin.data.Role;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

/**
 * The type Permission matrix.
 * @since 0.0.1
 * @author marensovich
 * @version 0.0.1
 */
public class PermissionMatrix {
    private final EnumMap<Role, Set<Permission>> rolePermissions = new EnumMap<>(Role.class);

    /**
     * Instantiates a new Permission matrix.
     *
     * @since 0.0.1
     * @author marensovich
     */
    public PermissionMatrix() {
        rolePermissions.put(Role.PRESIDENT, EnumSet.of(Permission.EDIT_ALL, Permission.VIEW_ALL));
        rolePermissions.put(Role.CURATOR, EnumSet.of(Permission.EDIT_ALL, Permission.VIEW_ALL));
        rolePermissions.put(Role.HEAD, EnumSet.of(Permission.EDIT_DEPARTMENT, Permission.VIEW_DEPARTMENT));
        rolePermissions.put(Role.DEPUTY_HEAD, EnumSet.of(Permission.EDIT_DEPARTMENT, Permission.VIEW_DEPARTMENT));
        rolePermissions.put(Role.MEMBER, EnumSet.of(Permission.VIEW_DEPARTMENT));
    }

    /**
     * Can boolean.
     *
     * @param role       the role
     * @param permission the permission
     * @return the boolean
     * @since 0.0.1
     * @author marensovich
     */
    public boolean can(Role role, Permission permission) {
        return rolePermissions.getOrDefault(role, EnumSet.noneOf(Permission.class)).contains(permission);
    }

}
