package com.geekbang.equipment.management.service.impl;

import com.geekbang.equipment.management.core.AbstractService;
import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.core.ResultGenerator;
import com.geekbang.equipment.management.dao.DeviceInfoMapper;
import com.geekbang.equipment.management.model.DeviceInfo;
import com.geekbang.equipment.management.service.DeviceInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * @author hejun
 * @date 2020/06/06
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DeviceInfoServiceImpl extends AbstractService<DeviceInfo> implements DeviceInfoService {

    @Resource
    private DeviceInfoMapper deviceInfoMapper;

    /**
     * 新增
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    @Override
    public Result<?> add(DeviceInfo record, String lang) {
        return null;
    }

    /**
     * 删除
     *
     * @param id   主键id
     * @param lang 国际化语言
     * @return Result
     */
    @Override
    public Result<?> delete(Integer id, String lang) {
        return null;
    }

    /**
     * 修改
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    @Override
    public Result<?> update(DeviceInfo record, String lang) {
        return null;
    }

    /**
     * 单个查询
     *
     * @param id   主键id
     * @param lang 国际化语言
     * @return Result
     */
    @Override
    public Result<?> detail(Integer id, String lang) {
        return ResultGenerator.genSuccessResult(deviceInfoMapper.selectByPrimaryKey(id));
    }

    /**
     * 分页查询
     *
     * @param page   页码
     * @param size   每页行数
     * @param record 查询条件
     * @param lang   国际化语言
     * @return Result
     */
    @Override
    public Result<?> list(Integer page, Integer size, DeviceInfo record, String lang) {
        PageInfo pageInfo = PageHelper.startPage(page, size)
                .doSelectPageInfo(() -> deviceInfoMapper.select(record));
        return ResultGenerator.genSuccessResult(pageInfo);
    }
}
