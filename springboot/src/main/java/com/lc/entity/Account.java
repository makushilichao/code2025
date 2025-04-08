package com.lc.entity;

import lombok.Data;

@Data
public class Account {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String name;
    private String role;
    private String token;
    private String avatar;
    private String newPassword;
    private String confirmPassword;
    private String occupation;
    private String address;
    private String adminId;
    private String blogTitle;
    private Integer blogId;
    private Integer sex;
}
