package com.fz.admin.module.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fz.admin.module.user.model.entity.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper extends BaseMapper<SysDept> {


    Boolean selectIdInAncestors(@Param("checkId") Long checkId, @Param("deptId") Long deptId);

    /**
     * 查询所有 ancestors 字段包含被更新的部门id的部门，也就是查找被更新部门的子部门
     * @param deptId 被更新的部门id
     * @return 被更新部门的子部门集合
     */
    List<SysDept> selectChildDeptById(Long deptId);

    /**
     * 批量更新部门的父部门列表
     * @param depts 需要被更新的部门
     */
    void updateBatchDeptAncestor(List<SysDept> depts);

    /**
     * 批量更新部门状态
     * @param deptIds 部门集合
     */
    void updateBatchDeptStatus(List<Long> deptIds);
}




