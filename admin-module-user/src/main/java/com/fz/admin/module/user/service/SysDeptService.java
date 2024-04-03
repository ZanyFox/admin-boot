package com.fz.admin.module.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.framework.common.pojo.TreeSelect;
import com.fz.admin.module.user.model.entity.SysDept;
import com.fz.admin.module.user.model.param.DeptCreateOrUpdateParam;
import com.fz.admin.module.user.model.param.DeptListParam;

import java.util.List;

public interface SysDeptService extends IService<SysDept> {

    /**
     * 获取所有子部门
     *
     * @param deptId
     * @return
     */
    List<Long> getChildDeptIdList(Long deptId);

    /**
     * 获取指定部门下的所有子部门
     * @param parentDeptId
     * @return
     */
    List<SysDept> getChildDeptList(Long parentDeptId);


    /**
     * 获取部门简单信息
     * @return 部门列表
     */
    List<SysDept> getDeptSimpleList();

    /**
     * 通过部门id获取部门信息
     * @param id
     * @return
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
}
