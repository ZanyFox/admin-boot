package com.fz.admin.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 角色标识枚举
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    SUPER_ADMIN("admin", "超级管理员");


    /**
     * 角色key
     */
    private final String key;
    /**
     * 名字
     */
    private final String name;

    public static boolean isSuperAdmin(String key) {
        return Objects.equals(key, SUPER_ADMIN.getKey());
    }

}
