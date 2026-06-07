package com.blog.service;

import com.blog.dto.CategoryDTO;
import com.blog.entity.Category;

import java.util.List;

/**
 * 分类Service接口
 *
 * @author blog
 * @since 2024-06-07
 */
public interface CategoryService {

    /**
     * 列表(全部)
     */
    List<Category> listAll();

    /**
     * 保存
     */
    void save(CategoryDTO dto);

    /**
     * 更新
     */
    void update(CategoryDTO dto);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 详情
     */
    Category getById(Long id);

    /**
     * 带文章数量统计
     */
    List<Category> listWithCount();
}
