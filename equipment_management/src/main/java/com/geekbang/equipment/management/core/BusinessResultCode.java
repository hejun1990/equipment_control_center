package com.geekbang.equipment.management.core;

/**
 * @author zwsky
 */

public enum BusinessResultCode {
    /**
     * 业务结果编码枚举
     */
    PARAM_ERROR(1001, "bus", "参数错误"),
    PARAM_ORG_CODE_NULL(1002, "bus", "公司编码为空"),

    ORG_SCRAP(10001, "bus", "公司已废弃"),
    GOODS_DOWN_SALE(10002, "bus", "商品已下架"),

    WX_MOBILE_NOT_BIND(10003, "bus", "微信号与手机号未绑定"),
    WX_MOBILE_BIND_MORE(10004, "bus", "微信号与不同手机号绑定多次"),
    WX_MOBILE_BIND_MOBILE_NULL(10005, "bus", "微信号绑定的手机号为空"),
    PASS_AUTH_EXCEPTION(10006, "bus", "微信号绑定的手机号为空"),
    USER_BIND_ORG_ALL_FREEZE(10007, "bus", "用户的所属公司全部冻结");

    /**
     * 错误编码
     **/
    private Integer code;
    /**
     * 类型：业务级别：bus，系统级别：sys
     **/
    private String codeType;
    /**
     * 描述messgae
     **/
    private String message;

    BusinessResultCode(Integer code, String codeType, String message) {
        this.code = code;
        this.codeType = codeType;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
