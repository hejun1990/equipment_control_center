package com.geekbang.equipment.management.core;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Date;
import java.util.Map;

/**
 * 定制版MyBatis Mapper插件接口，必须要实现建表语句
 *
 * @author hejun
 */
public interface TableMapper<T> extends Mapper<T> {

    /**
     * 动态创建表
     *
     * @param createSQL 建表语句
     */
    @SelectProvider(type = CreateTableBuilder.class, method = "createTable")
    void createTable(String createSQL);

    /**
     * 获取最后记录时间
     *
     * @param tableName 表名
     * @return Date
     */
    @Select("SELECT `record_time` AS recordTime FROM `${tableName}` ORDER BY `record_time` DESC LIMIT 1")
    Date getLastRecordTime(@Param("tableName") String tableName);

    /**
     * 查询建表语句
     *
     * @param tableName 表名
     * @return Map
     */
    @Select("SHOW CREATE TABLE `${tableName}`")
    Map<String, String> getCreateTableInfo(@Param("tableName") String tableName);

    class CreateTableBuilder {
        public static String createTable(String createSQL) {
            return createSQL;
        }
    }
}
