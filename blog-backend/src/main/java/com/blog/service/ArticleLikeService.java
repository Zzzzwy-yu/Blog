package com.blog.service;

public interface ArticleLikeService {

    void toggleLike(Long articleId, Long userId);

    boolean isLiked(Long articleId, Long userId);

    int getLikeCount(Long articleId);
}