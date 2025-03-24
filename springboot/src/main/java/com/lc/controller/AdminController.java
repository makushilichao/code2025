package com.lc.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageInfo;
import com.lc.common.Result;
import com.lc.entity.Admin;
import com.lc.service.AdminService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/selectAll")
    public Result selectAll(Admin admin) {
        List<Admin> adminList = adminService.selectAll(admin);
        return Result.success(adminList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Admin admin){
        PageInfo<Admin> adminPageInfo = adminService.selectPage(pageNum, pageSize, admin);
        return Result.success(adminPageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Admin admin){
        adminService.add(admin);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Admin admin){
        adminService.updateById(admin);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        adminService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Admin> list){
        adminService.deleteBatch(list);
        return Result.success();
    }

    //数据导出
    @GetMapping("/export")
    public void exportData(Admin admin, HttpServletResponse response) throws Exception{
        String ids = admin.getIds();
        if(StrUtil.isNotBlank(ids)){
            String[] idsArr = ids.split(",");
            admin.setIdsArr(idsArr);
        }
        //1、拿到全部数据
        List<Admin> list = adminService.selectAll(admin);
        //2、构建Writer对象
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //3、设置中文表头
        writer.addHeaderAlias("账号", "username");
        writer.addHeaderAlias("名称", "name");
        writer.addHeaderAlias("电话", "phone");
        writer.addHeaderAlias("邮箱", "email");
        //排除未添加的字段
        writer.setOnlyAlias(true);
        //4、写入数据到Writer
        writer.write(list);
        //5、设置输出文件的名称和输出流的头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("管理员信息", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        //6、写出输出流，并关闭writer
        ServletOutputStream ops = response.getOutputStream();
        writer.flush(ops);
        writer.close();
        ops.close();
    }

    @PostMapping("/import")
    public Result importData(MultipartFile file) throws Exception{
        //1、创建输入流,构建Reader
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        //2、通过Reader读取数据
        reader.addHeaderAlias("username", "账号");
        reader.addHeaderAlias("name", "名称");
        reader.addHeaderAlias("phone", "电话");
        reader.addHeaderAlias("email", "邮箱");
        List<Admin> list = reader.readAll(Admin.class);
        //3、将数据写到数据库
        for (Admin admin : list) {
            adminService.add(admin);
        }
        return Result.success();
    }
}
