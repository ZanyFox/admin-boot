package com.fz.admin.module.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fz.admin.framework.mybatis.model.BaseEntity;
import lombok.Data;

/**
 * 部门表
 */
@TableName(value ="sys_dept")
@Data
public class SysDept extends BaseEntity  {
    /**
     * 部门id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父部门id
     */
    private Long parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 显示顺序
     */
    private Integer order;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Integer deleted;


    /**
     * 根部门id
     */
    public static final Long PARENT_ID_ROOT = 0L;

    /**
     * 父部门id分隔符
     */
    public static final String ANCESTORS_SEPARATOR = ",";
}