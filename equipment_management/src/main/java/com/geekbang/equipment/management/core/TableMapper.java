package com.geekbang.equipment.management.core;


import org.apache.ibatis.annotations.Param;

/**
 * 定制版MyBatis Mapper插件接口，必须要实现建表语句
 *
 * @author hejun
 */
public interface TableMapper<T> extends Mapper<T> {

    /**
     * 动态创建表
     *
     * @param tableName    表名
     * @param tableComment 注释
     * @return int
     */
    int createTable(@Param("tableName") String tableName, @Param("tableComment") String tableComment);
}
