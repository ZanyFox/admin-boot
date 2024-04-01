package com.fz.admin.module.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fz.admin.framework.mybatis.model.BaseEntity;
import lombok.Data;

/**
 * 岗位信息表
 */
@TableName(value ="sys_post")
@Data
public class SysPost extends BaseEntity {
    /**
     * 岗位ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 岗位编码
     */
    private String code;

    /**
     * 岗位名称
     */
    private String name;

    /**
     * 显示顺序
     */
    private Integer order;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}