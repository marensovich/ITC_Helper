package me.marensovich.itsKipfin.settings;

import me.marensovich.itsKipfin.data.Permission;
import me.marensovich.itsKipfin.data.Role;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public class PermissionMatrix {
    private final EnumMap<Role, Set<Permission>> rolePermissions = new EnumMap<>(Role.class);

    public PermissionMatrix() {
        rolePermissions.put(Role.PRESIDENT, EnumSet.of(Permission.EDIT_ALL, Permission.VIEW_ALL));
        rolePermissions.put(Role.CURATOR, EnumSet.of(Permission.EDIT_ALL, Permission.VIEW_ALL));
        rolePermissions.put(Role.HEAD, EnumSet.of(Permission.EDIT_DEPARTMENT, Permission.VIEW_DEPARTMENT));
        rolePermissions.put(Role.DEPUTY_HEAD, EnumSet.of(Permission.EDIT_DEPARTMENT, Permission.VIEW_DEPARTMENT));
        rolePermissions.put(Role.MEMBER, EnumSet.of(Permission.VIEW_DEPARTMENT));
    }

    public boolean can(Role role, Permission permission) {
        return rolePermissions.getOrDefault(role, EnumSet.noneOf(Permission.class)).contains(permission);
    }

}
