package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签DTO
 *
 * @author blog
 * @since 2024-06-07
 */
@Data
public class TagDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50")
    private String name;

    @Size(max = 255, message = "描述长度不能超过255")
    private String description;

    private Integer status = 1;
}
