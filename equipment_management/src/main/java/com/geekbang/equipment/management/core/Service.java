package com.geekbang.equipment.management.core;

import org.apache.ibatis.exceptions.TooManyResultsException;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * Service 层 基础接口，其他Service 接口 请继承该接口
 *
 * @author hejun
 */
public interface Service<T> {
    /**
     * 持久化
     *
     * @param model model
     */
    void save(T model);

    /**
     * 批量持久化
     *
     * @param models models
     */
    void save(List<T> models);

    /**
     * 通过主鍵刪除
     *
     * @param id 主鍵id
     */
    void deleteById(Integer id);

    /**
     * 批量刪除
     * <br/>
     * eg：ids -> “1,2,3,4”
     *
     * @param ids 主鍵id
     */
    void deleteByIds(String ids);

    /**
     * 更新
     *
     * @param model model
     */
    void update(T model);

    /**
     * 通过ID查找
     *
     * @param id 主鍵id
     * @return T
     */
    T findById(Integer id);

    /**
     * 通过Model中某个成员变量名称（非数据表中column的名称）查找,value需符合unique约束
     *
     * @param fieldName 成员变量名称
     * @param value     成员变量值
     * @return T
     * @throws TooManyResultsException
     */
    T findBy(String fieldName, Object value) throws TooManyResultsException;

    /**
     * 通过多个ID查找
     * <br/>
     * eg：ids -> “1,2,3,4”
     *
     * @param ids 主鍵id
     * @return List
     */
    List<T> findByIds(String ids);

    /**
     * 根据条件查找
     *
     * @param condition 查询条件
     * @return List
     */
    List<T> findByCondition(Condition condition);

    /**
     * 获取所有
     *
     * @return List
     */
    List<T> findAll();
}
