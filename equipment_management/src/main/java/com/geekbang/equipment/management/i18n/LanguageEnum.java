package com.geekbang.equipment.management.i18n;

import org.apache.commons.lang3.StringUtils;

/**
 * @author hejun
 */

public enum LanguageEnum {
	/**
     * 美式英文
     */
    LANGUAGE_EN_US("en_us"),
    /**
     * 简体中文
     */
    LANGUAGE_ZH_CN("zh_cn");

    LanguageEnum(String language){
        this.language = language;
    }

    private String language;
    
    public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	/**
     * 获取指定语言类型(如果没有对应的语言类型,则返回中文)
     *
     * @param language 语言类型
     * @return
     */
    public static String getLanguageType(String language){
        if (StringUtils.isEmpty(language)) {
            return LANGUAGE_ZH_CN.language;
        }
        for (LanguageEnum languageEnum : LanguageEnum.values()) {
            if (languageEnum.language.equalsIgnoreCase(language)) {
                return languageEnum.language;
            }
        }
        return null;
    }
}
