package com.atguigu.yygh.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.yygh.oss.service.FileService;
import com.atguigu.yygh.oss.utils.ConstantOssPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author SIYU
 * 上传文件
 */
@Service //将这个类标记为Spring的服务（Service）组件。
public class FileServiceImpl implements FileService {

    /**
     * 上传文件并返回文件路径
     * @param file 要上传的文件
     * @return 文件路径
     */
    @Override
    public String upload(MultipartFile file) {
        // 获取地域、API、Bucket等信息
        String endpoint = ConstantOssPropertiesUtils.ENDPOINT;
        String accessKeyId = ConstantOssPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantOssPropertiesUtils.SECRECT;
        String bucketName = ConstantOssPropertiesUtils.BUCKET;

        // 上传文件流
        try {
            // 创建OSSClient实例
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            InputStream inputStream = file.getInputStream();

            // 生成新的文件名
            String fileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid + fileName;

            // 按照当前日期创建文件夹，上传到创建的文件夹中
            String timeUrl = new DateTime().toString("yyyy/MM/dd");
            fileName = timeUrl + "/" + fileName;

            // 将文件上传到指定的Bucket
            ossClient.putObject(bucketName, fileName, inputStream);

            // 关闭OSSClient
            ossClient.shutdown();

            // 获取文件上传后的路径
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;

            // 返回上传后的路径
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取阿里云OSS的相关信息，包括Endpoint、Access Key ID、Access Key Secret和Bucket名称，这些信息都来自于ConstantOssPropertiesUtils类中的静态变量。
     * 创建一个OSSClient实例，并通过Endpoint、Access Key ID和Access Key Secret进行初始化。
     * 从上传的文件中获取原始文件名，并生成一个新的文件名。采用UUID来确保文件名的唯一性。
     * 根据当前日期创建一个文件夹，将文件上传到该文件夹中。文件夹的格式为"年/月/日"。
     * 调用OSSClient的putObject方法，将文件流上传到指定的Bucket中。
     * 关闭OSSClient。
     * 构建文件上传后的完整URL，并将其作为结果返回。
     * 如果上传过程中出现异常，则打印异常堆栈信息并返回null。
     */
}
