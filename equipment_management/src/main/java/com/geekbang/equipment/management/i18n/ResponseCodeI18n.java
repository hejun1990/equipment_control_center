package com.geekbang.equipment.management.i18n;

/**
 * 结果返回异常信息
 *
 * @author jun_h
 */

public enum ResponseCodeI18n {

    /**
     * 新增失败
     */
    INSERT_FAIL(100001, "api.response.insert.fail"),
    /**
     * 删除失败
     */
    DELETE_FAIL(100002, "api.response.delete.fail"),
    /**
     * 更新失败
     */
    UPDATE_FAIL(100003, "api.response.update.fail"),
    /**
     * 创建表失败
     */
    CREATE_TABLE_FAIL(100004, "api.response.create.table.fail"),
    /**
     * 查询数据为空
     */
    DATA_IS_NULL(100005, "api.response.data.is.null"),
    /**
     * 表前缀名为空
     */
    TABLE_PREFIX_NAME_IS_NULL(100011, "api.response.table.prefixname.is.null"),
    /**
     * 表名为空
     */
    TABLE_NAME_IS_NULL(100012, "api.response.table.name.is.null"),
    /**
     * 参数不正确
     */
    PARAMS_ARE_ERROR(100013, "api.response.params.are.error"),

    /**
     * 数据库名为空
     */
    TABLE_SCHEMA_IS_NULL(100014, "api.response.table.schema.is.null");

    /**
     * 返回码
     */
    private int code;
    /**
     * 返回信息
     */
    private String msg;

    ResponseCodeI18n(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}