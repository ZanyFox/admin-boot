package com.fz.admin.module.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fz.admin.module.system.model.entity.SysDictData;


public interface SysDictDataMapper extends BaseMapper<SysDictData> {


    /**
     * 更新字典数据类型
     *
     * @param newType 新类型
     * @param oldType 旧类型
     */
    default void updateTypeByType(String newType, String oldType) {
        update(Wrappers.<SysDictData>lambdaUpdate()
                .set(SysDictData::getDictType, newType)
                .eq(SysDictData::getDictType, oldType));

    }
}




