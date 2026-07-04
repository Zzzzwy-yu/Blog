package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.Comment;
import com.blog.entity.CommentLike;
import com.blog.mapper.CommentLikeMapper;
import com.blog.mapper.CommentMapper;
import com.blog.service.CommentLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Resource
    private CommentMapper commentMapper;

    @Override
    @Transactional
    public Map<String, Object> toggleLike(Long commentId, Long userId) {
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId).eq(CommentLike::getUserId, userId);
        CommentLike exist = commentLikeMapper.selectOne(wrapper);

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        Map<String, Object> result = new HashMap<>();

        if (exist != null) {
            commentLikeMapper.deleteById(exist.getId());
            comment.setLikeCount(comment.getLikeCount() - 1);
            result.put("liked", false);
        } else {
            CommentLike like = new CommentLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            commentLikeMapper.insert(like);
            comment.setLikeCount(comment.getLikeCount() + 1);
            result.put("liked", true);
        }
        commentMapper.updateById(comment);
        result.put("likeCount", comment.getLikeCount());
        return result;
    }

    @Override
    public Map<String, Object> getLikeStatus(Long commentId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            result.put("liked", false);
            result.put("likeCount", 0);
            return result;
        }
        result.put("likeCount", comment.getLikeCount());

        if (userId == null) {
            result.put("liked", false);
            return result;
        }

        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId).eq(CommentLike::getUserId, userId);
        CommentLike exist = commentLikeMapper.selectOne(wrapper);
        result.put("liked", exist != null);
        return result;
    }
}
