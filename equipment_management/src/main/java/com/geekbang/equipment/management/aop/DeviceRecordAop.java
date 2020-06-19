package com.geekbang.equipment.management.aop;

import com.geekbang.equipment.management.constant.BasicConstant;
import com.geekbang.equipment.management.constant.DeviceRecordTableConstant;
import com.geekbang.equipment.management.core.TableMapper;
import com.geekbang.equipment.management.dao.DeviceRecordTableInfoMapper;
import com.geekbang.equipment.management.i18n.I18nMessageUtil;
import com.geekbang.equipment.management.i18n.LanguageEnum;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.DeviceRecordTableInfo;
import com.geekbang.equipment.management.util.BeanHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import javax.persistence.Table;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备数据上报AOP
 *
 * @author hejun
 */
@Aspect
@Component
@Slf4j
public class DeviceRecordAop {

    @Resource
    private DeviceRecordTableInfoMapper deviceRecordTableInfoMapper;

    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    @Pointcut("execution(public com.geekbang.equipment.management.core.Result com.geekbang.equipment.management.service.impl.*.add(..)) && @args(javax.persistence.Table,..)")
    public void addPointcut() {
    }

    @Pointcut("execution(public com.geekbang.equipment.management.core.Result com.geekbang.equipment.management.service.impl.*.update(..)) && @args(javax.persistence.Table,..)")
    public void updatePointcut() {
    }

    @Before("addPointcut()")
    public void beforeAdd(JoinPoint joinPoint) {
        Object model = joinPoint.getArgs()[0];
        Class<?> modelClass = model.getClass();
        Table table = modelClass.getAnnotation(Table.class);
        String prefixName = table.name();
        DeviceRecordTableConstant deviceRecordTableConstant = DeviceRecordTableConstant.getTableConstant(prefixName);
        if (deviceRecordTableConstant == null) {
            log.error("前缀名[{}]，未匹配到对应的设备上报数据记录表表信息常量", prefixName);
            return;
        }
        try {
            // 给第一个参数设置“前缀名”
            Method setMethod = modelClass.getMethod("setPrefixName", String.class);
            setMethod.invoke(model, prefixName);
            // 获取第一个参数设置“表名”的方法
            setMethod = modelClass.getMethod("setTableName", String.class);
            String tableName = deviceRecordTableConstant.getTableName();
            if (StringUtils.isBlank(tableName)) {
                // 查询设备上报数据记录表表信息表
                Condition condition = new Condition(DeviceRecordTableInfo.class);
                Condition.Criteria criteria = condition.createCriteria();
                criteria.andEqualTo("prefixName", prefixName);
                List<DeviceRecordTableInfo> deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
                // 表中没有信息，创建新表
                if (CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                    TransactionStatus transStatus = null;
                    // 加锁
                    deviceRecordTableConstant.lock.lock();
                    try {
                        // 创建事务
                        DefaultTransactionDefinition transDefinition = new DefaultTransactionDefinition();
                        transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
                        transDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
                        // 设置事务超时时间（秒）
                        transDefinition.setTimeout(5);
                        // 启动事务
                        transStatus = platformTransactionManager.getTransaction(transDefinition);
                        // 再次检查设备上报数据记录表表信息
                        deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
                        // 表中依然没有信息，创建新表
                        if (CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                            // 新表表名
                            tableName = prefixName + "_1";
                            // 新表注释
                            String tableComment = deviceRecordTableConstant.getTableComment() + "1";
                            // 获取Mapper
                            String[] prefixNameBlock = prefixName.split("_");
                            StringBuilder mapperNameBuilder = new StringBuilder(prefixNameBlock[0]);
                            for (int i = 1; i < prefixNameBlock.length; i++) {
                                String block = prefixNameBlock[i];
                                mapperNameBuilder.append(Character.toUpperCase(block.charAt(0)))
                                        .append(block.substring(1));
                            }
                            String mapperName = mapperNameBuilder.toString();
                            TableMapper<?> mapper = BeanHeader.getBean(mapperName);
                            assert mapper != null;
                            boolean success = mapper.createTable(tableName, tableComment) == 0;
                            if (!success) {
                                String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                                        ResponseCodeI18n.CREATE_TABLE_FAIL.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
                                log.error("{}", message);
                            }
                            // 新增表信息
                            DeviceRecordTableInfo createRecord = new DeviceRecordTableInfo();
                            createRecord.setPrefixName(prefixName);
                            createRecord.setTableName(tableName);
                            createRecord.setRowNumber(0);
                            success = deviceRecordTableInfoMapper.insertSelective(createRecord) == 1;
                            if (!success) {
                                String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                                        ResponseCodeI18n.INSERT_FAIL.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
                                log.error("{}", message);
                            }
                            // 表名同步到对应枚举类
                            deviceRecordTableConstant.setTableName(tableName);
                        }
                        // 表中有信息
                        else {
                            DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfos.stream()
                                    .sorted((o1, o2) -> o2.getId() - o1.getId()).collect(Collectors.toList())
                                    .get(0);
                            tableName = deviceRecordTableInfo.getTableName();
                            // 给第一个参数设置“表名”
                            setMethod.invoke(model, tableName);
                        }
                        // 提交事务
                        platformTransactionManager.commit(transStatus);
                    } catch (Exception e) {
                        log.error("异常：", e);
                        // 事务回滚
                        if (transStatus != null) {
                            platformTransactionManager.rollback(transStatus);
                        }
                    } finally {
                        // 解锁
                        deviceRecordTableConstant.lock.unlock();
                    }
                } else {
                    DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfos.stream()
                            .sorted((o1, o2) -> o2.getId() - o1.getId()).collect(Collectors.toList())
                            .get(0);
                    tableName = deviceRecordTableInfo.getTableName();
                    // 给第一个参数设置“表名”
                    setMethod.invoke(model, tableName);
                }
            } else {
                // 给第一个参数设置“表名”
                setMethod.invoke(model, tableName);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("异常：", e);
        }
    }

    @AfterReturning("addPointcut()")
    public void afterReturningAdd(JoinPoint joinPoint) {
        Object model = joinPoint.getArgs()[0];
        Class<?> modelClass = model.getClass();
        try {
            Method getMethod = modelClass.getMethod("getPrefixName");
            String prefixName = (String) getMethod.invoke(model);
            getMethod = modelClass.getMethod("getTableName");
            String tableName = (String) getMethod.invoke(model);
            // 检查“前缀名”，“表名”不能为空
            if (StringUtils.isBlank(prefixName) || StringUtils.isBlank(tableName)) {
                return;
            }
            // 更新设备上报数据记录表表信息表中的总行数
            Condition condition = new Condition(DeviceRecordTableInfo.class);
            Condition.Criteria criteria;
            DeviceRecordTableInfo updateRecord = new DeviceRecordTableInfo();
            List<DeviceRecordTableInfo> deviceRecordTableInfos;
            DeviceRecordTableInfo deviceRecordTableInfo;
            boolean success;
            // 创建事务
            DefaultTransactionDefinition transDefinition = new DefaultTransactionDefinition();
            transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
            transDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
            // 启动事务
            TransactionStatus transStatus = platformTransactionManager.getTransaction(transDefinition);
            // 这里有并发竞争，如果更新失败，就重复更新直到成功
            do {
                condition.clear();
                criteria = condition.createCriteria();
                criteria.andEqualTo("prefixName", prefixName)
                        .andEqualTo("tableName", tableName);
                deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
                if (CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                    log.error("{}表没有对应的表信息，请检查", tableName);
                    break;
                }
                deviceRecordTableInfo = deviceRecordTableInfos.get(0);
                updateRecord.setRowNumber(deviceRecordTableInfo.getRowNumber() + 1);
                updateRecord.setVersionNo(deviceRecordTableInfo.getVersionNo() + 1);
                /**
                 * TODO
                 * 检查插入pojo返回的id是否是1，是1表示是该行是首行
                 */
                condition.clear();
                criteria = condition.createCriteria();
                criteria.andEqualTo("id", deviceRecordTableInfo.getId())
                        .andEqualTo("versionNo", deviceRecordTableInfo.getVersionNo());
                success = deviceRecordTableInfoMapper.updateByConditionSelective(updateRecord, condition) == 1;
            } while (!success);
            // 提交事务
            platformTransactionManager.commit(transStatus);

            // 检查表行数是否超过阈值，如果超过则创建新表
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("异常：", e);
        }
    }

    @Before("updatePointcut()")
    public void beforeUpdate(JoinPoint joinPoint) {
        log.info("---------- update开始前 ----------");
        Object model = joinPoint.getArgs()[0];
        Class<?> modelClass = model.getClass();
        Table table = modelClass.getAnnotation(Table.class);
        String prefixName = table.name();
        try {
            Method setPrefixName = modelClass.getMethod("setPrefixName", String.class);
            setPrefixName.invoke(model, prefixName);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("异常：", e);
        }
    }
}
