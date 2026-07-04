package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.BusinessException;
import com.blog.common.PageResult;
import com.blog.common.ResultCode;
import com.blog.dto.ArticleDTO;
import com.blog.dto.ArticleQueryDTO;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.entity.Comment;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.CommentMapper;
import com.blog.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章Service实现
 *
 * @author blog
 * @since 2024-06-07
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Resource
    private CommentMapper commentMapper;

    @Override
    public PageResult<Article> pageFront(ArticleQueryDTO dto) {
        dto.setStatus(1);
        return buildPage(dto);
    }

    @Override
    public PageResult<Article> pageAdmin(ArticleQueryDTO dto) {
        return buildPage(dto);
    }

    private PageResult<Article> buildPage(ArticleQueryDTO dto) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (dto.getStatus() != null) {
            wrapper.eq(Article::getStatus, dto.getStatus());
        }
        if (dto.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId, dto.getCategoryId());
        }
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(Article::getTitle, dto.getKeyword())
                    .or().like(Article::getSummary, dto.getKeyword()));
        }
        wrapper.orderByDesc(Article::getCreateTime);

        IPage<Article> page = new Page<>(
                dto.getPageNum() == null ? 1 : dto.getPageNum(),
                dto.getPageSize() == null ? 10 : dto.getPageSize());

        // 根据 tagId 过滤文章
        if (dto.getTagId() != null) {
            List<Long> articleIds = getArticleIdsByTagId(dto.getTagId());
            if (articleIds.isEmpty()) {
                return PageResult.of(new Page<>(dto.getPageNum(), dto.getPageSize()));
            }
            wrapper.in(Article::getId, articleIds);
        }

        IPage<Article> result = articleMapper.selectPage(page, wrapper);
        // 填充标签 + 分类名 + 评论数（用 articleMapper 原生 SQL，避免自定义 SQL 导致的 500）
        result.getRecords().forEach(a -> {
            a.setTagList(articleMapper.selectTagsByArticleId(a.getId()));
            if (a.getCategoryId() != null) {
                a.setCategoryName(articleMapper.selectCategoryNameByArticleId(a.getId()));
            }
            a.setCommentCount(commentMapper.countByArticleId(a.getId()));
        });
        return PageResult.of(result);
    }

    private List<Long> getArticleIdsByTagId(Long tagId) {
        return articleTagMapper.selectList(
                        new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, tagId))
                .stream().map(ArticleTag::getArticleId).toList();
    }

    @Override
    public Article detail(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        // 浏览量+1
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        article.setViewCount(article.getViewCount() + 1);
        articleMapper.updateById(article);

        article.setTagList(articleMapper.selectTagsByArticleId(id));
        return article;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveArticle(ArticleDTO dto) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setContentType(dto.getContentType() == null ? "markdown" : dto.getContentType());
        article.setCoverImage(dto.getCoverImage());
        article.setCategoryId(dto.getCategoryId());
        article.setAuthor("admin");
        article.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.insert(article);

        saveArticleTags(article.getId(), dto.getTagIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(ArticleDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Article exist = articleMapper.selectById(dto.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        exist.setTitle(dto.getTitle());
        exist.setSummary(dto.getSummary());
        exist.setContent(dto.getContent());
        if (dto.getContentType() != null) {
            exist.setContentType(dto.getContentType());
        }
        exist.setCoverImage(dto.getCoverImage());
        exist.setCategoryId(dto.getCategoryId());
        exist.setStatus(dto.getStatus() == null ? exist.getStatus() : dto.getStatus());
        exist.setUpdateTime(LocalDateTime.now());
        articleMapper.updateById(exist);

        // 重建标签
        articleTagMapper.deleteByArticleId(exist.getId());
        saveArticleTags(exist.getId(), dto.getTagIds());
    }

    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        for (Long tagId : tagIds) {
            ArticleTag at = new ArticleTag();
            at.setArticleId(articleId);
            at.setTagId(tagId);
            at.setCreateTime(LocalDateTime.now());
            articleTagMapper.insert(at);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Article exist = articleMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(Comment::getArticleId, id);
        commentMapper.delete(commentWrapper);
        articleTagMapper.deleteByArticleId(id);
        articleMapper.deleteById(id);
    }

    @Override
    public void switchStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Article exist = articleMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        exist.setStatus(status);
        exist.setUpdateTime(LocalDateTime.now());
        articleMapper.updateById(exist);
    }
}
