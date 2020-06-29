package com.geekbang.equipment.management.service.impl;

import com.geekbang.equipment.management.constant.BasicConstant;
import com.geekbang.equipment.management.constant.DeviceTypeConstant;
import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.core.ResultGenerator;
import com.geekbang.equipment.management.dao.DeviceInfoMapper;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.DeviceElectricityRecord;
import com.geekbang.equipment.management.model.DeviceInfo;
import com.geekbang.equipment.management.model.DeviceSensirionRecord;
import com.geekbang.equipment.management.service.DeviceElectricityRecordService;
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
    private DeviceElectricityRecordService deviceElectricityRecordService;

    @Resource
    private DeviceInfoMapper deviceInfoMapper;

    /**
     * 模拟设备数据上报
     *
     * @param lang 国际化语言
     * @return Result
     */
    @Override
    public Result<?> simulationDeviceRecordPush(String lang) {
        DeviceInfo deviceFindRecord = new DeviceInfo();
        deviceFindRecord.setIsDeleted(BasicConstant.NO_DELETE);
        deviceFindRecord.setDeviceTypeNo(DeviceTypeConstant.SENSIRION.getDeviceTypeNo());
        List<DeviceInfo> sensirionList = deviceInfoMapper.select(deviceFindRecord);
        if (CollectionUtils.isEmpty(sensirionList)) {
            return ResultGenerator.genFailResult(ResponseCodeI18n.DATA_IS_NULL.getMsg(), lang);
        }
        deviceFindRecord.setDeviceTypeNo(DeviceTypeConstant.ELECTRICITY.getDeviceTypeNo());
        List<DeviceInfo> electricityList = deviceInfoMapper.select(deviceFindRecord);
        if (CollectionUtils.isEmpty(electricityList)) {
            return ResultGenerator.genFailResult(ResponseCodeI18n.DATA_IS_NULL.getMsg(), lang);
        }
        final List<String> sensirionDeviceCodeList = sensirionList.stream().map(DeviceInfo::getDeviceCode)
                .collect(Collectors.toList());
        final List<String> electricityDeviceCodeList = electricityList.stream().map(DeviceInfo::getDeviceCode)
                .collect(Collectors.toList());
        final int count = 2400;
        // 温湿度
        ThreadPoolFactory.CACHED.getPool().execute(() ->
                addSensirionRecord(0, count, sensirionDeviceCodeList, lang));
        ThreadPoolFactory.CACHED.getPool().execute(() ->
                addSensirionRecord(101, count, sensirionDeviceCodeList, lang));
        ThreadPoolFactory.CACHED.getPool().execute(() ->
                addSensirionRecord(1002, count, sensirionDeviceCodeList, lang));
        ThreadPoolFactory.CACHED.getPool().execute(() ->
                addSensirionRecord(10003, count, sensirionDeviceCodeList, lang));
        ThreadPoolFactory.CACHED.getPool().execute(() ->
                addSensirionRecord(100004, count, sensirionDeviceCodeList, lang));
        // 电表
        ThreadPoolFactory.CACHED.getPool().execute(() ->
                addElectricityRecord(1000005, count, electricityDeviceCodeList, lang));
        ThreadPoolFactory.CACHED.getPool().execute(() ->
                addElectricityRecord(10000006, count, electricityDeviceCodeList, lang));
        ThreadPoolFactory.CACHED.getPool().execute(() ->
                addElectricityRecord(100000007, count, electricityDeviceCodeList, lang));
        ThreadPoolFactory.CACHED.getPool().execute(() ->
                addElectricityRecord(1000000008, count, electricityDeviceCodeList, lang));
        ThreadPoolFactory.CACHED.getPool().execute(() ->
                addElectricityRecord(1100000001, count, electricityDeviceCodeList, lang));
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
            deviceSensirionRecord.setTemperature(getRandomDecimal(random, 100));
            deviceSensirionRecord.setHumidity(getRandomDecimal(random, 100));
            deviceSensirionRecord.setBattery(random.nextInt(100));
            Date recordTime = new Date();
            deviceSensirionRecord.setRecordTime(recordTime);
            deviceSensirionRecordService.add(deviceSensirionRecord, lang);
        }
        log.info("执行完毕{}", salt);
    }

    /**
     * 新增电表数据
     *
     * @param salt           盐值
     * @param count          循环次数
     * @param deviceCodeList 设备编码
     * @param lang           国际化语言
     */
    private void addElectricityRecord(int salt, int count, List<String> deviceCodeList, String lang) {
        int size = deviceCodeList.size();
        Random random = new Random(System.nanoTime() + salt);
        for (int i = 0; i < count; i++) {
            DeviceElectricityRecord deviceElectricityRecord = new DeviceElectricityRecord();
            int idx = random.nextInt(size);
            String deviceCode = deviceCodeList.get(idx);
            deviceElectricityRecord.setDeviceCode(deviceCode);
            deviceElectricityRecord.setDegree(getRandomDecimal(random, 1000));
            deviceElectricityRecord.setPowerRate(getRandomDecimal(random, 1000));
            deviceElectricityRecord.setCurrent(getRandomDecimal(random, 1000));
            deviceElectricityRecord.setVoltage(getRandomDecimal(random, 1000));
            Date recordTime = new Date();
            deviceElectricityRecord.setRecordTime(recordTime);
            deviceElectricityRecordService.add(deviceElectricityRecord, lang);
        }
        log.info("执行完毕{}", salt);
    }

    /**
     * 获取随机小数
     *
     * @param random Random
     * @return BigDecimal
     */
    private BigDecimal getRandomDecimal(Random random, int bound) {
        int i = random.nextInt(bound);
        int d = random.nextInt(bound);
        String num = i + "." + d;
        return new BigDecimal(num);
    }
}
