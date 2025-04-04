package com.lc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer id;
    private String content;
    private String createTime;
    private Integer blogId;
    private String blogTitle;
    private Integer userId;
    private String userName;
    private String avatar;

    // 添加父评论 ID 属性，对应数据库中的 parent_comment_id 字段
    private Integer parentCommentId;
    // 添加回复数量属性，对应数据库中的 reply_count 字段
    private Integer replyCount;
    // 添加是否为回复的标识属性，对应数据库中的 is_reply 字段
    private Integer isReply;

    // 用于存储该评论的所有回复
    @TableField(exist = false)
    private List<Comment> replies;
}