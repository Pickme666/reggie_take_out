package com.pickme.reggie.controller;

import com.pickme.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 实现文件上传下载的通用控制器
 */

@Slf4j
@RestController
@RequestMapping("/common")
public class FileTransferController {

    //文件储存位置
    @Value("${reggie.path}")
    private String filePath;

    /**
     * 接收上传的文件，MultipartFile 类是在多部分请求中接收上传文件的表示形式。
     * @param file 接收浏览器上传的文件，这是一个临时文件，如果不转存到指定位置，本次请求完成后临时文件会自动删除
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //获取上传文件名
        String filename = file.getOriginalFilename();
        //截取上传文件的后缀名
        String suffix = filename.substring(filename.lastIndexOf("."));
        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖，并拼接上后缀名
        String newFilename = UUID.randomUUID() + suffix;
        //将文件储存到指定位置
        try {
            file.transferTo(new File(filePath + newFilename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //返回新文件名
        return R.success(newFilename);
    }

    /**
     * 提供文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //字节输入流，读取文件内容
            FileInputStream inputStream = new FileInputStream(filePath + name);
            //获取字节输出流，将文件写回到浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            //文件传输
            int len;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
