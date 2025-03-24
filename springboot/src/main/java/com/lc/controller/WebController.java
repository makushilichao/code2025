package com.lc.controller;

import com.lc.common.Result;
import com.lc.entity.Account;
import com.lc.entity.User;
import com.lc.exception.CustomerException;
import com.lc.service.AdminService;
import com.lc.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody Account account){
        Account dbAccount = null;
        if("ADMIN".equals(account.getRole())){
            dbAccount = adminService.login(account);
        }else if ("USER".equals(account.getRole())){
            dbAccount = userService.login(account);
        }else {
            throw new CustomerException("非法请求！");
        }
        return Result.success(dbAccount);
    }

    @PostMapping("/register")
    public Result Register(@RequestBody User user){
        userService.register(user);
        return Result.success();
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account){
        if("ADMIN".equals(account.getRole())){
            adminService.updatePassword(account);
        }else if("USER".equals(account.getRole())){
            userService.updatePassword(account);
        }
        return Result.success();
    }
}
