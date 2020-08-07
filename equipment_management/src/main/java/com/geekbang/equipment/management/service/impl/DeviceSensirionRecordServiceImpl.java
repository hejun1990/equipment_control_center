package com.geekbang.equipment.management.service.impl;

import com.geekbang.equipment.management.constant.DeviceRecordTableConstant;
import com.geekbang.equipment.management.core.AbstractService;
import com.geekbang.equipment.management.core.ParamsCheck;
import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.core.ResultGenerator;
import com.geekbang.equipment.management.dao.DeviceRecordTableInfoMapper;
import com.geekbang.equipment.management.dao.DeviceSensirionRecordMapper;
import com.geekbang.equipment.management.model.dto.DeviceRecordQueryDTO;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.DeviceRecordTableInfo;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import com.geekbang.equipment.management.service.DeviceSensirionRecordService;
import com.geekbang.equipment.management.util.PojoCheck;
import com.geekbang.equipment.management.model.vo.DeviceSensirionRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
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
    @Transactional(readOnly = true)
    public Result<?> list(Integer page, Integer size, DeviceSensirionRecordVO record, String lang) {
        // 设备编码
        String deviceCode = record.getDeviceCode();
        ParamsCheck.init().notEmpty(record.getDeviceCode(), ResponseCodeI18n.DEVICE_CODE_IS_NULL.getMsg());
        try {
            // 查询设备上报数据记录表表信息
            DeviceRecordTableInfo deviceRecordTableInfoRecord = new DeviceRecordTableInfo();
            deviceRecordTableInfoRecord.setPrefixName(DeviceRecordTableConstant.SENSIRION.getPrefixName());
            List<DeviceRecordTableInfo> deviceRecordTableInfoList = deviceRecordTableInfoMapper.select(deviceRecordTableInfoRecord);
            if (CollectionUtils.isEmpty(deviceRecordTableInfoList)) {
                return ResultGenerator.genSuccessResult();
            }
            // 通过查询的时间范围确定要查询的表
            String queryStartTime = record.getQueryStartTime();
            if (StringUtils.isNotBlank(queryStartTime)) {
                Date startTime = DateUtils.parseDate(queryStartTime, "yyyy-MM-dd HH:mm:ss.ssssss");
                // 根据查询开始时间过滤表
                deviceRecordTableInfoList = deviceRecordTableInfoList.stream()
                        .filter(deviceRecordTableInfo -> deviceRecordTableInfo.getEndRecordTime() == null
                                || startTime.compareTo(deviceRecordTableInfo.getEndRecordTime()) <= 0)
                        .collect(Collectors.toList());
            }
            String queryEndTime = record.getQueryEndTime();
            if (StringUtils.isNotBlank(queryEndTime)) {
                Date endTime = DateUtils.parseDate(queryEndTime, "yyyy-MM-dd HH:mm:ss.ssssss");
                // 根据查询结束时间过滤表
                deviceRecordTableInfoList = deviceRecordTableInfoList.stream()
                        .filter(deviceRecordTableInfo -> deviceRecordTableInfo.getEndRecordTime() == null
                                || endTime.compareTo(deviceRecordTableInfo.getEndRecordTime()) >= 0)
                        .collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(deviceRecordTableInfoList)) {
                return ResultGenerator.genSuccessResult();
            }
            // 所有记录表按时间倒序排序
            deviceRecordTableInfoList.sort((o1, o2) -> o2.getId() - o1.getId());
            // 查询每个表中的总记录数
            List<DeviceRecordQueryDTO> deviceRecordQueryDTOS = new ArrayList<>(deviceRecordTableInfoList.size());
            int temporaryOffset = 0;
            for (DeviceRecordTableInfo deviceRecordTableInfo : deviceRecordTableInfoList) {
                String tableName = deviceRecordTableInfo.getTableName();
                int count = deviceSensirionRecordMapper.getRecordCountByCode(tableName, deviceCode);
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
            Integer wholeStartOffset = (page - 1) * size + 1;
            // 全局结束偏移量
            Integer wholeEndOffset = wholeStartOffset + size - 1;

        } catch (Exception e) {
            log.error("异常", e);
            return ResultGenerator.genFailResult(e.getMessage());
        }
        return ResultGenerator.genSuccessResult();
    }
}
