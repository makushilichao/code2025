package com.lc.entity;

import lombok.Data;

@Data
public class Blog {
    private Integer id;
    private String img;
    private String title;
    private String content;
    private String createTime;
    private Integer categoryId;
    private String categoryTitle;
    private Integer userId;
    private String userName;
}
