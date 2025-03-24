package com.lc.service;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.entity.Account;
import com.lc.entity.Admin;
import com.lc.entity.User;
import com.lc.exception.CustomerException;
import com.lc.mapper.UserMapper;
import com.lc.utils.TokenUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    @Resource
    UserMapper userMapper;

    public String login(String username) {
        if ("user".equals(username)) {
            return "user";
        }else {
            throw new CustomerException("用户名不存在");
        }
    }

    public List<User> selectAll(User user){
        return userMapper.selectAll(user);
    }

    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize, User user) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll(user);
        return PageInfo.of(list);
    }

    public void add(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if(dbUser != null){
            throw new CustomerException("账号已存在！");
        }
        if(StrUtil.isBlank(user.getPassword())){
            user.setPassword("user");
        }
        if(StrUtil.isBlank(user.getName())){
            user.setName(user.getUsername());
        }
        user.setRole("USER");
        userMapper.insert(user);
    }

    public void updateById(User user) {
        userMapper.updateById(user);
    }

    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    public void deleteBatch(List<User> list) {
        for (User user : list) {
            this.deleteById(user.getId());
        }
    }

    public User login(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if(dbUser == null){
            throw new CustomerException("用户不存在！");
        }
        if(!dbUser.getPassword().equals(account.getPassword())){
            throw new CustomerException("账号或密码错误！");
        }
        String token = TokenUtils.genToken(dbUser.getId() + "-" + "USER", dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    public void register(User user) {
        this.add(user);
    }

    public User selectById(String id) {
        return  userMapper.selectById(id);
    }

    public void updatePassword(Account account) {
        if(!account.getNewPassword().equals(account.getConfirmPassword())){
            throw new CustomerException("500", "您两次输入的密码不一致！");
        }
        Account currentUser = TokenUtils.getCurrentUser();
        if(!account.getPassword().equals(currentUser.getPassword())){
            throw new CustomerException("500", "原密码错误！");
        }
        User user = userMapper.selectById(currentUser.getId().toString());
        user.setPassword(account.getNewPassword());
        userMapper.updateById(user);
    }
}
