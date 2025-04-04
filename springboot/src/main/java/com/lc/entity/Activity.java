package com.lc.entity;

import lombok.Data;

@Data
public class Activity {
    private Integer id;
    private String img;
    private String title;
    private String content;
    private String createTime;
    private Integer categoryId;
    private String categoryTitle;
    private Integer adminId;
    private String adminName;
}
