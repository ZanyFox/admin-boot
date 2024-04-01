package com.fz.admin.module.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.module.system.model.entity.SysDictData;
import com.fz.admin.module.system.model.param.DictDataPageParam;
import com.fz.admin.module.system.model.param.DictDataSaveParam;

import java.util.List;


public interface SysDictDataService extends IService<SysDictData> {



    /**
     * 获取字典数据分页
     * @param param 分页参数
     * @return 分页结果
     */
    PageResult<SysDictData> getDictDataPage(DictDataPageParam param);


    /**
     * 获取可用数据字典列表
     * @return 数据字典列表
     */
    List<SysDictData> getDictDataSimpleList();

    /**
     * 根据 id 删除字典数据
     * @param id 字典数据 id
     */
    void deleteDictDataById(Long id);

    /**
     * 批量删除
     * @param ids ids
     */
    void deleteDictDataBatchByIds(List<Long> ids);


    /**
     * 更新字典数据
     * @param param 字典数据参数
     */
    void updateDictData(DictDataSaveParam param);

    /**
     * 新增字典数据
     * @param param 字典参数
     * @return 字典id
     */
    Long createDictData(DictDataSaveParam param);
}
