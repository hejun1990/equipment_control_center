package com.geekbang.equipment.management.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备数据上报POJO的父POJO
 *
 * @author hejun
 */
@Data
public class TableEntity implements Serializable {

    private static final long serialVersionUID = -5278551652508193795L;

    /**
     * 前缀名
     */
    private String prefixName;

    /**
     * 表名
     */
    private String tableName;
}
