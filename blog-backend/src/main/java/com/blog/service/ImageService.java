package com.blog.service;

import com.blog.entity.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    Image upload(MultipartFile file);

    Image getById(Long id);

    void deleteById(Long id);
}
