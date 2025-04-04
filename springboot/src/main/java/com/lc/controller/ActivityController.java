package com.lc.controller;

import com.github.pagehelper.PageInfo;
import com.lc.common.Result;
import com.lc.entity.Activity;
import com.lc.service.ActivityService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Resource
    ActivityService activityService;

    @GetMapping("/selectAll")
    public Result selectAll(Activity activity) {
        List<Activity> activityList = activityService.selectAll(activity);
        return Result.success(activityList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Activity activity){
        PageInfo<Activity> activityPageInfo = activityService.selectPage(pageNum, pageSize, activity);
        return Result.success(activityPageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Activity activity){
        activityService.add(activity);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Activity activity){
        activityService.updateById(activity);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        activityService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        Activity activity = activityService.selectById(id);
        return Result.success(activity);
    }

    @GetMapping("/selectByTitle")
    public Result selectByTitle(@RequestParam Activity activity) {
        PageInfo<Activity> activityPageInfo = activityService.selectByTitle(activity);
        return Result.success(activityPageInfo);
    }
}
