package com.lc.mapper;

import com.lc.entity.Carousel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarouselMapper {

    List<Carousel> selectAll(Carousel carousel);

    void insert(Carousel carousel);

    void updateById(Carousel carousel);

    @Delete("delete from `carousel` where id = #{id}")
    void deleteById(Integer id);
}
