package com.blog.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据统计看板VO
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
public class DashboardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文章总数 */
    private Long articleCount;

    /** 分类总数 */
    private Long categoryCount;

    /** 标签总数 */
    private Long tagCount;

    /** 评论总数 */
    private Long commentCount;

    /** 待审核评论 */
    private Long pendingCommentCount;

    /** 今日发布文章数 */
    private Long todayArticleCount;

    /** 总浏览量 */
    private Long totalViewCount;
}
