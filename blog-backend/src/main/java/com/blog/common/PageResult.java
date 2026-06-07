package com.blog.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果统一封装
 *
 * @param <T> 数据类型
 * @author blog
 * @since 2024-06-07
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页 */
    private long pageNum;

    /** 每页条数 */
    private long pageSize;

    /** 总条数 */
    private long total;

    /** 总页数 */
    private long pages;

    /** 结果集 */
    private List<T> list;

    public PageResult() {
    }

    public PageResult(long pageNum, long pageSize, long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = pageSize > 0 ? (total + pageSize - 1) / pageSize : 0;
        this.list = list;
    }

    public static <T> PageResult<T> of(long pageNum, long pageSize, long total, List<T> list) {
        return new PageResult<>(pageNum, pageSize, total, list);
    }

    /** MyBatis-Plus IPage 转换工具 */
    public static <T> PageResult<T> of(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        return new PageResult<>(page.getCurrent(), page.getSize(),
                page.getTotal(), page.getRecords());
    }
}
