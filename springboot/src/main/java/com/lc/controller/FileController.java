package com.lc.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.lc.common.Result;
import com.lc.exception.CustomerException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws Exception{
        //找到文件位置
        String filePath = System.getProperty("user.dir") + "/files/";
        String realPath = filePath + fileName;
        boolean exist = FileUtil.exist(realPath);
        if(!exist){
            throw new CustomerException("文件不存在！");
        }
        byte[] bytes = FileUtil.readBytes(realPath);
        ServletOutputStream ops = response.getOutputStream();
        ops.write(bytes);
        ops.flush();
        ops.close();
    }

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception{
        //找到文件位置
        String filePath = System.getProperty("user.dir") + "/files/";
        if(!FileUtil.isDirectory(filePath)){
            FileUtil.mkdir(filePath);
        }
        byte[] bytes = file.getBytes();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        //写入文件
        FileUtil.writeBytes(bytes, filePath + fileName);
        String url = "http://localhost:9999/files/download/" + fileName;
        return Result.success(url);
    }

    @PostMapping("/wang/upload")
    public Map<String, Object> wangEditorUpload(MultipartFile file) {
        String flag = System.currentTimeMillis() + "";
        String fileName = file.getOriginalFilename();
        Map<String, Object> resMap = new HashMap<>();
        try {
            String filePath = System.getProperty("user.dir") + "/files/";
            FileUtil.writeBytes(file.getBytes(), filePath + flag + "-" + fileName);
            System.out.println(fileName + "--上传成功！");
            Thread.sleep(1L);
            String http = "http://localhost:9999/files/download/";
            resMap.put("errno", 0);
            resMap.put("data", CollUtil.newArrayList(Dict.create().set("url", http + flag + "-" + fileName)));
        } catch (Exception e) {
            System.err.println(fileName + "--上传失败！");
            resMap.put("errno", 1);
            resMap.put("message", "上传失败: " + e.getMessage());
        }
        return resMap;
    }
}
