package ${basePackage}.service;

import com.geekbang.equipment.management.core.Result;
import ${basePackage}.model.${modelNameUpperCamel};
import ${basePackage}.core.Service;


/**
 * @author ${author}
 * @date ${date}
 */
public interface ${modelNameUpperCamel}Service extends Service<${modelNameUpperCamel}> {

    /**
     * 新增
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    Result<?> add(${modelNameUpperCamel} record, String lang);

    /**
     * 删除
     *
     * @param id   主键id
     * @param lang 国际化语言
     * @return Result
     */
    Result<?> delete(Integer id, String lang);

    /**
     * 修改
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    Result<?> update(${modelNameUpperCamel} record, String lang);

    /**
     * 单个查询
     *
     * @param id   主键id
     * @param lang 国际化语言
     * @return Result
     */
    Result<?> detail(Integer id, String lang);

    /**
     * 分页查询
     *
     * @param page   页码
     * @param size   每页行数
     * @param record 查询条件
     * @param lang   国际化语言
     * @return Result
     */
    Result<?> list(Integer page, Integer size, ${modelNameUpperCamel} record, String lang);
}
