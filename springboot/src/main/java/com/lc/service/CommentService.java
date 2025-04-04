package com.lc.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.entity.Account;
import com.lc.entity.Blog;
import com.lc.entity.Comment;
import com.lc.entity.User;
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
public class CommentService {

    @Resource
    CommentMapper commentMapper;
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
        commentMapper.insert(comment);
    }

    public void updateById(Comment comment) {
        comment.setCreateTime(DateUtil.now());
        commentMapper.updateById(comment);
    }

    public void deleteById(Integer id) {
        commentMapper.deleteById(id);
    }

    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    public PageInfo<Comment> selectPage(Integer pageNum, Integer pageSize, Comment comment) {
        PageHelper.startPage(pageNum, pageSize);
        Integer blogId1 = comment.getBlogId();
        List<Comment> list = commentMapper.selectByBlogId(blogId1);
        for (Comment dbComment : list) {
            Integer blogId = dbComment.getBlogId();
            Blog blog = blogMapper.selectById(blogId);
            if(ObjectUtil.isNotEmpty(blog)){
                dbComment.setBlogTitle(blog.getTitle());
            }
        }
        return PageInfo.of(list);
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

        commentMapper.insertReply(comment);

        // 更新父评论的回复数量
        Comment parentComment = new Comment();
        parentComment.setId(comment.getParentCommentId());
        parentComment = commentMapper.selectById(parentComment);
        if (parentComment != null) {
            int replyCount = parentComment.getReplyCount() == null? 0 : parentComment.getReplyCount();
            parentComment.setReplyCount(replyCount + 1);
            commentMapper.updateById(parentComment);
        }
    }

    /*public Comment selectByBlogId(Integer blogId) {
        Comment comment = commentMapper.selectByBlogId(blogId);
        Integer userId = comment.getUserId();
        User user = userMapper.selectById(userId.toString());
        return comment;
    }*/
}
