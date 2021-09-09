package com.matrix.gmall.product.controller;

import com.matrix.gmall.common.result.Result;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: yihaosun
 * @Date: 2021/9/9 20:37
 */
@RestController
@RequestMapping("admin/product")
public class FileUploadController {

    @Value("${fileServer.url}")
    private String fileServerUrl;

    @PostMapping("fileUpload")
    public Result<String> fileUpload(MultipartFile file) throws MyException, IOException {
        // 1. 载配置文件tracker.conf
        String confFile = this.getClass().getResource("/tracker.conf").getFile();
        String path = "";
        if (confFile != null) {
            // 2. 初始化当前文件
            ClientGlobal.init(confFile);
            // 3. 创建TrackerClient
            TrackerClient trackerClient = new TrackerClient();
            // 4. 创建TrackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            // 5. 创建StorageClient1用来做上传的 storageServer是用来做存储的暂时不用创建
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, null);
            // 6. 文件上传
            // 获取文件的后缀名
            String extName = FilenameUtils.getExtension(file.getOriginalFilename());
            // 7. 获取文件上传之后的URL
            path = storageClient1.upload_appender_file1(file.getBytes(), extName, null);
            System.out.println("文件上传的路径: \t" + path);
        }
        // 返回最终的路径
        return Result.ok(fileServerUrl+path);
    }
}
