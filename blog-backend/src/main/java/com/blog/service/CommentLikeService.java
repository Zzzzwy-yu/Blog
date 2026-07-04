package com.blog.service;

import java.util.Map;

public interface CommentLikeService {

    Map<String, Object> toggleLike(Long commentId, Long userId);

    Map<String, Object> getLikeStatus(Long commentId, Long userId);
}
