package com.fz.admin.module.system.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fz.admin.framework.common.enums.ServRespCode;
import com.fz.admin.framework.common.exception.ServiceException;
import com.fz.admin.framework.common.pojo.PageResult;
import com.fz.admin.framework.mybatis.util.MyBatisUtils;
import com.fz.admin.module.system.mapper.SysDictDataMapper;
import com.fz.admin.module.system.mapper.SysDictTypeMapper;
import com.fz.admin.module.system.model.entity.SysDictData;
import com.fz.admin.module.system.model.entity.SysDictType;
import com.fz.admin.module.system.model.param.DictTypeCreateOrUpdateParam;
import com.fz.admin.module.system.model.param.DictTypePageParam;
import com.fz.admin.module.system.service.SysDictTypeService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    private SysDictTypeMapper dictTypeMapper;


    private SysDictDataMapper dictDataMapper;

    @Override
    public Long createDictType(DictTypeCreateOrUpdateParam param) {

        validateDictTypeNameUnique(null, param.getName());
        validateDictTypeUnique(null, param.getType());

        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(param, dictType);
        dictTypeMapper.insert(dictType);
        return dictType.getId();
    }

    @Transactional
    @Override
    public void updateDictType(DictTypeCreateOrUpdateParam param) {
        SysDictType existDictType = validateDictTypeExistsAndGet(param.getId());
        validateDictTypeNameUnique(param.getId(), param.getName());
        validateDictTypeUnique(param.getId(), param.getType());

        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(param, dictType);
        dictTypeMapper.updateById(dictType);

        // 更新字典数据类型
        dictDataMapper.updateTypeByType(param.getType(), existDictType.getType());

    }

    @Override
    public void deleteDictTypeByIds(List<Long> ids) {

        List<SysDictType> sysDictTypes = lambdaQuery().in(SysDictType::getId, ids).list();
        List<String> types = sysDictTypes.stream().map(SysDictType::getType).toList();
        Long count = dictDataMapper.selectCount(new LambdaQueryWrapper<SysDictData>().in(SysDictData::getDictType, types));
        // 该type存在data
        if (ObjectUtils.compare(count, 0L) > 0) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "无法删除,类型下存在字典数据");
        }
        dictTypeMapper.deleteBatchIds(ids);
    }

    @Override
    public PageResult<SysDictType> getDictTypePage(DictTypePageParam param) {

        IPage<SysDictType> page = MyBatisUtils.buildPage(param);
        lambdaQuery().eq(param.getStatus() != null, SysDictType::getStatus, param.getStatus())
                .like(StringUtils.isNotBlank(param.getName()), SysDictType::getName, param.getName())
                .like(StringUtils.isNotBlank(param.getType()), SysDictType::getType, param.getType())
                .ge(param.getBeginTime() != null, SysDictType::getCreateTime, param.getBeginTime())
                .le(param.getEndTime() != null, SysDictType::getCreateTime, param.getEndTime())
                .orderByAsc(SysDictType::getId).page(page);

        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public List<SysDictType> getDictTypeSimpleList() {
         return lambdaQuery().select(SysDictType::getId, SysDictType::getName, SysDictType::getType).list();
    }


    /**
     * 校验字典类型是否存在,如果存在则返回
     *
     * @param id
     * @return
     */
    SysDictType validateDictTypeExistsAndGet(Long id) {

        SysDictType dictType = dictTypeMapper.selectById(id);
        if (dictType == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "不存在");
        }
        return dictType;
    }

    /**
     * 校验字典类型名称是否唯一
     *
     * @param id   字典类型id
     * @param name 字典类型名称
     */
    void validateDictTypeNameUnique(Long id, String name) {

        SysDictType dictType = lambdaQuery().eq(SysDictType::getName, name).one();

        if (dictType == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "名称重复");
        }
        if (!dictType.getId().equals(id)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "名称重复");
        }
    }

    /**
     * 校验字典类型是否唯一
     *
     * @param id   字典类型id
     * @param type 字典类型
     */
    void validateDictTypeUnique(Long id, String type) {
        if (StrUtil.isEmpty(type)) {
            return;
        }
        SysDictType dictType = lambdaQuery().eq(SysDictType::getType, type).one();
        if (dictType == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "类型重复");
        }
        if (!dictType.getId().equals(id)) {
            throw new ServiceException(ServRespCode.REQUEST_PARAMETER_ERROR.getCode(), "类型重复");
        }
    }
}




