package com.atguigu.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author SIYU
 * 上传文件
 */
public interface FileService {
    /**
     * 上传文件并返回文件路径
     * @param file 要上传的文件
     * @return 文件路径
     */
    String upload(MultipartFile file);
}
