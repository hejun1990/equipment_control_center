package com.geekbang.equipment.management.configurer;


import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;

/**
 * 自定义MyBatis Configuration配置
 *
 * @author hejun
 */
public class CustomizeMyBatisConfiguration extends Configuration {

    public CustomizeMyBatisConfiguration() {
        // mybatis一级缓存的级别（默认SESSION）。设置为"statement"级别后，每次查询结束都会清掉一级缓存。
        // 避免mysql的隔离级别“读已提交”无法起作用
        this.setLocalCacheScope(LocalCacheScope.STATEMENT);
    }
}
