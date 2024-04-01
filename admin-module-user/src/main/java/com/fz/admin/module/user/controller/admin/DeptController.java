package com.fz.admin.module.user.controller.admin;


import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.module.user.model.entity.SysDept;
import com.fz.admin.module.user.model.param.DeptCreateOrUpdateParam;
import com.fz.admin.module.user.model.param.DeptListParam;
import com.fz.admin.module.user.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fz.admin.framework.common.pojo.ServRespEntity.success;


@Tag(name = "管理后台 - 部门")
@RestController
@RequestMapping("/system/dept")
@Validated
public class DeptController {

    @Resource
    private SysDeptService deptService;

    @PostMapping("create")
    @Operation(summary = "创建部门")
    @PreAuthorize("@ss.hasPermission('system:dept:create')")
    public ServRespEntity<Long> createDept(@Validated @RequestBody DeptCreateOrUpdateParam param) {
        Long deptId = deptService.createDept(param);
        return success(deptId);
    }

    @PutMapping("update")
    @Operation(summary = "更新部门")
    // @PreAuthorize("@ss.hasPermission('system:dept:update')")
    public ServRespEntity<Boolean> updateDept(@Validated @RequestBody DeptCreateOrUpdateParam pram) {
        deptService.updateDept(pram);
        return success(true);
    }

    @DeleteMapping("delete")
    @Operation(summary = "删除部门")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public ServRespEntity<Boolean> deleteDept(@RequestParam("id") Long id) {
        deptService.deleteDeptById(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取部门列表")
    // @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public ServRespEntity<List<SysDept>> getDeptList(DeptListParam param) {
        List<SysDept> list = deptService.getDeptList(param);
        return success(list);
    }

    @GetMapping(value = {"/list-all-simple", "/simple-list"})
    @Operation(summary = "获取部门精简信息列表", description = "只包含被开启的部门，主要用于前端的下拉选项")
    public ServRespEntity<List<SysDept>> getSimpleDeptList() {
        List<SysDept> list = deptService.getDeptSimpleList();
        return success(list);
    }

    @GetMapping("/get")
    @Operation(summary = "获得部门信息")
    @Parameter(name = "id", description = "编号", required = true, example = "0")
    // @PreAuthorize("@ss.hasPermission('system:dept:query')")
    public ServRespEntity<SysDept> getDept(@RequestParam("id") Long id) {
        SysDept dept = deptService.getDeptById(id);
        return success(dept);
    }

}
