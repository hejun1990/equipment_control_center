package com.geekbang.equipment.management.service.impl;

import com.geekbang.equipment.management.core.AbstractService;
import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.core.ResultGenerator;
import com.geekbang.equipment.management.dao.DeviceRecordTableInfoMapper;
import com.geekbang.equipment.management.dao.DeviceSensirionRecordMapper;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.DeviceRecordTableInfo;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import com.geekbang.equipment.management.service.DeviceSensirionRecordService;
import com.geekbang.equipment.management.util.PojoCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.List;


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

    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

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
        boolean success = deviceSensirionRecordMapper.addRecord(record) == 1;
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
    public Result<?> update(DeviceSensirionRecord record, String lang) {
        // 创建事务
        DefaultTransactionDefinition transDefinition = new DefaultTransactionDefinition();
        transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        transDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
        // 设置事务超时时间（秒）
        transDefinition.setTimeout(60);
        // 启动事务
        TransactionStatus transStatus = platformTransactionManager.getTransaction(transDefinition);
        Condition condition = new Condition(DeviceRecordTableInfo.class);
        Condition.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("tableName", "device_sensirion_record_1");
        List<DeviceRecordTableInfo> deviceRecordTableInfoList = deviceRecordTableInfoMapper.selectByCondition(condition);
        if (CollectionUtils.isEmpty(deviceRecordTableInfoList)) {
            return ResultGenerator.genSuccessResult();
        }
        DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfoList.get(0);
        log.info("thread = {}, row_number = {}", Thread.currentThread().getName(), deviceRecordTableInfo.getRowNumber());
        if (record != null && record.getId() != null) {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            deviceRecordTableInfoList = deviceRecordTableInfoMapper.selectByCondition(condition);
            if (CollectionUtils.isEmpty(deviceRecordTableInfoList)) {
                return ResultGenerator.genSuccessResult();
            }
            deviceRecordTableInfo = deviceRecordTableInfoList.get(0);
            log.info("thread = {}, row_number = {}", Thread.currentThread().getName(), deviceRecordTableInfo.getRowNumber());
        } else {
            DeviceRecordTableInfo updateRecord = new DeviceRecordTableInfo();
            updateRecord.setId(deviceRecordTableInfo.getId());
            updateRecord.setRowNumber(10);
            deviceRecordTableInfoMapper.updateByPrimaryKeySelective(updateRecord);
        }
        platformTransactionManager.commit(transStatus);
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
