package com.geekbang.equipment.management.util;

import com.geekbang.equipment.management.core.Result;
import com.geekbang.equipment.management.core.ResultGenerator;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import com.geekbang.equipment.management.model.TableEntity;
import org.apache.commons.lang3.StringUtils;

/**
 * POJO对象属性值检查
 *
 * @author hejun
 */
public class PojoCheck {

    /**
     * 设备数据上报POJO的父POJO检查
     *
     * @param entity POJO
     * @param lang   国际化语言
     * @return Result
     */
    public static Result<?> tableCheck(TableEntity entity, String lang) {
        if (StringUtils.isBlank(entity.getPrefixName())) {
            return ResultGenerator.genFailResult(ResponseCodeI18n.TABLE_PREFIX_NAME_IS_NULL.getMsg(), lang);
        }
        if (StringUtils.isBlank(entity.getTableName())) {
            return ResultGenerator.genFailResult(ResponseCodeI18n.TABLE_NAME_IS_NULL.getMsg(), lang);
        }
        return null;
    }
}
