package com.lc.mapper;

import com.lc.entity.Notice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {

    List<Notice> selectAll(Notice notice);

    void insert(Notice notice);

    void updateById(Notice notice);

    @Delete("delete from `notice` where id = #{id}")
    void deleteById(Integer id);
}
