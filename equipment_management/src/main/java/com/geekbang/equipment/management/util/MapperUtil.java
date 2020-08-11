package com.geekbang.equipment.management.util;

import com.geekbang.equipment.management.constant.BasicConstant;
import com.geekbang.equipment.management.core.TableMapper;
import com.geekbang.equipment.management.i18n.I18nMessageUtil;
import com.geekbang.equipment.management.i18n.LanguageEnum;
import com.geekbang.equipment.management.i18n.ResponseCodeI18n;
import org.apache.commons.lang3.StringUtils;

/**
 * 获取Mapper工具类
 *
 * @author hejun
 */
public class MapperUtil {

    /**
     * 获取Mapper
     *
     * @param prefixName 前缀名
     * @return TableMapper
     */
    public static TableMapper<?> getMapperBean(final String prefixName) {
        String mapperName = MapperUtil.getMapperName(prefixName);
        return BeanHeader.getBean(mapperName);
    }

    /**
     * 获取Mapper类名称
     *
     * @param prefixName 前缀名
     * @return String
     */
    private static String getMapperName(final String prefixName) {
        if (StringUtils.isBlank(prefixName)) {
            String message = I18nMessageUtil.getMessage(LanguageEnum.LANGUAGE_ZH_CN.getLanguage(),
                    ResponseCodeI18n.PARAMS_ARE_ERROR.getMsg(), BasicConstant.DEFAULT_ERROR_MESSAGE);
            throw new RuntimeException(message);
        }
        String[] prefixNameBlock = prefixName.split("_");
        StringBuilder mapperNameBuilder = new StringBuilder(prefixNameBlock[0]);
        for (int i = 1; i < prefixNameBlock.length; i++) {
            String block = prefixNameBlock[i];
            mapperNameBuilder.append(Character.toUpperCase(block.charAt(0)))
                    .append(block.substring(1));
        }
        mapperNameBuilder.append("Mapper");
        return mapperNameBuilder.toString();
    }
}
