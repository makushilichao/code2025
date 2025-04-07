package com.lc.entity;

import lombok.Data;

@Data
public class Activity {
    private Integer id;
    private String img;
    private String title;
    private String content;
    private String createTime;
    private Integer category2Id;
    private String category2Title;
    private Integer adminId;
    private String adminName;
}
