package com.lc.controller;

import com.github.pagehelper.PageInfo;
import com.lc.common.Result;
import com.lc.entity.Comment;
import com.lc.service.BehindCommentService;
import com.lc.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/behindComment")
public class BehindCommentController {

    @Resource
    BehindCommentService behindCommentService;

    @GetMapping("/selectAll")
    public Result selectAll(Comment comment) {
        List<Comment> commentList = behindCommentService.selectAll(comment);
        return Result.success(commentList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Comment comment){
        PageInfo<Comment> commentPageInfo = behindCommentService.selectPage(pageNum, pageSize, comment);
        return Result.success(commentPageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Comment comment){
        behindCommentService.add(comment);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Comment comment){
        behindCommentService.updateById(comment);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id){
        behindCommentService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/reply")
    public Result reply(@RequestBody Comment comment) {
        // 输入参数校验
        /*if (comment.getParentCommentId() == null) {
            return Result.error("父评论 ID 不能为空");
        }*/
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return Result.error("回复内容不能为空");
        }

        try {
            behindCommentService.replyComment(comment);
            // 可以考虑返回新插入的回复评论的 ID 等信息，这里简单返回成功
            return Result.success();
        } catch (Exception e) {
            // 捕获异常并返回错误信息给前端
            return Result.error("回复评论失败：" + e.getMessage());
        }
    }

}
