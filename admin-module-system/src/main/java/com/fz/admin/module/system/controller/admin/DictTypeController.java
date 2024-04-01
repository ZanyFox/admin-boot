package com.fz.admin.module.system.controller.admin;


import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.module.system.model.entity.SysDictType;
import com.fz.admin.module.system.model.param.DictTypeCreateOrUpdateParam;
import com.fz.admin.module.system.model.param.DictTypePageParam;
import com.fz.admin.module.system.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fz.admin.framework.common.pojo.ServRespEntity.success;

@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/system/dict-type")
@Validated
public class DictTypeController {

    @Resource
    private SysDictTypeService dictTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建字典类型")
    // @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public ServRespEntity<Long> createDictType(@Validated @RequestBody DictTypeCreateOrUpdateParam param) {
        Long dictTypeId = dictTypeService.createDictType(param);
        return success(dictTypeId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改字典类型")
    // @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public ServRespEntity<Boolean> updateDictType(@Valid @RequestBody DictTypeCreateOrUpdateParam param) {
        dictTypeService.updateDictType(param);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字典类型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public ServRespEntity<Boolean> deleteDictTypeByIds(@RequestBody @NotEmpty(message = "id列表不能为空") List<Long> ids) {
        dictTypeService.deleteDictTypeByIds(ids);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得字典类型的分页列表")
    // @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public ServRespEntity<PageResult<SysDictType>> pageDictTypes(@Validated DictTypePageParam param) {
        PageResult<SysDictType> pageResult = dictTypeService.getDictTypePage(param);
        return success(pageResult);
    }

    @Operation(summary = "查询字典类型详细")
    @Parameter(name = "id", description = "字典类型id", required = true, example = "1")
    @GetMapping(value = "/get")
    // @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public ServRespEntity<SysDictType> getDictType(@RequestParam("id") Long id) {
        SysDictType dictType = dictTypeService.getById(id);
        return success(dictType);
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获得全部字典类型列表", description = "包括开启 + 禁用的字典类型，主要用于前端的下拉选项")
    // 无需添加权限认证，因为前端全局都需要
    public ServRespEntity<List<SysDictType>> getSimpleDictTypeList() {
        List<SysDictType> list = dictTypeService.getDictTypeSimpleList();
        return success(list);
    }

    // @Operation(summary = "导出数据类型")
    // @GetMapping("/export")
    // @PreAuthorize("@ss.hasPermission('system:dict:query')")
    // @OperateLog(type = EXPORT)
    // public void export(HttpServletResponse response, @Valid DictTypePageReqVO exportReqVO) throws IOException {
    //     exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
    //     List<DictTypeDO> list = dictTypeService.getDictTypePage(exportReqVO).getList();
    //     // 导出
    //     ExcelUtils.write(response, "字典类型.xls", "数据", DictTypeRespVO.class,
    //             BeanUtils.toBean(list, DictTypeRespVO.class));
    // }

}
