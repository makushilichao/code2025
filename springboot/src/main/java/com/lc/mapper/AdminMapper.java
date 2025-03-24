package com.lc.mapper;

import com.lc.entity.Admin;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper{

    List<Admin> selectAll(Admin admin);

    void insert(Admin admin);

    @Select("select * from `admin` where username = #{username}")
    Admin selectByUsername(String username);

    void updateById(Admin admin);

    @Delete("delete from `admin` where id = #{id}")
    void deleteById(Integer id);

    Admin selectById(String id);
}
