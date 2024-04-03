package com.fz.admin.module.system.controller.admin;


import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.common.pojo.ServRespEntity;
import com.fz.admin.framework.web.validation.group.CreateOrUpdateGroup;
import com.fz.admin.module.system.model.entity.SysDictData;
import com.fz.admin.module.system.model.param.DictDataPageParam;
import com.fz.admin.module.system.model.param.DictDataSaveParam;
import com.fz.admin.module.system.service.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fz.admin.framework.common.pojo.ServRespEntity.success;


@Tag(name = "字典数据管理")
@RestController
@RequestMapping("/system/dict-data")
@Validated
public class DictDataController {

    @Resource
    private SysDictDataService dictDataService;

    @PostMapping("/create")
    @Operation(summary = "新增字典数据")
    // @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public ServRespEntity<Long> createDictData(@Validated({CreateOrUpdateGroup.CreateGroup.class}) @RequestBody DictDataSaveParam param) {
        Long dictDataId = dictDataService.createDictData(param);
        return success(dictDataId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改字典数据")
    // @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public ServRespEntity<Boolean> updateDictData(@Validated({CreateOrUpdateGroup.UpdateGroup.class}) @RequestBody DictDataSaveParam param) {
        dictDataService.updateDictData(param);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字典数据")
    @Parameter(name = "id", description = "字典数据id", required = true, example = "0")
    // @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public ServRespEntity<Boolean> deleteDictData(@RequestParam("id") Long id) {
        dictDataService.deleteDictDataById(id);
        return success(true);
    }

    @DeleteMapping("/delete-batch")
    @Operation(summary = "批量删除字典数据")
    @Parameter(name = "ids", description = "字典数据id列表", required = true, example = "[0]")
    // @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public ServRespEntity<Boolean> deleteDictDataBatch(@RequestBody @NotEmpty(message = "不可为空") List<Long> ids) {
        dictDataService.deleteDictDataBatchByIds(ids);
        return success(true);
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获得全部字典数据列表", description = "一般用于管理后台缓存字典数据在本地")
    // 无需添加权限认证，因为前端全局都需要
    public ServRespEntity<List<SysDictData>> getSimpleDictDataList() {
        List<SysDictData> list = dictDataService.getDictDataSimpleList();
        return success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "获得字典类型的分页列表")
    // @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public ServRespEntity<PageResult<SysDictData>> getDictTypePage(@Validated DictDataPageParam param) {
        PageResult<SysDictData> pageResult = dictDataService.getDictDataPage(param);
        return success(pageResult);
    }

    @GetMapping(value = "/get")
    @Operation(summary = "查询字典数据详细")
    @Parameter(name = "id", description = "编号", required = true, example = "0")
    // @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public ServRespEntity<SysDictData> getDictData(@RequestParam("id") Long id) {
        SysDictData dictData = dictDataService.getById(id);
        return success();
    }

    @GetMapping(value = "/get-by-type")
    @Operation(summary = "根据字典类型获取字典数据")
    @Parameter(name = "type", description = "字典类型", required = true, example = "sys_user_sex")
    public ServRespEntity<List<SysDictData>> getDictDataByType(@RequestParam("type") String type) {
        List<SysDictData> dictDataList = dictDataService.getDictDataByType(type);
        return success(dictDataList);
    }


    // @GetMapping("/export")
    // @Operation(summary = "导出字典数据")
    // // @PreAuthorize("@ss.hasPermission('system:dict:export')")
    // @OperateLog(type = EXPORT)
    // public void export(HttpServletResponse response, @Valid DictDataPageReqVO exportReqVO) throws IOException {
    //     exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
    //     List<DictDataDO> list = dictDataService.getDictDataPage(exportReqVO).getList();
    //     // 输出
    //     ExcelUtils.write(response, "字典数据.xls", "数据", DictDataRespVO.class,
    //             BeanUtils.toBean(list, DictDataRespVO.class));
    // }

}
