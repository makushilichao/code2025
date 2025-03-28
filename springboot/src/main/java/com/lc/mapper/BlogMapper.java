package com.lc.mapper;

import com.lc.entity.Blog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogMapper {

    List<Blog> selectAll(Blog blog);

    void insert(Blog blog);

    void updateById(Blog blog);

    @Delete("delete from `blog` where id = #{id}")
    void deleteById(Integer id);

    Blog selectById(Integer id);
}
