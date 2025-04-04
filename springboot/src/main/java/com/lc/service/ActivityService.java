package com.lc.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.entity.Account;
import com.lc.entity.Activity;
import com.lc.entity.Category;
import com.lc.entity.User;
import com.lc.mapper.ActivityMapper;
import com.lc.mapper.CategoryMapper;
import com.lc.mapper.UserMapper;
import com.lc.utils.TokenUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ActivityService {

    @Resource
    ActivityMapper activityMapper;
    @Resource
    CategoryMapper categoryMapper;
    @Resource
    UserMapper userMapper;

    public void add(Activity activity) {
        Account currentUser = TokenUtils.getCurrentUser();
        activity.setAdminId(currentUser.getId());
        activity.setCreateTime(DateUtil.now());
        activityMapper.insert(activity);
    }

    public void updateById(Activity activity) {
        activity.setCreateTime(DateUtil.now());
        activityMapper.updateById(activity);
    }

    public void deleteById(Integer id) {
        activityMapper.deleteById(id);
    }

    public List<Activity> selectAll(Activity activity) {
        return activityMapper.selectAll(activity);
    }

    public PageInfo<Activity> selectPage(Integer pageNum, Integer pageSize, Activity activity) {
        Account currentUser = TokenUtils.getCurrentUser();
        if("USER".equals(currentUser.getRole())){
            activity.setAdminId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Activity> list = activityMapper.selectAll(activity);
        for (Activity dbActivity : list) {
            Integer categoryId = dbActivity.getCategoryId();
            Category category = categoryMapper.selectById(categoryId);
            if(ObjectUtil.isNotEmpty(category)){
                dbActivity.setCategoryTitle(category.getTitle());
            }

        }
        return PageInfo.of(list);
    }

    public Activity selectById(Integer id) {
        Activity activity = activityMapper.selectById(id);
        Integer categoryId = activity.getCategoryId();
        Integer userId = activity.getAdminId();
        Category category = categoryMapper.selectById(categoryId);
        User user = userMapper.selectById(userId.toString());
        if(ObjectUtil.isNotEmpty(category)){
            activity.setCategoryTitle(category.getTitle());
        }
        if(ObjectUtil.isNotEmpty(user)){
            activity.setAdminName(user.getName());
        }
        return activity;
    }

    public PageInfo<Activity> selectByTitle(Activity activity) {
        List<Activity> list = activityMapper.selectAll(activity);
        return PageInfo.of(list);
    }
}
