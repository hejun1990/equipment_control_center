package com.geekbang.equipment.management.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tk.mybatis.mapper.entity.Condition;

import java.io.Serializable;

/**
 * 水平分表分页查询
 *
 * @author hejun
 */
@Getter
@Setter
@ToString
public class DistributedQueryVO implements Serializable {

    private static final long serialVersionUID = -1712168771120936094L;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 分页查询条件
     */
    private Condition condition;

    /**
     * 分页查询开始时间
     */
    private String queryStartTime;

    /**
     * 分页查询结束时间
     */
    private String queryEndTime;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 分页偏移量
     */
    private Integer offset;

    /**
     * 分页行数
     */
    private Integer rows;
}
