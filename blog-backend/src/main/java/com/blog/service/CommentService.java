package com.blog.service;

import com.blog.common.PageResult;
import com.blog.dto.CommentDTO;
import com.blog.dto.CommentQueryDTO;
import com.blog.entity.Comment;

/**
 * 评论留言Service接口
 *
 * @author blog
 * @since 2024-06-07
 */
public interface CommentService {

    /**
     * 分页查询(前台-已通过)
     */
    PageResult<Comment> pageFront(CommentQueryDTO dto);

    /**
     * 分页查询(后台-所有)
     */
    PageResult<Comment> pageAdmin(CommentQueryDTO dto);

    /**
     * 访客留言/评论
     */
    void submit(CommentDTO dto, String ip, String userAgent);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 审核通过
     */
    void approve(Long id);

    /**
     * 审核拒绝
     */
    void reject(Long id);
}
