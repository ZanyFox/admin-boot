package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.framework.common.pojo.TreeSelect;
import com.fz.admin.module.user.model.entity.SysDept;
import com.fz.admin.module.user.model.param.DeptCreateOrUpdateParam;
import com.fz.admin.module.user.model.param.DeptListParam;

import java.util.Collection;
import java.util.List;

public interface SysDeptService extends IService<SysDept> {


    /**
     * 获取指定部门下的所有子部门
     * @param parentDeptId 父部门id
     * @return 部门列表
     */
    List<SysDept> getChildDeptList(Long parentDeptId);


    /**
     * 获取部门简单信息
     * @return 部门列表
     */
    List<SysDept> getDeptSimpleList();

    /**
     * 通过部门id获取部门信息
     * @param id 部门id
     * @return 部门数据
     */
    SysDept getDeptById(Long id);

    List<SysDept> getDeptList(DeptListParam param);

    /**
     * 删除部门
     * @param id 部门id
     */
    void deleteDeptById(Long id);

    /**
     * 更新部门
     * @param param 部门参数
     */
    void updateDept(DeptCreateOrUpdateParam param);


    /**
     * 新增部门
     * @param param 部门参数
     * @return 部门id
     */
    Long createDept(DeptCreateOrUpdateParam param);

    /**
     * 获取部门树形数据
     * @param param 部门参数
     * @return 树形信息
     */
    List<TreeSelect> getDeptTree(DeptListParam param);


    /**
     * 校验部门是否有效。如下情况，视为无效：
     * 1. 部门编号不存在
     * 2. 部门被禁用
     *
     * @param ids 部门id列表
     */
    void validateDeptList(Collection<Long> ids);

    void validateDept(Long id);

    /**
     * 获取角色拥有的部门数据权限
     * @param roleId 角色id
     * @return 部门列表
     */
    List<SysDept> listDeptByRoleId(Long roleId);
}
