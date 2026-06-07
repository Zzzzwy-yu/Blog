package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.BusinessException;
import com.blog.common.ResultCode;
import com.blog.dto.CategoryDTO;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.service.CategoryService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类Service实现
 *
 * @author blog
 * @since 2024-06-07
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public List<Category> listAll() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder).orderByDesc(Category::getCreateTime);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public void save(CategoryDTO dto) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, dto.getName());
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_EXIST.getCode(), "分类名称已存在");
        }
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        category.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);
    }

    @Override
    public void update(CategoryDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Category exist = categoryMapper.selectById(dto.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, dto.getName()).ne(Category::getId, dto.getId());
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_EXIST.getCode(), "分类名称已存在");
        }
        exist.setName(dto.getName());
        exist.setDescription(dto.getDescription());
        exist.setSortOrder(dto.getSortOrder());
        exist.setStatus(dto.getStatus() == null ? exist.getStatus() : dto.getStatus());
        exist.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(exist);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Category exist = categoryMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getCategoryId, id);
        if (articleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该分类下存在文章,无法删除");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public Category getById(Long id) {
        return id == null ? null : categoryMapper.selectById(id);
    }

    @Override
    public List<Category> listWithCount() {
        List<Category> list = listAll();
        for (Category c : list) {
            LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Article::getCategoryId, c.getId()).eq(Article::getStatus, 1);
            long count = articleMapper.selectCount(wrapper);
            c.setSortOrder((int) count);
        }
        return list;
    }
}
