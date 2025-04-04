package com.lc.controller;

import com.github.pagehelper.PageInfo;
import com.lc.common.Result;
import com.lc.entity.Blog;
import com.lc.service.BlogService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    BlogService blogService;

    @GetMapping("/selectAll")
    public Result selectAll(Blog blog) {
        List<Blog> blogList = blogService.selectAll(blog);
        return Result.success(blogList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Blog blog){
        PageInfo<Blog> blogPageInfo = blogService.selectPage(pageNum, pageSize, blog);
        return Result.success(blogPageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Blog blog){
        blogService.add(blog);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Blog blog){
        blogService.updateById(blog);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        blogService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        Blog blog = blogService.selectById(id);
        return Result.success(blog);
    }

    @GetMapping("/selectByTitle")
    public Result selectByTitle(@RequestParam Blog blog) {
        PageInfo<Blog> blogPageInfo = blogService.selectByTitle(blog);
        return Result.success(blogPageInfo);
    }
}
