package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.BusinessException;
import com.blog.common.ResultCode;
import com.blog.dto.TagDTO;
import com.blog.entity.ArticleTag;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.TagService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签Service实现
 *
 * @author blog
 * @since 2024-06-07
 */
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Override
    public List<Tag> listAll() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Tag::getCreateTime);
        return tagMapper.selectList(wrapper);
    }

    @Override
    public void save(TagDTO dto) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, dto.getName());
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_EXIST.getCode(), "标签名称已存在");
        }
        Tag tag = new Tag();
        tag.setName(dto.getName());
        tag.setDescription(dto.getDescription());
        tag.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        tagMapper.insert(tag);
    }

    @Override
    public void update(TagDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Tag exist = tagMapper.selectById(dto.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, dto.getName()).ne(Tag::getId, dto.getId());
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_EXIST.getCode(), "标签名称已存在");
        }
        exist.setName(dto.getName());
        exist.setDescription(dto.getDescription());
        exist.setStatus(dto.getStatus() == null ? exist.getStatus() : dto.getStatus());
        exist.setUpdateTime(LocalDateTime.now());
        tagMapper.updateById(exist);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Tag exist = tagMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        // 检查是否有文章使用此标签
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getTagId, id);
        if (articleTagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该标签被文章引用,无法删除");
        }
        tagMapper.deleteById(id);
    }

    @Override
    public Tag getById(Long id) {
        return id == null ? null : tagMapper.selectById(id);
    }
}
