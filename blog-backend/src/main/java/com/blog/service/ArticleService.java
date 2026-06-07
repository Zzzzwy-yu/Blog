package com.blog.service;

import com.blog.common.PageResult;
import com.blog.dto.ArticleDTO;
import com.blog.dto.ArticleQueryDTO;
import com.blog.entity.Article;

/**
 * 文章Service接口
 *
 * @author blog
 * @since 2024-06-07
 */
public interface ArticleService {

    /**
     * 分页查询(前台-上架状态)
     */
    PageResult<Article> pageFront(ArticleQueryDTO dto);

    /**
     * 分页查询(后台)
     */
    PageResult<Article> pageAdmin(ArticleQueryDTO dto);

    /**
     * 文章详情(+浏览量+1)
     */
    Article detail(Long id);

    /**
     * 发布文章
     */
    void saveArticle(ArticleDTO dto);

    /**
     * 更新文章
     */
    void updateArticle(ArticleDTO dto);

    /**
     * 删除文章
     */
    void deleteArticle(Long id);

    /**
     * 上下架切换
     */
    void switchStatus(Long id, Integer status);
}
