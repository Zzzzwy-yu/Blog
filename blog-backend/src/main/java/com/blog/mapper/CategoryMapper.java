package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类数据访问接口
 *
 * @author blog
 * @since 2024-06-07
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
