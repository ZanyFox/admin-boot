package com.fz.admin.module.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fz.admin.framework.mybatis.model.BaseEntity;
import lombok.Data;

/**
 * 角色信息表
 */
@TableName(value = "sys_role")
@Data
public class SysRole extends BaseEntity {
    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色权限字符串
     */
    private String key;

    /**
     * 显示顺序
     */
    private Integer order;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    private Integer dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    private Integer menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    private Integer deptCheckStrictly;


    /**
     * 数据范围(指定部门数组) [1,2,3] 这样的格式
     */
    private String dataScopeDeptIds;

    /**
     * 角色状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Integer deleted;

    /**
     * 备注
     */
    private String remark;

}