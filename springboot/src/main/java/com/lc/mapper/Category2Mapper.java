package com.lc.mapper;

import com.lc.entity.Category2;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Category2Mapper {

    List<Category2> selectAll(Category2 category2);

    void insert(Category2 category2);

    void updateById(Category2 category2);

    @Delete("delete from `category2` where id = #{id}")
    void deleteById(Integer id);

    Category2 selectById(Integer category2Id);
}
