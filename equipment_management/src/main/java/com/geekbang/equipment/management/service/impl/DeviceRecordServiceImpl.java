package com.geekbang.equipment.management.service.impl;

import com.geekbang.equipment.management.constant.BasicConstant;
import com.geekbang.equipment.management.core.ParamsCheck;
import com.geekbang.equipment.management.core.ParamsException;
import com.geekbang.equipment.management.core.TableMapper;
import com.geekbang.equipment.management.dao.DeviceRecordTableInfoMapper;
import com.geekbang.equipment.management.i18n.I18nMessageUtil;
import com.geekbang.equipment.management.i18n.LanguageEnum;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.DeviceRecordTableInfo;
import com.geekbang.equipment.management.model.dto.DeviceRecordQueryDTO;
import com.geekbang.equipment.management.model.vo.DistributedQueryResultVO;
import com.geekbang.equipment.management.model.vo.DistributedQueryVO;
import com.geekbang.equipment.management.service.DeviceRecordService;
import com.geekbang.equipment.management.util.MapperUtil;
import com.geekbang.equipment.management.util.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 设备数据上报通用接口
 *
 * @author hejun
 * @date 2020/08/10
 **/
@Slf4j
@Service
public class DeviceRecordServiceImpl implements DeviceRecordService {

    @Resource
    private DeviceRecordTableInfoMapper deviceRecordTableInfoMapper;

    /**
     * 水平分表分页查询
     *
     * @param distributedQueryVO 水平分表分页查询参数
     * @return DistributedQueryResultVO
     */
    @Override
    @Transactional(readOnly = true)
    public DistributedQueryResultVO distributedSelectByCondition(DistributedQueryVO distributedQueryVO) {
        ParamsCheck.init().notNull(distributedQueryVO, ResponseCodeI18n.PARAMS_ARE_ERROR.getMsg());
        // 分页查询条件
        Condition condition = distributedQueryVO.getCondition();
        // 页码
        Integer page = distributedQueryVO.getPage();
        // 分页行数
        Integer rows = distributedQueryVO.getRows();
        ParamsCheck.init().notNull(page, ResponseCodeI18n.PARAMS_ARE_ERROR.getMsg())
                .notNull(rows, ResponseCodeI18n.PARAMS_ARE_ERROR.getMsg());
        // 分页查询开始时间
        String queryStartTime = distributedQueryVO.getQueryStartTime();
        // 分页查询结束时间
        String queryEndTime = distributedQueryVO.getQueryEndTime();
        Class<?> recordClass = condition.getEntityClass();
        Table table = recordClass.getAnnotation(Table.class);
        if (table == null) {
            String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                    ResponseCodeI18n.PARAMS_ARE_ERROR.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
            throw new ParamsException(message);
        }
        String prefixName = table.name();
        DistributedQueryResultVO result = new DistributedQueryResultVO();
        try {
            // 查询设备上报数据记录表表信息
            DeviceRecordTableInfo deviceRecordTableInfoRecord = new DeviceRecordTableInfo();
            deviceRecordTableInfoRecord.setPrefixName(prefixName);
            List<DeviceRecordTableInfo> deviceRecordTableInfoList = deviceRecordTableInfoMapper.select(deviceRecordTableInfoRecord);
            if (CollectionUtils.isEmpty(deviceRecordTableInfoList)) {
                return result;
            }
            // 通过查询的时间范围确定要查询的表
            if (StringUtils.isNotBlank(queryStartTime)) {
                Date startTime = DateUtils.parseDate(queryStartTime, "yyyy-MM-dd HH:mm:ss");
                // 根据查询开始时间过滤表
                deviceRecordTableInfoList = deviceRecordTableInfoList.stream()
                        .filter(deviceRecordTableInfo -> deviceRecordTableInfo.getEndRecordTime() == null
                                || startTime.compareTo(deviceRecordTableInfo.getEndRecordTime()) <= 0)
                        .collect(Collectors.toList());
            }
            if (StringUtils.isNotBlank(queryEndTime)) {
                Date endTime = DateUtils.parseDate(queryEndTime, "yyyy-MM-dd HH:mm:ss");
                // 根据查询结束时间过滤表
                deviceRecordTableInfoList = deviceRecordTableInfoList.stream()
                        .filter(deviceRecordTableInfo -> endTime.compareTo(deviceRecordTableInfo.getStartRecordTime()) >= 0)
                        .collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(deviceRecordTableInfoList)) {
                return result;
            }
            // 添加时间段查询条件
            if (StringUtils.isNotBlank(queryStartTime) || StringUtils.isNotBlank(queryEndTime)) {
                List<Condition.Criteria> criteriaList = condition.getOredCriteria();
                Condition.Criteria criteria0 = criteriaList.get(0);
                if (StringUtils.isNotBlank(queryStartTime)) {
                    if (StringUtils.isNotBlank(queryEndTime)) {
                        criteria0.andBetween("recordTime", queryStartTime, queryEndTime);
                    } else {
                        criteria0.andGreaterThanOrEqualTo("recordTime", queryStartTime);
                    }
                } else {
                    if (StringUtils.isNotBlank(queryEndTime)) {
                        criteria0.andLessThanOrEqualTo("recordTime", queryEndTime);
                    }
                }
            }
            // 所有记录表按时间倒序排序
            deviceRecordTableInfoList.sort((o1, o2) -> o2.getId() - o1.getId());
            // 获取Mapper
            TableMapper<?> mapper = MapperUtil.getMapperBean(prefixName);
            assert mapper != null;

            // 查询每个表中的总记录数
            int tableCount = deviceRecordTableInfoList.size();
            CompletableFuture<DeviceRecordQueryDTO>[] countFutureArray = new CompletableFuture[tableCount];
            for (int i = 0; i < tableCount; i++) {
                final DeviceRecordTableInfo deviceRecordTableInfo = deviceRecordTableInfoList.get(i);
                CompletableFuture<DeviceRecordQueryDTO> future = CompletableFuture.supplyAsync(() -> {
                    String tableName = deviceRecordTableInfo.getTableName();
                    int count = mapper.getRecordCountByCondition(tableName, condition);
                    DeviceRecordQueryDTO deviceRecordQueryDTO = new DeviceRecordQueryDTO();
                    deviceRecordQueryDTO.setTableName(tableName);
                    deviceRecordQueryDTO.setCount(count);
                    deviceRecordQueryDTO.setTableId(deviceRecordTableInfo.getId());
                    return deviceRecordQueryDTO;
                }, ThreadPoolFactory.CACHED.getPool());
                countFutureArray[i] = future;
            }
            CompletableFuture<List<DeviceRecordQueryDTO>> countCombineFuture = CompletableFuture.allOf(countFutureArray)
                    .thenApply(v ->
                            Stream.of(countFutureArray)
                                    .map(CompletableFuture::join)
                                    .collect(Collectors.toList()));
            List<DeviceRecordQueryDTO> deviceRecordQueryDTOS = countCombineFuture.get();
            if (CollectionUtils.isEmpty(deviceRecordQueryDTOS)) {
                return result;
            }
            deviceRecordQueryDTOS = deviceRecordQueryDTOS.stream()
                    .filter(deviceRecordQueryDTO -> deviceRecordQueryDTO.getCount() > 0)
                    .sorted((o1, o2) -> o2.getTableId() - o1.getTableId())
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(deviceRecordQueryDTOS)) {
                return result;
            }
            int temporaryOffset = 0;
            int totalRows = 0;
            for (DeviceRecordQueryDTO deviceRecordQueryDTO : deviceRecordQueryDTOS) {
                int count = deviceRecordQueryDTO.getCount();
                int startOffset = temporaryOffset + 1;
                int endOffset = temporaryOffset + count;
                deviceRecordQueryDTO.setStartOffset(startOffset);
                deviceRecordQueryDTO.setEndOffset(endOffset);
                temporaryOffset = endOffset;
                totalRows += count;
            }
            result.setPage(page);
            result.setRows(rows);
            result.setTotalRows(totalRows);
            // 全局起始偏移量
            Integer wholeStartOffset = (page - 1) * rows + 1;
            // 全局结束偏移量
            Integer wholeEndOffset = wholeStartOffset + rows - 1;
            // 通过全局偏移量过滤表
            deviceRecordQueryDTOS = deviceRecordQueryDTOS.stream()
                    .filter(deviceRecordQueryDTO -> wholeStartOffset <= deviceRecordQueryDTO.getEndOffset())
                    .filter(deviceRecordQueryDTO -> wholeEndOffset >= deviceRecordQueryDTO.getStartOffset())
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(deviceRecordQueryDTOS)) {
                return result;
            }

            // 查询记录数据
            CompletableFuture<List<Map<String, Object>>>[] listFutureArray = new CompletableFuture[deviceRecordQueryDTOS.size()];
            for (int i = 0; i < deviceRecordQueryDTOS.size(); i++) {
                DeviceRecordQueryDTO deviceRecordQueryDTO = deviceRecordQueryDTOS.get(i);
                DistributedQueryVO queryVO = new DistributedQueryVO();
                BeanUtils.copyProperties(distributedQueryVO, queryVO);
                queryVO.setTableName(deviceRecordQueryDTO.getTableName());
                int offset = wholeStartOffset - deviceRecordQueryDTO.getStartOffset();
                queryVO.setOffset(offset <= 0 ? 0 : offset);
                CompletableFuture<List<Map<String, Object>>> future = CompletableFuture
                        .supplyAsync(() -> mapper.selectRecordByCondition(queryVO),
                                ThreadPoolFactory.CACHED.getPool());
                listFutureArray[i] = future;
            }
            CompletableFuture<List<Map<String, Object>>> listCombineFuture = CompletableFuture.allOf(listFutureArray)
                    .thenApply(v ->
                            Stream.of(listFutureArray)
                                    .flatMap(listFuture -> {
                                        List<Map<String, Object>> maps = listFuture.join();
                                        return maps.stream();
                                    })
                                    .collect(Collectors.toList()));
            List<Map<String, Object>> resultList = listCombineFuture.get();
            if (!CollectionUtils.isEmpty(resultList)) {
                resultList = resultList.stream()
                        .sorted((o1, o2) -> {
                            Timestamp recordTime1 = (Timestamp) o1.get("recordTime");
                            Timestamp recordTime2 = (Timestamp) o2.get("recordTime");
                            return (int) (recordTime2.getTime() - recordTime1.getTime());
                        })
                        .limit(rows)
                        .collect(Collectors.toList());
                resultList.forEach(stringObjectMap -> {
                    if (stringObjectMap.containsKey("recordTime")) {
                        Timestamp recordTime = (Timestamp) stringObjectMap.get("recordTime");
                        stringObjectMap.put("recordTime", DateFormatUtils.format(recordTime, "yyyy-MM-dd HH:mm:ss.SSS"));
                    }
                });
                result.setList(resultList);
            }
        } catch (Exception e) {
            log.error("异常", e);
        }
        return result;
    }
}
