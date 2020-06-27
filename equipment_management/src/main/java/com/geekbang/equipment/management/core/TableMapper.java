package com.geekbang.equipment.management.core;


import com.geekbang.equipment.management.constant.BasicConstant;
import com.geekbang.equipment.management.constant.DeviceRecordTableConstant;
import com.geekbang.equipment.management.i18n.I18nMessageUtil;
import com.geekbang.equipment.management.i18n.LanguageEnum;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.TableEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.CollectionUtils;

import javax.persistence.Table;
import java.util.Date;
import java.util.List;
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
     * @param createSql 建表语句
     */
    @SelectProvider(type = MySqlBuilder.class, method = "createTableSql")
    void createTable(String createSql);

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

    /**
     * 查询表字段
     *
     * @param tableEntity TableEntity
     * @return List
     */
    @SelectProvider(type = MySqlBuilder.class, method = "getTableColumnsSql")
    List<Map<String, String>> getTableColumns(TableEntity tableEntity);

    /**
     * 新增设备上报数据记录
     *
     * @param tableEntity POJO
     * @return int
     */
    @InsertProvider(type = MySqlBuilder.class, method = "insertRecordSql")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertRecord(TableEntity tableEntity);

    /**
     * 拼接SQL语句类
     */
    class MySqlBuilder {
        public static String createTableSql(String createSql) {
            return createSql;
        }

        public static String getTableColumnsSql(TableEntity tableEntity) {
            Table table = tableEntity.getClass().getDeclaredAnnotation(Table.class);
            String tableName = table.name();
            String schema = table.schema();
            if (StringUtils.isBlank(tableName)) {
                String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                        ResponseCodeI18n.TABLE_NAME_IS_NULL.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
                throw new RuntimeException(message);
            }
            if (StringUtils.isBlank(schema)) {
                String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                        ResponseCodeI18n.TABLE_SCHEMA_IS_NULL.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
                throw new RuntimeException(message);
            }
            return new SQL() {{
                SELECT("column_name");
                FROM("information_schema.columns");
                WHERE("table_name = '" + tableName + "'", "table_schema = '" + schema + "'");
            }}.toString();
        }

        public static String insertRecordSql(TableEntity tableEntity) {
            DeviceRecordTableConstant deviceRecordTableConstant = DeviceRecordTableConstant
                    .getTableConstant(tableEntity.getPrefixName());
            final List<String> columns = deviceRecordTableConstant.getColumns();
            if (CollectionUtils.isEmpty(columns)) {
                String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                        ResponseCodeI18n.PARAMS_ARE_ERROR.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
                throw new RuntimeException(message);
            }
            return new SQL() {{
                INSERT_INTO(tableEntity.getTableName());
                for (String column : columns) {
                    // 转驼峰命名法，获取get方法
                    String valuName;
                    if (column.contains("_")) {
                        String[] columnArray = column.split("_");
                        StringBuilder builder = new StringBuilder(columnArray[0]);
                        for (int i = 1; i < columnArray.length; i++) {
                            String c = columnArray[i];
                            builder.append(Character.toUpperCase(c.charAt(0)));
                            if (c.length() > 1) {
                                builder.append(c.substring(1));
                            }
                        }
                        valuName = builder.toString();
                    } else {
                        valuName = column;
                    }
                    VALUES(column, "#{" + valuName + "}");
                }
            }}.toString();
        }
    }
}
