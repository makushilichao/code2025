package com.lc.controller;

import com.github.pagehelper.PageInfo;
import com.lc.common.Result;
import com.lc.entity.Category2;
import com.lc.service.Category2Service;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category2")
public class Category2Controller {

    @Resource
    Category2Service category2Service;

    @GetMapping("/selectAll")
    public Result selectAll(Category2 category2) {
        List<Category2> category2List = category2Service.selectAll(category2);
        return Result.success(category2List);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Category2 category2){
        PageInfo<Category2> category2PageInfo = category2Service.selectPage(pageNum, pageSize, category2);
        return Result.success(category2PageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Category2 category2){
        category2Service.add(category2);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Category2 category2){
        category2Service.updateById(category2);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        category2Service.deleteById(id);
        return Result.success();
    }

}
