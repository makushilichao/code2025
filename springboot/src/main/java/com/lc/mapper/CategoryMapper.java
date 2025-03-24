package com.lc.mapper;

import com.lc.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> selectAll(Category category);

    void insert(Category category);

    void updateById(Category category);

    @Delete("delete from `category` where id = #{id}")
    void deleteById(Integer id);

    Category selectById(Integer categoryId);
}
