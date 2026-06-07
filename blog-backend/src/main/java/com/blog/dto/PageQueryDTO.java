package com.blog.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基础DTO
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
public class PageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码,默认1 */
    private Integer pageNum = 1;

    /** 每页条数,默认10 */
    private Integer pageSize = 10;

    /** 关键字模糊查询 */
    private String keyword;
}
