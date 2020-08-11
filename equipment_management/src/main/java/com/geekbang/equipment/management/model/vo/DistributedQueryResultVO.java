package com.geekbang.equipment.management.model.vo;

import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 水平分表分页查询结果
 *
 * @author hejun
 */
@ToString
public class DistributedQueryResultVO implements Serializable {

    /**
     * 列表
     */
    private List<Map<String, Object>> list;

    /**
     * 总行数
     */
    private Integer totalRows;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 行数
     */
    private Integer rows;

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getTotalPage() {
        if (this.totalRows != null && this.totalRows > 0) {
            if (this.rows != null && this.rows > 0) {
                return this.totalRows % this.rows == 0 ? this.totalRows / this.rows : this.totalRows / this.rows + 1;
            }
        }
        return 0;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
