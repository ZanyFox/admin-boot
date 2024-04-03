package com.fz.admin.module.system.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fz.admin.framework.common.enums.CommonStatusEnum;
import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.mybatis.util.MyBatisUtils;
import com.fz.admin.module.system.mapper.SysDictDataMapper;
import com.fz.admin.module.system.model.entity.SysDictData;
import com.fz.admin.module.system.model.entity.SysDictType;
import com.fz.admin.module.system.model.param.DictDataPageParam;
import com.fz.admin.module.system.model.param.DictDataSaveParam;
import com.fz.admin.module.system.service.SysDictDataService;
import com.fz.admin.module.system.service.SysDictTypeService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;


@Service
@AllArgsConstructor
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {


    private final SysDictDataMapper dictDataMapper;

    private SysDictTypeService dictTypeService;

    @Override
    public PageResult<SysDictData> getDictDataPage(DictDataPageParam param) {

        IPage<SysDictData> page = MyBatisUtils.buildPage(param);
        lambdaQuery().eq(param.getStatus() != null, SysDictData::getStatus, param.getStatus())
                .eq(StringUtils.isNotBlank(param.getDictType()), SysDictData::getDictType, param.getDictType())
                .like(StringUtils.isNotBlank(param.getLabel()), SysDictData::getLabel, param.getLabel())
                .page(page);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public List<SysDictData> getDictDataSimpleList() {

        return lambdaQuery().eq(SysDictData::getStatus, CommonStatusEnum.ENABLE.getStatus()).list()
                .stream().sorted(Comparator
                        .comparing(SysDictData::getDictType)
                        .thenComparingInt(SysDictData::getOrder)).toList();
    }

    @Override
    public void deleteDictDataById(Long id) {

        validateDictDataExistsAndGet(id);
        dictDataMapper.deleteById(id);
    }

    @Override
    public void deleteDictDataBatchByIds(List<Long> ids) {
        dictDataMapper.deleteBatchIds(ids);
    }

    @Override
    public void updateDictData(DictDataSaveParam param) {

        param.setDictType(null);
        SysDictData existDictData = validateDictDataExistsAndGet(param.getId());
        validateDictDataValueUnique(param.getId(), existDictData.getDictType(), param.getValue());

        SysDictData dictData = new SysDictData();
        BeanUtils.copyProperties(param, dictData);
        dictDataMapper.updateById(dictData);
    }

    @Override
    public Long createDictData(DictDataSaveParam param) {

        validateDictTypeExists(param.getDictType());
        validateDictDataValueUnique(null, param.getDictType(), param.getValue());

        SysDictData dictData = new SysDictData();
        BeanUtils.copyProperties(param, dictData);
        dictDataMapper.insert(dictData);
        return dictData.getId();
    }

    @Override
    public List<SysDictData> getDictDataByType(String type) {

        return lambdaQuery().eq(SysDictData::getDictType, type).list();
    }

    /**
     * 校验字典类型是否存在
     * @param type 字典类型
     */
    private void validateDictTypeExists(String type) {


        SysDictType dictType = dictTypeService.lambdaQuery().eq(SysDictType::getType, type).one();

        if (dictType == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "该字典类型不存在");
        }
        if (!CommonStatusEnum.ENABLE.getStatus().equals(dictType.getStatus())) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "该字典类型被禁用");
        }
    }

    /**
     * 校验该字典类型下 字典数据值是否唯一
     * @param id 字典数据id
     * @param dictType 字典类型
     * @param value 值
     */
    public void validateDictDataValueUnique(Long id, String dictType, String value) {


        SysDictData dictData = lambdaQuery().eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getValue, value).one();

        if (dictData == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典数据
        if (id == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "该字典类型下已存在该字典数据");
        }
        if (!dictData.getId().equals(id)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "该字典类型下已存在该字典数据");
        }
    }

    /**
     * 校验字典数据是否存在
     * @param id 字典数据id
     */
    private SysDictData validateDictDataExistsAndGet(Long id) {

        SysDictData dictData = dictDataMapper.selectById(id);

        if (dictData == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "id不存在");
        }

        return dictData;
    }
}



