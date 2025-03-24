package com.lc.controller;

import com.github.pagehelper.PageInfo;
import com.lc.common.Result;
import com.lc.entity.Category;
import com.lc.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    CategoryService categoryService;

    @GetMapping("/selectAll")
    public Result selectAll(Category category) {
        List<Category> categoryList = categoryService.selectAll(category);
        return Result.success(categoryList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Category category){
        PageInfo<Category> categoryPageInfo = categoryService.selectPage(pageNum, pageSize, category);
        return Result.success(categoryPageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Category category){
        categoryService.add(category);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        categoryService.deleteById(id);
        return Result.success();
    }

}
