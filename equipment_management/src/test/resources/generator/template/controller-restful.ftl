package ${basePackage}.web.controller;

import ${basePackage}.core.Result;
import ${basePackage}.model.${modelNameUpperCamel};
import ${basePackage}.service.${modelNameUpperCamel}Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 *
 * @author ${author}
 * @date ${date}
 **/
@Slf4j
@RestController
@RequestMapping("${baseRequestMapping}/{lang}")
public class ${modelNameUpperCamel}Controller {

    @Resource
    private ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;

    /**
     * 新增
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    @PostMapping
    public Result<?> add(@RequestBody ${modelNameUpperCamel} record, @PathVariable(value = "lang") String lang) {
        return ${modelNameLowerCamel}Service.add(record, lang);
    }

    /**
     * 删除
     *
     * @param id   主键id
     * @param lang 国际化语言
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id, @PathVariable(value = "lang") String lang) {
        return ${modelNameLowerCamel}Service.delete(id, lang);
    }

    /**
     * 修改
     *
     * @param record 参数
     * @param lang   国际化语言
     * @return Result
     */
    @PutMapping
    public Result<?> update(@RequestBody ${modelNameUpperCamel} record, @PathVariable(value = "lang") String lang) {
        return ${modelNameLowerCamel}Service.update(record, lang);
    }

    /**
     * 单个查询
     *
     * @param id   主键id
     * @param lang 国际化语言
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Integer id, @PathVariable(value = "lang") String lang) {
        return ${modelNameLowerCamel}Service.detail(id, lang);
    }

    /**
     * 分页查询
     *
     * @param page   页码
     * @param size   每页行数
     * @param record 查询条件
     * @param lang   国际化语言
     * @return Result
     */
    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size,
                          ${modelNameUpperCamel} record, @PathVariable(value = "lang") String lang) {
        return ${modelNameLowerCamel}Service.list(page, size, record, lang);
    }
}
