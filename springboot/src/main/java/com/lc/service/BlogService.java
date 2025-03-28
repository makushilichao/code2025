package com.lc.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.entity.Account;
import com.lc.entity.Blog;
import com.lc.entity.Category;
import com.lc.entity.User;
import com.lc.mapper.BlogMapper;
import com.lc.mapper.CategoryMapper;
import com.lc.mapper.UserMapper;
import com.lc.utils.TokenUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BlogService {

    @Resource
    BlogMapper blogMapper;
    @Resource
    CategoryMapper categoryMapper;
    @Resource
    UserMapper userMapper;

    public void add(Blog blog) {
        Account currentUser = TokenUtils.getCurrentUser();
        blog.setUserId(currentUser.getId());
        blog.setCreateTime(DateUtil.now());
        blogMapper.insert(blog);
    }

    public void updateById(Blog blog) {
        blog.setCreateTime(DateUtil.now());
        blogMapper.updateById(blog);
    }

    public void deleteById(Integer id) {
        blogMapper.deleteById(id);
    }

    public List<Blog> selectAll(Blog blog) {
        return blogMapper.selectAll(blog);
    }

    public PageInfo<Blog> selectPage(Integer pageNum, Integer pageSize, Blog blog) {
        Account currentUser = TokenUtils.getCurrentUser();
        if("USER".equals(currentUser.getRole())){
            blog.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Blog> list = blogMapper.selectAll(blog);
        for (Blog dbBlog : list) {
            Integer categoryId = dbBlog.getCategoryId();
            Category category = categoryMapper.selectById(categoryId);
            if(ObjectUtil.isNotEmpty(category)){
                dbBlog.setCategoryTitle(category.getTitle());
            }

        }
        return PageInfo.of(list);
    }

    public Blog selectById(Integer id) {
        Blog blog = blogMapper.selectById(id);
        Integer categoryId = blog.getCategoryId();
        Integer userId = blog.getUserId();
        Category category = categoryMapper.selectById(categoryId);
        User user = userMapper.selectById(userId.toString());
        if(ObjectUtil.isNotEmpty(category)){
            blog.setCategoryTitle(category.getTitle());
        }
        if(ObjectUtil.isNotEmpty(user)){
            blog.setUserName(user.getName());
        }
        return blog;
    }
}
