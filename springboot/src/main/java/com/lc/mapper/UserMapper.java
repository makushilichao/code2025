package com.lc.mapper;

import com.lc.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper{

    List<User> selectAll(User user);

    void insert(User user);

    @Select("select * from `user` where username = #{username}")
    User selectByUsername(String username);

    void updateById(User user);

    @Delete("delete from `user` where id = #{id}")
    void deleteById(Integer id);

    User selectById(String id);
}
