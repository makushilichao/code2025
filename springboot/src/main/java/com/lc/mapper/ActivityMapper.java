package com.lc.mapper;

import com.lc.entity.Activity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActivityMapper {

    List<Activity> selectAll(Activity activity);

    void insert(Activity activity);

    void updateById(Activity activity);

    @Delete("delete from `activity` where id = #{id}")
    void deleteById(Integer id);

    Activity selectById(Integer id);

    Activity selectByTitle(String title);
}
