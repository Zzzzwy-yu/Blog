package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.BusinessException;
import com.blog.common.PageResult;
import com.blog.common.ResultCode;
import com.blog.dto.CommentDTO;
import com.blog.dto.CommentQueryDTO;
import com.blog.entity.Comment;
import com.blog.mapper.CommentMapper;
import com.blog.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 评论留言Service实现
 *
 * @author blog
 * @since 2024-06-07
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public PageResult<Comment> pageFront(CommentQueryDTO dto) {
        dto.setStatus(1);
        return buildPage(dto);
    }

    @Override
    public PageResult<Comment> pageAdmin(CommentQueryDTO dto) {
        return buildPage(dto);
    }

    private PageResult<Comment> buildPage(CommentQueryDTO dto) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (dto.getArticleId() != null) {
            wrapper.eq(Comment::getArticleId, dto.getArticleId());
        } else if (Boolean.TRUE.equals(dto.getOnlyMsg())) {
            wrapper.isNull(Comment::getArticleId);
        }
        if (dto.getStatus() != null) {
            wrapper.eq(Comment::getStatus, dto.getStatus());
        }
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(Comment::getNickname, dto.getKeyword())
                    .or().like(Comment::getContent, dto.getKeyword()));
        }
        wrapper.orderByDesc(Comment::getCreateTime);

        IPage<Comment> page = new Page<>(
                dto.getPageNum() == null ? 1 : dto.getPageNum(),
                dto.getPageSize() == null ? 10 : dto.getPageSize());

        IPage<Comment> result = commentMapper.selectPage(page, wrapper);
        // 按需补填文章标题（避免手写 LEFT JOIN + customSqlSegment 造成的 500）
        result.getRecords().forEach(c -> {
            if (c.getArticleId() != null) {
                c.setArticleTitle(commentMapper.selectArticleTitleById(c.getArticleId()));
            }
        });
        return PageResult.of(result);
    }

    @Override
    public void submit(CommentDTO dto, String ip, String userAgent) {
        Comment comment = new Comment();
        comment.setArticleId(dto.getArticleId());
        comment.setParentId(dto.getParentId());
        comment.setNickname(dto.getNickname());
        comment.setEmail(dto.getEmail());
        comment.setContent(dto.getContent());
        comment.setIp(ip);
        comment.setUserAgent(userAgent);
        comment.setStatus(0); // 默认待审核
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.insert(comment);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Comment exist = commentMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        commentMapper.deleteById(id);
    }

    @Override
    public void approve(Long id) {
        updateStatus(id, 1);
    }

    @Override
    public void reject(Long id) {
        updateStatus(id, 2);
    }

    private void updateStatus(Long id, Integer status) {
        if (id == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Comment exist = commentMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        exist.setStatus(status);
        exist.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(exist);
    }
}
