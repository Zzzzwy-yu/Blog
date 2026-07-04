package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 评论留言数据访问接口
 *
 * @author blog
 * @since 2024-06-07
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 根据文章ID查询文章标题(供 Service 层补填 articleTitle 使用)
     */
    @Select("SELECT title FROM t_article WHERE id = #{articleId}")
    String selectArticleTitleById(@Param("articleId") Long articleId);

    /**
     * 根据文章ID统计有效评论数
     */
    @Select("SELECT COUNT(*) FROM t_comment WHERE article_id = #{articleId} AND status = 1")
    int countByArticleId(@Param("articleId") Long articleId);
}
