package com.geekbang.equipment.management.service;

import com.geekbang.equipment.management.model.vo.DistributedQueryResultVO;
import com.geekbang.equipment.management.model.vo.DistributedQueryVO;

import java.util.List;
import java.util.Map;

/**
 * 设备数据上报通用接口
 *
 * @author hejun
 * @date 2020/08/10
 **/
public interface DeviceRecordService {

    /**
     * 水平分表分页查询
     *
     * @param distributedQueryVO 水平分表分页查询参数
     * @return DistributedQueryResultVO
     */
    DistributedQueryResultVO distributedSelectByCondition(DistributedQueryVO distributedQueryVO);
}
