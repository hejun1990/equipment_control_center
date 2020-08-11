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
import com.geekbang.equipment.management.model.vo.DistributedQueryVO;
import com.geekbang.equipment.management.service.DeviceRecordService;
import com.geekbang.equipment.management.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * @return List
     */
    @Override
    public List<Map<String, Object>> distributedSelectByCondition(DistributedQueryVO distributedQueryVO) {
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
        List<Map<String, Object>> resultData = new ArrayList<>();
        try {
            // 查询设备上报数据记录表表信息
            DeviceRecordTableInfo deviceRecordTableInfoRecord = new DeviceRecordTableInfo();
            deviceRecordTableInfoRecord.setPrefixName(prefixName);
            List<DeviceRecordTableInfo> deviceRecordTableInfoList = deviceRecordTableInfoMapper.select(deviceRecordTableInfoRecord);
            if (CollectionUtils.isEmpty(deviceRecordTableInfoList)) {
                return null;
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
                        .filter(deviceRecordTableInfo -> deviceRecordTableInfo.getEndRecordTime() == null
                                || endTime.compareTo(deviceRecordTableInfo.getEndRecordTime()) >= 0)
                        .collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(deviceRecordTableInfoList)) {
                return null;
            }
            // 所有记录表按时间倒序排序
            deviceRecordTableInfoList.sort((o1, o2) -> o2.getId() - o1.getId());
            // 查询每个表中的总记录数
            List<DeviceRecordQueryDTO> deviceRecordQueryDTOS = new ArrayList<>(deviceRecordTableInfoList.size());
            int temporaryOffset = 0;
            // 获取Mapper
            TableMapper<?> mapper = MapperUtil.getMapperBean(prefixName);
            assert mapper != null;
            for (DeviceRecordTableInfo deviceRecordTableInfo : deviceRecordTableInfoList) {
                String tableName = deviceRecordTableInfo.getTableName();
                int count = mapper.getRecordCountByCondition(tableName, condition);
                if (count == 0) {
                    continue;
                }
                int startOffset = temporaryOffset + 1;
                int endOffset = temporaryOffset + count;
                DeviceRecordQueryDTO deviceRecordQueryDTO = new DeviceRecordQueryDTO();
                deviceRecordQueryDTO.setTableName(tableName);
                deviceRecordQueryDTO.setStartOffset(startOffset);
                deviceRecordQueryDTO.setEndOffset(endOffset);
                deviceRecordQueryDTOS.add(deviceRecordQueryDTO);
                temporaryOffset = endOffset;
            }
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
                return null;
            }
            for (DeviceRecordQueryDTO deviceRecordQueryDTO : deviceRecordQueryDTOS) {
                distributedQueryVO.setTableName(deviceRecordQueryDTO.getTableName());
                int offset = wholeStartOffset - deviceRecordQueryDTO.getStartOffset();
                distributedQueryVO.setOffset(offset <= 0 ? 0 : offset);
                List<Map<String, Object>> recordList = mapper.selectRecordByCondition(distributedQueryVO);
                resultData.addAll(recordList);
            }
            if (!CollectionUtils.isEmpty(resultData)) {
                resultData = resultData.stream()
                        .sorted((o1, o2) -> {
                            Timestamp recordTime1 = (Timestamp) o1.get("record_time");
                            Timestamp recordTime2 = (Timestamp) o2.get("record_time");
                            return (int) (recordTime2.getTime() - recordTime1.getTime());
                        })
                        .limit(rows)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("异常", e);
        }
        return resultData;
    }
}
