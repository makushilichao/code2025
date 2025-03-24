package com.lc.service;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.entity.Account;
import com.lc.entity.Admin;
import com.lc.exception.CustomerException;
import com.lc.mapper.AdminMapper;
import com.lc.utils.TokenUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AdminService{

    @Resource
    AdminMapper adminMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String login(String username) {
        if ("admin".equals(username)) {
            return "admin";
        }else {
            throw new CustomerException("用户名不存在");
        }
    }

    public List<Admin> selectAll(Admin admin){
        return adminMapper.selectAll(admin);
    }

    public PageInfo<Admin> selectPage(Integer pageNum, Integer pageSize, Admin admin) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = adminMapper.selectAll(admin);
        return PageInfo.of(list);
    }

    public void add(Admin admin) {
        Admin dbAdmin = adminMapper.selectByUsername(admin.getUsername());
        if(dbAdmin != null){
            throw new CustomerException("账号已存在！");
        }
        if(StrUtil.isBlank(admin.getPassword())){
            admin.setPassword("admin");
        }
        if(StrUtil.isBlank(admin.getName())){
            admin.setName(admin.getUsername());
        }
        admin.setRole("ADMIN");
        adminMapper.insert(admin);
    }

    public void updateById(Admin admin) {
        adminMapper.updateById(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    public void deleteBatch(List<Admin> list) {
        for (Admin admin : list) {
            this.deleteById(admin.getId());
        }
    }

    public Admin login(Account account) {
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if(dbAdmin == null){
            throw new CustomerException("用户不存在！");
        }
        if(!dbAdmin.getPassword().equals(account.getPassword())){
            throw new CustomerException("账号或密码错误！");
        }
        String token = TokenUtils.genToken(dbAdmin.getId() + "-" + "ADMIN", dbAdmin.getPassword());
        dbAdmin.setToken(token);
        return dbAdmin;
    }

    public Admin selectById(String id) {
        return adminMapper.selectById(id);
    }

    public void updatePassword(Account account) {
        if(!account.getNewPassword().equals(account.getConfirmPassword())){
            throw new CustomerException("500", "您两次输入的密码不一致！");
        }
        Account currentUser = TokenUtils.getCurrentUser();
        if(!account.getPassword().equals(currentUser.getPassword())){
            throw new CustomerException("500", "原密码错误！");
        }
        Admin admin = adminMapper.selectById(currentUser.getId().toString());
        admin.setPassword(account.getNewPassword());
        adminMapper.updateById(admin);
    }
}
