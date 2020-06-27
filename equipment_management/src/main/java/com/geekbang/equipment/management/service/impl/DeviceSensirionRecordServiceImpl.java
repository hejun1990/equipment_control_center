package com.geekbang.equipment.management.service.impl;

import com.geekbang.equipment.management.core.AbstractService;
import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.core.ResultGenerator;
import com.geekbang.equipment.management.dao.DeviceSensirionRecordMapper;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import com.geekbang.equipment.management.service.DeviceSensirionRecordService;
import com.geekbang.equipment.management.util.PojoCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * 温湿度设备数据上报记录
 *
 * @author hejun
 * @date 2020/06/07
 */
@Slf4j
@Service
public class DeviceSensirionRecordServiceImpl extends AbstractService<DeviceSensirionRecord> implements DeviceSensirionRecordService {

    @Resource
    private DeviceSensirionRecordMapper deviceSensirionRecordMapper;

    /**
     * 新增
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    @Override
    public Result<?> add(DeviceSensirionRecord record, String lang) {
        Result<?> check = PojoCheck.tableCheck(record, lang);
        if (check != null) {
            return check;
        }
        boolean success = deviceSensirionRecordMapper.insertRecord(record) == 1;
        if (!success) {
            return ResultGenerator.genFailResult(ResponseCodeI18n.INSERT_FAIL.getMsg(), lang);
        }
        return ResultGenerator.genSuccessResult();
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
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(DeviceSensirionRecord record, String lang) {
        return ResultGenerator.genSuccessResult();
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
        return null;
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
    public Result<?> list(Integer page, Integer size, DeviceSensirionRecord record, String lang) {
        return null;
    }
}
