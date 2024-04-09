package com.fz.admin.module.user.service.impl;

import com.fz.admin.framework.security.service.SpringSecurityService;
import com.fz.admin.module.user.service.PermissionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static com.fz.admin.framework.security.util.SecurityContextUtils.getLoginUserId;

@Service("ss")
@AllArgsConstructor
public class SpringSecurityServiceImpl implements SpringSecurityService {

    private PermissionService permissionService;

    @Override
    public boolean hasAnyPermissions(String... permissions) {
        return permissionService.hasAnyPermissions(getLoginUserId(), permissions);
    }

    @Override
    public boolean hasPermission(String permission) {
        return permissionService.hasPermission(getLoginUserId(), permission);
    }

    @Override
    public boolean hasAnyRoles(String... roles) {
        return permissionService.hasAnyRoles(getLoginUserId(), roles);
    }
}
