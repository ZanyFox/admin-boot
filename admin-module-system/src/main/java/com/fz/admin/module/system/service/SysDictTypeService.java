package com.fz.admin.module.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.module.system.model.entity.SysDictType;
import com.fz.admin.module.system.model.param.DictTypeCreateOrUpdateParam;
import com.fz.admin.module.system.model.param.DictTypePageParam;

import java.util.List;


public interface SysDictTypeService extends IService<SysDictType> {


    /**
     * 创建字典类型
     *
     * @param param 字典类型参数
     * @return 字典类型id
     */
    Long createDictType(DictTypeCreateOrUpdateParam param);

    /**
     * 更新字典类型
     * @param param 字典类型参数
     */
    void updateDictType(DictTypeCreateOrUpdateParam param);

    void deleteDictTypeByIds(List<Long> ids);

    /**
     * 字典类型分页查询
     * @param param 分页参数
     * @return 分页结果
     */
    PageResult<SysDictType> getDictTypePage(DictTypePageParam param);

    /**
     * 获取精简信息
     * @return 数据字典类型列表
     */
    List<SysDictType> getDictTypeSimpleList();

}
