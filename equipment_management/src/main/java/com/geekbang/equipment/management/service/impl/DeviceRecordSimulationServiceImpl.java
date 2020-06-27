package com.geekbang.equipment.management.service.impl;

import com.geekbang.equipment.management.constant.BasicConstant;
import com.geekbang.equipment.management.constant.DeviceTypeConstant;
import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.core.ResultGenerator;
import com.geekbang.equipment.management.dao.DeviceInfoMapper;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.DeviceInfo;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import com.geekbang.equipment.management.service.DeviceRecordSimulationService;
import com.geekbang.equipment.management.service.DeviceSensirionRecordService;
import com.geekbang.equipment.management.util.ThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 设备数据上报模拟器
 *
 * @author hejun
 * @date 2020/06/06
 **/
@Slf4j
@Service
public class DeviceRecordSimulationServiceImpl implements DeviceRecordSimulationService {

    @Resource
    private DeviceSensirionRecordService deviceSensirionRecordService;

    @Resource
    private DeviceInfoMapper deviceInfoMapper;

    /**
     * 模拟温湿度数据上报
     *
     * @param lang 国际化语言
     * @return Result
     */
    @Override
    public Result<?> simulationSensirion(String lang) {
        DeviceInfo deviceFindRecord = new DeviceInfo();
        deviceFindRecord.setDeviceTypeNo(DeviceTypeConstant.SENSIRION.getDeviceTypeNo());
        deviceFindRecord.setIsDeleted(BasicConstant.NO_DELETE);
        List<DeviceInfo> sensirionList = deviceInfoMapper.select(deviceFindRecord);
        if (CollectionUtils.isEmpty(sensirionList)) {
            return ResultGenerator.genFailResult(ResponseCodeI18n.DATA_IS_NULL.getMsg(), lang);
        }
        final List<String> deviceCodeList = sensirionList.stream().map(DeviceInfo::getDeviceCode)
                .collect(Collectors.toList());
        final int count = 30;
        ThreadPoolFactory.COMMON.getPool().execute(() ->
                addSensirionRecord(0, count, deviceCodeList, lang));
        ThreadPoolFactory.COMMON.getPool().execute(() ->
                addSensirionRecord(101, count, deviceCodeList, lang));
        /*
        ThreadPoolFactory.COMMON.getPool().execute(() ->
                addSensirionRecord(1002, count, deviceCodeList, lang));
        ThreadPoolFactory.COMMON.getPool().execute(() ->
                addSensirionRecord(10003, count, deviceCodeList, lang));
        ThreadPoolFactory.COMMON.getPool().execute(() ->
                addSensirionRecord(100004, count, deviceCodeList, lang));
         */
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 新增温湿度数据
     *
     * @param salt           盐值
     * @param count          循环次数
     * @param deviceCodeList 设备编码
     * @param lang           国际化语言
     */
    private void addSensirionRecord(int salt, int count, List<String> deviceCodeList, String lang) {
        int size = deviceCodeList.size();
        Random random = new Random(System.nanoTime() + salt);
        for (int i = 0; i < count; i++) {
            DeviceSensirionRecord deviceSensirionRecord = new DeviceSensirionRecord();
            int idx = random.nextInt(size);
            String deviceCode = deviceCodeList.get(idx);
            deviceSensirionRecord.setDeviceCode(deviceCode);
            int tempI = random.nextInt(100);
            int tempD = random.nextInt(100);
            String temperature = tempI + "." + tempD;
            deviceSensirionRecord.setTemperature(new BigDecimal(temperature));
            int humI = random.nextInt(100);
            int humD = random.nextInt(100);
            String humidity = humI + "." + humD;
            deviceSensirionRecord.setHumidity(new BigDecimal(humidity));
            int battery = random.nextInt(100);
            deviceSensirionRecord.setBattery(battery);
            Date recordTime = new Date();
            deviceSensirionRecord.setRecordTime(recordTime);
            deviceSensirionRecordService.add(deviceSensirionRecord, lang);
        }
        log.info("执行完毕{}", salt);
    }
}
