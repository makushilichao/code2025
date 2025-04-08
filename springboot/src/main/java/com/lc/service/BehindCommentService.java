package com.lc.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.entity.Account;
import com.lc.entity.Blog;
import com.lc.entity.Category;
import com.lc.entity.Comment;
import com.lc.mapper.BehindCommentMapper;
import com.lc.mapper.BlogMapper;
import com.lc.mapper.CommentMapper;
import com.lc.mapper.UserMapper;
import com.lc.utils.TokenUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BehindCommentService {

    @Resource
    BehindCommentMapper behindCommentMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    BlogMapper blogMapper;

    public void add(Comment comment) {
        Account currentUser = TokenUtils.getCurrentUser();
        comment.setUserName(currentUser.getName());
        comment.setAvatar(currentUser.getAvatar());
        comment.setUserId(currentUser.getId());
        comment.setCreateTime(DateUtil.now());
        behindCommentMapper.insert(comment);
    }

    public void updateById(Comment comment) {
        comment.setCreateTime(DateUtil.now());
        behindCommentMapper.updateById(comment);
    }

    public void deleteById(Integer id) {
        behindCommentMapper.deleteById(id);
    }

    public List<Comment> selectAll(Comment comment) {
        return behindCommentMapper.selectAll(comment);
    }

    public PageInfo<Comment> selectPage(Integer pageNum, Integer pageSize, Comment comment) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> comments = behindCommentMapper.selectAll(comment);
        for (Comment comment1 : comments) {
            Integer blogId = comment1.getBlogId();
            Blog blog = blogMapper.selectById(blogId);
            if(ObjectUtil.isNotEmpty(blog)){
                comment1.setBlogTitle(blog.getTitle());
            }
        }
        return PageInfo.of(comments);
    }

    public void replyComment(Comment comment) {
        Account currentUser = TokenUtils.getCurrentUser();
        comment.setUserName(currentUser.getUsername());
        comment.setAvatar(currentUser.getAvatar());
        comment.setUserId(currentUser.getId());
        comment.setCreateTime(DateUtil.now());

        // 设置当前回复评论的父评论 ID（假设前端传入了被回复评论的 ID）
        if (comment.getParentCommentId() == null) {
            throw new IllegalArgumentException("父评论 ID 不能为空");
        }
        comment.setIsReply(1); // 将当前评论标记为回复

        behindCommentMapper.insertReply(comment);

        // 更新父评论的回复数量
        Comment parentComment = new Comment();
        parentComment.setId(comment.getParentCommentId());
        parentComment = behindCommentMapper.selectById(parentComment);
        if (parentComment != null) {
            int replyCount = parentComment.getReplyCount() == null? 0 : parentComment.getReplyCount();
            parentComment.setReplyCount(replyCount + 1);
            behindCommentMapper.updateById(parentComment);
        }
    }

    /*public Comment selectByBlogId(Integer blogId) {
        Comment comment = behindCommentMapper.selectByBlogId(blogId);
        Integer userId = comment.getUserId();
        User user = userMapper.selectById(userId.toString());
        return comment;
    }*/
}
