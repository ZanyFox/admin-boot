package com.fz.admin.module.user.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.module.user.mapper.SysDeptMapper;
import com.fz.admin.module.user.mapper.SysUserMapper;
import com.fz.admin.module.user.model.entity.SysDept;
import com.fz.admin.module.user.model.entity.SysUser;
import com.fz.admin.module.user.model.param.DeptCreateOrUpdateParam;
import com.fz.admin.module.user.model.param.DeptListParam;
import com.fz.admin.module.user.service.SysDeptService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;

import static com.fz.admin.framework.common.util.CollectionConverter.convertList;


@Service
@AllArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {


    private SysDeptMapper deptMapper;

    private SysUserMapper userMapper;

    @Override
    public List<Long> getChildDeptIdList(Long deptId) {


        return null;
    }

    @Override
    public List<SysDept> getChildDeptList(Long parentDeptId) {

        List<SysDept> list = list();

        List<SysDept> allChildDept = new ArrayList<>();
        List<SysDept> childDepts = list.stream().filter((dept) -> parentDeptId.equals(dept.getParentId())).toList();

        while (ObjectUtils.isNotEmpty(childDepts)) {
            allChildDept.addAll(childDepts);
            List<SysDept> finalChildDepts = childDepts;
            childDepts = list.stream()
                    .filter((dept) -> convertList(finalChildDepts, SysDept::getId).contains(dept.getParentId()))
                    .toList();
        }
        return allChildDept;
    }


    @Override
    public List<SysDept> getDeptSimpleList() {
        List<SysDept> list = lambdaQuery()
                .select(SysDept::getId, SysDept::getDeptName, SysDept::getParentId)
                .eq(SysDept::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .list();
        list.sort(Comparator.comparingInt(SysDept::getOrder));
        return list;
    }


    @Override
    public SysDept getDeptById(Long id) {


        return deptMapper.selectById(id);
    }

    @Override
    public List<SysDept> getDeptList(DeptListParam param) {

        List<SysDept> list = lambdaQuery()
                .eq(SysDept::getStatus, param.getStatus())
                .like(SysDept::getDeptName, param.getName())
                .list();

        list.sort(Comparator.comparingInt(SysDept::getOrder));
        return list;
    }

    @Override
    public void deleteDeptById(Long id) {
        validateDeptExists(id);

        Long childDeptCount = lambdaQuery().eq(SysDept::getParentId, id).count();
        if (childDeptCount > 0)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "存在子部门，无法删除");

        Long deptUserCount = userMapper.selectCount(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getDeptId, id));
        if (deptUserCount > 0)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "存在子部门，无法删除");

        deptMapper.deleteById(id);
    }

    @Transactional
    @Override
    public void updateDept(DeptCreateOrUpdateParam param) {

        SysDept dept = null;
        if (param.getId() == null || (dept = deptMapper.selectById(param.getId())) == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "部门不存在");
        }

        // 如果要修改的部门是顶级部门也就是公司，那么无法修改
        if (dept.getParentId().equals(SysDept.PARENT_ID_ROOT)
                && param.getParentId() != null
                && !param.getParentId().equals(SysDept.PARENT_ID_ROOT)) {

            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "不能修改根部门的父部门");
        }

        Supplier<Long> getChildDeptCount = () -> lambdaQuery()
                .eq(SysDept::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .eq(SysDept::getParentId, param.getId())
                .count();

        if (CommonStatusEnum.isDisable(param.getStatus()) && getChildDeptCount.get() > 0)
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "该部门包含未停用的子部门");

        // 校验父部门是否合法
        SysDept parentDept = validateParentDeptAndGet(param.getId(), param.getParentId());

        // 校验部门名称是否合法
        validateDeptNameUnique(param.getId(), param.getParentId(), param.getDeptName());

        SysDept updateDept = new SysDept();

        // 如果更改父部门，那么需要更新ancestor字段

        if (parentDept != null && param.getParentId() != null && !Objects.equals(param.getParentId(), dept.getId())) {

            // SysDept newParentDept = lambdaQuery().eq(SysDept::getId, param.getParentId()).one();
            String newAncestors = parentDept.getAncestors() + "," + parentDept.getId();
            String oldAncestors = dept.getAncestors();
            dept.setAncestors(newAncestors);
            updateChildDeptAncestor(dept.getId(), newAncestors, oldAncestors);
            updateDept.setAncestors(newAncestors);
        }

        // 如果该部门是启用状态，则启用该部门的所有上级部门
        if (CommonStatusEnum.isEnable(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals(dept.getAncestors(), String.valueOf(SysDept.PARENT_ID_ROOT))) {
            String ancestors = dept.getAncestors();
            List<Long> ancestorIds = Arrays.stream(ancestors.split(SysDept.ANCESTORS_SEPARATOR)).map(Long::valueOf).toList();
            deptMapper.updateBatchDeptStatus(ancestorIds);
        }

        BeanUtils.copyProperties(param, updateDept);
        deptMapper.updateById(updateDept);
    }

    @Override
    public Long createDept(DeptCreateOrUpdateParam param) {

        SysDept parentDept = validateParentDeptAndGet(null, param.getParentId());
        validateDeptNameUnique(null, param.getParentId(), param.getDeptName());

        // 设置ancestors字段
        String parentAncestors = parentDept.getAncestors();
        SysDept dept = new SysDept();
        dept.setAncestors(parentAncestors + SysDept.ANCESTORS_SEPARATOR + parentDept.getId());
        BeanUtils.copyProperties(param, dept);
        deptMapper.insert(dept);

        return dept.getId();
    }


    /**
     * 更新子部门的ancestors
     *
     * @param deptId       被更新的部门
     * @param newAncestors 更新后部门的父部门列表
     * @param oldAncestors 原部门的父部门列表
     */
    void updateChildDeptAncestor(Long deptId, String newAncestors, String oldAncestors) {
        List<SysDept> childDept = deptMapper.selectChildDeptById(deptId);
        for (SysDept child : childDept) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }

        if (!childDept.isEmpty()) {
            deptMapper.updateBatchDeptAncestor(childDept);
        }
    }

    /**
     * 校验部门名称是否合法
     *
     * @param id       部门id
     * @param parentId 父部门id
     * @param name     部门名称
     */
    void validateDeptNameUnique(Long id, Long parentId, String name) {
        SysDept dept = lambdaQuery().eq(SysDept::getParentId, parentId)
                .eq(SysDept::getDeptName, name)
                .one();

        if (dept == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的部门
        if (id == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "部门名称冲突");
        }
        if (id.equals(dept.getId())) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "该部门包含未停用的子部门");
        }
    }


    /**
     * 校验父部门是否合法   如果合法那么获取父部门 避免之后再查询
     *
     * @param id       部门id
     * @param parentId 父部门id
     */
    SysDept validateParentDeptAndGet(Long id, Long parentId) {
        if (id != null && parentId == null) {
            return null;
        }
        // 1. 不能设置自己为父部门
        if (id != null && Objects.equals(id, parentId)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "不能设置自己为父部门");
        }

        // 2. 父部门不存在
        SysDept parentDept = deptMapper.selectById(parentId);
        if (parentDept == null || CommonStatusEnum.isDisable(parentDept.getStatus())) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "父部门不存在");
        }


        if (id == null) { // id 为空，说明新增，不需要考虑环路
            return parentDept;
        }

        // 判断部门id 是否存在于父部门的祖先部门id集合中
        Boolean isIdInAncestors = deptMapper.selectIdInAncestors(id, parentId);

        if (Boolean.TRUE.equals(isIdInAncestors)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "不能设置父部门为自己的子部门");
        }

        // 3. 递归校验父部门，如果父部门是自己的子部门，则报错，避免形成环路
        // 判断父部门是不是自己的子部门  循环查表
        // for (int i = 0; i < Short.MAX_VALUE; i++) {
        //     // 3.1 校验环路
        //     parentId = parentDept.getParentId();
        //     if (Objects.equals(id, parentId)) {
        //         throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "不能设置父部门为自己的子部门");
        //     }
        //     // 3.2 继续递归下一级父部门
        //     if (parentId == null || SysDept.PARENT_ID_ROOT.equals(parentId)) {
        //         break;
        //     }
        //     parentDept = deptMapper.selectById(parentId);
        //     if (parentDept == null) {
        //         break;
        //     }
        // }

        return parentDept;
    }

    void validateDeptExists(Long id) {
        if (id == null || deptMapper.selectById(id) == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "部门不存在");
        }
    }


    /**
     * 获取部门的子部门
     *
     * @param id
     * @param result
     * @param depts
     */
    void findChildDept1(Long id, List<Long> result, List<SysDept> depts) {

        for (SysDept dept : depts) {
            if (Objects.equals(dept.getParentId(), id)) {
                result.add(dept.getId());
                findChildDept1(dept.getId(), result, depts);
            }
        }
    }


    /**
     * 获取部门的子部门
     *
     * @param id
     * @param result
     * @param depts
     */

    void findChildDept2(List<Long> target, List<SysDept> depts, List<SysDept> result) {

        // 不显示使用循环的第一种写法  remove移除元素
        // List<Long> targetList = new ArrayList<>();
        // // 可以把匹配到的添加后移除 提高后续匹配效率
        // boolean removed = depts.removeIf(new Predicate<SysDept>() {
        //     @Override
        //     public boolean test(SysDept dept) {
        //         boolean contains = target.contains(dept.getParentId());
        //         if (contains) {
        //             result.add(dept);
        //             targetList.add(dept.getId());
        //         }
        //         return contains;
        //     }
        // });
        //
        // if (removed)
        //     findChildDept2(targetList, depts, result);


        // 第二种写法  不remove元素
        List<SysDept> list = depts.stream().filter(dept -> target.contains(dept.getParentId())).toList();
        if (ObjectUtils.isNotEmpty(list)) {
            result.addAll(list);
            List<Long> targetIds = list.stream().map(SysDept::getId).toList();
            findChildDept2(targetIds, depts, result);
        }

    }
}




