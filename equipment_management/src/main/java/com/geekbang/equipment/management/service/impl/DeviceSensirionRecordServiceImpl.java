package com.geekbang.equipment.management.service.impl;

import com.geekbang.equipment.management.constant.DeviceRecordTableConstant;
import com.geekbang.equipment.management.core.AbstractService;
import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.core.ResultGenerator;
import com.geekbang.equipment.management.dao.DeviceRecordTableInfoMapper;
import com.geekbang.equipment.management.dao.DeviceSensirionRecordMapper;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.DeviceRecordTableInfo;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import com.geekbang.equipment.management.model.dto.DeviceSensirionRecordDTO;
import com.geekbang.equipment.management.service.DeviceSensirionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 温湿度设备数据上报记录
 *
 * @author hejun
 * @date 2020/06/07
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DeviceSensirionRecordServiceImpl extends AbstractService<DeviceSensirionRecord> implements DeviceSensirionRecordService {

    @Resource
    private DeviceSensirionRecordMapper deviceSensirionRecordMapper;

    @Resource
    private DeviceRecordTableInfoMapper deviceRecordTableInfoMapper;

    /**
     * 新增
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    @Override
    public Result<?> add(DeviceSensirionRecordDTO record, String lang) {
        String prefixName = record.getPrefixName();
        if (StringUtils.isEmpty(prefixName)) {
            return ResultGenerator.genFailResult(ResponseCodeI18n.INSERT_FAIL.getMsg(), lang);
        }
        DeviceRecordTableConstant deviceRecordTableConstant = DeviceRecordTableConstant.getTableConstant(prefixName);
        if (deviceRecordTableConstant == null) {
            return ResultGenerator.genFailResult(ResponseCodeI18n.INSERT_FAIL.getMsg(), lang);
        }
        String tableName = deviceRecordTableConstant.getTableName();
        if (StringUtils.isEmpty(tableName)) {
            // 查询设备上报数据记录表表信息表
            Condition condition = new Condition(DeviceRecordTableInfo.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("prefixName", prefixName);
            List<DeviceRecordTableInfo> deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
            if (CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                /*
                 创建新表
                 todo
                */
            } else {
                DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfos.stream()
                        .sorted((o1, o2) -> o2.getId() - o1.getId()).collect(Collectors.toList())
                        .get(0);
                tableName = deviceRecordTableInfo.getTableName();
            }
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
    public Result<?> update(DeviceSensirionRecord record, String lang) {
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
