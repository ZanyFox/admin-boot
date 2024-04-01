package com.fz.admin.module.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fz.admin.framework.mybatis.model.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息表
 */
@TableName(value ="sys_user")
@Data
public class SysUser extends BaseEntity {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 岗位编号
     */
    private String postIds;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户类型（00系统用户）
     */
    private String type;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private Integer gender;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private Integer deleted;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private List<SysRole> roles;

    @TableField(exist = false)
    private SysDept dept;

    @TableField(exist = false)
    private List<SysPost> posts;
}