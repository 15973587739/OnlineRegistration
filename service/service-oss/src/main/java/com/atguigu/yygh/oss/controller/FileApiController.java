package com.atguigu.yygh.oss.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.oss.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author SIYU
 * RESTful API接口，通过调用FileService的upload方法将文件上传到阿里云OSS，并返回上传成功后的文件路径。
 */
@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件到阿里云OSS
     * @param file 要上传的文件
     * @return 成功上传后的文件路径
     */
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file){
        // 调用FileService接口的upload方法，上传文件并获取文件路径
        String url = fileService.upload(file);
        // 返回上传成功后的文件路径
        return Result.ok(url);
    }

}
