package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章数据访问接口
 *
 * @author blog
 * @since 2024-06-07
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 根据文章ID查询所有标签
     */
    @Select("SELECT t.* FROM t_tag t INNER JOIN t_article_tag at ON t.id = at.tag_id WHERE at.article_id = #{articleId}")
    List<com.blog.entity.Tag> selectTagsByArticleId(@Param("articleId") Long articleId);

    /**
     * 根据文章ID查询分类名
     */
    @Select("SELECT c.name FROM t_category c INNER JOIN t_article a ON c.id = a.category_id WHERE a.id = #{articleId}")
    String selectCategoryNameByArticleId(@Param("articleId") Long articleId);
}
