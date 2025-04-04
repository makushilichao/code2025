package com.lc.mapper;

import com.lc.entity.Comment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectAll(Comment comment);

    void insert(Comment comment);

    void updateById(Comment comment);

    @Delete("delete from `comment` where id = #{id}")
    void deleteById(Integer id);

    List<Comment> selectByBlogId(Integer blogId);

    // 添加根据评论 ID 查询单个评论的方法
    @Select("select * from `comment` where id = #{id}")
    Comment selectById(Comment comment);

    @Insert("insert into `comment` (content, create_time, blog_id, user_id, avatar, user_name, parent_comment_id, is_reply, reply_count) " +
            "values (#{content}, #{createTime}, #{blogId}, #{userId}, #{avatar}, #{userName}, #{parentCommentId}, #{isReply}, 1)")
    void insertReply(Comment comment);
}