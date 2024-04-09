package com.fz.admin.module.user.model.pojo;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * 部门的数据权限
 *
 *
 */
@Data
public class RoleDataScope {

    /**
     * 是否可查看全部数据
     */
    private Boolean all;
    /**
     * 是否可查看自己的数据
     */
    private Boolean self;
    /**
     * 可查看的部门编号数组
     */
    private Set<Long> deptIds;

    public RoleDataScope() {
        this.all = false;
        this.self = false;
        this.deptIds = new HashSet<>();
    }

}
