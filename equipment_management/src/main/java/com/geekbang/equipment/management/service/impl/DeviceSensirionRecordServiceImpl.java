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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
        // 创建事务
        DefaultTransactionDefinition transDefinition = new DefaultTransactionDefinition();
        transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        transDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
        // 设置事务超时时间（秒）
        transDefinition.setTimeout(5);
        // 启动事务
        TransactionStatus transStatus = platformTransactionManager.getTransaction(transDefinition);
        boolean success = deviceSensirionRecordMapper.addRecord(record) == 1;
        // 提交事务
        platformTransactionManager.commit(transStatus);
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
        log.info("---------- update ----------");
        log.info("prefixName = {}", record.getPrefixName());
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
