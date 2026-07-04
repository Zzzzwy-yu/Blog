package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface ArticleLikeMapper extends BaseMapper<ArticleLike> {

    @Select("SELECT COUNT(*) FROM t_article_like WHERE article_id = #{articleId}")
    int countByArticleId(@Param("articleId") Long articleId);

    @Select("SELECT COUNT(*) FROM t_article_like WHERE article_id = #{articleId} AND user_id = #{userId}")
    int countByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    @Delete("DELETE FROM t_article_like WHERE article_id = #{articleId} AND user_id = #{userId}")
    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);
}