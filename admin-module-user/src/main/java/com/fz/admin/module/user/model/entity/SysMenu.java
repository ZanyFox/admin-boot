package com.fz.admin.module.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fz.admin.framework.mybatis.model.BaseEntity;
import com.fz.admin.module.user.enums.MenuTypeEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表
 */
@TableName(value ="sys_menu")
@Data
public class SysMenu extends BaseEntity  {
    /**
     * 菜单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer sort;


    /**
     * 菜单类型 1: 目录 2: 菜单 3: 按钮  详见 {@link MenuTypeEnum MenuTypeEnum}
     */
    private Integer type;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 是否为外链
     */
    private Boolean isFrame;

    /**
     * 是否缓存
     */
    private Boolean isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    private String menuType;

    /**
     * 菜单状态（0隐藏 1显示）
     */
    private Boolean visible;

    /**
     * 菜单状态（0正常 1停用） 详见 {@link com.fz.admin.framework.common.enums.CommonStatusEnum CommonStatusEnum}
     */
    private Integer status;

    /**
     * 权限标识
     */
    private String perm;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;

    private Integer deleted;


    /** 子菜单 */
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SysMenu> children = new ArrayList<>();

    /**
     * 菜单编号 - 根节点
     */
    public static final Long ID_ROOT = 0L;
}