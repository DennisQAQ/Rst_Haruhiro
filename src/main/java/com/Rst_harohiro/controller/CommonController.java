package com.Rst_harohiro.controller;

import com.Rst_harohiro.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("common")
public class CommonController {
    @Value("${haruhiro.path}")
    private String basePath;
    //文件上传
    @PostMapping("/upload")
    R<String> upload (MultipartFile file){
        //file 是一个临时文件，需要转存到指定位置，否则请求完成后临时文件会删除
        log.info("file:{}",file.toString());
        //原始文件名称
        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        ////使用UUID随机生成文件名，防止因为文件名相同造成文件覆盖
        String fileName = UUID.randomUUID().toString()+suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdir();
        }

        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件回显
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void dawnload(String name, HttpServletResponse response){
        //通过输入流读取文件内容
        try {
            //通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //通过输出流将文件返回浏览器展示
            ServletOutputStream outputStream = response.getOutputStream();
            //设置相应头
            response.setContentType("/image/jepg");

            int leng= 0;
            byte [] bytes = new byte[2048];
            while ((leng = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,leng);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
