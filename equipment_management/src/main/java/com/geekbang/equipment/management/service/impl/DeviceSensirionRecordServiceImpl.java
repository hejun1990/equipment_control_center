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
        // 前缀名
        String prefixName = sensirionConstant.getPrefixName();
        // 当前表名
        String tableName = sensirionConstant.getTableName();
        if (StringUtils.isBlank(tableName)) {
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
                        DeviceRecordTableInfo createRecord = new DeviceRecordTableInfo();
                        createRecord.setPrefixName(prefixName);
                        createRecord.setTableName(tableName);
                        createRecord.setRowNumber(0);
                        success = deviceRecordTableInfoMapper.insertSelective(createRecord) == 1;
                        if (!success) {
                            return ResultGenerator.genFailResult(ResponseCodeI18n.INSERT_FAIL.getMsg(), lang);
                        }
                    }
                    if (StringUtils.isBlank(sensirionConstant.getTableName())) {
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
        Condition condition = new Condition(DeviceRecordTableInfo.class);
        Condition.Criteria criteria;
        DeviceRecordTableInfo updateRecord = new DeviceRecordTableInfo();
        List<DeviceRecordTableInfo> deviceRecordTableInfos;
        DeviceRecordTableInfo deviceRecordTableInfo;
        // 这里有并发竞争，因此更新失败就继续更新
        do {
            condition.clear();
            criteria = condition.createCriteria();
            criteria.andEqualTo("prefixName", prefixName)
                    .andEqualTo("tableName", tableName);
            deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
            if (CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                throw new RuntimeException("系统数据异常");
            }
            deviceRecordTableInfo = deviceRecordTableInfos.get(0);
            updateRecord.setRowNumber(deviceRecordTableInfo.getRowNumber() + 1);
            updateRecord.setVersionNo(deviceRecordTableInfo.getVersionNo() + 1);
            condition.clear();
            criteria = condition.createCriteria();
            criteria.andEqualTo("id", deviceRecordTableInfo.getId())
                    .andEqualTo("versionNo", deviceRecordTableInfo.getVersionNo());
            success = deviceRecordTableInfoMapper.updateByConditionSelective(updateRecord, condition) == 1;
        } while (!success);
        // 超过阈值，创建新表
        if (updateRecord.getRowNumber() > sensirionConstant.getRowThreshold()) {
            if (sensirionConstant.lock.tryLock()) {
                try {
                    // 检查当前插入表的表名和枚举类缓存的表名以及表信息表中最新表名是否一致
                    condition.clear();
                    criteria = condition.createCriteria();
                    criteria.andEqualTo("prefixName", prefixName);
                    deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
                    if (!CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                        deviceRecordTableInfo = deviceRecordTableInfos.stream()
                                .sorted((o1, o2) -> o2.getId() - o1.getId()).collect(Collectors.toList())
                                .get(0);
                        if (tableName.equals(sensirionConstant.getTableName())
                                && tableName.equals(deviceRecordTableInfo.getTableName())) {
                            String index = tableName.substring(tableName.indexOf("_") + 1);
                            StringBuilder newTableName = new StringBuilder();
                            int newIndex = Integer.parseInt(index) + 1;
                            newTableName.append(prefixName).append("_").append(newIndex);
                            // 新表表名
                            tableName = newTableName.toString();
                            // 新表注释
                            String tableComment = sensirionConstant.getTableComment() + newIndex;
                            success = deviceSensirionRecordMapper.createTable(tableName, tableComment) == 0;
                            if (!success) {
                                return ResultGenerator.genFailResult(ResponseCodeI18n.CREATE_TABLE_FAIL.getMsg(), lang);
                            }
                            DeviceRecordTableInfo createRecord = new DeviceRecordTableInfo();
                            createRecord.setPrefixName(prefixName);
                            createRecord.setTableName(tableName);
                            createRecord.setRowNumber(0);
                            success = deviceRecordTableInfoMapper.insertSelective(createRecord) == 1;
                            if (!success) {
                                return ResultGenerator.genFailResult(ResponseCodeI18n.INSERT_FAIL.getMsg(), lang);
                            }
                            sensirionConstant.setTableName(tableName);
                        }
                    }
                } finally {
                    sensirionConstant.lock.unlock();
                }
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
