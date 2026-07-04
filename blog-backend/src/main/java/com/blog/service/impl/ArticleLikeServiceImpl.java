package com.blog.service.impl;

import com.blog.entity.Article;
import com.blog.entity.ArticleLike;
import com.blog.mapper.ArticleLikeMapper;
import com.blog.mapper.ArticleMapper;
import com.blog.service.ArticleLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ArticleLikeServiceImpl implements ArticleLikeService {

    @Resource
    private ArticleLikeMapper articleLikeMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Override
    @Transactional
    public void toggleLike(Long articleId, Long userId) {
        int count = articleLikeMapper.countByArticleIdAndUserId(articleId, userId);
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return;
        }
        if (count > 0) {
            articleLikeMapper.deleteByArticleIdAndUserId(articleId, userId);
            if (article.getLikeCount() != null && article.getLikeCount() > 0) {
                article.setLikeCount(article.getLikeCount() - 1);
            }
        } else {
            ArticleLike like = new ArticleLike();
            like.setArticleId(articleId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            articleLikeMapper.insert(like);
            article.setLikeCount(article.getLikeCount() != null ? article.getLikeCount() + 1 : 1);
        }
        articleMapper.updateById(article);
    }

    @Override
    public boolean isLiked(Long articleId, Long userId) {
        return articleLikeMapper.countByArticleIdAndUserId(articleId, userId) > 0;
    }

    @Override
    public int getLikeCount(Long articleId) {
        return articleLikeMapper.countByArticleId(articleId);
    }
}