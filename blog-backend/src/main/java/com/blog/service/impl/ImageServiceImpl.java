package com.blog.service.impl;

import com.blog.common.BusinessException;
import com.blog.common.ResultCode;
import com.blog.entity.Image;
import com.blog.mapper.ImageMapper;
import com.blog.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    private ImageMapper imageMapper;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp");

    @Override
    public Image upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }

        String fileName = file.getOriginalFilename();
        if (!StringUtils.hasText(fileName)) {
            throw new BusinessException("文件名不能为空");
        }

        String ext = getFileExtension(fileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException("不支持的图片格式，支持的格式：jpg, jpeg, png, gif, webp, bmp");
        }

        long size = file.getSize();
        if (size > 10 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过10MB");
        }

        Image image = new Image();
        image.setFileName(fileName);
        image.setFileExt(ext);
        image.setContentType(file.getContentType());
        image.setFileSize(size);

        try {
            image.setData(file.getBytes());
        } catch (IOException e) {
            throw new BusinessException("图片读取失败");
        }

        imageMapper.insert(image);
        return image;
    }

    @Override
    public Image getById(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        return imageMapper.selectById(id);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        imageMapper.deleteById(id);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }
}
