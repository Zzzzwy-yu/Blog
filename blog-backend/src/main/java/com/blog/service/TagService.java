package com.blog.service;

import com.blog.dto.TagDTO;
import com.blog.entity.Tag;

import java.util.List;

/**
 * 标签Service接口
 *
 * @author blog
 * @since 2024-06-07
 */
public interface TagService {

    List<Tag> listAll();

    void save(TagDTO dto);

    void update(TagDTO dto);

    void delete(Long id);

    Tag getById(Long id);
}
