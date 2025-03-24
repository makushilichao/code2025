package com.lc.controller;

import com.github.pagehelper.PageInfo;
import com.lc.common.Result;
import com.lc.entity.Notice;
import com.lc.service.NoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    NoticeService noticeService;

    @GetMapping("/selectAll")
    public Result selectAll(Notice notice) {
        List<Notice> noticeList = noticeService.selectAll(notice);
        return Result.success(noticeList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Notice notice){
        PageInfo<Notice> noticePageInfo = noticeService.selectPage(pageNum, pageSize, notice);
        return Result.success(noticePageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Notice notice){
        noticeService.add(notice);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Notice notice){
        noticeService.updateById(notice);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        noticeService.deleteById(id);
        return Result.success();
    }

}
