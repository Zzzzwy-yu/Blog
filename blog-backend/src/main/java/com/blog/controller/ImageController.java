package com.blog.controller;

import com.blog.common.BusinessException;
import com.blog.common.Result;
import com.blog.entity.Image;
import com.blog.service.ImageService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Resource
    private ImageService imageService;

    @PostMapping("/admin/image/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        Image image = imageService.upload(file);
        Map<String, Object> result = new HashMap<>();
        result.put("id", image.getId());
        result.put("url", "/api/image/" + image.getId());
        result.put("fileName", image.getFileName());
        result.put("fileSize", image.getFileSize());
        return Result.success(result);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Image image = imageService.getById(id);
        if (image == null || image.getData() == null) {
            throw new BusinessException("图片不存在");
        }
        HttpHeaders headers = new HttpHeaders();
        String contentType = image.getContentType();
        if (contentType == null) {
            contentType = "image/jpeg";
        }
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(image.getData().length);
        return new ResponseEntity<>(image.getData(), headers, HttpStatus.OK);
    }

    @PostMapping("/admin/image/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        imageService.deleteById(id);
        return Result.success();
    }
}
