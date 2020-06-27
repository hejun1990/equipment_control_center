package com.geekbang.equipment.management.aop;

import com.geekbang.equipment.management.constant.BasicConstant;
import com.geekbang.equipment.management.constant.DeviceRecordTableConstant;
import com.geekbang.equipment.management.core.TableMapper;
import com.geekbang.equipment.management.dao.DeviceRecordTableInfoMapper;
import com.geekbang.equipment.management.i18n.I18nMessageUtil;
import com.geekbang.equipment.management.i18n.LanguageEnum;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.DeviceRecordTableInfo;
import com.geekbang.equipment.management.model.TableEntity;
import com.geekbang.equipment.management.util.BeanHeader;
import com.geekbang.equipment.management.util.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    @Before("addPointcut()")
    public void beforeAdd(JoinPoint joinPoint) {
        Object model = joinPoint.getArgs()[0];
        Class<?> modelClass = model.getClass();
        Table table = modelClass.getAnnotation(Table.class);
        String prefixName = table.name();
        DeviceRecordTableConstant deviceRecordTableConstant = DeviceRecordTableConstant.getTableConstant(prefixName);
        if (deviceRecordTableConstant == null) {
            log.error("前缀名[{}]，未匹配到对应的表缓存", prefixName);
            return;
        }
        try {
            // 给第一个参数设置“前缀名”
            Method setMethod = modelClass.getMethod("setPrefixName", String.class);
            setMethod.invoke(model, prefixName);
            // 获取第一个参数设置“表名”的方法
            setMethod = modelClass.getMethod("setTableName", String.class);
            // 获取缓存中的表名
            String tableName = deviceRecordTableConstant.getTableName();
            // 如果缓存的表名为空，有2种情况。
            // 1、数据库中没有表，需要首次创建表。
            // 2、数据库中有表，需要将数据库中的表名同步到缓存里。
            if (StringUtils.isBlank(tableName)) {
                // 创建事务
                DefaultTransactionDefinition transDefinition = new DefaultTransactionDefinition();
                transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
                transDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
                // 设置事务超时时间（秒）
                transDefinition.setTimeout(5);
                // 启动事务
                TransactionStatus transStatus = platformTransactionManager.getTransaction(transDefinition);
                // 查询设备上报数据记录表表信息
                Condition condition = new Condition(DeviceRecordTableInfo.class);
                Condition.Criteria criteria = condition.createCriteria();
                criteria.andEqualTo("prefixName", prefixName);
                List<DeviceRecordTableInfo> deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
                // 表中没有对应的表信息，需要首次创建新表
                if (CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                    // 加锁
                    deviceRecordTableConstant.lock.lock();
                    try {
                        // 再次检查设备上报数据记录表表信息
                        deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
                        // 依然没有表信息，则首次创建新表
                        if (CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                            // 新表表名
                            tableName = prefixName + "_1";
                            // 新表注释
                            String basicComment = deviceRecordTableConstant.getTableComment();
                            String tableComment = basicComment + "1";
                            log.info("首创新表{}, threadName = {}", tableName, Thread.currentThread().getName());
                            createDeviceRecordTable(prefixName, basicComment, tableName, tableComment);
                            // 提交事务
                            platformTransactionManager.commit(transStatus);
                        }
                        // 表中有信息，获取表名
                        else {
                            DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfos.stream()
                                    .sorted((o1, o2) -> o2.getId() - o1.getId()).collect(Collectors.toList())
                                    .get(0);
                            tableName = deviceRecordTableInfo.getTableName();
                        }
                        // 若缓存为空，将表名同步到缓存
                        if (StringUtils.isBlank(deviceRecordTableConstant.getTableName())) {
                            deviceRecordTableConstant.setTableName(tableName);
                        }
                        // 给第一个参数设置“表名”
                        setMethod.invoke(model, tableName);
                    } catch (Exception e) {
                        log.error("异常：", e);
                        // 事务回滚
                        platformTransactionManager.rollback(transStatus);
                    } finally {
                        // 解锁
                        deviceRecordTableConstant.lock.unlock();
                    }
                }
                // 表中已有对应的表信息
                else {
                    DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfos.stream()
                            .sorted((o1, o2) -> o2.getId() - o1.getId()).collect(Collectors.toList())
                            .get(0);
                    tableName = deviceRecordTableInfo.getTableName();
                    // 给第一个参数设置“表名”
                    setMethod.invoke(model, tableName);
                    // 若缓存为空，将表名同步到缓存
                    // 加锁
                    deviceRecordTableConstant.lock.lock();
                    try {
                        // 再次检查缓存中是否有表名
                        if (StringUtils.isBlank(deviceRecordTableConstant.getTableName())) {
                            deviceRecordTableConstant.setTableName(tableName);
                        }
                    } finally {
                        // 解锁
                        deviceRecordTableConstant.lock.unlock();
                    }
                }
                // 检查事务是否已完成（已提交或发生回滚），未完成则提交事务
                if (!transStatus.isCompleted()) {
                    platformTransactionManager.commit(transStatus);
                }
            } else {
                // 给第一个参数设置“表名”
                setMethod.invoke(model, tableName);
            }
            // 缓存表字段
            storeTableColumns(deviceRecordTableConstant, model);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("异常：", e);
        }
    }

    @Around("addPointcut()")
    public Object aroundAdd(ProceedingJoinPoint proceedingJoinPoint) {
        // 创建事务
        DefaultTransactionDefinition transDefinition = new DefaultTransactionDefinition();
        transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        transDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_REPEATABLE_READ);
        // 设置事务超时时间（秒）
        transDefinition.setTimeout(5);
        // 启动事务
        TransactionStatus transStatus = platformTransactionManager.getTransaction(transDefinition);
        try {
            Object result = proceedingJoinPoint.proceed();
            // 提交事务
            platformTransactionManager.commit(transStatus);
            return result;
        } catch (Throwable throwable) {
            // 事务回滚
            platformTransactionManager.rollback(transStatus);
            log.error("异常：", throwable);
        }
        return null;
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
            DeviceRecordTableInfo updateRecord = new DeviceRecordTableInfo();
            // 检查插入记录返回的id是否是1，是1表示该行是表首行，要记录首行的上报时间
            getMethod = modelClass.getMethod("getId");
            Integer id = (Integer) getMethod.invoke(model);
            if (id == 1) {
                getMethod = modelClass.getMethod("getRecordTime");
                Date recordTime = (Date) getMethod.invoke(model);
                updateRecord.setStartRecordTime(recordTime);
            }
            // 创建事务
            DefaultTransactionDefinition transDefinition = new DefaultTransactionDefinition();
            transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
            // 启动事务
            TransactionStatus transStatus = platformTransactionManager.getTransaction(transDefinition);
            try {
                // 这里有并发竞争，采用更新直到成功策略
                updateUntilSuccessful(prefixName, tableName, updateRecord, true);
                // 提交事务
                platformTransactionManager.commit(transStatus);
            } catch (Exception e) {
                log.error("异常：", e);
                // 事务回滚
                platformTransactionManager.rollback(transStatus);
                return;
            }

            // 检查表行数是否超过阈值，如果超过则创建新表
            DeviceRecordTableConstant deviceRecordTableConstant = DeviceRecordTableConstant.getTableConstant(prefixName);
            if (deviceRecordTableConstant == null) {
                log.error("前缀名[{}]，未匹配到对应的设备上报数据记录表表信息常量", prefixName);
                return;
            }
            if (updateRecord.getRowNumber() > deviceRecordTableConstant.getRowThreshold()) {
                // 检查缓存的表名是否和当前的插入表名一致，如果一致，可以创建新表。
                // 如果不一致，说明新表已被别的线程创建，则不必再创建
                if (tableName.equals(deviceRecordTableConstant.getTableName())) {
                    // 尝试加锁，失败就放弃。将建表操作让渡给其他线程。
                    if (deviceRecordTableConstant.lock.tryLock()) {
                        try {
                            // 创建事务
                            transDefinition = new DefaultTransactionDefinition();
                            transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
                            transDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
                            // 设置事务超时时间（秒）
                            transDefinition.setTimeout(5);
                            // 启动事务
                            transStatus = platformTransactionManager.getTransaction(transDefinition);
                            // 新表表名
                            String index = tableName.substring(tableName.lastIndexOf("_") + 1);
                            int nextIndex = Integer.parseInt(index) + 1;
                            tableName = prefixName + "_" + nextIndex;
                            // 新表注释
                            String basicComment = deviceRecordTableConstant.getTableComment();
                            String tableComment = basicComment + nextIndex;
                            log.info("超过阈值创建新表{}", tableName);
                            createDeviceRecordTable(prefixName, basicComment, tableName, tableComment);
                            platformTransactionManager.commit(transStatus);
                            // 同步新表名到缓存
                            deviceRecordTableConstant.setTableName(tableName);
                        } catch (Exception e) {
                            log.error("异常：", e);
                            // 事务回滚
                            platformTransactionManager.rollback(transStatus);
                            return;
                        } finally {
                            deviceRecordTableConstant.lock.unlock();
                        }
                        // 更新历史表单的末行记录时间
                        updateEndRecordTime(prefixName, tableName);
                    }
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("异常：", e);
        }
    }

    /**
     * 创建新的设备上报数据记录表
     *
     * @param prefixName   前缀名
     * @param basicComment 表注释
     * @param tableName    新表名
     * @param tableComment 新表注释
     */
    private void createDeviceRecordTable(String prefixName, String basicComment, String tableName, String tableComment) {
        // 获取Mapper
        String mapperName = getMapperName(prefixName);
        TableMapper<?> mapper = BeanHeader.getBean(mapperName);
        assert mapper != null;
        // 获取建表语句
        Map<String, String> createSqlMap = mapper.getCreateTableInfo(prefixName);
        if (createSqlMap == null || !createSqlMap.containsKey(BasicConstant.TABLE_FIELD_CREATE_TABLE)) {
            String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                    ResponseCodeI18n.CREATE_TABLE_FAIL.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
            throw new RuntimeException(message);
        }
        String createSql = createSqlMap.get(BasicConstant.TABLE_FIELD_CREATE_TABLE);
        // 替换建表语句的“表名”和“注释”
        createSql = createSql.replaceFirst(prefixName, tableName)
                .replaceFirst(basicComment, tableComment);
        // 执行建表语句
        mapper.createTable(createSql);
        // 新增表信息
        DeviceRecordTableInfo createRecord = new DeviceRecordTableInfo();
        createRecord.setPrefixName(prefixName);
        createRecord.setTableName(tableName);
        createRecord.setRowNumber(0);
        boolean success = deviceRecordTableInfoMapper.insertSelective(createRecord) == 1;
        if (!success) {
            String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                    ResponseCodeI18n.INSERT_FAIL.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
            throw new RuntimeException(message);
        }
    }

    /**
     * 缓存表字段
     *
     * @param deviceRecordTableConstant DeviceRecordTableConstant
     * @param model                     Object
     */
    private void storeTableColumns(DeviceRecordTableConstant deviceRecordTableConstant, Object model) {
        if (CollectionUtils.isEmpty(deviceRecordTableConstant.getColumns())) {
            deviceRecordTableConstant.lock.lock();
            try {
                if (CollectionUtils.isEmpty(deviceRecordTableConstant.getColumns())) {
                    String mapperName = getMapperName(deviceRecordTableConstant.getPrefixName());
                    TableMapper<?> mapper = BeanHeader.getBean(mapperName);
                    assert mapper != null;
                    if (model instanceof TableEntity) {
                        TableEntity tableEntity = (TableEntity) model;
                        List<Map<String, String>> columnMapList = mapper.getTableColumns(tableEntity);
                        if (CollectionUtils.isEmpty(columnMapList)) {
                            log.error("获取表[{}]字段失败", deviceRecordTableConstant.getPrefixName());
                            String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                                    ResponseCodeI18n.DATA_IS_NULL.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
                            throw new RuntimeException(message);
                        }
                        List<String> columns = columnMapList.stream().map(stringMap -> stringMap.get("column_name"))
                                .filter(s -> !"id".equals(s)).collect(Collectors.toList());
                        deviceRecordTableConstant.setColumns(columns);
                    } else {
                        log.error("入参没有继承TableEntity");
                        String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                                ResponseCodeI18n.PARAMS_ARE_ERROR.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
                        throw new RuntimeException(message);
                    }
                }
            } finally {
                deviceRecordTableConstant.lock.unlock();
            }
        }
    }

    /**
     * 更新历史表单的末行记录时间
     *
     * @param prefixName prefixName
     * @param tableName  表名（最新）
     */
    private void updateEndRecordTime(final String prefixName, final String tableName) {
        if (StringUtils.isBlank(prefixName) || StringUtils.isBlank(tableName)) {
            String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                    ResponseCodeI18n.PARAMS_ARE_ERROR.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
            log.error(message);
            return;
        }
        ThreadPoolFactory.COMMON.getPool().execute(() -> {
            // 创建事务
            DefaultTransactionDefinition transDefinition = new DefaultTransactionDefinition();
            transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
            transDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
            // 启动事务
            TransactionStatus transStatus = platformTransactionManager.getTransaction(transDefinition);
            try {
                String index = tableName.substring(tableName.lastIndexOf("_") + 1);
                int preIndex = Integer.parseInt(index) - 1;
                Condition condition = new Condition(DeviceRecordTableInfo.class);
                Condition.Criteria criteria;
                DeviceRecordTableInfo updateRecord = new DeviceRecordTableInfo();
                for (int i = preIndex; i > 0; i--) {
                    condition.clear();
                    criteria = condition.createCriteria();
                    String preTableName = prefixName + "_" + i;
                    criteria.andEqualTo("prefixName", prefixName)
                            .andEqualTo("tableName", preTableName);
                    List<DeviceRecordTableInfo> deviceRecordTableInfos = deviceRecordTableInfoMapper.selectByCondition(condition);
                    // 表信息不存在，跳过
                    if (CollectionUtils.isEmpty(deviceRecordTableInfos)) {
                        log.warn("{}表信息不存在", preTableName);
                        continue;
                    }
                    DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfos.get(0);
                    // 末行记录时间已存在，跳过
                    if (deviceRecordTableInfo.getEndRecordTime() != null) {
                        continue;
                    }
                    // Mapper类名称
                    String mapperName = getMapperName(prefixName);
                    TableMapper<?> mapper = BeanHeader.getBean(mapperName);
                    assert mapper != null;
                    // 获取设备上报数据记录表最后一条记录的上报时间
                    Date recordTime = mapper.getLastRecordTime(preTableName);
                    updateRecord.setEndRecordTime(recordTime);
                    // 并发竞争的概率很小，以防万一，依然采用更新直到成功的策略
                    updateUntilSuccessful(prefixName, preTableName, updateRecord, false);
                }
                platformTransactionManager.commit(transStatus);
            } catch (Exception e) {
                log.error("异常：", e);
                // 事务回滚
                platformTransactionManager.rollback(transStatus);
            }
        });
    }

    /**
     * 获取Mapper类名称
     *
     * @param prefixName 前缀名
     * @return String
     */
    private String getMapperName(final String prefixName) {
        if (StringUtils.isBlank(prefixName)) {
            String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                    ResponseCodeI18n.PARAMS_ARE_ERROR.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
            throw new RuntimeException(message);
        }
        String[] prefixNameBlock = prefixName.split("_");
        StringBuilder mapperNameBuilder = new StringBuilder(prefixNameBlock[0]);
        for (int i = 1; i < prefixNameBlock.length; i++) {
            String block = prefixNameBlock[i];
            mapperNameBuilder.append(Character.toUpperCase(block.charAt(0)))
                    .append(block.substring(1));
        }
        mapperNameBuilder.append("Mapper");
        return mapperNameBuilder.toString();
    }

    /**
     * 更新表信息
     * <br/>
     * 更新策略：更新直到成功
     *
     * @param prefixName   前缀名
     * @param tableName    表名
     * @param updateRecord 更新参数
     * @param isUpdateRow  是否更新总行数
     */
    private void updateUntilSuccessful(String prefixName, String tableName, DeviceRecordTableInfo updateRecord,
                                       boolean isUpdateRow) {
        List<DeviceRecordTableInfo> deviceRecordTableInfos;
        DeviceRecordTableInfo deviceRecordTableInfo;
        boolean success;
        Condition condition = new Condition(DeviceRecordTableInfo.class);
        Condition.Criteria criteria;
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
            if (isUpdateRow) {
                updateRecord.setRowNumber(deviceRecordTableInfo.getRowNumber() + 1);
            }
            updateRecord.setVersionNo(deviceRecordTableInfo.getVersionNo() + 1);
            condition.clear();
            criteria = condition.createCriteria();
            criteria.andEqualTo("id", deviceRecordTableInfo.getId())
                    .andEqualTo("versionNo", deviceRecordTableInfo.getVersionNo());
            success = deviceRecordTableInfoMapper.updateByConditionSelective(updateRecord, condition) == 1;
        } while (!success);
    }
}