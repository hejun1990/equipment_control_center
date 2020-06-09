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
import org.springframework.transaction.annotation.Isolation;
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
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public Result<?> add(DeviceSensirionRecordDTO record, String lang) {
        DeviceRecordTableConstant sensirionConstant = DeviceRecordTableConstant.SENSIRION;
        // 当前表名
        String tableName = sensirionConstant.getTableName();
        if (StringUtils.isBlank(tableName)) {
            String prefixName = sensirionConstant.getPrefixName();
            // 查询设备上报数据记录表表信息表
            Condition condition = new Condition(DeviceRecordTableInfo.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("prefixName", prefixName);
            List<DeviceRecordTableInfo> deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
            // 如果表信息表中的也没有表信息，则创建新表，即为初次创建数据上报表
            if (CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                sensirionConstant.lock.lock();
                try {
                    // 重新判断设备上报数据记录表表信息表中是否有表的信息
                    deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
                    if (!CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                        DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfos.stream()
                                .sorted((o1, o2) -> o2.getId() - o1.getId()).collect(Collectors.toList())
                                .get(0);
                        tableName = deviceRecordTableInfo.getTableName();
                    } else {
                        // 新表表名
                        tableName = prefixName + "_1";
                        // 新表注释
                        String tableComment = sensirionConstant.getTableComment() + "1";
                        boolean success = deviceSensirionRecordMapper.createTable(tableName, tableComment) == 0;
                        if (!success) {
                            return ResultGenerator.genFailResult(ResponseCodeI18n.CREATE_TABLE_FAIL.getMsg(), lang);
                        }
                        DeviceRecordTableInfo deviceRecordTableInfo = new DeviceRecordTableInfo();
                        deviceRecordTableInfo.setPrefixName(prefixName);
                        deviceRecordTableInfo.setTableName(tableName);
                        deviceRecordTableInfo.setRowNumber(0);
                        success = deviceRecordTableInfoMapper.insertSelective(deviceRecordTableInfo) == 1;
                        if (!success) {
                            return ResultGenerator.genFailResult(ResponseCodeI18n.INSERT_FAIL.getMsg(), lang);
                        }
                        sensirionConstant.setTableName(tableName);
                    }
                } finally {
                    sensirionConstant.lock.unlock();
                }
            }
            // 如果表信息表中有表信息，则更新枚举类中的当前表名
            else {
                DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfos.stream()
                        .sorted((o1, o2) -> o2.getId() - o1.getId()).collect(Collectors.toList())
                        .get(0);
                sensirionConstant.lock.lock();
                try {
                    // 需要再次确认枚举类中是否已有当前表名
                    if (StringUtils.isBlank(sensirionConstant.getTableName())) {
                        tableName = deviceRecordTableInfo.getTableName();
                        sensirionConstant.setTableName(tableName);
                    }
                } finally {
                    sensirionConstant.lock.unlock();
                }
            }
        }
        // 新增温湿度设备上报数据
        record.setTableName(tableName);
        boolean success = deviceSensirionRecordMapper.addRecord(record) == 1;
        if (!success) {
            return ResultGenerator.genFailResult(ResponseCodeI18n.INSERT_FAIL.getMsg(), lang);
        }
        // 更新设备上报数据记录表表信息表中的总行数，如果总行数超过阈值，则创建新表
        DeviceRecordTableInfo deviceRecordTableInfoRecord = new DeviceRecordTableInfo();
        deviceRecordTableInfoRecord.setPrefixName(sensirionConstant.getPrefixName());
        deviceRecordTableInfoRecord.setTableName(tableName);
        List<DeviceRecordTableInfo> deviceRecordTableInfos = deviceRecordTableInfoMapper.select(deviceRecordTableInfoRecord);
        if (CollectionUtils.isEmpty(deviceRecordTableInfos)) {
            throw new RuntimeException("系统数据异常");
        }
        DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfos.get(0);
        Integer rowNumber = deviceRecordTableInfo.getRowNumber();
        DeviceRecordTableInfo updateRecord = new DeviceRecordTableInfo();
        updateRecord.setId(deviceRecordTableInfo.getId());
        updateRecord.setRowNumber(++rowNumber);
        updateRecord.setVersionNo(deviceRecordTableInfo.getVersionNo() + 1);
        do {

        } while (!success);
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
