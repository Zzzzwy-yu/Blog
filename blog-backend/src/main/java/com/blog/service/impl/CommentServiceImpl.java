package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.BusinessException;
import com.blog.common.PageResult;
import com.blog.common.ResultCode;
import com.blog.dto.CommentDTO;
import com.blog.dto.CommentQueryDTO;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.CommentLike;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CommentLikeMapper;
import com.blog.mapper.CommentMapper;
import com.blog.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

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

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public PageResult<Comment> pageFront(CommentQueryDTO dto) {
        dto.setStatus(1);
        return buildPage(dto);
    }

    @Override
    public PageResult<Comment> pageFront(CommentQueryDTO dto, Long userId) {
        dto.setStatus(1);
        PageResult<Comment> result = buildPage(dto);
        if (userId != null) {
            for (Comment c : result.getList()) {
                LambdaQueryWrapper<CommentLike> likeWrapper = new LambdaQueryWrapper<>();
                likeWrapper.eq(CommentLike::getCommentId, c.getId()).eq(CommentLike::getUserId, userId);
                CommentLike like = commentLikeMapper.selectOne(likeWrapper);
                c.setLiked(like != null);
            }
        } else {
            for (Comment c : result.getList()) {
                c.setLiked(false);
            }
        }
        return result;
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
    @Transactional(rollbackFor = Exception.class)
    public void submit(CommentDTO dto, String ip, String userAgent) {
        Comment comment = new Comment();
        comment.setArticleId(dto.getArticleId());

        Long parentId = dto.getParentId();
        if (parentId != null) {
            Comment parentComment = commentMapper.selectById(parentId);
            if (parentComment != null && parentComment.getParentId() != null) {
                parentId = parentComment.getParentId();
            }
        }
        comment.setParentId(parentId);

        comment.setNickname(dto.getNickname());
        comment.setEmail(dto.getEmail());
        comment.setContent(dto.getContent());
        comment.setIp(ip);
        comment.setUserAgent(userAgent);
        comment.setStatus(1);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.insert(comment);

        if (dto.getArticleId() != null) {
            Article article = articleMapper.selectById(dto.getArticleId());
            if (article != null) {
                if (article.getCommentCount() == null) {
                    article.setCommentCount(0);
                }
                article.setCommentCount(article.getCommentCount() + 1);
                articleMapper.updateById(article);
            }
        }
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

    @Override
    public PageResult<Comment> pageFrontWithTree(CommentQueryDTO dto, Long userId) {
        dto.setStatus(1);
        LambdaQueryWrapper<Comment> rootWrapper = new LambdaQueryWrapper<>();
        if (dto.getArticleId() != null) {
            rootWrapper.eq(Comment::getArticleId, dto.getArticleId());
        } else if (Boolean.TRUE.equals(dto.getOnlyMsg())) {
            rootWrapper.isNull(Comment::getArticleId);
        }
        rootWrapper.eq(Comment::getStatus, 1);
        rootWrapper.isNull(Comment::getParentId);
        rootWrapper.orderByDesc(Comment::getCreateTime);

        IPage<Comment> page = new Page<>(
                dto.getPageNum() == null ? 1 : dto.getPageNum(),
                dto.getPageSize() == null ? 10 : dto.getPageSize());

        IPage<Comment> rootResult = commentMapper.selectPage(page, rootWrapper);
        java.util.List<Comment> rootComments = rootResult.getRecords();

        for (Comment c : rootComments) {
            c.setChildren(new java.util.ArrayList<>());
            if (c.getArticleId() != null) {
                c.setArticleTitle(commentMapper.selectArticleTitleById(c.getArticleId()));
            }
        }

        if (!rootComments.isEmpty()) {
            java.util.List<Long> rootIds = new java.util.ArrayList<>();
            for (Comment c : rootComments) {
                rootIds.add(c.getId());
            }

            LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.in(Comment::getParentId, rootIds);
            childWrapper.eq(Comment::getStatus, 1);
            childWrapper.orderByAsc(Comment::getCreateTime);

            java.util.List<Comment> childComments = commentMapper.selectList(childWrapper);

            Map<Long, Comment> rootMap = new java.util.HashMap<>();
            for (Comment c : rootComments) {
                rootMap.put(c.getId(), c);
            }

            for (Comment child : childComments) {
                if (rootMap.containsKey(child.getParentId())) {
                    rootMap.get(child.getParentId()).getChildren().add(child);
                }
            }
        }

        java.util.List<Comment> allComments = new java.util.ArrayList<>(rootComments);
        for (Comment root : rootComments) {
            if (root.getChildren() != null) {
                allComments.addAll(root.getChildren());
            }
        }

        if (userId != null) {
            for (Comment c : allComments) {
                LambdaQueryWrapper<CommentLike> likeWrapper = new LambdaQueryWrapper<>();
                likeWrapper.eq(CommentLike::getCommentId, c.getId()).eq(CommentLike::getUserId, userId);
                CommentLike like = commentLikeMapper.selectOne(likeWrapper);
                c.setLiked(like != null);
            }
        } else {
            for (Comment c : allComments) {
                c.setLiked(false);
            }
        }

        return PageResult.of(rootResult);
    }
}
