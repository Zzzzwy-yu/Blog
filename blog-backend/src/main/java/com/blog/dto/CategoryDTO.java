package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 分类DTO
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
public class CategoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50")
    private String name;

    @Size(max = 255, message = "描述长度不能超过255")
    private String description;

    private Integer sortOrder = 0;

    private Integer status = 1;
}
